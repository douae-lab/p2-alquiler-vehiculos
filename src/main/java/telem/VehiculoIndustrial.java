package telem;

import org.json.JSONObject;

public class VehiculoIndustrial extends Vehiculo {

    private int capacidadCarga;
    private int numeroEjes;

    public VehiculoIndustrial(String matricula, Categoria categoria, int annio,
        int capacidadCarga, int numeroEjes
    ) {
        super(matricula, categoria, annio);
        this.capacidadCarga = capacidadCarga;
        this.numeroEjes = numeroEjes;
    }

    public VehiculoIndustrial(String jsonString) {
        super(jsonString);
        JSONObject json = new JSONObject(jsonString);
        this.capacidadCarga = json.getInt("capacidadCarga");
        this.numeroEjes = json.getInt("numeroEjes");
    }

    public int getCapacidadCarga() {
        return capacidadCarga;
    }

    public int getNumeroEjes() {
        return numeroEjes;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

    @Override
    public String getTipo() {
        return "VehiculoIndustrial";
    }

    @Override
    public int getPrecioBase() {
        int precioBase = 0;
        switch (getCategoria()) {
            case NORMAL:
                precioBase = 90;
                break;
            case ESPECIAL:
                precioBase = 120;
                break;
            case SUPERIOR:
                precioBase = 150;
                break;
        }
        return precioBase;
    }
}
