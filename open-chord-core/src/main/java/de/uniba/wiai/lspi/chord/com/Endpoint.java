package de.uniba.wiai.lspi.chord.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import de.uniba.wiai.lspi.chord.com.local.ThreadEndpoint;
import de.uniba.wiai.lspi.chord.com.rmi.RMIEndpoint;
import de.uniba.wiai.lspi.chord.com.socket.SocketEndpoint;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * <p>
 * This class represents an endpoint, which wraps a {@link Node}, so that other nodes can connect to the node using a protocol.
 * </p>
 * <p>
 * This class must be extended by endpoints that support a certain protocol as e.g. <code>RMI</code> or a protocol over <code>Sockets</code>.
 * </p>
 * <p>
 * This is the abstract class that has to be implemented by all Endpoints. An Endpoint enables other chord peers to connect to a {@link Node node} with help of
 * a given protocol. Each node in a chord network has to have exactly one endpoint.
 * </p>
 * <p>
 * For each protocol that shall be supported a separate endpoint has to be implemented. To initialise endpoints for a {@link Node} an {@link URL} has to be
 * provided to the {@link #createEndpoint(Node, URL)} endpoint factory method. This methods tries to determine the endpoint with help of the protocol names
 * defined by the url. Supported protocols can be found in the {@link URL} class.
 * </p>
 * An Endpoint can be in three states:
 * <ul>
 * <li><code>STARTED</code>,</li>
 * <li><code>LISTENING</code>, and</li>
 * <li><code>ACCEPT_ENTRIES</code>.</li>
 * </ul>
 * <p>
 * In state <code>STARTED</code> the endpoint has been initialised but does not listen to (possibly) incoming messages from the chord network. An endpoint gets
 * into this state if it is created with help of its constructor. <br/>
 * <br/>
 * In state <code>LISTENING</code> the endpoint accepts messages that are received from the chord network to update the finger table or request the predecessor
 * or successor of the node of this endpoint. The transition to this state is made by invocation of {@link #listen()}. <br/>
 * <br/>
 * In state <code>ACCEPT_ENTRIES</code>. This endpoint accepts messages from the chord network, that request storage or removal of entries from the DHT. The
 * transition to this state is made by invocation of {@link #acceptEntries()}.
 * </p>
 *
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
@ToString
public abstract class Endpoint {

	private static final Logger logger = Logger.getLogger(Endpoint.class.getName());

	/**
	 * Map containing all endpoints. Key: {@link URL}. Value: <code>Endpoint</code>.
	 */
	// TODO refactor
	protected static final Map<URL, Endpoint> endpoints = new HashMap<URL, Endpoint>();

	@Getter
	public enum State {
		STARTED(-1), LISTENING(1), ACCEPT_ENTRIES(2), DISCONNECTED(3), CRASHED(Integer.MAX_VALUE);

		private int order;

		private State(int order) {
			this.order = order;
		}

		public boolean isRunning() {
			return this == LISTENING || this == ACCEPT_ENTRIES;
		}

		public boolean isCrashed() {
			return this == CRASHED;
		}

	}

	/**
	 * Array containing names of methods only allowed to be invoked in state {@link #ACCEPT_ENTRIES}. Remember to eventually edit this array if you change the
	 * methods in interface {@link Node}. The method names contained in this array must be sorted!
	 */
	public static final List<String> METHODS_ALLOWED_IN_ACCEPT_ENTRIES;

	static {
		String[] temp = new String[] { "insertEntry", "removeEntry", "retrieveEntries" };
		Arrays.sort(temp);
		List<String> list = new ArrayList<String>(Arrays.asList(temp));
		METHODS_ALLOWED_IN_ACCEPT_ENTRIES = Collections.unmodifiableList(list);
	}

	private State state = State.STARTED;
	private Set<EndpointStateListener> listeners = new HashSet<EndpointStateListener>();

	/**
	 * The {@link URL}that can be used to connect to this endpoint.
	 */
	protected URL url;

	/**
	 * The {@link Node node}on which this endpoint invokes methods.
	 */
	protected Node node;

	/**
	 * @param node1
	 *            The {@link Node} this is the Endpoint for.
	 * @param url1
	 *            The {@link URL} that describes the location of this endpoint.
	 */
	protected Endpoint(Node node1, URL url1) {
		logger.info("Endpoint for " + node1 + " with url " + url1 + "created.");
		this.node = node1;
		this.url = url1;
	}

	public Node getNode() {
		return node;
	}

	public URL getURL() {
		return url;
	}

	public Endpoint.State getState() {
		return state;
	}

	protected void setState(State state) {
		this.state = state;
		notify(state);
	}

	public void register(EndpointStateListener listener) {
		listeners.add(listener);
	}

	public void deregister(EndpointStateListener listener) {
		listeners.remove(listener);
	}

	protected void notify(State s) {
		logger.log(Level.FINE, "notifying state change.");
		synchronized (listeners) {
			logger.log(Level.FINE, "Size of listeners = " + listeners.size());
			for (EndpointStateListener listener : listeners)
				listener.notify(s);
		}
	}

	/**
	 * Tell this endpoint that it can listen to incoming messages from other chord nodes. TODO: This method may throw an exception when starting to listen for
	 * incoming connections.
	 */
	public void listen() {
		state = State.LISTENING;
		this.notify(state);
		this.openConnections();
	}

	/**
	 * To implemented by sub classes. This method is called by {@link #listen()}to make it possible for other chord nodes to connect to the node on that this
	 * endpoint invocates methods. TODO: This method may throw an exception when starting to listen for incoming connections.
	 */
	protected abstract void openConnections();

	/**
	 * Tell this endpoint that the node is now able to receive messages that request the storage and removal of entries.
	 */
	public final void acceptEntries() {
		logger.info("acceptEntries() called.");
		state = State.ACCEPT_ENTRIES;
		notify(state);
		entriesAcceptable();
	}

	/**
	 * This method has to be overwritten by subclasses. It is called from {@link #acceptEntries()}to indicate that entries can now be accepted. So maybe if an
	 * endpoint queues incoming requests for storage or removal of entries this requests can be answered when endpoint changes it state to
	 * <code>ACCEPT_ENTRIES</code>.
	 */
	protected abstract void entriesAcceptable();

	/**
	 * Tell this endpoint to disconnect and close all connections. If this method has been invoked the endpoint must be not reused!!!
	 */
	public final void disconnect() {
		state = State.STARTED;
		logger.log(Level.INFO, "Disconnecting.");
		notify(this.state);
		closeConnections();
		synchronized (endpoints) {
			endpoints.remove(node.nodeURL);
		}
	}

	/**
	 * This method has to be overwritten by sub classes and is invoked by {@link #disconnect()}to close all connections from the chord network.
	 */
	protected abstract void closeConnections();

	public static Endpoint getEndpoint(URL url) {
		synchronized (endpoints) {
			Endpoint endpoint = endpoints.get(url);
			logger.log(Level.FINE, "Endpoint for URL " + url + ": " + endpoint);
			return endpoint;
		}
	}

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

}