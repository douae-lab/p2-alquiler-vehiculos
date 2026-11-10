package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class FactoriaCalculadoresPrecioTest {

    @Test
    public void creaCalculadorSegunEnumerado() {
        assertTrue(FactoriaCalculadoresPrecio.getCalculador(PoliticasDePrecios.Normal)
                instanceof CalculadorPrecioNormal);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador(PoliticasDePrecios.TemporadaAlta)
                instanceof CalculadorPrecioTemporadaAlta);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador(PoliticasDePrecios.ClienteFrecuente)
                instanceof CalculadorPrecioClienteFrecuente);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador(PoliticasDePrecios.Oferton)
                instanceof CalculadorPrecioOferton);
    }

    @Test
    public void creaCalculadorSegunNombre() {
        assertTrue(FactoriaCalculadoresPrecio.getCalculador("Normal")
                instanceof CalculadorPrecioNormal);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador("TemporadaAlta")
                instanceof CalculadorPrecioTemporadaAlta);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador("ClienteFrecuente")
                instanceof CalculadorPrecioClienteFrecuente);
        assertTrue(FactoriaCalculadoresPrecio.getCalculador("Oferton")
                instanceof CalculadorPrecioOferton);
    }

    @Test
    public void nombreConEspaciosSeAcepta() {
        assertTrue(FactoriaCalculadoresPrecio.getCalculador("  Normal ")
                instanceof CalculadorPrecioNormal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nombreDesconocidoLanzaExcepcion() {
        FactoriaCalculadoresPrecio.getCalculador("Inventada");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nombreNullLanzaExcepcion() {
        FactoriaCalculadoresPrecio.getCalculador((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void enumeradoNullLanzaExcepcion() {
        FactoriaCalculadoresPrecio.getCalculador((PoliticasDePrecios) null);
    }

    @Test
    public void calculadorDeLaFactoriaCalculaBien() {
        LocalDate inicio = LocalDate.of(2026, 11, 10);
        Vehiculo v = new Turismo("T123", Categoria.NORMAL, 2020, 5, 400.0);
        ICalculadorPrecio c = FactoriaCalculadoresPrecio.getCalculador("ClienteFrecuente");
        assertEquals(150, c.calcularPrecioAlquiler(inicio, inicio.plusDays(3), v));
    }
}