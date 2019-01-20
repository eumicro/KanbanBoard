package org.kanban.shared;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class JSONMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -333333378355286019L;
	@Expose
	private String message;
	@Expose
	private Integer code = 0;
	@Expose
	private Boolean error = false;

	public static final String USER_UNINVITED_MSG = "Der Benutzer wurde erfolgreich entfernt.";
	public static final String TASK_EDITED_MSG = "Task wurde bearbeitet";
	public static final String STATION_EDITED_MSG = "Die Station wurde bearbeitet.";
	public static final String BOARD_EDITED_MSG = "Die Tafel wurde erfolgreich bearbeitet.";
	public static final String BOARD_HAS_NO_STATIONS_EXCEPTION = "Die Tafel hat momentan keine Stationen, in die ein Task hinzugefügt werden kann!";
	public static final String TASK_CREATED_MSG = "Der Task wurde erfolgreich erstellt!";
	public static final String USER_INVITED_TEXT = "Der Benutzer wurde eingeladen und kann nun die Tafel sehen.";
	public static final String STATION_IS_NOT_EMPTY_EXCEPTION = "Die Station kann nicht gelöscht werden, da sie nicht leer ist!";
	public static final String TASK_DELETED_SUCCESSFULLY = "Der Task wurde gelösch!";
	public static final String STATION_DOES_NOT_EXIST_EXCEPTION = "Diese Station existiert nicht mehr!";
	public static final String TASK_DOES_NOT_EXIST_EXCEPTION = "Dieser Task existiert nicht mehr!";
	public static final String TASK_MOVED_MSG = "Task erfolgreich verschoben.";
	public static final String BOARD_DELETED_MSG = "Die Tafel wurde unwiderruflich gelöscht!";
	public static final String INVALID_DB_INPUT_EXCEPTION = "Datenbank meldet: Die Eingaben sind ungültig!";
	public static final String BOARD_DOES_NOT_EXIST_EXCEPTION = "Diese Tafel existiert nicht mehr!";
	public static final String USER_PERMISSION_DENIED_EXCEPTION = "Sie sind nicht berechtigt diese Aktion auszuführen!";
	public static final String DATABASE_COULD_NOT_LOAD_BOARD_ERROR = "Datenbank: Ein Fehler ist aufgetreten, die Tafel konnte nicht gespeichert werden!";
	public static final String DATABASE_NOT_AVAILABLE_EXCEPTION = "Datenbank: Die Datenbank ist momentan nicht erreichbar!";
	public static final String USER_DOES_NOT_EXIST_ERROR = "Benutzerverwaltung: Schwerer Fehler im Benutzermanagement. Der Benutzer scheint nicht zu exisiteren!";
	public static final String INVALID_USER_DATA_EXCEPTION = "Die Benutzerdaten sind ungültig!";
	public static final String USER_DELETED_MSG = "Sie sind nicht länger ein Benutzer und werden nun weitergeleitet...";

	public JSONMessage() {
		// for Serializable
	}

	public JSONMessage(String msg) {
		this.setMessage(msg);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the error
	 */
	public Boolean getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(Boolean error) {
		this.error = error;
	}

}
