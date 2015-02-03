package de.uniba.wiai.lspi.chord.com;

/**
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public class CommunicationException extends Exception {

	private static final long serialVersionUID = -3606098863603794463L;

	public CommunicationException() {
		super();
	}

	public CommunicationException(String message) {
		super(message);
	}

	public CommunicationException(Throwable cause) {
		super(cause);
	}

	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
