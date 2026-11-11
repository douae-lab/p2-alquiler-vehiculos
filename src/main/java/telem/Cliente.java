package telem;

import org.json.JSONObject;

/**
 * Modela un cliente con nombre y DNI.
 * Cliente
 */
public class Cliente {

    String nombre;
    String dni;

    public Cliente(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    /** Deserialización: construye el cliente a partir de su JSON. */
    public Cliente(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        this.nombre = json.getString("nombre");
        this.dni = json.getString("dni");
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente("Juan Pérez", "12345678A");
        System.out.println(cliente.toString());
    }

}