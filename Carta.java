public class Carta {
  private final Valor valor;
  private final Naipe naipe;

  public Carta(Valor valor, Naipe naipe) {
      this.valor = valor;
      this.naipe = naipe;
  }

  public Valor getValor() {
      return valor;
  }

  public Naipe getNaipe() {
      return naipe;
  }

  @Override
  public String toString() {
      return valor + " de " + naipe;
  }
}
