package telem;

/**
 * Método Factoría: crea el calculador de precio adecuado
 * a partir del nombre de la política o del valor del enumerado.
 */
public class FactoriaCalculadoresPrecio {

    public static ICalculadorPrecio getCalculador(PoliticasDePrecios politica) {
        if (politica == null) {
            throw new IllegalArgumentException("La política de precios no puede ser null");
        }
        switch (politica) {
            case Normal:
                return new CalculadorPrecioNormal();
            case TemporadaAlta:
                return new CalculadorPrecioTemporadaAlta();
            case ClienteFrecuente:
                return new CalculadorPrecioClienteFrecuente();
            case Oferton:
                return new CalculadorPrecioOferton();
            default:
                throw new IllegalArgumentException("Política desconocida: " + politica);
        }
    }

    public static ICalculadorPrecio getCalculador(String nombrePolitica) {
        if (nombrePolitica == null) {
            throw new IllegalArgumentException("El nombre de la política no puede ser null");
        }
        try {
            return getCalculador(PoliticasDePrecios.valueOf(nombrePolitica.trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Política de precios desconocida: " + nombrePolitica);
        }
    }

    public static void main(String[] args) {
        ICalculadorPrecio c = FactoriaCalculadoresPrecio.getCalculador("Oferton");
        Vehiculo v = new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400.0);
        java.time.LocalDate hoy = java.time.LocalDate.now();
        System.out.println("Ofertón, 4 días: " + c.calcularPrecioAlquiler(hoy, hoy.plusDays(3), v));
    }
}