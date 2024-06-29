package matheus.ismael.distributed;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public enum Option {
    reservar(" - Solicita a reserva de uma sala"),
    registrar(" - Registra uma nova sala"),
    verificar(" - Busca uma sala a partir de um padrão"),
    listar(" - Lista as salas disponíveis"),
    sair(" - Sair do sistema"),;

    final String description;
    public static Optional<Option> findByCode(String value) {
        return Arrays.stream(values()).filter(v -> Objects.equals(v.toString(), value)).findFirst();
    }
}
