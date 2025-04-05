package jogo;

public enum Naipe {
    OUROS("Ouros"),
    ESPADAS("Espadas"),
    COPAS("Copas"),
    PAUS("Paus");

    private final String nome;

    Naipe(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
