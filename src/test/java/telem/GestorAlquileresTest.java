package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GestorAlquileresTest {

    private final Cliente ana = new Cliente("Ana Lítica", "005");
    private final Cliente lola = new Cliente("Lola Mento", "003");

    private Vehiculo turismoNormal(GestorAlquileres g) {
        return g.obtenerVehiculos("Turismo", Categoria.NORMAL)[0];
    }

    // ---------- obtenerVehiculos ----------

    @Test
    public void vehiculosPorTipo() {
        GestorAlquileres g = new GestorAlquileres();
        assertEquals(3, g.obtenerVehiculos("Turismo").length);
        assertEquals(3, g.obtenerVehiculos("Motocicleta").length);
        assertEquals(3, g.obtenerVehiculos("VehiculoIndustrial").length);
        assertEquals(0, g.obtenerVehiculos("Bicicleta").length);
    }

    @Test
    public void vehiculosPorTipoYCategoria() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo[] r = g.obtenerVehiculos("Motocicleta", Categoria.SUPERIOR);
        assertEquals(1, r.length);
        assertEquals("MSA2021", r[0].getMatricula());
        assertEquals(Categoria.SUPERIOR, r[0].getCategoria());
    }

    @Test
    public void todosLosVehiculosEmpiezanDisponibles() {
        GestorAlquileres g = new GestorAlquileres();
        for (Vehiculo v : g.obtenerVehiculos("Turismo")) {
            assertTrue(v.isDisponible());
        }
    }

    // ---------- alquilar ----------

    @Test
    public void alquilerNormalCalculaPrecioYOcupaVehiculo() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo v = turismoNormal(g);
        int precio = g.alquilar(ana, v, 3); // 3 días * 50
        assertEquals(150, precio);
        assertFalse(v.isDisponible());
        assertEquals(1, g.obtenerAlquileres().length);
    }

    @Test(expected = IllegalStateException.class)
    public void noSePuedeAlquilarUnVehiculoOcupado() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo v = turismoNormal(g);
        g.alquilar(ana, v, 2);
        g.alquilar(lola, v, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duracionCeroLanzaExcepcion() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(ana, turismoNormal(g), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clienteNullLanzaExcepcion() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(null, turismoNormal(g), 1);
    }

    // ---------- cliente frecuente ----------

    @Test
    public void tras3AlquileresElClienteEsFrecuente() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo v = turismoNormal(g);

        for (int i = 0; i < 3; i++) {
            assertEquals(50, g.alquilar(ana, v, 1)); // tarifa normal
            g.entregar(g.obtenerAlquileresPendientes(ana)[0]);
        }
        // 4º alquiler: 50 * 0.80 = 40
        assertEquals(40, g.alquilar(ana, v, 1));
    }

    @Test
    public void laTarifaFrecuenteNoAfectaAOtrosClientes() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo v = turismoNormal(g);
        for (int i = 0; i < 3; i++) {
            g.alquilar(ana, v, 1);
            g.entregar(g.obtenerAlquileresPendientes(ana)[0]);
        }
        assertEquals(50, g.alquilar(lola, v, 1));
    }

    // ---------- histórico ----------

    @Test
    public void historicoDeUnCliente() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo t = turismoNormal(g);
        Vehiculo m = g.obtenerVehiculos("Motocicleta", Categoria.NORMAL)[0];
        g.alquilar(ana, t, 1);
        g.alquilar(lola, m, 1);

        assertEquals(1, g.obtenerAlquileres(ana).length);
        assertEquals(1, g.obtenerAlquileres(lola).length);
        assertEquals(2, g.obtenerAlquileres().length);
        assertEquals(0, g.obtenerAlquileres(new Cliente("Nadie", "999")).length);
    }

    @Test
    public void sinAlquileresElArrayEstaVacio() {
        GestorAlquileres g = new GestorAlquileres();
        assertEquals(0, g.obtenerAlquileres().length);
    }

    // ---------- entrega ----------

    @Test
    public void entregarDejaElVehiculoDisponible() {
        GestorAlquileres g = new GestorAlquileres();
        Vehiculo v = turismoNormal(g);
        g.alquilar(ana, v, 2);

        Alquiler[] pendientes = g.obtenerAlquileresPendientes(ana);
        assertEquals(1, pendientes.length);
        assertTrue(g.entregar(pendientes[0]));
        assertTrue(v.isDisponible());
        assertEquals(0, g.obtenerAlquileresPendientes(ana).length);
        // el histórico se conserva
        assertEquals(1, g.obtenerAlquileres(ana).length);
    }

    @Test
    public void noSePuedeEntregarDosVeces() {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(ana, turismoNormal(g), 1);
        Alquiler a = g.obtenerAlquileresPendientes(ana)[0];
        assertTrue(g.entregar(a));
        assertFalse(g.entregar(a));
    }
}