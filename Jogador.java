
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jogador {

    private final String nome;
    private final List<Carta> mao;
    private final Scanner scanner;
    private int pontuacao;

    public Jogador(String nome, Scanner scanner) {
        this.nome = nome;
        this.scanner = scanner;
        this.mao = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void adicionarPonto() {
        pontuacao++;
    }

    public void adicionarPonto(int pontos) {
        pontuacao += pontos;
    }

    public void receberCarta(Carta carta) {
        if (mao.size() < 3) {
            mao.add(carta);
        }
    }

    public void mostrarMao() {
        System.out.println("Cartas de " + nome + ":");
        for (int i = 0; i < mao.size(); i++) {
            System.out.println((i + 1) + " - " + mao.get(i));
        }
    }

    public Carta jogarCarta() {
        mostrarMao();
        int escolha = -1;

        while (escolha < 1 || escolha > mao.size()) {
            System.out.print("Escolha uma carta para jogar (1, 2 ou 3): ");
            try {
                escolha = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Tente novamente.");
            }
        }

        return mao.remove(escolha - 1);
    }

    public boolean temCartasNaMao() {
        return !mao.isEmpty();
    }

    public List<Carta> getMao() {
        return mao;
    }
}
