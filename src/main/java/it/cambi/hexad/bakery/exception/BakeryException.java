/**
 * 
 */
package it.cambi.hexad.bakery.exception;

/**
 * @author luca
 *
 */
public class BakeryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470179020128749590L;

	/**
	 * Exception message constructor
	 */
	public BakeryException(String message) {
		super(message);
	}

	/**
	 * Throwable constructor
	 */
	public BakeryException(Throwable e) {
		super(e);
	}
}
