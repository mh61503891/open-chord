package de.uniba.wiai.lspi.chord.data;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

/**
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
@Data
public class Entry implements Serializable {

	private static final long serialVersionUID = -5143074153184345697L;
	@NonNull
	private ID id;
	@NonNull
	private Serializable value;

}
