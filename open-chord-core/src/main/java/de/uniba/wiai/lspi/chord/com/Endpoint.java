package de.uniba.wiai.lspi.chord.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;
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

	@Getter
	protected Node node;
	@Getter
	protected URL url;
	@Getter
	private State state;
	private Set<EndpointListener> listeners;

	protected Endpoint(Node node, URL url) {
		this.node = node;
		this.url = url;
		this.state = Endpoint.State.STARTED;
		this.listeners = new HashSet<EndpointListener>();
	}

	public void register(EndpointListener listener) {
		listeners.add(listener);
	}

	public void unregister(EndpointListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Tell this endpoint that it can listen to incoming messages from other chord nodes.
	 */
	// TODO This method may throw an exception when starting to listen for incoming connections.
	public void listen() throws CommunicationException {
		state = State.LISTENING;
		this.onStateChanged(state);
		this.openConnections();
	}

	/**
	 * Tell this endpoint that the node is now able to receive messages that request the storage and removal of entries.
	 */
	public void acceptEntries() {
		setState(State.ACCEPT_ENTRIES);
		onStateChanged(getState());
		entriesAcceptable();
	}

	/**
	 * Tell this endpoint to disconnect and close all connections. If this method has been invoked the endpoint must be not reused!!!
	 */
	public void disconnect() throws CommunicationException {
		state = State.STARTED;
		onStateChanged(state);
		closeConnections();
		synchronized (Endpoints.endpoints) {
			Endpoints.endpoints.remove(node.url);
		}
	}

	public static Endpoint getEndpoint(URL url) {
		synchronized (Endpoints.endpoints) {
			return Endpoints.endpoints.get(url);
		}
	}

	protected void setState(Endpoint.State state) {
		this.state = state;
		onStateChanged(state);
	}

	protected void onStateChanged(Endpoint.State state) {
		synchronized (listeners) {
			for (EndpointListener listener : listeners)
				listener.onStateChanged(state);
		}
	}

	/**
	 * To implemented by sub classes. This method is called by {@link #listen()} to make it possible for other chord nodes to connect to the node on that this
	 * endpoint invocates methods.
	 */
	protected abstract void openConnections() throws CommunicationException;

	/**
	 * This method has to be overwritten by sub classes and is invoked by {@link #disconnect()} to close all connections from the chord network.
	 */
	protected abstract void closeConnections() throws CommunicationException;

	/**
	 * This method has to be overwritten by subclasses. It is called from {@link #acceptEntries()} to indicate that entries can now be accepted. So maybe if an
	 * endpoint queues incoming requests for storage or removal of entries this requests can be answered when endpoint changes it state to
	 * <code>ACCEPT_ENTRIES</code>.
	 */
	protected abstract void entriesAcceptable();

}