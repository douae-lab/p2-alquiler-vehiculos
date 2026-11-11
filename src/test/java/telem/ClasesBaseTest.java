package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.Test;

public class ClasesBaseTest {

    // ---------- Cliente ----------

    @Test
    public void clienteGetters() {
        Cliente c = new Cliente("Juan Pérez", "12345678A");
        assertEquals("Juan Pérez", c.getNombre());
        assertEquals("12345678A", c.getDni());
    }

    @Test
    public void clienteToStringEsJson() {
        JSONObject json = new JSONObject(new Cliente("Ana", "005").toString());
        assertEquals("Ana", json.getString("nombre"));
        assertEquals("005", json.getString("dni"));
    }

    // ---------- Vehiculo (a través de las subclases) ----------

    @Test
    public void vehiculoGettersComunes() {
        Vehiculo v = new Turismo("T1", Categoria.ESPECIAL, 2022, 5, 400.0);
        assertEquals("T1", v.getMatricula());
        assertEquals(Categoria.ESPECIAL, v.getCategoria());
        assertEquals(2022, v.getAnnio());
    }

    @Test
    public void disponibilidadSeModifica() {
        Vehiculo v = new Motocicleta("M1", Categoria.NORMAL, 2020, 125);
        v.setDisponible(true);
        assertTrue(v.isDisponible());
        v.setDisponible(false);
        assertFalse(v.isDisponible());
    }

    @Test
    public void turismoGettersPropios() {
        Turismo t = new Turismo("T1", Categoria.NORMAL, 2020, 7, 650.5);
        assertEquals(7, t.getPlazas());
        assertEquals(650.5, t.getCapacidadMaletero(), 0.0001);
        assertEquals("Turismo", t.getTipo());
    }

    @Test
    public void motocicletaGettersPropios() {
        Motocicleta m = new Motocicleta("M1", Categoria.NORMAL, 2020, 600);
        assertEquals(600, m.getCilindrada());
        assertEquals("Motocicleta", m.getTipo());
    }

    @Test
    public void industrialGettersPropios() {
        VehiculoIndustrial vi = new VehiculoIndustrial("I1", Categoria.NORMAL, 2020, 2400, 3);
        assertEquals(2400, vi.getCapacidadCarga());
        assertEquals(3, vi.getNumeroEjes());
        assertEquals("VehiculoIndustrial", vi.getTipo());
    }

    @Test
    public void toStringDeVehiculoIncluyeTipoYPrecioBase() {
        JSONObject json = new JSONObject(
                new Turismo("T1", Categoria.SUPERIOR, 2020, 5, 400.0).toString());
        assertEquals("Turismo", json.getString("tipo"));
        assertEquals(100, json.getInt("precioBase"));
        assertEquals("SUPERIOR", json.getString("categoria"));
    }

    // ---------- Alquiler ----------

    @Test
    public void alquilerGetters() {
        Vehiculo v = new Turismo("T1", Categoria.NORMAL, 2020, 5, 400.0);
        Cliente c = new Cliente("Juan", "1");
        LocalDate inicio = LocalDate.of(2026, 11, 10);
        Alquiler a = new Alquiler(v, c, inicio, inicio.plusDays(3));
        assertEquals(v, a.getVehiculo());
        assertEquals(c, a.getCliente());
        assertEquals(inicio, a.getFechaInicio());
        assertEquals(inicio.plusDays(3), a.getFechaFin());
    }

    @Test
    public void alquilerToStringEsJsonConTodosLosCampos() {
        Vehiculo v = new Motocicleta("M1", Categoria.NORMAL, 2020, 125);
        Alquiler a = new Alquiler(v, new Cliente("Juan", "1"),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        JSONObject json = new JSONObject(a.toString());
        assertEquals("2026-11-10", json.getString("fechaInicio"));
        assertEquals("2026-11-12", json.getString("fechaFin"));
        assertNotNull(json.getJSONObject("cliente"));
        assertEquals("Motocicleta", json.getJSONObject("vehiculo").getString("tipo"));
    }

    // ---------- Enumerados ----------

    @Test
    public void categoriasYPoliticasExisten() {
        assertEquals(3, Categoria.values().length);
        assertEquals(4, PoliticasDePrecios.values().length);
    }
}
