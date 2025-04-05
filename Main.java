
import java.util.Scanner;
import jogo.Jogo;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha o modo de jogo:");
        System.out.println("1 - 1x1 contra bot");
        System.out.println("2 - 2x2 com bots");
        System.out.println("3 - 1x1 local (2 jogadores)");
        System.out.println("4 - 2x2 local (4 jogadores)");

        int modo;

        while (true) {
            System.out.print("Digite o número do modo: ");
            modo = scanner.nextInt();
            scanner.nextLine();

            if (modo >= 1 && modo <= 4) {
                break;
            } else {
                System.out.println("Modo inválido. Tente novamente.");
            }
        }

        Jogo jogo = new Jogo(modo);
        jogo.iniciar();
    }
}
