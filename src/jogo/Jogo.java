package jogo;

import jogador.Jogador;
import jogador.JogadorBot;
import jogador.JogadorRemoto;
import java.util.*;
import java.io.*;

public class Jogo {

    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogador3;
    private Jogador jogador4;
    private Jogador ultimoQuePediuTruco = null;

    private Dupla dupla1;
    private Dupla dupla2;
    private boolean aumentoPendentes = false;

    private int pontosJogador1 = 0;
    private int pontosJogador2 = 0;

    private int modoJogo;

    private final Baralho baralho = new Baralho();
    private String manilhaValor;

    private final List<String> ordemValores = Arrays.asList("4", "5", "6", "7", "Q", "J", "K", "A", "2", "3");
    private final List<String> ordemNaipes = Arrays.asList("Ouros", "Espadas", "Copas", "Paus");

    public Jogo(int modoJogo) {
        this.modoJogo = modoJogo;
        Scanner sc = new Scanner(System.in);

        jogador1 = new Jogador("Jogador 1", sc);

        switch (modoJogo) {
            case 1 ->
                jogador2 = new JogadorBot("Bot", sc);
            case 2 -> {
                jogador2 = new JogadorBot("Bot 2", sc);
                jogador3 = new JogadorBot("Bot 3", sc);
                jogador4 = new JogadorBot("Bot 4", sc);
            }
            case 3 ->
                jogador2 = new Jogador("Jogador 2", sc);
            case 4 -> {
                jogador2 = new Jogador("Jogador 2", sc);
                jogador3 = new Jogador("Jogador 3", sc);
                jogador4 = new Jogador("Jogador 4", sc);
            }
        }

        if (modoJogo == 2 || modoJogo == 4) {
            dupla1 = new Dupla(jogador1, jogador3);
            dupla2 = new Dupla(jogador2, jogador4);
        }
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        Jogador primeiroJogador = jogador1;

        while ((modoJogo <= 3 && pontosJogador1 < 12 && pontosJogador2 < 12)
                || (modoJogo >= 4 && dupla1.getPontuacao() < 12 && dupla2.getPontuacao() < 12)) {

            System.out.println("\n--- Nova Mão ---");
            baralho.embaralhar();

            Carta cartaVirada = baralho.distribuirCarta();
            definirManilha(cartaVirada);
            System.out.println("Carta virada: " + cartaVirada);
            System.out.println("Manilha: " + manilhaValor);

            distribuirCartas();
            jogador1.mostrarMao();

            int vitoriasDupla1 = 0;
            int vitoriasDupla2 = 0;
            int valorDaMao = 1;

            for (int i = 0; i < 3; i++) {
                System.out.println("\nRodada " + (i + 1));
                List<Carta> cartasRodada = new ArrayList<>();
                Map<Carta, Jogador> mapa = new HashMap<>();

                List<Jogador> todos = modoJogo <= 3
                        ? Arrays.asList(jogador1, jogador2)
                        : Arrays.asList(jogador1, jogador2, jogador3, jogador4);

                List<Jogador> ordemJogadores = new ArrayList<>();
                int startIndex = todos.indexOf(primeiroJogador);
                for (int j = 0; j < todos.size(); j++) {
                    ordemJogadores.add(todos.get((startIndex + j) % todos.size()));
                }

                for (Jogador jogador : ordemJogadores) {
                    jogador.mostrarMao();

                    boolean podePedirTruco = (ultimoQuePediuTruco == null || (aumentoPendentes && jogador != ultimoQuePediuTruco));

                    if (podePedirTruco) {
                        if (jogador instanceof JogadorRemoto remoto) {
                            try {
                                remoto.enviarComando("TRUCO_PEDIR?");
                                String resposta = remoto.receberComando();
                                if (resposta.equals("s")) {
                                    int novoValor = pedirTruco(scanner, valorDaMao, jogador);
                                    if (novoValor == -1) {
                                        return;
                                    }
                                    valorDaMao = novoValor;
                                    ultimoQuePediuTruco = jogador;
                                    aumentoPendentes = false;
                                }
                            } catch (IOException e) {
                                System.out.println("Erro de comunicação com jogador remoto: " + e.getMessage());
                                return;
                            }
                        } else if (!(jogador instanceof JogadorBot)) {
                            System.out.print(jogador.getNome() + ", deseja pedir truco nesta rodada? (s/n): ");
                            String resposta = scanner.nextLine().trim().toLowerCase();
                            if (resposta.equals("s")) {
                                int novoValor = pedirTruco(scanner, valorDaMao, jogador);
                                if (novoValor == -1) {
                                    return;
                                }
                                valorDaMao = novoValor;
                                ultimoQuePediuTruco = jogador;
                                aumentoPendentes = false;
                            }
                        }
                    }

                    Carta carta = jogador.jogarCarta();
                    System.out.println(jogador.getNome() + " jogou: " + carta);
                    cartasRodada.add(carta);
                    mapa.put(carta, jogador);

                    for (Jogador outro : ordemJogadores) {
                        if (outro instanceof JogadorRemoto remotoOutro && outro != jogador) {
                            remotoOutro.enviarComando("INFO: " + jogador.getNome() + " jogou: " + carta);
                        }
                    }

                    boolean jogadorGanhouRodadaAnterior
                            = (dupla1 != null && dupla1.contem(jogador) && vitoriasDupla1 == 1)
                            || (dupla2 != null && dupla2.contem(jogador) && vitoriasDupla2 == 1)
                            || (modoJogo <= 3 && ((jogador == jogador1 && vitoriasDupla1 == 1)
                            || (jogador == jogador2 && vitoriasDupla2 == 1)));

                    boolean ehGato = carta.getValor().toString().equals(manilhaValor)
                            && carta.getNaipe().toString().equals("Paus");

                    if (jogadorGanhouRodadaAnterior && ehGato) {
                        System.out.println("\n" + jogador.getNome() + " jogou o GATO (manilha de Paus) e já tinha vencido uma rodada!");
                        System.out.println("Mão encerrada automaticamente.");

                        for (Jogador outro : ordemJogadores) {
                            if (outro instanceof JogadorRemoto remotoOutro) {
                                remotoOutro.enviarComando("INFO: " + jogador.getNome() + " jogou o GATO e venceu a mão!");
                            }
                        }

                        if (modoJogo <= 3) {
                            adicionarPontosDupla(jogador == jogador1 ? 1 : 2, valorDaMao);
                        } else {
                            adicionarPontosDupla(dupla1.contem(jogador) ? 1 : 2, valorDaMao);
                        }

                        mostrarPontuacoes();
                        return;
                    }
                }

                Carta melhor = cartasRodada.get(0);
                for (int j = 1; j < cartasRodada.size(); j++) {
                    if (compararCartas(cartasRodada.get(j), melhor) > 0) {
                        melhor = cartasRodada.get(j);
                    }
                }

                Jogador vencedorRodada = mapa.get(melhor);
                System.out.println("Vencedor da rodada: " + vencedorRodada.getNome());
                primeiroJogador = vencedorRodada;

                if (modoJogo == 1 || modoJogo == 3) {
                    if (vencedorRodada == jogador1) {
                        vitoriasDupla1++;
                    } else {
                        vitoriasDupla2++;
                    }
                } else {
                    if (dupla1.contem(vencedorRodada)) {
                        vitoriasDupla1++;
                    } else {
                        vitoriasDupla2++;
                    }
                }

                if (vitoriasDupla1 == 2 || vitoriasDupla2 == 2) {
                    break;
                }
            }

            if (vitoriasDupla1 > vitoriasDupla2) {
                adicionarPontosDupla(1, valorDaMao);
                System.out.println((modoJogo == 1 ? "Jogador 1" : "Dupla 1") + " venceu a mão!");
            } else {
                adicionarPontosDupla(2, valorDaMao);
                System.out.println((modoJogo == 1 ? "Jogador 2" : "Dupla 2") + " venceu a mão!");
            }

            mostrarPontuacoes();
        }

        System.out.println("\n=== Fim de jogo ===");
        if (modoJogo == 1 || modoJogo == 3) {
            System.out.println((pontosJogador1 >= 12 ? "Jogador 1" : "Jogador 2") + " venceu o jogo!");
        } else {
            System.out.println((dupla1.getPontuacao() >= 12 ? "Dupla 1" : "Dupla 2") + " venceu o jogo!");
        }
    }

