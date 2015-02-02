package de.uniba.wiai.lspi.chord.com;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import de.uniba.wiai.lspi.chord.data.ID;

/**
 * @author Karsten Loesing
 * @author Masayuki Higashino
 * @version 1.0.5
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class Entry implements Serializable {

	private static final long serialVersionUID = 3473253817147038992L;
	@NonNull
	private ID id;
	@NonNull
	private Serializable value;

}
