package telem;

public class Motocicleta extends Vehiculo {

    int cilindrada;

    public Motocicleta(String matricula, Categoria categoria, int annio,
        int cilindrada
    ) {
        super(matricula, categoria, annio);
        this.cilindrada = cilindrada;
    }

    public int getCilindrada() {
        return cilindrada;
    }
    @Override
    public String getTipo() {
        return "Motocicleta";
    }

    @Override
    public int getPrecioBase() {
        int precioBase = 0;
        switch (getCategoria()) {
            case NORMAL:
                precioBase = 30;
                break;
            case ESPECIAL:
                precioBase = 50;
                break;
            case SUPERIOR:
                precioBase = 70;
                break;
        }
        return precioBase;
    }
}
