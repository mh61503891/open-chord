package de.uniba.wiai.lspi.chord.com;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import de.uniba.wiai.lspi.chord.com.local.ThreadEndpoint;
import de.uniba.wiai.lspi.chord.com.rmi.RMIEndpoint;
import de.uniba.wiai.lspi.chord.com.socket.SocketEndpoint;
import de.uniba.wiai.lspi.chord.data.URL;

public class Endpoints {

	/**
	 * Map containing all endpoints. Key: {@link URL}. Value: <code>Endpoint</code>.
	 */
	// TODO remove static list
	@Deprecated
	public static final Map<URL, Endpoint> endpoints = new HashMap<URL, Endpoint>();

	/**
	 * Create the endpoints for the protocol given by <code>url</code>. An URL must have a known protocol. An endpoint for an {@link URL} can only be create
	 * once and then be obtained with help of {@link Endpoint#getEndpoint(URL)}. An endpoint for an url must again be created if the
	 * {@link Endpoint#disconnect()} has been invoked.
	 *
	 * @param node
	 *            The node to which this endpoint delegates incoming requests.
	 * @param url
	 *            The URL under which <code>node</code> will be reachable by other nodes.
	 * @return The endpoint created for <code>node</code> for the protocol specified in <code>url</code>.
	 * @throws RuntimeException
	 *             This can occur if any error that cannot be handled by this method occurs during endpoint creation.
	 */
	public static Endpoint createEndpoint(Node node, @NonNull URL url) {
		synchronized (endpoints) {
			if (endpoints.containsKey(url))
				throw new RuntimeException("Endpoint already created!");
			Endpoint endpoint = null;
			if (url.getProtocol().equals(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL))) {
				endpoint = new SocketEndpoint(node, url);
			} else if (url.getProtocol().equals(URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL))) {
				endpoint = new ThreadEndpoint(node, url);
			} else if (url.getProtocol().equals(URL.KNOWN_PROTOCOLS.get(URL.RMI_PROTOCOL))) {
				endpoint = new RMIEndpoint(node, url);
			} else {
				throw new IllegalArgumentException("Url does not contain a " + "supported protocol " + "(" + url.getProtocol() + ")!");
			}
			endpoints.put(url, endpoint);
			return endpoint;
		}
	}

	public static Endpoint getEndpoint(URL url) {
		synchronized (Endpoints.endpoints) {
			return Endpoints.endpoints.get(url);
		}
	}

	public static void removeEndpoint(URL url) {
		synchronized (Endpoints.endpoints) {
			Endpoints.endpoints.remove(url);
		}
	}

	public static void removeEndpoint(Node node) {
		removeEndpoint(node.getUrl());
	}

}
