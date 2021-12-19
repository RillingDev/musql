package org.felixrilling.musql.core.persistence;

import java.io.Serial;

public class PersistenceException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7717725742104778161L;

	public PersistenceException(Throwable cause) {
		super(cause);
	}
}
