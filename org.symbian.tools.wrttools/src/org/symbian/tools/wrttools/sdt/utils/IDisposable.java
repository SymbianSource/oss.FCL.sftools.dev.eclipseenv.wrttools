package org.symbian.tools.wrttools.sdt.utils;

	/**
	 * Generic interface for disposable objects
	 *
	 */
public interface IDisposable {

	/**
	 * Disposes of any resources held by the object.
	 * The object should not be used after dispose has been called.
	 */
	void dispose();
}
