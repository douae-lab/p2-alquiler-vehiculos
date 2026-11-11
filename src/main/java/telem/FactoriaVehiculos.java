package telem;

import org.json.JSONObject;

/**
 * Método Factoría: crea el vehículo de la subclase adecuada
 * a partir de su representación JSON (campo "tipo").
 */
public class FactoriaVehiculos {

    public static Vehiculo getVehiculo(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String clase = json.getString("tipo");
        switch (clase) {
            case "Turismo":
                return new Turismo(jsonString);
            case "Motocicleta":
                return new Motocicleta(jsonString);
            case "VehiculoIndustrial":
                return new VehiculoIndustrial(jsonString);
            default:
                throw new IllegalArgumentException(
                        "Tipo de vehículo desconocido: " + clase);
        }
    }

    public static void main(String[] args) {
        Vehiculo v1 = new Turismo("1234ABC", Categoria.NORMAL, 2020, 5, 450.0);
        String serializado = v1.toString();
        System.out.println(serializado);
        Vehiculo v2 = FactoriaVehiculos.getVehiculo(serializado);
        System.out.println(v2.getTipo() + " " + v2.getMatricula());
    }
}
