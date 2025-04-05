
import java.io.*;
import java.net.*;
import java.util.*;

public class JogadorRemoto extends Jogador {

    private final BufferedReader in;
    private final PrintWriter out;

    public JogadorRemoto(String nome, Socket socket, Scanner scanner) throws IOException {
        super(nome, scanner);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void mostrarMao() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cartas de ").append(getNome()).append(":\n");
        List<Carta> mao = getMao();
        for (int i = 0; i < mao.size(); i++) {
            builder.append((i + 1)).append(" - ").append(mao.get(i)).append("\n");
        }
        out.println("MAO:\n" + builder.toString());
    }

    @Override
    public Carta jogarCarta() {
        out.println("SELECIONE_CARTA");
        try {
            while (true) {
                String resposta = in.readLine();
                try {
                    int escolha = Integer.parseInt(resposta);
                    return getMao().remove(escolha - 1);
                } catch (Exception e) {
                    out.println("ERRO: Entrada inválida. Envie o número da carta.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler jogada remota.");
        }
    }

    public String receberComando() throws IOException {
        return in.readLine();
    }

    public void enviarComando(String comando) {
        out.println(comando);
    }
}
