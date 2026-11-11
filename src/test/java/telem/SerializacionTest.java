package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.Test;

public class SerializacionTest {

    private static boolean mismoJson(Object a, Object b) {
        return new JSONObject(a.toString()).similar(new JSONObject(b.toString()));
    }

    // ---------- Cliente ----------

    @Test
    public void clienteIdaYVuelta() {
        Cliente original = new Cliente("Juan Pérez", "12345678A");
        Cliente copia = FactoriaClientes.getCliente(original.toString());
        assertEquals("Juan Pérez", copia.getNombre());
        assertEquals("12345678A", copia.getDni());
        assertTrue(mismoJson(original, copia));
    }

    // ---------- Vehículos ----------

    @Test
    public void turismoIdaYVuelta() {
        Turismo original = new Turismo("T123", Categoria.SUPERIOR, 2024, 5, 550.5);
        Vehiculo copia = FactoriaVehiculos.getVehiculo(original.toString());
        assertTrue(copia instanceof Turismo);
        Turismo t = (Turismo) copia;
        assertEquals("T123", t.getMatricula());
        assertEquals(Categoria.SUPERIOR, t.getCategoria());
        assertEquals(2024, t.getAnnio());
        assertEquals(5, t.getPlazas());
        assertEquals(550.5, t.getCapacidadMaletero(), 0.0001);
        assertTrue(mismoJson(original, copia));
    }

    @Test
    public void motocicletaIdaYVuelta() {
        Motocicleta original = new Motocicleta("M123", Categoria.ESPECIAL, 2021, 600);
        Vehiculo copia = FactoriaVehiculos.getVehiculo(original.toString());
        assertTrue(copia instanceof Motocicleta);
        assertEquals(600, ((Motocicleta) copia).getCilindrada());
        assertTrue(mismoJson(original, copia));
    }

    @Test
    public void vehiculoIndustrialIdaYVuelta() {
        VehiculoIndustrial original =
                new VehiculoIndustrial("I123", Categoria.NORMAL, 2019, 1400, 2);
        Vehiculo copia = FactoriaVehiculos.getVehiculo(original.toString());
        assertTrue(copia instanceof VehiculoIndustrial);
        VehiculoIndustrial vi = (VehiculoIndustrial) copia;
        assertEquals(1400, vi.getCapacidadCarga());
        assertEquals(2, vi.getNumeroEjes());
        assertTrue(mismoJson(original, copia));
    }

    @Test
    public void disponibilidadSeConserva() {
        Turismo original = new Turismo("T999", Categoria.NORMAL, 2020, 5, 400.0);
        original.setDisponible(true);
        Vehiculo copia = FactoriaVehiculos.getVehiculo(original.toString());
        assertTrue(copia.isDisponible());
    }

    @Test
    public void elJsonDelVehiculoIncluyeElTipo() {
        Vehiculo v = new Motocicleta("M1", Categoria.NORMAL, 2020, 125);
        assertEquals("Motocicleta", new JSONObject(v.toString()).getString("tipo"));
    }

    @Test
    public void precioBaseSeRecalculaTrasDeserializar() {
        Vehiculo original = new Turismo("T1", Categoria.ESPECIAL, 2020, 5, 400.0);
        Vehiculo copia = FactoriaVehiculos.getVehiculo(original.toString());
        assertEquals(original.getPrecioBase(), copia.getPrecioBase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tipoDesconocidoLanzaExcepcion() {
        FactoriaVehiculos.getVehiculo(
                "{\"tipo\":\"Bicicleta\",\"matricula\":\"B1\",\"categoria\":\"NORMAL\","
                + "\"annio\":2020,\"disponible\":true}");
    }

    @Test(expected = org.json.JSONException.class)
    public void jsonSinTipoLanzaExcepcion() {
        FactoriaVehiculos.getVehiculo("{\"matricula\":\"B1\"}");
    }

    // ---------- Alquiler ----------

    @Test
    public void alquilerIdaYVuelta() {
        Vehiculo v = new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400.0);
        Cliente c = new Cliente("Juan Pérez", "123456789");
        LocalDate inicio = LocalDate.of(2026, 11, 10);
        Alquiler original = new Alquiler(v, c, inicio, inicio.plusDays(3));

        Alquiler copia = FactoriaAlquileres.getAlquiler(original.toString());

        assertEquals(inicio, copia.getFechaInicio());
        assertEquals(inicio.plusDays(3), copia.getFechaFin());
        assertEquals("Juan Pérez", copia.getCliente().getNombre());
        assertEquals("ABC123", copia.getVehiculo().getMatricula());
        assertTrue(copia.getVehiculo() instanceof Turismo);
        assertTrue(mismoJson(original, copia));
    }

    @Test
    public void alquilerConMotocicleta() {
        Vehiculo v = new Motocicleta("M77", Categoria.SUPERIOR, 2023, 900);
        Cliente c = new Cliente("Ana Lítica", "005");
        LocalDate inicio = LocalDate.of(2026, 12, 1);
        Alquiler copia = FactoriaAlquileres.getAlquiler(
                new Alquiler(v, c, inicio, inicio).toString());
        assertTrue(copia.getVehiculo() instanceof Motocicleta);
        assertEquals(inicio, copia.getFechaFin());
    }
}