# Facturación — Spring Boot 3 + Arquitectura Hexagonal

Proyecto de práctica con **Spring Boot 3.3**, **Java 17**, **H2 en memoria** y **arquitectura hexagonal (Ports & Adapters)**. CRUDs simples de `Cliente` y `Factura` (con detalles) enfocados en practicar **tests unitarios** de dominio y aplicación.

---

## Estructura por bounded context

Cada contexto (`cliente`, `factura`) sigue la misma forma:

```
co.smdevs.facturacion.<contexto>
├── domain
│   ├── model        ← entidades / value objects puros (sin Spring, sin JPA)
│   └── exception    ← excepciones de negocio
├── application
│   ├── port
│   │   ├── in       ← UseCase (lo que el mundo le pide al dominio)
│   │   └── out      ← RepositoryPort (lo que el dominio le pide al mundo)
│   └── service      ← implementación del UseCase (orquesta dominio + puertos)
└── infrastructure
    └── adapter
        ├── in/web   ← REST controller + DTOs
        └── out/persistence  ← Entity JPA + adapter del puerto + mapper
```

La regla clave: **el dominio y la aplicación no conocen Spring ni JPA**. Solo la infraestructura tiene esas dependencias. Esto permite testear el núcleo sin levantar el contexto de Spring.

---

## Cómo correr

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:facturaciondb`
  - User: `sa` (sin password)

## Cómo correr los tests

```bash
mvn test
```

Los tests son 100% unitarios (no levantan Spring), así que corren en segundos.

---

## Endpoints

### Clientes

```bash
# Crear
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Brian Maldonado","email":"brian@smdevs.co"}'

# Listar
curl http://localhost:8080/api/clientes

# Obtener por id
curl http://localhost:8080/api/clientes/1

# Actualizar
curl -X PUT http://localhost:8080/api/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Brian M.","email":"brian@smdevs.co"}'

# Eliminar
curl -X DELETE http://localhost:8080/api/clientes/1
```

### Facturas

```bash
# Crear (requiere clienteId existente)
curl -X POST http://localhost:8080/api/facturas \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "fecha": "2026-05-24",
    "detalles": [
      {"descripcion":"Café","cantidad":2,"precioUnitario":4500.00},
      {"descripcion":"Pan","cantidad":5,"precioUnitario":1500.00}
    ]
  }'

# Listar todas (o filtradas por cliente)
curl http://localhost:8080/api/facturas
curl "http://localhost:8080/api/facturas?clienteId=1"

# Obtener por id
curl http://localhost:8080/api/facturas/1

# Actualizar detalles
curl -X PUT http://localhost:8080/api/facturas/1/detalles \
  -H "Content-Type: application/json" \
  -d '[{"descripcion":"Té","cantidad":1,"precioUnitario":3000.00}]'

# Eliminar
curl -X DELETE http://localhost:8080/api/facturas/1
```

---

## Modelo de tests (lo importante)

### Tests de dominio (`*/domain/`)
Pruebas puras de Java, sin mocks. Validan **invariantes de negocio**:
- `ClienteTest`: validación de nombre/email, normalización, inmutabilidad, igualdad.
- `DetalleFacturaTest`: cálculo de subtotal, validación de cantidad/precio.
- `FacturaTest`: cálculo de total, rechazo de facturas sin detalles, inmutabilidad.

### Tests de aplicación (`*/application/`)
Con **JUnit 5 + Mockito**. Validan **orquestación** del servicio frente a los puertos:
- `ClienteServiceTest`: mockea `ClienteRepositoryPort`, valida flujos de creación/actualización/eliminación y casos de error (email duplicado, cliente no existe).
- `FacturaServiceTest`: mockea ambos puertos (`Factura` + `Cliente`), valida que se rechace la creación si el cliente no existe.

### Patrones usados en los tests
- `@Nested` para agrupar por comportamiento (Crear / Actualizar / Eliminar / Consultas).
- `@DisplayName` en español para que el reporte sea legible.
- `@ParameterizedTest` con `@ValueSource` y `@NullAndEmptySource` para los casos negativos.
- `ArgumentCaptor` para verificar que el servicio le pasa al puerto los datos correctos.
- `verify(..., never())` para confirmar que **no** se persiste cuando falla una validación.
- `AssertJ` (`assertThat`, `assertThatThrownBy`, `isEqualByComparingTo` para `BigDecimal`).

---

## Decisiones de diseño que conviene notar

1. **`DetalleFactura` como Value Object dentro del agregado `Factura`** (no como entidad aparte con su propio CRUD). El detalle no tiene sentido fuera de una factura, así que se modela como parte del agregado. Esto evita la trampa típica de tener `DetalleService` haciendo updates parciales que rompen invariantes.

2. **El total se calcula, no se persiste**: `Factura.calcularTotal()` siempre suma los subtotales actuales. Así nunca quedan inconsistencias entre detalles y total.

3. **`FacturaService` depende del `ClienteRepositoryPort`, no del `ClienteUseCase`**. Esto mantiene la dirección de dependencias limpia: ambos servicios son pares, no uno encima del otro.

4. **Separación entre `Cliente` (dominio) y `ClienteEntity` (JPA)** con mapper explícito. Es más código, sí, pero es la única forma de mantener el dominio libre de anotaciones JPA y testeable sin contexto Spring.

5. **Constructores con validación + métodos `crear()` / `actualizar()` que devuelven nuevas instancias**. El dominio es inmutable, lo que elimina toda una clase de bugs de mutación accidental.

---
