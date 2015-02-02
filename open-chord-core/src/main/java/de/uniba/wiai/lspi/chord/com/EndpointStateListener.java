package de.uniba.wiai.lspi.chord.com;

/**
 * This interface must be implemented by classes that want to be notified about state changes of an {@link Endpoint}.
 *
 * @author Sven Kaffille
 * @version 1.0.5
 */
public interface EndpointStateListener {

	/**
	 * Notify this listener that the endpoint changed it state to <code>newState</code>.
	 *
	 * @param newState
	 *            The new state of the endpoint.
	 */
	public void notify(int newState);

}
