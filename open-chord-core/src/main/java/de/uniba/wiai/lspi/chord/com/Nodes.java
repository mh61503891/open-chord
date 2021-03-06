package de.uniba.wiai.lspi.chord.com;

import java.net.MalformedURLException;

import lombok.NonNull;
import de.uniba.wiai.lspi.chord.com.local.ThreadProxy;
import de.uniba.wiai.lspi.chord.com.rmi.RMIProxy;
import de.uniba.wiai.lspi.chord.com.socket.SocketProxy;
import de.uniba.wiai.lspi.chord.data.URL;

/**
 * This class is used to represent other {@link de.uniba.wiai.lspi.chord.service.Chord nodes} at a {@link de.uniba.wiai.lspi.chord.service.Chord node}, so that
 * these nodes are able to connect to the node. A Proxy should establish a connection to the {@link Endpoint} of the node that is represented by this proxy. So
 * all protocol specific implementation for connections between nodes must be realized in an pair of {@link Endpoint} and {@link Nodes}. This class has to be
 * extended by all Proxies that are used to provide a connection to a remote node via the {@link Node} interface.
 *
 * @author Sven Kaffille
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public class Nodes {

	public static Node create(@NonNull URL src, @NonNull URL dst) throws CommunicationException {
		if (src.equals(dst))
			throw new IllegalArgumentException("URLs must not be equal: " + src);
		String protocol = dst.getProtocol();
		if (protocol.equals(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL))) {
			return SocketProxy.create(src, dst);
		} else if (protocol.equals(URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL))) {
			return new ThreadProxy(src, dst);
		} else if (protocol.equals(URL.KNOWN_PROTOCOLS.get(URL.RMI_PROTOCOL))) {
			return RMIProxy.create(src, dst);
		} else {
			throw new CommunicationException(new MalformedURLException());
		}
	}

}
