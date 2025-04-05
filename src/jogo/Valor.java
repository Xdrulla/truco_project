package jogo;

public enum Valor {
    QUATRO("4"),
    CINCO("5"),
    SEIS("6"),
    SETE("7"),
    Q("Q"),
    J("J"),
    K("K"),
    A("A"),
    DOIS("2"),
    TRES("3");

    private final String simbolo;

    Valor(String simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return simbolo;
    }
}
