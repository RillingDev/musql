package org.felixrilling.musql.core;

import java.io.Serial;

class PersistenceException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7717725742104778161L;

	PersistenceException(Throwable cause) {
		super(cause);
	}
}
