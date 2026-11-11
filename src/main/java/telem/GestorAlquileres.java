package telem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;

public class GestorAlquileres {

    /** Nº mínimo de alquileres previos para considerar a un cliente frecuente. */
    private static final int UMBRAL_CLIENTE_FRECUENTE = 3;

    /** Fichero que usa el menú para guardar y cargar los datos. */
    public static final String FICHERO_POR_DEFECTO = "alquileres.json";

    private int contadorAlquileres = 0;
    private Alquiler[] alquileres = new Alquiler[1000];
    // entregados[i] es true si el alquiler i ya ha sido devuelto
    private boolean[] entregados = new boolean[1000];

    // Para simplificar, vamos a definir un array de clientes y
    // otro de vehículos dentro del gestor.
    // En una aplicación real estarían en una base de datos.
    private Cliente[] clientes = new Cliente[10];
    private Vehiculo[] vehiculos = new Vehiculo[100];


    public GestorAlquileres() {
        inicializarClientes();
        inicializarVehiculos();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // INICIALIZACIÓN DE CLIENTES Y VEHÍCULOS DE EJEMPLO

    private void inicializarClientes() {
        clientes[0] = new Cliente("Clara Videncia", "001");
        clientes[1] = new Cliente("Aitor Menta", "002");
        clientes[2] = new Cliente("Lola Mento", "003");
        clientes[3] = new Cliente("Armando Guerra", "004");
        clientes[4] = new Cliente("Ana Lítica", "005");
        clientes[5] = new Cliente("Carmelo Cotón", "006");
    }

    private void inicializarVehiculos() {
        vehiculos[0] = new Turismo("TNA2020", Categoria.NORMAL, 2020, 5, 450.0);
        vehiculos[1] = new Motocicleta("MNA2021", Categoria.NORMAL, 2021, 200);
        vehiculos[2] = new VehiculoIndustrial("CNA2019", Categoria.NORMAL, 2019, 1400, 2);
        vehiculos[3] = new Turismo("TSA2024", Categoria.SUPERIOR, 2024, 5, 550.0);
        vehiculos[4] = new Motocicleta("MSA2021", Categoria.SUPERIOR, 2021, 500);
        vehiculos[5] = new VehiculoIndustrial("CSA2025", Categoria.SUPERIOR, 2025, 2400, 3);
        vehiculos[6] = new Turismo("TEA2026", Categoria.ESPECIAL, 2026, 7, 650.0);
        vehiculos[7] = new Motocicleta("MEA2026", Categoria.ESPECIAL, 2026, 1000);
        vehiculos[8] = new VehiculoIndustrial("CEA2025", Categoria.ESPECIAL, 2025, 4400, 5);

        // Al empezar, todos los vehículos están disponibles.
        for (Vehiculo v : vehiculos) {
            if (v != null) {
                v.setDisponible(true);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS PARA GESTIONAR CLIENTES Y VEHÍCULOS

    /** Devuelve los clientes registrados (sin posiciones vacías). */
    public Cliente[] obtenerClientes() {
        int contador = 0;
        for (Cliente c : clientes) {
            if (c != null) {
                contador++;
            }
        }
        Cliente[] resultado = new Cliente[contador];
        int j = 0;
        for (Cliente c : clientes) {
            if (c != null) {
                resultado[j++] = c;
            }
        }
        return resultado;
    }

    public Vehiculo[] obtenerVehiculos(String tipo) {

        Vehiculo[] vehiculosFiltrados = new Vehiculo[vehiculos.length];
        int contador = 0;

        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo != null) {
                if (vehiculo.getTipo().equals(tipo)) {
                    vehiculosFiltrados[contador++] = vehiculo;
                }
            }
        }

        // Ajustar el tamaño del array para que solo contenga los vehículos encontrados
        Vehiculo[] resultado = new Vehiculo[contador];
        System.arraycopy(vehiculosFiltrados, 0, resultado, 0, contador);
        return resultado;
    }

    /**
     * Devuelve un array con los vehículos del tipo y categoría especificados.
     * @param tipo
     * @param categoria
     * @return lista de vehículos del tipo y categoría especificados
     */
    public Vehiculo[] obtenerVehiculos(String tipo, Categoria categoria) {
        Vehiculo[] delTipo = obtenerVehiculos(tipo);
        Vehiculo[] filtrados = new Vehiculo[delTipo.length];
        int contador = 0;

        for (Vehiculo v : delTipo) {
            if (v.getCategoria() == categoria) {
                filtrados[contador++] = v;
            }
        }

        Vehiculo[] resultado = new Vehiculo[contador];
        System.arraycopy(filtrados, 0, resultado, 0, contador);
        return resultado;
    }

    private Vehiculo[] soloDisponibles(Vehiculo[] lista) {
        Vehiculo[] tmp = new Vehiculo[lista.length];
        int contador = 0;
        for (Vehiculo v : lista) {
            if (v.isDisponible()) {
                tmp[contador++] = v;
            }
        }
        Vehiculo[] resultado = new Vehiculo[contador];
        System.arraycopy(tmp, 0, resultado, 0, contador);
        return resultado;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS PARA GESTIONAR ALQUILERES

    /**
     * Agrega un nuevo alquiler al array de alquileres.
     * @param alquiler
     */
    public void agregarAlquiler(Alquiler alquiler) {
        if (contadorAlquileres < alquileres.length) {
            alquileres[contadorAlquileres] = alquiler;
            contadorAlquileres++;
        } else {
            System.out.println("No se pueden agregar más alquileres. Capacidad máxima alcanzada.");
        }
    }

    /**
     * Devuelve un array con todos los alquileres registrados en el gestor
     * (sin posiciones vacías).
     */
    public Alquiler[] obtenerAlquileres() {
        Alquiler[] resultado = new Alquiler[contadorAlquileres];
        System.arraycopy(alquileres, 0, resultado, 0, contadorAlquileres);
        return resultado;
    }

    private static boolean mismoCliente(Cliente a, Cliente b) {
        return a != null && b != null && a.getDni().equals(b.getDni());
    }

    /**
     * Devuelve el histórico de alquileres de un cliente.
     */
    public Alquiler[] obtenerAlquileres(Cliente cliente) {
        Alquiler[] tmp = new Alquiler[contadorAlquileres];
        int contador = 0;
        for (int i = 0; i < contadorAlquileres; i++) {
            if (mismoCliente(alquileres[i].getCliente(), cliente)) {
                tmp[contador++] = alquileres[i];
            }
        }
        Alquiler[] resultado = new Alquiler[contador];
        System.arraycopy(tmp, 0, resultado, 0, contador);
        return resultado;
    }

    /**
     * Devuelve los alquileres del cliente cuyo vehículo aún no ha sido entregado.
     */
    public Alquiler[] obtenerAlquileresPendientes(Cliente cliente) {
        Alquiler[] tmp = new Alquiler[contadorAlquileres];
        int contador = 0;
        for (int i = 0; i < contadorAlquileres; i++) {
            if (!entregados[i] && mismoCliente(alquileres[i].getCliente(), cliente)) {
                tmp[contador++] = alquileres[i];
            }
        }
        Alquiler[] resultado = new Alquiler[contador];
        System.arraycopy(tmp, 0, resultado, 0, contador);
        return resultado;
    }

    /**
     * Informa si un cliente es cliente frecuente en función del número de alquileres previos.
     * @param cliente cliente a evaluar
     * @param umbral número mínimo de alquileres previos que se consideran para determinar si es cliente frecuente
     * @return true si el cliente tiene al menos "umbral" alquileres previos
     */
    private boolean esClienteFrecuente(Cliente cliente, int umbral) {
        return obtenerAlquileres(cliente).length >= umbral;
    }

    /**
     * Registra un alquiler que empieza hoy y dura "dias" días.
     * Aplica la política ClienteFrecuente si corresponde, y Normal en caso contrario.
     * @return el precio total del alquiler
     */
    public int alquilar(Cliente cliente, Vehiculo vehiculo, int dias) {
        if (cliente == null || vehiculo == null) {
            throw new IllegalArgumentException("Cliente y vehículo son obligatorios");
        }
        if (dias < 1) {
            throw new IllegalArgumentException("El alquiler debe durar al menos un día");
        }
        if (!vehiculo.isDisponible()) {
            throw new IllegalStateException("El vehículo no está disponible");
        }
        if (contadorAlquileres >= alquileres.length) {
            throw new IllegalStateException("Capacidad máxima de alquileres alcanzada");
        }

        // Se decide la política ANTES de registrar el alquiler (solo cuentan los previos).
        PoliticasDePrecios politica = esClienteFrecuente(cliente, UMBRAL_CLIENTE_FRECUENTE)
                ? PoliticasDePrecios.ClienteFrecuente
                : PoliticasDePrecios.Normal;

        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusDays(dias - 1); // el día de inicio cuenta como día 1
        Alquiler alquiler = new Alquiler(vehiculo, cliente, inicio, fin);
        agregarAlquiler(alquiler);
        vehiculo.setDisponible(false);

        return alquiler.calcularPrecioAlquiler(inicio, fin, politica, vehiculo);
    }

    /**
     * Entrega (devuelve) el vehículo de un alquiler y lo deja disponible.
     * @return true si se ha entregado, false si el alquiler no existe o ya estaba entregado
     */
    public boolean entregar(Alquiler alquiler) {
        for (int i = 0; i < contadorAlquileres; i++) {
            if (alquileres[i] == alquiler) {
                if (entregados[i]) {
                    return false;
                }
                entregados[i] = true;
                alquiler.getVehiculo().setDisponible(true);
                return true;
            }
        }
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // PERSISTENCIA EN FICHERO DE TEXTO (JSON)

    /**
     * Guarda clientes, vehículos y alquileres en un fichero de texto en formato JSON.
     * @param ruta ruta del fichero (se crea o se sobrescribe)
     * @throws IOException si no se puede escribir el fichero
     */
    public void guardar(String ruta) throws IOException {
        JSONArray jsonClientes = new JSONArray();
        for (Cliente c : clientes) {
            if (c != null) {
                jsonClientes.put(new JSONObject(c.toString()));
            }
        }

        JSONArray jsonVehiculos = new JSONArray();
        for (Vehiculo v : vehiculos) {
            if (v != null) {
                jsonVehiculos.put(new JSONObject(v.toString()));
            }
        }

        JSONArray jsonAlquileres = new JSONArray();
        for (int i = 0; i < contadorAlquileres; i++) {
            JSONObject entrada = new JSONObject();
            entrada.put("alquiler", new JSONObject(alquileres[i].toString()));
            entrada.put("entregado", entregados[i]);
            jsonAlquileres.put(entrada);
        }

        JSONObject raiz = new JSONObject();
        raiz.put("clientes", jsonClientes);
        raiz.put("vehiculos", jsonVehiculos);
        raiz.put("alquileres", jsonAlquileres);

        Files.write(Paths.get(ruta), raiz.toString(2).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Recupera los datos guardados con {@link #guardar(String)} y sustituye el estado actual.
     * Si el fichero no existe o no es válido, el estado actual NO se modifica.
     * @param ruta ruta del fichero
     * @throws IOException si no se puede leer el fichero
     * @throws org.json.JSONException si el contenido no es un JSON válido
     * @throws IllegalArgumentException si los datos son incoherentes
     */
    public void cargar(String ruta) throws IOException {
        String texto = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        JSONObject raiz = new JSONObject(texto);

        JSONArray jsonClientes = raiz.getJSONArray("clientes");
        JSONArray jsonVehiculos = raiz.getJSONArray("vehiculos");
        JSONArray jsonAlquileres = raiz.getJSONArray("alquileres");

        if (jsonClientes.length() > clientes.length
                || jsonVehiculos.length() > vehiculos.length
                || jsonAlquileres.length() > alquileres.length) {
            throw new IllegalArgumentException("El fichero tiene más datos de los que caben en el gestor");
        }

        // Todo se construye en estructuras temporales: si algo falla, no se toca el estado.
        Cliente[] nuevosClientes = new Cliente[clientes.length];
        for (int i = 0; i < jsonClientes.length(); i++) {
            nuevosClientes[i] = FactoriaClientes.getCliente(jsonClientes.getJSONObject(i).toString());
        }

        Vehiculo[] nuevosVehiculos = new Vehiculo[vehiculos.length];
        for (int i = 0; i < jsonVehiculos.length(); i++) {
            nuevosVehiculos[i] = FactoriaVehiculos.getVehiculo(jsonVehiculos.getJSONObject(i).toString());
        }

        Alquiler[] nuevosAlquileres = new Alquiler[alquileres.length];
        boolean[] nuevosEntregados = new boolean[alquileres.length];
        for (int i = 0; i < jsonAlquileres.length(); i++) {
            JSONObject entrada = jsonAlquileres.getJSONObject(i);
            Alquiler leido = FactoriaAlquileres.getAlquiler(entrada.getJSONObject("alquiler").toString());

            // El alquiler debe referirse a los objetos del gestor, no a copias.
            Vehiculo v = buscarVehiculo(nuevosVehiculos, leido.getVehiculo().getMatricula());
            Cliente c = buscarCliente(nuevosClientes, leido.getCliente().getDni());
            if (v == null || c == null) {
                throw new IllegalArgumentException(
                        "Alquiler con cliente o vehículo que no existen en el fichero");
            }
            nuevosAlquileres[i] = new Alquiler(v, c, leido.getFechaInicio(), leido.getFechaFin());
            nuevosEntregados[i] = entrada.getBoolean("entregado");
        }

        // Todo correcto: se sustituye el estado.
        this.clientes = nuevosClientes;
        this.vehiculos = nuevosVehiculos;
        this.alquileres = nuevosAlquileres;
        this.entregados = nuevosEntregados;
        this.contadorAlquileres = jsonAlquileres.length();
    }

    private static Vehiculo buscarVehiculo(Vehiculo[] lista, String matricula) {
        for (Vehiculo v : lista) {
            if (v != null && v.getMatricula().equals(matricula)) {
                return v;
            }
        }
        return null;
    }

    private static Cliente buscarCliente(Cliente[] lista, String dni) {
        for (Cliente c : lista) {
            if (c != null && c.getDni().equals(dni)) {
                return c;
            }
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // INTERFAZ DE USUARIO (CONSOLA) Y LÓGICA DE NEGOCIO

    public void menuPrincipal() {

        while (true) {

            System.out.println("Gestión de Alquileres");
            System.out.println("(G) Guardar datos en " + FICHERO_POR_DEFECTO);
            System.out.println("(C) Cargar datos desde " + FICHERO_POR_DEFECTO);
            System.out.println("¿Desea salir de la app (S/N)?");

            char s = KeyBoard.readChar();
            if (s == 's' || s == 'S') {
                System.out.println("Bye ...");
                return;
            }
            if (s == 'g' || s == 'G') {
                try {
                    guardar(FICHERO_POR_DEFECTO);
                    System.out.println("Datos guardados en " + FICHERO_POR_DEFECTO);
                } catch (IOException e) {
                    System.out.println("No se pudo guardar: " + e.getMessage());
                }
                continue;
            }
            if (s == 'c' || s == 'C') {
                try {
                    cargar(FICHERO_POR_DEFECTO);
                    System.out.println("Datos cargados desde " + FICHERO_POR_DEFECTO);
                } catch (IOException | RuntimeException e) {
                    System.out.println("No se pudo cargar: " + e.getMessage());
                }
                continue;
            }

            Cliente cliente = identificarCliente();

            if (cliente != null) {
                System.out.println("Sr " + cliente.getNombre() + " Introduzca una opción");
                System.out.println("(1) seleccionar coche para alquilar");
                System.out.println("(2) entregar un coche alquilado");
                System.out.println("(3) listar su histórico de alquileres");

                int opcion = KeyBoard.readInt();
                System.out.println("opción escogida: " + opcion);

                switch (opcion) {
                    case 1:
                        Vehiculo v = seleccionarVehiculo(cliente);
                        if (v != null && v.isDisponible()) {
                            System.out.println("¿cuántos días desea alquilar el vehículo?");
                            int dias = KeyBoard.readInt();
                            try {
                                boolean frecuente = esClienteFrecuente(cliente, UMBRAL_CLIENTE_FRECUENTE);
                                int precio = alquilar(cliente, v, dias);
                                if (frecuente) {
                                    System.out.println("Alquiler realizado. Tarifa Usuario frecuente.");
                                } else {
                                    System.out.println("Alquiler realizado. Tarifa Normal.");
                                }
                                System.out.println("Precio total: " + precio + " euros");
                            } catch (IllegalArgumentException | IllegalStateException e) {
                                System.out.println("No se pudo realizar el alquiler: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Debe seleccionar un vehículo disponible");
                        }
                        break;
                    case 2:
                        Alquiler[] pendientes = obtenerAlquileresPendientes(cliente);
                        if (pendientes.length == 0) {
                            System.out.println("No tiene vehículos pendientes de entrega.");
                        } else {
                            System.out.println("Vehículos pendientes de entrega:");
                            for (int i = 0; i < pendientes.length; i++) {
                                System.out.println(i + " ---> " + pendientes[i]);
                            }
                            System.out.println("Introduzca el número del alquiler que desea entregar:");
                            int sel = KeyBoard.readInt();
                            if (sel >= 0 && sel < pendientes.length) {
                                entregar(pendientes[sel]);
                                System.out.println("Vehículo entregado. Gracias.");
                            } else {
                                System.out.println("Selección inválida.");
                            }
                        }
                        break;
                    case 3:
                        Alquiler[] historico = obtenerAlquileres(cliente);
                        if (historico.length == 0) {
                            System.out.println("No tiene alquileres registrados.");
                        }
                        for (Alquiler q : historico) {
                            System.out.println(q);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("Debe identificarse correctamente o salir de la app");
            }
        }
    }

    public Cliente identificarCliente() {

        System.out.println("Bienvenido al Gestor de Alquileres");
        for (int i = 0; i < clientes.length; i++) {
            if (clientes[i] != null) {
                System.out.println(i + " ---> " + clientes[i].getNombre() + " - " + clientes[i].getDni());
            }
        }
        System.out.println("Identifíquese como cliente con su número de cliente (-1 para salir):");
        int numeroCliente = KeyBoard.readInt();
        if (numeroCliente == -1) {
            return null;
        } else if (numeroCliente < 0 || numeroCliente >= clientes.length
                || clientes[numeroCliente] == null) {
            System.out.println("identificarCliente: Número de cliente inválido.");
            return null;
        }
        return clientes[numeroCliente];
    }

    public Vehiculo seleccionarVehiculo(Cliente cliente) {

        while (true) {

            System.out.println("Estimado Sr./Sra. " + cliente.getNombre());
            System.out.println("Introduzca el número correspondiente al tipo de vehículo que desea alquilar:");
            System.out.println("1. Turismo, 2. Motocicleta, 3. Vehículo Industrial: ");

            int tipoVehiculo = KeyBoard.readInt();
            String tipo;

            switch (tipoVehiculo) {
                case 1:
                    System.out.println("Ha seleccionado Turismo.");
                    tipo = "Turismo";
                    break;
                case 2:
                    System.out.println("Ha seleccionado Motocicleta.");
                    tipo = "Motocicleta";
                    break;
                case 3:
                    System.out.println("Ha seleccionado Vehículo Industrial.");
                    tipo = "VehiculoIndustrial"; // debe coincidir con getTipo()
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, seleccione un tipo de vehículo válido.");
                    continue;
            }

            System.out.println("Introduzca el número correspondiente a la categoría que desea alquilar:");
            System.out.println("1. Normal, 2. Superior, 3. Especial, 4. Cualquiera: ");

            int categoriaVehiculo = KeyBoard.readInt();
            Vehiculo[] elegibles;

            switch (categoriaVehiculo) {
                case 1:
                    elegibles = obtenerVehiculos(tipo, Categoria.NORMAL);
                    break;
                case 2:
                    elegibles = obtenerVehiculos(tipo, Categoria.SUPERIOR);
                    break;
                case 3:
                    elegibles = obtenerVehiculos(tipo, Categoria.ESPECIAL);
                    break;
                case 4:
                    elegibles = obtenerVehiculos(tipo);
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, seleccione una categoría válida.");
                    continue;
            }

            elegibles = soloDisponibles(elegibles);
            if (elegibles.length == 0) {
                System.out.println("No hay vehículos disponibles con esos criterios.");
                return null;
            }

            System.out.println("Coches elegibles");
            for (int i = 0; i < elegibles.length; i++) {
                System.out.println(i + " ---> " + elegibles[i]);
            }

            System.out.println("Introduzca número coche");
            int seleccion = KeyBoard.readInt();
            if (seleccion < 0 || seleccion >= elegibles.length) {
                System.out.println("Selección inválida.");
                return null;
            }
            return elegibles[seleccion];
        }
    }

    public void listarAlquileres() {
        System.out.println("Listado de alquileres");
        for (Alquiler alq : obtenerAlquileres()) {
            System.out.println(alq);
        }
        System.out.println();
    }

    public void listarVehiculos() {
        System.out.println("Listado de vehículos");
        for (Vehiculo veh : vehiculos) {
            if (veh != null) {
                System.out.println(veh);
            }
        }
        System.out.println();
    }


    public static void main(String[] args) {
        GestorAlquileres app = new GestorAlquileres();
        app.menuPrincipal();
    }
}