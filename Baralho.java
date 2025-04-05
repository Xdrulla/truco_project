
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {

    private final List<Carta> cartas = new ArrayList<>();

    public Baralho() {
        for (Valor valor : Valor.values()) {
            for (Naipe naipe : Naipe.values()) {
                cartas.add(new Carta(valor, naipe));
            }
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public Carta distribuirCarta() {
        if (!cartas.isEmpty()) {
            return cartas.remove(0);
        }
        return null;
    }

    public boolean temCartas() {
        return !cartas.isEmpty();
    }
}
