package telem;

import java.time.LocalDate;

public class CalculadorPrecioNormal implements ICalculadorPrecio {

    @Override
    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin, Vehiculo vehiculo) {
        int dias = calcularDias(fechaInicio, fechaFin);
        return vehiculo.getPrecioBase() * dias;
    }
}