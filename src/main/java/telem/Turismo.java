package telem;

import org.json.JSONObject;

public class Turismo extends Vehiculo {

    int plazas;
    double capacidadMaletero;

    public Turismo(String matricula, Categoria categoria, int annio,
        int plazas, double capacidadMaletero
    ) {
        super(matricula, categoria, annio);
        this.plazas = plazas;
        this.capacidadMaletero = capacidadMaletero;
    }

    public Turismo(String jsonString) {
        super(jsonString);
        JSONObject json = new JSONObject(jsonString);
        this.plazas = json.getInt("plazas");
        this.capacidadMaletero = json.getDouble("capacidadMaletero");
    }

    public int getPlazas() {
        return plazas;
    }

    public double getCapacidadMaletero() {
        return capacidadMaletero;
    }

    @Override
    public String getTipo() {
        return "Turismo";
    }

    @Override
    public int getPrecioBase() {
        int precioBase = 0;
        switch (getCategoria()) {
            case NORMAL:
                precioBase = 50;
                break;
            case ESPECIAL:
                precioBase = 80;
                break;
            case SUPERIOR:
                precioBase = 100;
                break;
        }
        return precioBase;
    }
}
