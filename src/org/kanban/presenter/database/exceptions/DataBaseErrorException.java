package org.kanban.presenter.database.exceptions;

public class DataBaseErrorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1894639569559336347L;
	Exception e;

	public DataBaseErrorException(Exception e) {
		this.e = e;
	}

	public Exception getInternalException() {
		return e;
	}

}
