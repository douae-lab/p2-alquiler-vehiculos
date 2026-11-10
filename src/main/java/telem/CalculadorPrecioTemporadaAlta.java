package telem;

import java.time.LocalDate;

public class CalculadorPrecioTemporadaAlta implements ICalculadorPrecio {

    @Override
    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin, Vehiculo vehiculo) {
        int dias = calcularDias(fechaInicio, fechaFin);
        return 2 * vehiculo.getPrecioBase() * dias;
    }
}