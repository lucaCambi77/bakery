/** */
package it.cambi.hexad.bakery.exception;

/**
 * @author luca
 */
public class BakeryException extends RuntimeException {

  /** Exception message constructor */
  public BakeryException(String message) {
    super(message);
  }

  /** Throwable constructor */
  public BakeryException(Throwable e) {
    super(e);
  }
}
