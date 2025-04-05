package jogador;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import jogo.Carta;

public class JogadorBot extends Jogador {

    public JogadorBot(String nome, Scanner scanner) {
        super(nome, scanner);
    }

    @Override
    public Carta jogarCarta() {
        List<Carta> mao = getMao();
        Carta cartaEscolhida = mao.get(new Random().nextInt(mao.size()));
        getMao().remove(cartaEscolhida);
        System.out.println(getNome() + " jogou: " + cartaEscolhida);
        return cartaEscolhida;
    }
}
