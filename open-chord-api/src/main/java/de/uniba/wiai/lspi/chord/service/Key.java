package de.uniba.wiai.lspi.chord.service;

/**
 * Key under which an object is stored in the chord network. This may either be a unique identifier if the object to be stored is unique (e.g. for white pages)
 * or a known keyword or metadata information under which the object should be retrieved together with others (e.g. for yellow pages). Note that this key is
 * different to the Chord ID, since the ID is calculated by applying a hash function on this key. Thus, this key may return an arbitrary long byte array with
 * uniquely identifies the object to be stored.
 *
 * @author Sven Kaffille
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public interface Key {

	/**
	 * Returns the byte for this key which is then used to calculate a unique ID for storage in the chord network.
	 *
	 * @return Byte representation of the key.
	 */
	public abstract byte[] getBytes();

}