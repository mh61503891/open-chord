package de.uniba.wiai.lspi.chord.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Identifier for nodes and user-defined objects. New instances of this class are created either when a node joins the network, or by the local node inserting a
 * user-defined object. Once created, an ID instance is unmodifiable. IDs of same length can be compared as this class implements java.lang.Comparable. IDs of
 * different length cannot be compared.
 *
 * @author Sven Kaffille
 * @author Karsten Loesing
 * @version 1.0.5
 */
public class ID implements Comparable<ID>, Serializable {

	private static final long serialVersionUID = 1L;
	private byte[] payload;

	public ID(byte[] payload) {
		this.payload = Arrays.copyOf(payload, payload.length);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ID))
			return false;
		return compareTo((ID) o) == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(payload);
		return result;
	}

	public String toHexString(int numberOfBytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < numberOfBytes; i++) {
			String block = Integer.toHexString(payload[i] & 0xff).toUpperCase();
			// add leading zero to block, if necessary
			if (block.length() < 2)
				block = "0" + block;
			result.append(block + " ");
		}
		return result.toString();
	}

	public String toHexString() {
		return toHexString(payload.length);
	}

	public String toDecimalString(int numberOfBytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < numberOfBytes; i++) {
			String block = Integer.toString(payload[i] & 0xff);
			result.append(block + " ");
		}
		return result.toString();
	}

	public String toDecimalString() {
		return toDecimalString(payload.length);
	}

	public String toBinaryString(int numberOfBytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < numberOfBytes; i++) {
			String block = Integer.toBinaryString(payload[i] & 0xff);
			// add leading zero to block, if necessary
			while (block.length() < 8)
				block = "0" + block;
			result.append(block + " ");
		}
		return result.toString();
	}

	public String toBinaryString() {
		return toBinaryString(payload.length);
	}

	/**
	 * Returns length of this ID measured in bits. ID length is determined by the length of the stored byte[] array, i.e. leading zeros have to be stored in the
	 * array.
	 *
	 * @return Length of this ID measured in bits.
	 */
	public int getBitLength() {
		return payload.length * Byte.SIZE;
	}

	/**
	 * Calculates the ID which is 2^powerOfTwo bits greater than the current ID modulo the maximum ID and returns it.
	 *
	 * @param powerOfTwo
	 *            Power of two which is added to the current ID. Must be a value of the interval [0, length-1], including both extremes.
	 * @return ID which is 2^powerOfTwo bits greater than the current ID modulo the maximum ID.
	 */
	public ID addPowerOfTwo(int powerOfTwo) {
		if (powerOfTwo < 0 || powerOfTwo >= (payload.length * Byte.SIZE))
			throw new IllegalArgumentException("The power of two is out of range! It must be in the interval " + "[0, length-1]");
		// copy ID
		byte[] copy = Arrays.copyOf(payload, payload.length);
		// determine index of byte and the value to be added
		int indexOfByte = this.payload.length - 1 - (powerOfTwo / 8);
		byte[] toAdd = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToAdd = toAdd[powerOfTwo % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] += valueToAdd;

			// reset value to 1 for possible overflow situation
			valueToAdd = 1;
		}
		// check for overflow - occurs if old value had a leading one, i.e. it
		// was negative, and new value has a leading zero, i.e. it is zero or
		// positive; indexOfByte >= 0 prevents running out of the array to the
		// left in case of going over the maximum of the ID space
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ID(copy);
	}

	@Override
	public int compareTo(ID o) throws ClassCastException {
		if (getBitLength() != o.getBitLength())
			throw new ClassCastException(getBitLength() + " != " + o.getBitLength());
		for (int i = 0; i < payload.length; i++) {
			if (payload[i] - 128 < o.payload[i] - 128) {
				return -1; // this ID is smaller
			} else if (payload[i] - 128 > o.payload[i] - 128) {
				return 1; // this ID is greater
			}
		}
		return 0;
	}

	public static ID getMinID(int length) {
		return new ID(new byte[length]);
	}

	public static ID getMaxID(int length) {
		byte[] max = new byte[length];
		for (int i = 0; i < max.length; i++)
			max[i] = -1;
		return new ID(max);
	}

	/**
	 * Checks if this ID is in the interval determined by the two given IDs. Neither of the boundary IDs is included in the interval. If both IDs match, the
	 * interval is assumed to span the whole ID ring.
	 *
	 * @param from
	 *            Lower bound of interval.
	 * @param to
	 *            Upper bound of interval.
	 * @return If this key is included in the given interval.
	 */
	public boolean isInInterval(ID from, ID to) {
		// both interval bounds are equal -> calculate out of equals
		if (from.equals(to)) {
			// every ID is contained in the interval except of the two bounds
			return (!this.equals(from));
		}
		// interval does not cross zero -> compare with both bounds
		if (from.compareTo(to) < 0) {
			return (this.compareTo(from) > 0 && this.compareTo(to) < 0);
		}
		// interval crosses zero -> split interval at zero
		// calculate min and max IDs
		ID minID = ID.getMinID(payload.length);
		ID maxID = ID.getMaxID(payload.length);
		// check both splitted intervals
		// first interval: (fromID, maxID]
		return ((!from.equals(ID.getMinID(payload.length)) && this.compareTo(from) > 0 && this.compareTo(maxID) <= 0) ||
				// second interval: [minID, toID)
				(!minID.equals(to) && compareTo(minID) >= 0 && this.compareTo(to) < 0));
	}

}