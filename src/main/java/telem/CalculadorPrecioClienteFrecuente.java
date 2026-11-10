package telem;

import java.time.LocalDate;

public class CalculadorPrecioClienteFrecuente implements ICalculadorPrecio {

    @Override
    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin, Vehiculo vehiculo) {
        int dias = calcularDias(fechaInicio, fechaFin);
        int base = vehiculo.getPrecioBase();
        int diasConDescuento20 = Math.min(dias, 3);
        int diasConDescuento40 = Math.max(dias - 3, 0);
        double total = base * 0.80 * diasConDescuento20 + base * 0.60 * diasConDescuento40;
        return (int) Math.round(total);
    }
}
