package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GestorPersistenciaTest {

    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

    private String ruta(String nombre) {
        return new File(carpeta.getRoot(), nombre).getPath();
    }

    private static Cliente cliente(GestorAlquileres g, String dni) {
        for (Cliente c : g.obtenerClientes()) {
            if (c.getDni().equals(dni)) {
                return c;
            }
        }
        return null;
    }

    private static Vehiculo turismoNormal(GestorAlquileres g) {
        return g.obtenerVehiculos("Turismo", Categoria.NORMAL)[0];
    }

    // ---------- guardar ----------

    @Test
    public void guardarCreaFicheroConTresSecciones() throws IOException {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(cliente(g, "005"), turismoNormal(g), 2);

        String ruta = ruta("datos.json");
        g.guardar(ruta);

        assertTrue(new File(ruta).exists());
        String texto = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        JSONObject raiz = new JSONObject(texto);
        assertEquals(6, raiz.getJSONArray("clientes").length());
        assertEquals(9, raiz.getJSONArray("vehiculos").length());
        assertEquals(1, raiz.getJSONArray("alquileres").length());
        assertFalse(raiz.getJSONArray("alquileres").getJSONObject(0).getBoolean("entregado"));
    }

    @Test
    public void guardarSinAlquileresGeneraArrayVacio() throws IOException {
        GestorAlquileres g = new GestorAlquileres();
        String ruta = ruta("vacio.json");
        g.guardar(ruta);
        String texto = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        assertEquals(0, new JSONObject(texto).getJSONArray("alquileres").length());
    }

    // ---------- ida y vuelta ----------

    @Test
    public void guardarYCargarConservaClientesYVehiculos() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);

        assertEquals(6, copia.obtenerClientes().length);
        assertEquals("Ana Lítica", cliente(copia, "005").getNombre());
        assertEquals(3, copia.obtenerVehiculos("Turismo").length);
        assertEquals(3, copia.obtenerVehiculos("Motocicleta").length);
        assertEquals(3, copia.obtenerVehiculos("VehiculoIndustrial").length);
    }

    @Test
    public void cargarRestauraAlquileresYDisponibilidad() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        Vehiculo v = turismoNormal(original);
        original.alquilar(cliente(original, "005"), v, 3);
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);

        Cliente ana = cliente(copia, "005");
        assertEquals(1, copia.obtenerAlquileres().length);
        assertEquals(1, copia.obtenerAlquileres(ana).length);
        assertEquals(1, copia.obtenerAlquileresPendientes(ana).length);
        Vehiculo vCopia = turismoNormal(copia);
        assertEquals(v.getMatricula(), vCopia.getMatricula());
        assertFalse(vCopia.isDisponible());
    }

    @Test
    public void elAlquilerCargadoUsaLosObjetosDelGestor() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        original.alquilar(cliente(original, "005"), turismoNormal(original), 2);
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);

        Alquiler a = copia.obtenerAlquileres()[0];
        assertSame(turismoNormal(copia), a.getVehiculo());
        assertSame(cliente(copia, "005"), a.getCliente());
    }

    @Test
    public void cargarConservaLasFechas() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        original.alquilar(cliente(original, "003"), turismoNormal(original), 5);
        Alquiler a1 = original.obtenerAlquileres()[0];
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);
        Alquiler a2 = copia.obtenerAlquileres()[0];

        assertEquals(a1.getFechaInicio(), a2.getFechaInicio());
        assertEquals(a1.getFechaFin(), a2.getFechaFin());
    }

    @Test
    public void cargarConservaLosAlquileresEntregados() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        Cliente ana = cliente(original, "005");
        Vehiculo v = turismoNormal(original);
        original.alquilar(ana, v, 1);
        original.entregar(original.obtenerAlquileresPendientes(ana)[0]);
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);

        Cliente anaCopia = cliente(copia, "005");
        assertEquals(1, copia.obtenerAlquileres(anaCopia).length);
        assertEquals(0, copia.obtenerAlquileresPendientes(anaCopia).length);
        assertTrue(turismoNormal(copia).isDisponible());
    }

    @Test
    public void elClienteFrecuenteSeMantieneTrasCargar() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        Cliente ana = cliente(original, "005");
        Vehiculo v = turismoNormal(original);
        for (int i = 0; i < 3; i++) {
            original.alquilar(ana, v, 1);
            original.entregar(original.obtenerAlquileresPendientes(ana)[0]);
        }
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        GestorAlquileres copia = new GestorAlquileres();
        copia.cargar(ruta);

        // 4º alquiler con tarifa de cliente frecuente: 50 * 0.80 = 40
        assertEquals(40, copia.alquilar(cliente(copia, "005"), turismoNormal(copia), 1));
    }

    @Test
    public void cargarSustituyeElEstadoActual() throws IOException {
        GestorAlquileres vacio = new GestorAlquileres();
        String ruta = ruta("vacio.json");
        vacio.guardar(ruta);

        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(cliente(g, "005"), turismoNormal(g), 2);
        assertEquals(1, g.obtenerAlquileres().length);

        g.cargar(ruta);
        assertEquals(0, g.obtenerAlquileres().length);
        assertTrue(turismoNormal(g).isDisponible());
    }

    // ---------- errores ----------

    @Test(expected = IOException.class)
    public void cargarFicheroInexistenteLanzaIOException() throws IOException {
        new GestorAlquileres().cargar(ruta("no_existe.json"));
    }

    @Test(expected = JSONException.class)
    public void cargarContenidoInvalidoLanzaJSONException() throws IOException {
        String ruta = ruta("roto.json");
        Files.write(Paths.get(ruta), "esto no es json".getBytes(StandardCharsets.UTF_8));
        new GestorAlquileres().cargar(ruta);
    }

    @Test(expected = JSONException.class)
    public void cargarJsonSinSeccionesLanzaExcepcion() throws IOException {
        String ruta = ruta("incompleto.json");
        Files.write(Paths.get(ruta), "{\"clientes\":[]}".getBytes(StandardCharsets.UTF_8));
        new GestorAlquileres().cargar(ruta);
    }

    @Test
    public void siLaCargaFallaElEstadoNoCambia() throws IOException {
        GestorAlquileres g = new GestorAlquileres();
        g.alquilar(cliente(g, "005"), turismoNormal(g), 2);

        String ruta = ruta("roto.json");
        Files.write(Paths.get(ruta), "{\"clientes\":[],\"vehiculos\":[],\"alquileres\":[{}]}"
                .getBytes(StandardCharsets.UTF_8));
        try {
            g.cargar(ruta);
        } catch (RuntimeException e) {
            // esperado
        }

        assertEquals(6, g.obtenerClientes().length);
        assertEquals(1, g.obtenerAlquileres().length);
        assertFalse(turismoNormal(g).isDisponible());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alquilerConClienteInexistenteEsRechazado() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        original.alquilar(cliente(original, "005"), turismoNormal(original), 1);
        String ruta = ruta("datos.json");
        original.guardar(ruta);

        // Se borra a todos los clientes del fichero, dejando el alquiler huérfano.
        String texto = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        JSONObject raiz = new JSONObject(texto);
        raiz.put("clientes", new org.json.JSONArray());
        Files.write(Paths.get(ruta), raiz.toString().getBytes(StandardCharsets.UTF_8));

        new GestorAlquileres().cargar(ruta);
    }
}