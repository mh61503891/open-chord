package de.uniba.wiai.lspi.chord.com;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Getter;

/**
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
@Getter
@Deprecated
public class ReferencesAndEntries implements Serializable {

	private static final long serialVersionUID = -3436082603636957676L;
	private List<Node> references;
	private Set<Entry> entries;

	public ReferencesAndEntries(List<Node> references, Set<Entry> entries) {
		this.references = references;
		this.entries = entries;
	}

}
