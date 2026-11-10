package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

/**
 * Pruebas del patrón Estrategia para el cálculo del precio del alquiler.
 */
public class CalculadoresPrecioTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 11, 10);

    private static LocalDate fin(int dias) {
        // alquiler de "dias" días, contando el día de inicio
        return INICIO.plusDays(dias - 1);
    }

    private static Vehiculo turismo(Categoria c) {
        return new Turismo("T123", c, 2020, 5, 400.0);
    }

    private static Vehiculo moto(Categoria c) {
        return new Motocicleta("M123", c, 2020, 200);
    }

    private static Vehiculo industrial(Categoria c) {
        return new VehiculoIndustrial("I123", c, 2020, 1000, 3);
    }

    // ---------- Precio base por tipo y categoría ----------

    @Test
    public void precioBaseTurismo() {
        assertEquals(50, turismo(Categoria.NORMAL).getPrecioBase());
        assertEquals(80, turismo(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(100, turismo(Categoria.SUPERIOR).getPrecioBase());
    }

    @Test
    public void precioBaseMotocicleta() {
        assertEquals(30, moto(Categoria.NORMAL).getPrecioBase());
        assertEquals(50, moto(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(70, moto(Categoria.SUPERIOR).getPrecioBase());
    }

    @Test
    public void precioBaseVehiculoIndustrial() {
        assertEquals(90, industrial(Categoria.NORMAL).getPrecioBase());
        assertEquals(120, industrial(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(150, industrial(Categoria.SUPERIOR).getPrecioBase());
    }

    // ---------- Política Normal ----------

    @Test
    public void normalUnDia() {
        ICalculadorPrecio c = new CalculadorPrecioNormal();
        assertEquals(50, c.calcularPrecioAlquiler(INICIO, fin(1), turismo(Categoria.NORMAL)));
    }

    @Test
    public void normalCuatroDias() {
        ICalculadorPrecio c = new CalculadorPrecioNormal();
        assertEquals(200, c.calcularPrecioAlquiler(INICIO, fin(4), turismo(Categoria.NORMAL)));
    }

    @Test
    public void normalSegunTipoYCategoria() {
        ICalculadorPrecio c = new CalculadorPrecioNormal();
        assertEquals(240, c.calcularPrecioAlquiler(INICIO, fin(3), turismo(Categoria.ESPECIAL)));
        assertEquals(210, c.calcularPrecioAlquiler(INICIO, fin(3), moto(Categoria.SUPERIOR)));
        assertEquals(270, c.calcularPrecioAlquiler(INICIO, fin(3), industrial(Categoria.NORMAL)));
    }

    // ---------- Política Temporada Alta ----------

    @Test
    public void temporadaAltaDuplicaPrecio() {
        ICalculadorPrecio c = new CalculadorPrecioTemporadaAlta();
        assertEquals(100, c.calcularPrecioAlquiler(INICIO, fin(1), turismo(Categoria.NORMAL)));
        assertEquals(400, c.calcularPrecioAlquiler(INICIO, fin(4), turismo(Categoria.NORMAL)));
        assertEquals(800, c.calcularPrecioAlquiler(INICIO, fin(5), turismo(Categoria.ESPECIAL)));
    }

    @Test
    public void temporadaAltaEsElDobleDeNormal() {
        ICalculadorPrecio normal = new CalculadorPrecioNormal();
        ICalculadorPrecio alta = new CalculadorPrecioTemporadaAlta();
        Vehiculo v = industrial(Categoria.SUPERIOR);
        assertEquals(2 * normal.calcularPrecioAlquiler(INICIO, fin(6), v),
                alta.calcularPrecioAlquiler(INICIO, fin(6), v));
    }

    // ---------- Política Ofertón ----------

    @Test
    public void ofertonUnDiaDescuento30() {
        ICalculadorPrecio c = new CalculadorPrecioOferton();
        // 50 * 0.70 = 35
        assertEquals(35, c.calcularPrecioAlquiler(INICIO, fin(1), turismo(Categoria.NORMAL)));
    }

    @Test
    public void ofertonCuatroDias() {
        ICalculadorPrecio c = new CalculadorPrecioOferton();
        assertEquals(140, c.calcularPrecioAlquiler(INICIO, fin(4), turismo(Categoria.NORMAL)));
    }

    @Test
    public void ofertonSieteDiasSinDescuentoAdicional() {
        ICalculadorPrecio c = new CalculadorPrecioOferton();
        // 50 * 0.70 * 7 = 245 (no es "más de siete días")
        assertEquals(245, c.calcularPrecioAlquiler(INICIO, fin(7), turismo(Categoria.NORMAL)));
    }

    @Test
    public void ofertonOchoDiasConDescuentoAdicional() {
        ICalculadorPrecio c = new CalculadorPrecioOferton();
        // 50 * 0.70 * 8 = 280, y 280 * 0.90 = 252
        assertEquals(252, c.calcularPrecioAlquiler(INICIO, fin(8), turismo(Categoria.NORMAL)));
    }

    @Test
    public void ofertonDiezDiasConDescuentoAdicional() {
        ICalculadorPrecio c = new CalculadorPrecioOferton();
        // 100 * 0.70 * 10 = 700, y 700 * 0.90 = 630
        assertEquals(630, c.calcularPrecioAlquiler(INICIO, fin(10), turismo(Categoria.SUPERIOR)));
    }

    // ---------- Política Cliente Frecuente ----------

    @Test
    public void clienteFrecuenteUnDia() {
        ICalculadorPrecio c = new CalculadorPrecioClienteFrecuente();
        // 50 * 0.80 = 40
        assertEquals(40, c.calcularPrecioAlquiler(INICIO, fin(1), turismo(Categoria.NORMAL)));
    }

    @Test
    public void clienteFrecuenteTresDias() {
        ICalculadorPrecio c = new CalculadorPrecioClienteFrecuente();
        // 3 * 40 = 120
        assertEquals(120, c.calcularPrecioAlquiler(INICIO, fin(3), turismo(Categoria.NORMAL)));
    }

    @Test
    public void clienteFrecuenteCuatroDias() {
        ICalculadorPrecio c = new CalculadorPrecioClienteFrecuente();
        // 3 * 40 + 1 * 30 = 150
        assertEquals(150, c.calcularPrecioAlquiler(INICIO, fin(4), turismo(Categoria.NORMAL)));
    }

    @Test
    public void clienteFrecuenteDiezDias() {
        ICalculadorPrecio c = new CalculadorPrecioClienteFrecuente();
        // 3 * 40 + 7 * 30 = 330
        assertEquals(330, c.calcularPrecioAlquiler(INICIO, fin(10), turismo(Categoria.NORMAL)));
    }

    // ---------- Comparación entre políticas ----------

    @Test
    public void ofertonYClienteFrecuenteSonMasBaratasQueNormal() {
        Vehiculo v = turismo(Categoria.NORMAL);
        int normal = new CalculadorPrecioNormal().calcularPrecioAlquiler(INICIO, fin(5), v);
        int oferton = new CalculadorPrecioOferton().calcularPrecioAlquiler(INICIO, fin(5), v);
        int frecuente = new CalculadorPrecioClienteFrecuente().calcularPrecioAlquiler(INICIO, fin(5), v);
        assertTrue(oferton < normal);
        assertTrue(frecuente < normal);
    }

    // ---------- Errores ----------

    @Test(expected = IllegalArgumentException.class)
    public void fechaFinAnteriorAInicioLanzaExcepcion() {
        new CalculadorPrecioNormal().calcularPrecioAlquiler(
                INICIO, INICIO.minusDays(1), turismo(Categoria.NORMAL));
    }

    // ---------- Selección de la estrategia desde Alquiler ----------

    @Test
    public void alquilerSeleccionaCalculadorSegunPolitica() {
        Vehiculo v = turismo(Categoria.NORMAL);
        Cliente cli = new Cliente("Juan Pérez", "123456789");
        Alquiler a = new Alquiler(v, cli, INICIO, fin(4));

        assertEquals(200, a.calcularPrecioAlquiler(INICIO, fin(4), PoliticasDePrecios.Normal, v));
        assertEquals(400, a.calcularPrecioAlquiler(INICIO, fin(4), PoliticasDePrecios.TemporadaAlta, v));
        assertEquals(150, a.calcularPrecioAlquiler(INICIO, fin(4), PoliticasDePrecios.ClienteFrecuente, v));
        assertEquals(140, a.calcularPrecioAlquiler(INICIO, fin(4), PoliticasDePrecios.Oferton, v));
    }

    // ---------- getTipo ----------

    @Test
    public void getTipoDevuelveNombreDeLaClase() {
        assertEquals("Turismo", turismo(Categoria.NORMAL).getTipo());
        assertEquals("Motocicleta", moto(Categoria.NORMAL).getTipo());
        assertEquals("VehiculoIndustrial", industrial(Categoria.NORMAL).getTipo());
    }
}


