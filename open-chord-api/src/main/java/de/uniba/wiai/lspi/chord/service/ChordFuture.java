package de.uniba.wiai.lspi.chord.service;

import java.io.Serializable;

/**
 * <p>
 * This interface represents the result of an asynchronouse invocation on an implementation of {@link AsynChord}.
 * </p>
 * <p>
 * The methods:
 * <ul>
 * <li>{@link AsynChord#insertAsync(Key, Serializable)}</li>
 * <li>{@link AsynChord#removeAsync(Key, Serializable)}</li>
 * <li>{@link AsynChord#retrieveAsync(Key)}</li>
 * </ul>
 * return immediately and return an instance of this, which can be used later on to check if the execution of an insertion, removal, or retrieval has been
 * completed.
 * </p>
 *
 * @author sven
 * @author Masayuki Higashino
 * @version 1.0.5
 */
public interface ChordFuture {

	/**
	 * @return Any Throwable that occured during execution of the method associated with this. May be <code>null</code>. If {@link #isDone()} returns
	 *         <code>true</code> and this returns <code>null</code> the associated method has been executed successfully.
	 */
	Throwable getThrowable();

	/**
	 * Method to test if the method associated with this has been finished. This method does not block the calling thread.
	 *
	 * @return <code>true</code> if the method associated with this has finished successfully.
	 * @throws ServiceException
	 *             Thrown if the execution has not been successful. Contains the {@link Throwable} that can be obtained by {@link #getThrowable()} as cause.
	 */
	boolean isDone() throws ServiceException;

	/**
	 * This method blocks the calling thread until the execution of the method associated with this has been finished.
	 *
	 * @throws ServiceException
	 *             Thrown if the execution has not been successful. Contains the {@link Throwable} that can be obtained by {@link #getThrowable()} as cause.
	 * @throws InterruptedException
	 *             Occurs if the thread waiting with help of this method has been interrupted.
	 */
	void waitForBeingDone() throws ServiceException, InterruptedException;

}