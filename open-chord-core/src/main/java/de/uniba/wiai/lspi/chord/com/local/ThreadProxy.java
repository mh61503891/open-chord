package de.uniba.wiai.lspi.chord.com.local;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.Nodes;
import de.uniba.wiai.lspi.chord.com.ReferencesAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.util.logging.Logger;

/**
 * This class represents a {@link Nodes} for the protocol that allows to be build a (local) chord network within one JVM.
 *
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public class ThreadProxy extends Node {

	private static final Logger logger = Logger.getLogger(ThreadProxy.class.getName());
	protected Registry registry = null;
	protected URL creatorURL;
	protected boolean isValid = true;
	protected boolean hasBeenUsed = false;
	private ThreadEndpoint endpoint = null;

	private ThreadProxy(URL src, URL dst, ID nodeID1) {
		this.registry = Registry.getRegistryInstance();
		this.url = dst;
		this.id = nodeID1;
		this.creatorURL = src;
	}

	public ThreadProxy(URL src, URL dst) throws CommunicationException {
		this.creatorURL = src;
		this.url = dst;
		this.registry = Registry.getRegistryInstance();
		ThreadEndpoint endpoint_ = registry.lookup(dst);
		if (endpoint_ == null)
			throw new CommunicationException();
		this.id = endpoint_.getNodeID();
	}

	void reSetNodeID(ID id) {
		setId(id);
	}

	/**
	 * Method to check if this proxy is valid.
	 *
	 * @throws CommunicationException
	 */
	private void checkValidity() throws CommunicationException {
		if (!isValid)
			throw new CommunicationException("No valid connection!");
		if (endpoint == null) {
			endpoint = registry.lookup(this.url);
			if (endpoint == null)
				throw new CommunicationException();
		}

		/*
		 * Ensure that node id is set, if has not been set before.
		 */
		getId();
		if (!hasBeenUsed) {
			hasBeenUsed = true;
			Registry.getRegistryInstance().addProxyUsedBy(creatorURL, this);
		}
	}

	public boolean isValid() {
		return isValid;
	}

	public void invalidate() {
		this.isValid = false;
		this.endpoint = null;
	}

	/**
	 * Get a reference to the {@link ThreadEndpoint endpoint} this proxy delegates methods to. If there is no endpoint a {@link CommunicationException
	 * exception} is thrown.
	 *
	 * @return Reference to the {@link ThreadEndpoint endpoint} this proxy delegates methods to.
	 * @throws CommunicationException
	 *             If there is no endpoint this exception is thrown.
	 */
	public ThreadEndpoint getEndpoint() throws CommunicationException {
		ThreadEndpoint ep = registry.lookup(url);
		if (ep == null)
			throw new CommunicationException();
		return ep;
	}

	@Override
	public Node findSuccessor(ID key) throws CommunicationException {
		checkValidity();
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		Node succsessor = endpoint.findSuccessor(key);
		try {
			ThreadProxy temp = (ThreadProxy) succsessor;
			return temp.cloneMeAt(creatorURL);
		} catch (Throwable t) {
			throw new CommunicationException(t);
		}
	}

	@Override
	public void insertEntry(Entry entry) throws CommunicationException {
		this.checkValidity();
		logger.debug("Trying to execute insert().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		this.endpoint.insertEntry(entry);
		logger.debug("insert() executed");
	}

	@Override
	public void removeEntry(Entry entry) throws CommunicationException {
		this.checkValidity();
		this.endpoint.removeEntry(entry);
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[ThreadProxy ");
		buffer.append(this.url);
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public List<Node> notify(Node potentialPredecessor) throws CommunicationException {
		this.checkValidity();

		ThreadProxy potentialPredecessorProxy = new ThreadProxy(this.creatorURL, potentialPredecessor.getUrl());

		logger.debug("Trying to execute notify().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		List<Node> nodes = this.endpoint.notify(potentialPredecessorProxy);
		Node[] proxies = new Node[nodes.size()];
		try {
			int currentIndex = 0;
			// TODO Document why ThreadProxy instead of Node
			for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
				Object o = i.next();
				ThreadProxy current = (ThreadProxy) o;
				proxies[currentIndex++] = current.cloneMeAt(this.creatorURL);
			}
		} catch (Throwable t) {
			throw new CommunicationException(t);
		}
		return Arrays.asList(proxies);
	}

	@Override
	public void ping() throws CommunicationException {
		this.checkValidity();
		logger.debug("Trying to execute ping().");
		logger.debug("Found endpoint " + this.endpoint);
		this.endpoint.ping();
	}

	@Override
	public Set<Entry> retrieveEntries(ID id) throws CommunicationException {
		this.checkValidity();
		logger.debug("Trying to execute retrieve().");
		logger.debug("Found endpoint " + this.endpoint);
		return this.endpoint.retrieveEntries(id);
	}

	/**
	 * Creates a copy of this.
	 *
	 * @param creatorUrl
	 *            The url of the node where this is being copied.
	 * @return The copy of this.
	 */
	private ThreadProxy cloneMeAt(URL creatorUrl) {
		return new ThreadProxy(creatorUrl, url, id);
	}

	@Override
	public void leavesNetwork(Node predecessor) throws CommunicationException {
		this.checkValidity();

		ThreadProxy predecessorProxy = new ThreadProxy(this.creatorURL, predecessor.getUrl());

		logger.debug("Trying to execute leavesNetwork(" + predecessor + ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		this.endpoint.leavesNetwork(predecessorProxy);
	}

	@Override
	public void removeReplicas(ID sendingNodeID, Set<Entry> entriesToRemove) throws CommunicationException {
		this.checkValidity();
		logger.debug("Trying to execute removeReplicas(" + entriesToRemove + ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		this.endpoint.removeReplicas(sendingNodeID, entriesToRemove);
	}

	@Override
	public void insertReplicas(Set<Entry> entries) throws CommunicationException {
		this.checkValidity();
		logger.debug("Trying to execute insertReplicas(" + entries + ").");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		this.endpoint.insertReplicas(entries);
	}

	@Override
	public ReferencesAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException {
		this.checkValidity();

		ThreadProxy potentialPredecessorProxy = new ThreadProxy(this.creatorURL, potentialPredecessor.getUrl());

		logger.debug("Trying to execute notifyAndCopyEntries().");
		// ThreadEndpoint endpoint = this.registry.lookup(this.nodeName);
		logger.debug("Found endpoint " + this.endpoint);
		// if (endpoint == null) {
		// throw new CommunicationException();
		// }
		return this.endpoint.notifyAndCopyEntries(potentialPredecessorProxy);
	}

	@Override
	public void disconnect() {
	}

}