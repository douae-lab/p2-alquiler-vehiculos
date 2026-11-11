package telem;

import org.json.JSONObject;

/**
 * Modela un vehículo con matrícula, categoría, año y disponibilidad.
 * La matrícula debe ser única para cada vehículo.
 * Vehiculo
 */
public abstract class Vehiculo {

    private String matricula;
    private Categoria categoria;
    private int annio;
    private boolean disponible;

    public Vehiculo(String matricula, Categoria categoria, int annio) {
        this.matricula = matricula;
        this.categoria = categoria;
        this.annio = annio;
    }

    /** Deserialización: construye la parte común a partir del JSON. */
    public Vehiculo(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        this.matricula = json.getString("matricula");
        this.categoria = Categoria.valueOf(json.getString("categoria"));
        this.annio = json.getInt("annio");
        this.disponible = json.getBoolean("disponible");
    }

    public String getMatricula() {
        return matricula;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getAnnio() {
        return annio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

    /**
     * En serialización/deserialización: para obtener el tipo de
     * vehículo (clase) del objeto.
     * @return El tipo de vehículo (sub-clase) del objeto.
     */
    public abstract String getTipo();

    /**
     * Calcula el precio base diario del vehículo según su categoría y tipo.
     * @return precio base diario del vehículo.
     */
    public abstract int getPrecioBase();
}