package de.uniba.wiai.lspi.chord.com;

import de.uniba.wiai.lspi.chord.com.Endpoint.State;

/**
 * This interface must be implemented by classes that want to be notified about state changes of an {@link Endpoint}.
 *
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public interface EndpointStateListener {

	public void notify(State state);

}
