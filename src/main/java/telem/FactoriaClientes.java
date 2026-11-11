package telem;

/**
 * Método Factoría para clientes: crea un Cliente a partir de su JSON.
 */
public class FactoriaClientes {

    public static Cliente getCliente(String jsonString) {
        return new Cliente(jsonString);
    }

    public static void main(String[] args) {
        Cliente c1 = new Cliente("Juan Pérez", "12345678A");
        Cliente c2 = FactoriaClientes.getCliente(c1.toString());
        System.out.println(c2.getNombre() + " - " + c2.getDni());
    }
}
