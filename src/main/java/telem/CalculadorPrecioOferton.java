package telem;

import java.time.LocalDate;

public class CalculadorPrecioOferton implements ICalculadorPrecio {

    @Override
    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin, Vehiculo vehiculo) {
        int dias = calcularDias(fechaInicio, fechaFin);
        // 30% de descuento sobre el precio base
        double total = vehiculo.getPrecioBase() * 0.70 * dias;
        // más de 7 días: 10% adicional sobre el total
        if (dias > 7) {
            total = total * 0.90;
        }
        return (int) Math.round(total);
    }
}