    private void adicionarPontosDupla(int dupla, int pontos) {
        if (modoJogo == 1 || modoJogo == 3) {
            if (dupla == 1) {
                pontosJogador1 += pontos;
            } else {
                pontosJogador2 += pontos;
            }
        } else {
            if (dupla == 1) {
                dupla1.adicionarPonto(pontos);
            } else {
                dupla2.adicionarPonto(pontos);
            }
        }
    }

    private void distribuirCartas() {
        for (int i = 0; i < 3; i++) {
            jogador1.receberCarta(baralho.distribuirCarta());
            jogador2.receberCarta(baralho.distribuirCarta());
            if (modoJogo == 2 || modoJogo == 4) {
                jogador3.receberCarta(baralho.distribuirCarta());
                jogador4.receberCarta(baralho.distribuirCarta());
            }
        }
    }

    private void definirManilha(Carta cartaVirada) {
        int index = ordemValores.indexOf(cartaVirada.getValor().toString());
        manilhaValor = ordemValores.get((index + 1) % ordemValores.size());
    }

    private int compararCartas(Carta c1, Carta c2) {
        boolean c1EhManilha = c1.getValor().toString().equals(manilhaValor);
        boolean c2EhManilha = c2.getValor().toString().equals(manilhaValor);

        if (c1EhManilha && !c2EhManilha) {
            return 1;
        }
        if (!c1EhManilha && c2EhManilha) {
            return -1;
        }

        if (c1EhManilha && c2EhManilha) {
            int n1 = ordemNaipes.indexOf(c1.getNaipe().toString());
            int n2 = ordemNaipes.indexOf(c2.getNaipe().toString());
            return Integer.compare(n2, n1);
        }

        int v1 = ordemValores.indexOf(c1.getValor().toString());
        int v2 = ordemValores.indexOf(c2.getValor().toString());
        return Integer.compare(v1, v2);
    }

