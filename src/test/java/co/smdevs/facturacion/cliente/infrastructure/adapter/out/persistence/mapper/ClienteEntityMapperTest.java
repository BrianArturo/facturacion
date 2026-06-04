package co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence.mapper;

import co.smdevs.facturacion.cliente.domain.model.Cliente;
import co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClienteEntityMapper")
class ClienteEntityMapperTest {

    @Test
    @DisplayName("toEntity convierte correctamente desde dominio a entidad")
    void toEntityMapeaCampos() {
        var cliente = new Cliente(1L, "Brian", "brian@test.com");

        ClienteEntity entity = ClienteEntityMapper.toEntity(cliente);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("Brian");
        assertThat(entity.getEmail()).isEqualTo("brian@test.com");
    }

    @Test
    @DisplayName("toDomain convierte correctamente desde entidad a dominio")
    void toDomainMapeaCampos() {
        var entity = new ClienteEntity(2L, "Ana", "ana@test.com");

        Cliente cliente = ClienteEntityMapper.toDomain(entity);

        assertThat(cliente.getId()).isEqualTo(2L);
        assertThat(cliente.getNombre()).isEqualTo("Ana");
        assertThat(cliente.getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    @DisplayName("toEntity y toDomain son inversas entre sí")
    void roundTrip() {
        var original = new Cliente(3L, "Carlos", "carlos@test.com");
        Cliente resultado = ClienteEntityMapper.toDomain(ClienteEntityMapper.toEntity(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getNombre()).isEqualTo(original.getNombre());
        assertThat(resultado.getEmail()).isEqualTo(original.getEmail());
    }
}