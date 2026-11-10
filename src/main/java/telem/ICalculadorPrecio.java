package telem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Estrategia: calcula el precio de un alquiler según una política de precios.
 */
public interface ICalculadorPrecio {

    int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin, Vehiculo vehiculo);

    /** Número de días del alquiler, incluyendo el día de inicio. */
    default int calcularDias(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin es anterior a la de inicio");
        }
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }
}
