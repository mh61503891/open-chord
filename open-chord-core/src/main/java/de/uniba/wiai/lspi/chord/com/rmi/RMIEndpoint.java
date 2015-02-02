package de.uniba.wiai.lspi.chord.com.rmi;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Endpoint;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

public final class RMIEndpoint extends Endpoint implements RemoteNode {

	private Registry registry = null;

	private RemoteNode remoteNode = null;

	static final String NAME_IN_REGISTRY = "oc_endpoint_";

	public RMIEndpoint(Node node1, URL url1) {
		super(node1, url1);
	}

	/**
	 * Can only be called if Endpoint is not in state STARTED!
	 *
	 * @return
	 */
	RemoteNode getRemoteNode() {
		if (this.remoteNode == null) {
			throw new IllegalStateException();
		}
		return this.remoteNode;
	}

	@Override
	protected void closeConnections() {
		try {
			UnicastRemoteObject.unexportObject(this, true);
			registry.unbind(NAME_IN_REGISTRY + this.url.toString());
			this.setState(Endpoint.State.STARTED);
		} catch (AccessException e) {
		} catch (RemoteException e) {
		} catch (NotBoundException e) {
		}
	}

	@Override
	protected void entriesAcceptable() {
		this.setState(Endpoint.State.ACCEPT_ENTRIES);
	}

	@Override
	protected void openConnections() {
		try {
			if (registry == null) {
				registry = LocateRegistry.createRegistry(this.url.getPort());
			}
			Remote remoteRef;
			remoteNode = (RemoteNode) UnicastRemoteObject.exportObject(this);
			registry.bind(NAME_IN_REGISTRY + this.url.toString(), remoteNode);
			this.setState(Endpoint.State.LISTENING);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		} catch (AlreadyBoundException e) {
			throw new RuntimeException(e);
		}
	}

	private RemoteNodeInfo createInfo(Node node) {
		if (node.getID().equals(this.node.getID())) {
			return new RemoteNodeInfo(remoteNode, this.node.getID(), this.node.getURL());
		} else {
			return new RemoteNodeInfo(((RMIProxy) node).getRemoteNode(), node.getID(), node.getURL());
		}
	}

	public RemoteNodeInfo findSuccessor(ID key) throws CommunicationException, RemoteException {
		try {
			Node node = this.node.findSuccessor(key);
			return createInfo(node);
		} catch (ClassCastException e) {
			throw new RemoteException("Remote node uses unsuitable communication protocol!", e);
		}
	}

	public ID getNodeID() throws RemoteException {
		return this.node.getID();
	}

	public void insertEntry(Entry entryToInsert) throws RemoteException, CommunicationException {
		this.node.insertEntry(entryToInsert);
	}

	public void insertReplicas(Set<Entry> entries) throws RemoteException, CommunicationException {
		this.node.insertReplicas(entries);
	}

	public void leavesNetwork(RemoteNodeInfo predecessor) throws RemoteException, CommunicationException {
		this.node.leavesNetwork(new RMIProxy(predecessor, this.getURL()));
	}

	public List<RemoteNodeInfo> notify(RemoteNodeInfo potentialPredecessor) throws RemoteException, CommunicationException {
		List<Node> nodes = this.node.notify(new RMIProxy(potentialPredecessor, this.getURL()));
		List<RemoteNodeInfo> result = new LinkedList<RemoteNodeInfo>();
		for (Node node : nodes) {
			result.add(this.createInfo(node));

		}
		return result;
	}

	public RemoteRefsAndEntries notifyAndCopyEntries(RemoteNodeInfo potentialPredecessor) throws RemoteException, CommunicationException {
		RefsAndEntries raes = this.node.notifyAndCopyEntries(new RMIProxy(potentialPredecessor, this.getURL()));
		List<RemoteNodeInfo> rNodes = new LinkedList<RemoteNodeInfo>();
		List<Node> nodes = raes.getReferences();
		for (Node node : nodes) {
			rNodes.add(this.createInfo(node));
		}
		return new RemoteRefsAndEntries(raes.getEntries(), rNodes);
	}

	public void ping() throws RemoteException {
	}

	public void removeEntry(Entry entryToRemove) throws RemoteException, CommunicationException {
		this.node.removeEntry(entryToRemove);
	}

	public void removeReplicas(ID sendingNode, Set<Entry> replicasToRemove) throws RemoteException, CommunicationException {
		this.node.removeReplicas(sendingNode, replicasToRemove);

	}

	public Set<Entry> retrieveEntries(ID id) throws RemoteException, CommunicationException {
		return this.node.retrieveEntries(id);
	}
}
