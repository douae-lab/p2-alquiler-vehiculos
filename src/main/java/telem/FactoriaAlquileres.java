package telem;

import java.time.LocalDate;

/**
 * Método Factoría para alquileres: crea un Alquiler a partir de su JSON.
 */
public class FactoriaAlquileres {

    public static Alquiler getAlquiler(String jsonString) {
        return new Alquiler(jsonString);
    }

    public static void main(String[] args) {
        Vehiculo v = new Motocicleta("MOT123", Categoria.ESPECIAL, 2022, 600);
        Cliente c = new Cliente("Ana Lítica", "005");
        LocalDate hoy = LocalDate.now();
        Alquiler a1 = new Alquiler(v, c, hoy, hoy.plusDays(2));
        String json = a1.toString();
        System.out.println(json);
        Alquiler a2 = FactoriaAlquileres.getAlquiler(json);
        System.out.println(a2);
    }
}
