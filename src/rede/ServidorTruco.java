package rede;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import jogador.Jogador;
import jogador.JogadorRemoto;
import jogo.Jogo;

public class ServidorTruco {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor aguardando conexão...");
        Socket socket = serverSocket.accept();
        System.out.println("Cliente conectado!");

        Scanner scanner = new Scanner(System.in);
        Jogador jogador1 = new Jogador("Jogador 1", scanner);
        Jogador jogador2 = new JogadorRemoto("Jogador 2", socket, scanner);

        Jogo jogo = new Jogo(3);
        jogo.setJogadores(jogador1, jogador2);
        jogo.iniciar();

        socket.close();
        serverSocket.close();
    }
}
