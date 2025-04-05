package rede;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteTruco {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.15.9", 12345);
        System.out.println("Conectado ao servidor!");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String linha = in.readLine();
            if (linha == null) {
                break;
            }

            if (linha.startsWith("MAO:")) {
                System.out.println(linha.substring(4));
            } else if (linha.equals("SELECIONE_CARTA")) {
                System.out.print("Escolha sua carta (1, 2 ou 3): ");
                out.println(scanner.nextLine());
            } else if (linha.equals("TRUCO_PEDIR?")) {
                System.out.print("Deseja pedir truco? (s/n): ");
                out.println(scanner.nextLine());
            } else if (linha.equals("TRUCO_RESPOSTA?")) {
                System.out.print("Aceita o truco? (s = sim, a = aumenta, n = corre): ");
                out.println(scanner.nextLine());
            } else if (linha.startsWith("INFO:") || linha.startsWith("ERRO:")) {
                System.out.println(linha.substring(5));
            } else {
                System.out.println(linha);
            }
        }

        socket.close();
    }
}
