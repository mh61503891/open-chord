package de.uniba.wiai.lspi.chord.data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Address of nodes. Once created, a URL instance is unmodifiable.
 *
 * @author Sven Kaffille
 * @author Karsten Loesing
 * @version 1.0.5
 */
public class URL implements Serializable {

	private static final long serialVersionUID = 8223277048826783692L;

	private final String protocol;
	private final String host;
	private final int port;
	private final String path;
	private transient String urlString;

	/**
	 * The names of the protocols known to this chord implementation. The name for each protocol can be referenced with help of the constants for the protocoal
	 * e.g. <code>SOCKET_PROTOCOL</code>.
	 */
	public final static List<String> KNOWN_PROTOCOLS = Collections.unmodifiableList(Arrays.asList(new String[] { "ocsocket", "oclocal", "ocrmi" }));

	/**
	 * Array containing default ports for all known protocols. The port for each protocol can be referenced with help of the constants for the protocoal e.g.
	 * <code>SOCKET_PROTOCOL</code>.
	 */
	private final static int[] DEFAULT_PORTS = new int[] { 4242, -1, 4242 };

	/**
	 * Index of socket protocol in <code>{@link #KNOWN_PROTOCOLS}</code>.
	 */
	public final static int SOCKET_PROTOCOL = 0;

	/**
	 * Index of thread protocol (for local chord network ) in <code>{@link #KNOWN_PROTOCOLS}</code>.
	 */
	public final static int LOCAL_PROTOCOL = 1;

	/**
	 * Index of socket protocol in <code>{@link #KNOWN_PROTOCOLS}</code>.
	 */
	public final static int RMI_PROTOCOL = 2;

	/**
	 * Constant for URL parsing.
	 */
	private final static String DCOLON = ":";

	/**
	 * Constant for URL parsing.
	 */
	private final static String SLASH = "/";

	/**
	 * Constant for URL parsing.
	 */
	private final static String DCOLON_SLASHES = DCOLON + SLASH + SLASH;

	/**
	 * Create an instance of URL from <code>urlString</code>.
	 *
	 * @param urlString
	 *            The string to create an URL from.
	 * @throws MalformedURLException
	 *             This can occur if <code>urlString</code> does not match the pattern <code>protocol://host[:port]/path</code>, an unknown protocol is
	 *             specified, or port is negative.
	 */
	public URL(String urlString) throws MalformedURLException {

		// store textual representation of URL
		this.urlString = urlString;

		// parse protocol
		int indexOfColonAndTwoSlashes = urlString.indexOf(DCOLON_SLASHES);
		if (indexOfColonAndTwoSlashes < 0) {
			throw new MalformedURLException("Not a valid URL");
		}
		this.protocol = urlString.substring(0, indexOfColonAndTwoSlashes);
		urlString = urlString.substring(indexOfColonAndTwoSlashes + 3);

		// parse host and port
		int endOfHost = urlString.indexOf(DCOLON);
		if (endOfHost >= 0) {
			this.host = urlString.substring(0, endOfHost);
			urlString = urlString.substring(endOfHost + 1);
			int endOfPort = urlString.indexOf(SLASH);
			if (endOfPort < 0) {
				throw new MalformedURLException("Not a valid URL!");
			}
			/* initialise port */
			int tmp_port = Integer.parseInt(urlString.substring(0, endOfPort));
			/* port must not be negative */
			if ((tmp_port <= 0) || (tmp_port >= 65536)) {
				throw new MalformedURLException("Not a valid URL! " + "Port must be between 0 and 65536!");
			}
			this.port = tmp_port;
			urlString = urlString.substring(endOfPort + 1);
		} else {
			endOfHost = urlString.indexOf(SLASH);
			if (endOfHost < 0) {
				throw new MalformedURLException("Not a valid URL");
			}
			this.host = urlString.substring(0, endOfHost);
			urlString = urlString.substring(endOfHost + 1);
			if (this.protocol.equalsIgnoreCase(KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL))) {
				this.port = URL.DEFAULT_PORTS[URL.SOCKET_PROTOCOL];
			} else if (this.protocol.equalsIgnoreCase(KNOWN_PROTOCOLS.get(URL.RMI_PROTOCOL))) {
				this.port = URL.DEFAULT_PORTS[URL.RMI_PROTOCOL];
			} else {
				this.port = URL.DEFAULT_PORTS[URL.LOCAL_PROTOCOL];
			}
		}

		// parse path
		this.path = urlString;

		// check if protocol is known
		boolean protocolIsKnown = false;
		for (int i = 0; i < KNOWN_PROTOCOLS.size() && !protocolIsKnown; i++) {
			if (this.protocol.equals(KNOWN_PROTOCOLS.get(i))) {
				protocolIsKnown = true;
			}
		}
		if (!protocolIsKnown) {
			throw new MalformedURLException("Protocol is not known! " + this.protocol);
		}

	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public final int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37 * protocol.hashCode();
		hash += 37 * host.hashCode();
		hash += 37 * path.hashCode();
		hash += 37 * port;
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof URL))
			return false;
		URL url = (URL) o;
		if (!url.getProtocol().equalsIgnoreCase(getProtocol()))
			return false;
		if (!url.getHost().equalsIgnoreCase(getHost()))
			return false;
		if (!(url.getPort() == getPort()))
			return false;
		if (!url.getPath().equals(getPath()))
			return false;
		return true;
	}

	@Override
	public final String toString() {
		if (urlString == null) {
			StringBuilder builder = new StringBuilder();
			builder.append(protocol);
			builder.append(DCOLON_SLASHES);
			builder.append(host);
			builder.append(DCOLON);
			builder.append(port);
			builder.append(SLASH);
			builder.append(path);
			urlString = builder.toString().toLowerCase();
		}
		return urlString;
	}

}