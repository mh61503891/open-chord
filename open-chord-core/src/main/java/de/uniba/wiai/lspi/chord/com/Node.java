package de.uniba.wiai.lspi.chord.com;

import java.util.List;
import java.util.Set;

import lombok.ToString;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * Provides methods which remote nodes can invoke.
 *
 * @author Sven Kaffille
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
@ToString
public abstract class Node {

	protected ID id;
	protected URL nodeURL;

	protected void setID(ID id) {
		this.id = id;
	}

	protected void setURL(URL url) {
		this.nodeURL = url;
	}

	public ID getID() {
		return id;
	}

	public URL getURL() {
		return nodeURL;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Node))
			return false;
		return ((Node) o).id.equals(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * Returns the Chord node which is responsible for the given key.
	 *
	 * @param id
	 *            ID for which the successor is searched for.
	 * @return Responsible node.
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract Node findSuccessor(ID id) throws CommunicationException;

	/**
	 * Requests this node's predecessor in result[0] and successor list in result[1..length-1]. This method is invoked by another node which thinks it is this
	 * node's predecessor.
	 *
	 * @param potentialPredecessor
	 * @return A list containing the predecessor at first position of the list and the successors in the rest of the list.
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract List<Node> notify(Node potentialPredecessor) throws CommunicationException;

	/**
	 * Requests this node's predecessor, successor list and entries.
	 *
	 * @param potentialPredecessor
	 *            Remote node which invokes this method
	 * @return References to predecessor and successors and the entries this node will be responsible for.
	 * @throws CommunicationException
	 */
	public abstract RefsAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException;

	/**
	 * Requests a sign of live. This method is invoked by another node which thinks it is this node's successor.
	 *
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void ping() throws CommunicationException;

	/**
	 * Stores the given object under the given ID.
	 *
	 * @param entry
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void insertEntry(Entry entry) throws CommunicationException;

	/**
	 * Inserts replicates of the given entries.
	 *
	 * @param entries
	 *            The entries that are replicated.
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void insertReplicas(Set<Entry> entries) throws CommunicationException;

	/**
	 * Removes the given object from the list stored under the given ID.
	 *
	 * @param entry
	 *            The entry to remove from the dht.
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void removeEntry(Entry entry) throws CommunicationException;

	/**
	 * Removes replicates of the given entries.
	 *
	 * @param sendingNode
	 *            ID of sending node; if entriesToRemove is empty, all replicas with ID smaller than the sending node's ID are removed
	 * @param replicasToRemove
	 *            Replicas to remove; if empty, all replicas with ID smaller than the sending node's ID are removed
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void removeReplicas(ID sendingNode, Set<Entry> replicasToRemove) throws CommunicationException;

	/**
	 * Returns all entries stored under the given ID.
	 *
	 * @param id
	 * @return A {@link Set} of entries associated with <code>id</code>.
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract Set<Entry> retrieveEntries(ID id) throws CommunicationException;

	/**
	 * Inform a node that its predecessor leaves the network.
	 *
	 * @param predecessor
	 * @throws CommunicationException
	 *             Thrown if an unresolvable communication failure occurs.
	 */
	public abstract void leavesNetwork(Node predecessor) throws CommunicationException;

	/**
	 * Closes the connection to the node.
	 */
	public abstract void disconnect();

}