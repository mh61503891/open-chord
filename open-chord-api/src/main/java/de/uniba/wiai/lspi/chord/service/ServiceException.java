package de.uniba.wiai.lspi.chord.service;

/**
 * Whenever this exception is thrown, an error has occured which cannot be resolved by the service layer.
 *
 * @author Sven Kaffille
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public final class ServiceException extends Exception {

	private static final long serialVersionUID = 1039630030458301201L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
