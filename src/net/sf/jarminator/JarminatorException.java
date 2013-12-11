package net.sf.jarminator;

/**
 * Jarminator exception.
 */
public class JarminatorException extends Exception {

	public JarminatorException(String message) {
		super(message);
	}

	public JarminatorException(Throwable ex) {
		super(ex.toString());
	}

}
