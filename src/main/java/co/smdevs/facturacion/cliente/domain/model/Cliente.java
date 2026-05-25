package co.smdevs.facturacion.cliente.domain.model;

import java.util.Objects;

/**
 * Modelo de dominio Cliente. Encapsula las invariantes de negocio:
 * - nombre obligatorio
 * - email con formato válido
 */
public class Cliente {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final Long id;
    private final String nombre;
    private final String email;

    public Cliente(Long id, String nombre, String email) {
        validarNombre(nombre);
        validarEmail(email);
        this.id = id;
        this.nombre = nombre.trim();
        this.email = email.trim().toLowerCase();
    }

    public static Cliente crear(String nombre, String email) {
        return new Cliente(null, nombre, email);
    }

    public Cliente conId(Long nuevoId) {
        return new Cliente(nuevoId, this.nombre, this.email);
    }

    public Cliente actualizar(String nuevoNombre, String nuevoEmail) {
        return new Cliente(this.id, nuevoNombre, nuevoEmail);
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (nombre.trim().length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres");
        }
    }

    private static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (!email.trim().matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El email no tiene un formato válido: " + email);
        }
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(id, cliente.id) && Objects.equals(email, cliente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
