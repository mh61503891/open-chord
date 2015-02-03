package de.uniba.wiai.lspi.chord.com;

/**
 * This interface must be implemented by classes that want to be notified about state changes of an {@link Endpoint}.
 *
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public interface EndpointListener {

	default public void onStateChanged(Endpoint.State state) {
	}

}
