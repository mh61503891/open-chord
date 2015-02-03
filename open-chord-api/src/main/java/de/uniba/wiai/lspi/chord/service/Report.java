package de.uniba.wiai.lspi.chord.service;

/**
 * Provides the user application with methods for retrieving internal information about the state of a Chord node, e.g. entries or references.
 *
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public interface Report {

	/**
	 * Returns a formatted String containing all entries stored on this node.
	 *
	 * @return Formatted String containing all entries stored on this node.
	 */
	String printEntries();

	/**
	 * Returns a formatted String containing all references stored in the finger table of this node.
	 *
	 * @return Formatted String containing all references stored in the finger table of this node.
	 */
	String printFingerTable();

	/**
	 * Returns a formatted String containing all references stored in the successor list of this node.
	 *
	 * @return Formatted String containing all references stored in the successor list of this node.
	 */
	String printSuccessorList();

	/**
	 * Returns a formatted String containing all references stored on this node.
	 *
	 * @return Formatted String containing all references stored on this node.
	 */
	String printReferences();

	/**
	 * Returns a formatted String containing the predecessor reference of this node.
	 *
	 * @return Formatted String containing the predecessor reference of this node.
	 */
	String printPredecessor();
}
