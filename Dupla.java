
public class Dupla {

    private final Jogador jogador1;
    private final Jogador jogador2;
    private int pontuacao;

    public Dupla(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.pontuacao = 0;
    }

    public void adicionarPonto(int pontos) {
        pontuacao += pontos;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public String getNome() {
        return jogador1.getNome() + " e " + jogador2.getNome();
    }

    public boolean contem(Jogador jogador) {
        return jogador.equals(jogador1) || jogador.equals(jogador2);
    }
}