    private void mostrarPontuacoes() {
        System.out.println("Pontuação atual:");
        if (modoJogo == 1 || modoJogo == 3) {
            System.out.println("Jogador 1: " + pontosJogador1);
            System.out.println("Jogador 2: " + pontosJogador2);
        } else {
            System.out.println("Dupla 1 (" + dupla1.getNome() + "): " + dupla1.getPontuacao());
            System.out.println("Dupla 2 (" + dupla2.getNome() + "): " + dupla2.getPontuacao());
        }
    }

    private int pedirTruco(Scanner scanner, int valorAtual, Jogador jogadorQuePediu) {
        int novoValor = switch (valorAtual) {
            case 1 ->
                3;
            case 3 ->
                6;
            case 6 ->
                9;
            case 9 ->
                12;
            default ->
                valorAtual;
        };

        for (Jogador j : Arrays.asList(jogador1, jogador2, jogador3, jogador4)) {
            if (j != null && j instanceof JogadorRemoto remoto) {
                remoto.enviarComando("INFO: " + jogadorQuePediu.getNome() + " pediu TRUCO! Mão vale " + novoValor + " pontos.");
            }
        }
        System.out.println("INFO: " + jogadorQuePediu.getNome() + " pediu TRUCO! Mão vale " + novoValor + " pontos.");

        List<Jogador> todos = modoJogo <= 3
                ? Arrays.asList(jogador1, jogador2)
                : Arrays.asList(jogador1, jogador2, jogador3, jogador4);

        List<Jogador> adversarios = new ArrayList<>();
        for (Jogador j : todos) {
            if (modoJogo <= 3) {
                if (j != jogadorQuePediu) {
                    adversarios.add(j);
                }
            } else {
                if ((dupla1.contem(jogadorQuePediu) && dupla2.contem(j))
                        || (dupla2.contem(jogadorQuePediu) && dupla1.contem(j))) {
                    adversarios.add(j);
                }
            }
        }

        for (Jogador adversario : adversarios) {
            if (adversario instanceof JogadorBot) {
                System.out.println(adversario.getNome() + " (bot) aceitou o truco!");
                continue;
            }

            while (true) {
                String resposta;
                if (adversario instanceof JogadorRemoto remoto) {
                    try {
                        remoto.enviarComando("TRUCO_RESPOSTA?");
                        resposta = remoto.receberComando().trim().toLowerCase();
                    } catch (IOException e) {
                        System.out.println("Erro ao comunicar com o jogador remoto: " + e.getMessage());
                        return -1;
                    }
                } else {
                    System.out.print(adversario.getNome() + ", aceita (s), aumenta (a), corre (n)? ");
                    resposta = scanner.nextLine().trim().toLowerCase();
                }

                if (resposta.equals("n")) {
                    int pontosParaQuemPediu = valorAtual;
                    if (modoJogo <= 3) {
                        adicionarPontosDupla(jogadorQuePediu == jogador1 ? 1 : 2, pontosParaQuemPediu);
                    } else {
                        adicionarPontosDupla(dupla1.contem(jogadorQuePediu) ? 1 : 2, pontosParaQuemPediu);
                    }
                    System.out.println(adversario.getNome() + " correu!");
                    mostrarPontuacoes();
                    return -1;
                } else if (resposta.equals("s")) {
                    return novoValor;
                } else if (resposta.equals("a")) {
                    if (novoValor == 12) {
                        System.out.println("Valor máximo já atingido! Mão continua valendo 12.");
                        return novoValor;
                    }
                    int aumento = switch (novoValor) {
                        case 3 ->
                            6;
                        case 6 ->
                            9;
                        case 9 ->
                            12;
                        default ->
                            novoValor;
                    };

                    System.out.println(adversario.getNome() + " aumentou! Mão agora vale " + aumento + " pontos.");

                    aumentoPendentes = true;
                    ultimoQuePediuTruco = adversario;

                    while (true) {
                        String resposta2;
                        if (jogadorQuePediu instanceof JogadorRemoto remoto) {
                            try {
                                remoto.enviarComando("TRUCO_RESPOSTA?");
                                resposta2 = remoto.receberComando().trim().toLowerCase();
                            } catch (IOException e) {
                                System.out.println("Erro ao comunicar com o jogador remoto: " + e.getMessage());
                                return -1;
                            }
                        } else {
                            System.out.print(jogadorQuePediu.getNome() + ", aceita (s), aumenta (a), corre (n)? ");
                            resposta2 = scanner.nextLine().trim().toLowerCase();
                        }

                        if (resposta2.equals("n")) {
                            int pontosParaAdversario = novoValor;
                            if (modoJogo <= 3) {
                                adicionarPontosDupla(adversario == jogador1 ? 1 : 2, pontosParaAdversario);
                            } else {
                                adicionarPontosDupla(dupla1.contem(adversario) ? 1 : 2, pontosParaAdversario);
                            }
                            System.out.println(jogadorQuePediu.getNome() + " correu!");
                            mostrarPontuacoes();
                            return -1;
                        } else if (resposta2.equals("s")) {
                            return aumento;
                        } else if (resposta2.equals("a")) {
                            novoValor = aumento;
                            jogadorQuePediu = adversario;
                            break;
                        }
                    }
                } else {
                    System.out.println("Resposta inválida. Digite s, a ou n.");
                }
            }
        }

        return novoValor;
    }

    public void setJogadores(Jogador j1, Jogador j2) {
        this.jogador1 = j1;
        this.jogador2 = j2;
    }

}
