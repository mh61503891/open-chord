package de.uniba.wiai.lspi.chord.com;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;

public class Test {

	private Test() {
	}

	static final String URL1 = "ocrmi://localhost:4245/";

	static final String URL2 = "ocrmi://localhost/";

	public static void main(String[] args) throws MalformedURLException, CommunicationException {
		PropertiesLoader.loadPropertyFile();
		try {
			if (args[0] != null) {
				NodeImpl node = new NodeImpl(URL2);
				Endpoint ep = Endpoint.createEndpoint(node, node.url);
				ep.listen();
				ep.acceptEntries();
			}
		} catch (Exception e) {
			// TODO: handle exception

			NodeImpl node = new NodeImpl(URL1);
			Endpoint ep = Endpoint.createEndpoint(node, node.url);
			ep.listen();
			ep.acceptEntries();

			Node proxy = Nodes.create(new URL(URL1), new URL(URL2));

			List<Long> millis = new LinkedList<Long>();

			long start = System.currentTimeMillis();
			proxy.findSuccessor(node.id);
			long end = System.currentTimeMillis();
			System.out.println("findSuccessor took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.getId();
			end = System.currentTimeMillis();
			System.out.println("getNodeID took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.insertEntry(new Entry(node.id, "test"));
			end = System.currentTimeMillis();
			System.out.println("insertEntry took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.insertReplicas(new HashSet<Entry>());
			end = System.currentTimeMillis();
			System.out.println("insertReplicas took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.removeEntry(new Entry(node.id, "test"));
			end = System.currentTimeMillis();
			System.out.println("removeEntry took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.leavesNetwork(node);
			end = System.currentTimeMillis();
			System.out.println("leavesNetwork took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.removeReplicas(node.id, new HashSet<Entry>());
			end = System.currentTimeMillis();
			System.out.println("removeReplicas took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.notify(node);
			end = System.currentTimeMillis();
			System.out.println("notify took " + (end - start) + "ms");
			millis.add((end - start));

			proxy.notifyAndCopyEntries(node);
			end = System.currentTimeMillis();
			System.out.println("notifyAndCopyEntries took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.retrieveEntries(node.id);
			end = System.currentTimeMillis();
			System.out.println("retrieveEntries took " + (end - start) + "ms");
			millis.add((end - start));

			start = System.currentTimeMillis();
			proxy.ping();
			end = System.currentTimeMillis();
			System.out.println("ping took " + (end - start) + "ms");
			millis.add((end - start));

			long calls = 0;
			long total = 0;
			for (Long time : millis) {
				total += time;
				calls++;
			}
			System.out.println("Average duration of a call: " + (total / calls));

			proxy.disconnect();

			ep.disconnect();
		}
	}

	private static class NodeImpl extends Node {

		NodeImpl(String url) {
			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
			this.id = new ID(this.url.toString().getBytes());
		}

		@Override
		public void disconnect() {
			// TODO Auto-generated method stub

		}

		@Override
		public Node findSuccessor(ID key) throws CommunicationException {
			return this;
		}

		@Override
		public void insertEntry(Entry entryToInsert) throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertReplicas(Set<Entry> entries) throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void leavesNetwork(Node predecessor) throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public List<Node> notify(Node potentialPredecessor) throws CommunicationException {
			return new LinkedList<Node>();
		}

		@Override
		public ReferencesAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException {
			return new ReferencesAndEntries(new LinkedList<Node>(), new HashSet<Entry>());
		}

		@Override
		public void ping() throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeEntry(Entry entryToRemove) throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeReplicas(ID sendingNode, Set<Entry> replicasToRemove) throws CommunicationException {
			// TODO Auto-generated method stub

		}

		@Override
		public Set<Entry> retrieveEntries(ID id) throws CommunicationException {
			return new HashSet<Entry>();
		}

	}
}
