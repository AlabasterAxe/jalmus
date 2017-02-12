package net.jalmus.domain;

/**
 * Enum to represent time signature of a piece of
 * music.
 */
public enum TimeSignature {
  TWO_TWO(2, 2),
  TWO_FOUR(2, 4),
  THREE_FOUR(3, 4),
  FOUR_FOUR(4, 4),
  FIVE_FOUR(5, 4),
  SIX_EIGHT(6, 8);

  private final int numerator;
  private final int denominator;

  private TimeSignature(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }

  /**
   * @return the numerator of the time signature.
   */
  public int getNumerator() {
    return numerator;
  }

  /**
   * @return the denominator of the time signature.
   */
  public int getDenominator() {
    return denominator;
  }
}
