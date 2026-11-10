package telem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.json.JSONObject;

/**
 * Modela un alquiler de un vehículo por parte de un cliente, con fechas de inicio y fin.
 * Almacena los alquileres realizados en un array estático y proporciona métodos para calcular el precio del alquiler.
 * Alquiler
 */
public class Alquiler {
    
    private Vehiculo vehiculo;
    private Cliente cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private static Alquiler[] alquileres = new Alquiler[1000];

    public static final int PRECIO_BASE = 50;
    private static int contadorAlquileres = 0;

    public Alquiler(Vehiculo vehiculo, Cliente cliente, LocalDate fechaInicio,LocalDate fechaFin) {
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static Alquiler[] obtenerAlquileres() {
        return alquileres; 
    }

    public static void agregarAlquiler(Alquiler alquiler) {
        if (contadorAlquileres < alquileres.length) {
            alquileres[contadorAlquileres] = alquiler;
            contadorAlquileres++;
        } else {
            System.out.println("No se pueden agregar más alquileres. Capacidad máxima alcanzada.");
        }
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate  getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin) {
    // +1 to include the start day
    int dias = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    return dias * PRECIO_BASE;
}

    @Override 
    public String toString() {
        return new JSONObject(this).toString();
    }

   public static void main(String[] args) {
    Vehiculo vehiculo1 = new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400.0);
    Cliente cliente1 = new Cliente("Juan Pérez", "123456789");
    LocalDate hoy = LocalDate.now();
    Alquiler alquiler1 =
        new Alquiler(vehiculo1, cliente1, hoy, hoy.plusDays(3));
    System.out.println(alquiler1.toString());
    System.out.println("-----------------------------------------------");
    Vehiculo vehiculo2 =
        new VehiculoIndustrial("ABC123", Categoria.NORMAL, 2020, 1000, 3);
    Alquiler alquiler2 =
        new Alquiler(vehiculo2, cliente1, hoy, hoy.plusDays(3));
    System.out.println(alquiler2.toString());
    System.out.println("-----------------------------------------------");
}
}
