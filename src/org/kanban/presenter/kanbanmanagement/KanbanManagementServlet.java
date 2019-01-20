package org.kanban.presenter.kanbanmanagement;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kanban.model.Board;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.database.exceptions.InvalidDBInputException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardHasNoStationsException;
import org.kanban.presenter.kanbanmanagement.exceptions.StationDoesNotExist;
import org.kanban.presenter.kanbanmanagement.exceptions.StationIsNotEmptyException;
import org.kanban.presenter.kanbanmanagement.exceptions.TaskDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserIsAlreadyBoardUserException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserPermissionDeniedException;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.usermanagement.UserManagement;
import org.kanban.presenter.usermanagement.UserManagementUtil;
import org.kanban.presenter.usermanagement.UsermanagementCookieNames;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;
import org.kanban.shared.JSONMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KanbanManagementServlet extends HttpServlet {
	private static final String BOARD_CREATED_MSG = "Tafel wurde erfolgreich erstellt";
	private static final String USER_UNINVITED_MSG = "Der Benutzer wurde erfolgreich entfernt.";
	private static final String TASK_EDITED_MSG = "Task wurde bearbeitet";
	private static final String STATION_EDITED_MSG = "Die Station wurde bearbeitet.";
	private static final String BOARD_EDITED_MSG = "Die Tafel wurde erfolgreich bearbeitet.";
	private static final String BOARD_HAS_NO_STATIONS_EXCEPTION = "Die Tafel hat momentan keine Stationen, in die ein Task hinzugefügt werden kann!";
	private static final String TASK_CREATED_MSG = "Der Task wurde erfolgreich erstellt!";
	private static final String USER_INVITED_TEXT = "Der Benutzer wurde eingeladen und kann nun die Tafel sehen.";
	private static final String STATION_IS_NOT_EMPTY_EXCEPTION = "Die Station kann nicht gelöscht werden, da sie nicht leer ist!";
	private static final String TASK_DELETED_SUCCESSFULLY = "Der Task wurde gelösch!";
	private static final String STATION_DOES_NOT_EXIST_EXCEPTION = "Diese Station existiert nicht mehr!";
	private static final String TASK_DOES_NOT_EXIST_EXCEPTION = "Dieser Task existiert nicht mehr!";
	private static final String TASK_MOVED_MSG = "Task erfolgreich verschoben.";
	private static final String BOARD_DELETED_MSG = "Die Tafel wurde unwiderruflich gelöscht!";
	private static final String INVALID_DB_INPUT_EXCEPTION = "Datenbank meldet: Die Eingaben sind ungültig!";
	private static final String BOARD_DOES_NOT_EXIST_EXCEPTION = "Diese Tafel existiert nicht mehr!";
	private static final String USER_PERMISSION_DENIED_EXCEPTION = "Sie sind nicht berechtigt diese Aktion auszuführen!";
	private static final String DATABASE_COULD_NOT_LOAD_BOARD_ERROR = "Datenbank: Ein Fehler ist aufgetreten, die Tafel konnte nicht gespeichert werden!";
	private static final String DATABASE_NOT_AVAILABLE_EXCEPTION = "Datenbank: Die Datenbank ist momentan nicht erreichbar!";
	private static final String USER_DOES_NOT_EXIST_ERROR = "Benutzerverwaltung: Schwerer Fehler im Benutzermanagement. Der Benutzer scheint nicht zu exisiteren!";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4326464242325998L;
	private KanbanManagement kanban;
	private UserManagement userManagement;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JSONMessage jsonMessage = new JSONMessage();
		try {
			kanban = KanbanManagement.getinstance();
			userManagement = UserManagement.getInstance();

		} catch (Exception e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
			jsonMessage.setError(true);
			sendJSONResponse(resp, jsonMessage);
		}

		// handle requests

		if (req.getParameter(KanbanManagementRequestParameters.GET_USER_BOARDS) != null) {
			getUsersBoards(req, resp);
		}
		// TODO: move to usermanagement
		if (req.getParameter("get_users_like_name") != null) {
			getUsersLikeName(req, resp);
		}

		if (req.getParameter(KanbanManagementRequestParameters.CREATE_BOARD) != null) {
			createBoard(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.EDIT_BOARD) != null) {
			editBoard(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.DELETE_BOARD) != null) {
			deleteBoard(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.CREATE_STATION) != null) {
			addStation(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.EDIT_STATION) != null) {
			editStation(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.DELETE_STATION) != null) {
			deleteStation(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.CREATE_TASK) != null) {
			addTask(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.EDIT_TASK) != null) {
			editTask(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.DELETE_TASK) != null) {
			deleteTask(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.MOVE_TASK) != null) {
			moveTask(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.ASSIGN_USER_TO_TASK) != null) {
			assignUserToTask(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.INVITE_USER_TO_BOARD) != null) {
			inviteUserToBoard(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.UNINVITE_USER_FROM_BOARD) != null) {
			uninviteUserFromBoard(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.GET_HISTORY) != null) {
			getBoardHistory(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.UNDO) != null) {
			undo(req, resp);
		}
		if (req.getParameter(KanbanManagementRequestParameters.REDO) != null) {
			redo(req, resp);
		}

	}

	private void redo(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.REDO));
	}

	private void undo(HttpServletRequest req, HttpServletResponse resp) {
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.UNDO));

	}

	private void getBoardHistory(HttpServletRequest req, HttpServletResponse resp) {

		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.GET_HISTORY));
		JSONMessage jsonMessage = new JSONMessage();
		GsonBuilder gb = new GsonBuilder();
		Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
		String jsonToSend = null;
		try {
			List<BoardChangedEvent> h = KanbanManagement.getinstance().getBoardHistoryById(board_id);
			jsonToSend = json.toJson(h);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} finally {
			if (jsonMessage.getError()) {
				jsonToSend = json.toJson(jsonMessage);
			}
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");

			// send JSON back to client
			try {
				resp.getWriter().println(jsonToSend);
			} catch (IOException e) {
				log(e.getMessage());
			}
		}

	}

	private void moveTask(HttpServletRequest req, HttpServletResponse resp) {
		Long task_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.MOVE_TASK));
		Long station_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.STATION_ID));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		String username = getUsernameFromCookie(req);
		JSONMessage jsonMessage = new JSONMessage();
		try {
			User user = userManagement.getUserByName(username);
			kanban.moveTask(task_id, station_id, board_id, user);
			jsonMessage.setError(false);
			jsonMessage.setMessage(TASK_MOVED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (TaskDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(TASK_DOES_NOT_EXIST_EXCEPTION);
		} catch (StationDoesNotExist e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(STATION_DOES_NOT_EXIST_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void uninviteUserFromBoard(HttpServletRequest req, HttpServletResponse resp) {
		String unInvitingUserName = getUsernameFromCookie(req);
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		Long be_uninvited_user_id = Long
				.valueOf(req.getParameter(KanbanManagementRequestParameters.UNINVITE_USER_FROM_BOARD));
		JSONMessage jsonMessage = new JSONMessage();
		try {
			User beUnInvitedUser = UserManagement.getInstance().getUserById(be_uninvited_user_id);
			User unInvitingUser = UserManagement.getInstance().getUserByName(unInvitingUserName);
			KanbanManagement.getinstance().unInviteUserFromBoard(board_id, unInvitingUser, beUnInvitedUser);
			jsonMessage.setError(false);
			jsonMessage.setMessage(USER_UNINVITED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getInternalException().getMessage());
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void inviteUserToBoard(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		String userToInviteName = req.getParameter(KanbanManagementRequestParameters.INVITE_USER_TO_BOARD);
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		User invitingUser, beInvitedUser;
		JSONMessage jsonMessage = new JSONMessage();

		try {
			invitingUser = userManagement.getUserByName(username);
			beInvitedUser = userManagement.getUserByName(userToInviteName);
			kanban.inviteUserToBoard(board_id, invitingUser, beInvitedUser);
			jsonMessage.setMessage(USER_INVITED_TEXT);
			jsonMessage.setError(false);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e) {
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
			jsonMessage.setError(true);

		} catch (UserPermissionDeniedException e) {
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
			jsonMessage.setError(true);

		} catch (BoardDoesNotExistException e) {
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);

		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} catch (UserIsAlreadyBoardUserException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage("Dieser Benutzer ist bereits ein Nutzer dieser Tafel!");
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void assignUserToTask(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		Long task_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.TASK_ID));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));

	}

	private void deleteTask(HttpServletRequest req, HttpServletResponse resp) {
		JSONMessage jsonMessage = new JSONMessage();
		String username = null;
		User user = null;
		Long task_id = null;
		Long board_id = null;
		try {

			username = getUsernameFromCookie(req);
			task_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.DELETE_TASK));
			board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
			user = userManagement.getUserByName(username);
			kanban.deleteTask(task_id, board_id, user);
			jsonMessage.setMessage(TASK_DELETED_SUCCESSFULLY);
			jsonMessage.setError(false);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
			jsonMessage.setError(true);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (TaskDoesNotExistException e) {
			jsonMessage.setMessage(TASK_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e) {
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void editTask(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		String title = req.getParameter(KanbanManagementRequestParameters.TITLE);
		String description = req.getParameter(KanbanManagementRequestParameters.DESCRIPTION);
		String color = req.getParameter(KanbanManagementRequestParameters.COLOR);
		Long task_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.EDIT_TASK));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		Long assignee_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.ASSIGNEE_ID));
		User editor, assignee = null;
		JSONMessage jsonMessage = new JSONMessage();

		try {
			assignee = userManagement.getUserById(assignee_id);
			editor = userManagement.getUserByName(username);
			kanban.editTask(title, description, color, editor, assignee, task_id, board_id);
			jsonMessage.setError(false);
			jsonMessage.setMessage(TASK_EDITED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getInternalException().getMessage());
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} catch (TaskDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(TASK_DOES_NOT_EXIST_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void deleteStation(HttpServletRequest req, HttpServletResponse resp) {
		JSONMessage jsonMessage = new JSONMessage();
		String username = getUsernameFromCookie(req);
		Long station_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.DELETE_STATION));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		User user = null;
		try {
			user = userManagement.getUserByName(username);
			kanban.deleteStation(station_id, board_id, user);
			jsonMessage.setMessage("Die Station wurde gelöscht!");
			jsonMessage.setError(false);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
		} catch (StationDoesNotExist e) {
			jsonMessage.setMessage(STATION_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
			jsonMessage.setError(true);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e) {
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
			jsonMessage.setError(true);
		} catch (StationIsNotEmptyException e) {
			jsonMessage.setMessage(STATION_IS_NOT_EMPTY_EXCEPTION);
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void editStation(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		String title = req.getParameter(KanbanManagementRequestParameters.TITLE);
		String description = req.getParameter(KanbanManagementRequestParameters.DESCRIPTION);
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		Long station_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.EDIT_STATION));
		User user = null;
		JSONMessage jsonMessage = new JSONMessage();
		try {
			user = userManagement.getUserByName(username);
			kanban.editStation(title, description, station_id, board_id, user);
			jsonMessage.setError(false);
			jsonMessage.setMessage(STATION_EDITED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} catch (StationDoesNotExist e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(STATION_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void deleteBoard(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.DELETE_BOARD));
		User user;
		JSONMessage jsonMessage = new JSONMessage();
		try {
			user = userManagement.getUserByName(username);
			kanban.deleteBoard(board_id, user);
			jsonMessage.setMessage(BOARD_DELETED_MSG);
			jsonMessage.setError(false);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getInternalException().getMessage());
			jsonMessage.setError(true);
		} catch (TaskDoesNotExistException e) {
			// must not happen here
			jsonMessage.setMessage(TASK_DOES_NOT_EXIST_EXCEPTION);
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e) {
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
			jsonMessage.setError(true);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void editBoard(HttpServletRequest req, HttpServletResponse resp) {
		String title = req.getParameter(KanbanManagementRequestParameters.EDIT_BOARD);
		String description = req.getParameter(KanbanManagementRequestParameters.DESCRIPTION);
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));

		String username = getUsernameFromCookie(req);
		JSONMessage jsonMessage = new JSONMessage();
		User user = null;
		try {
			user = userManagement.getUserByName(username);
			kanban.editBoard(title, description, board_id, user);
			jsonMessage.setError(false);
			jsonMessage.setMessage(BOARD_EDITED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void addTask(HttpServletRequest req, HttpServletResponse resp) {
		String title = req.getParameter(KanbanManagementRequestParameters.CREATE_TASK);
		String description = req.getParameter(KanbanManagementRequestParameters.DESCRIPTION);
		String color = req.getParameter(KanbanManagementRequestParameters.COLOR);

		Long assignee_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.ASSIGNEE_ID));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		JSONMessage jsonMessage = new JSONMessage();
		String reporter = getUsernameFromCookie(req);
		try {
			User reporterUser = userManagement.getUserByName(reporter);
			User assigneeUser = userManagement.getUserById(assignee_id);
			kanban.addTaskToBoard(title, description, reporterUser, assigneeUser, color, board_id);
			jsonMessage.setError(false);
			jsonMessage.setMessage(TASK_CREATED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} catch (BoardHasNoStationsException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_HAS_NO_STATIONS_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void addStation(HttpServletRequest req, HttpServletResponse resp) {
		String username = getUsernameFromCookie(req);
		String title = req.getParameter(KanbanManagementRequestParameters.CREATE_STATION);
		String description = req.getParameter(KanbanManagementRequestParameters.DESCRIPTION);
		Short position = Short.valueOf(req.getParameter(KanbanManagementRequestParameters.POSITION));
		Long board_id = Long.valueOf(req.getParameter(KanbanManagementRequestParameters.BOARD_ID));
		JSONMessage jsonMessage = new JSONMessage();

		User user;
		try {
			user = userManagement.getUserByName(username);
			kanban.addStationToBoard(title, description, position, board_id, user);
			jsonMessage.setError(false);
			jsonMessage.setMessage("Die Station wurde erfolgreich erstellt.");
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(USER_PERMISSION_DENIED_EXCEPTION);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void createBoard(HttpServletRequest req, HttpServletResponse resp) {
		JSONMessage jsonMessage = new JSONMessage();

		try {
			String username = getUsernameFromCookie(req);
			String title = req.getParameter("create_board");
			String description = req.getParameter("description");
			Integer tNumber = Integer.valueOf(req.getParameter("template_number"));
			User user = userManagement.getUserByName(username);
			kanban.createBoard(title, description, user, tNumber);
			jsonMessage.setMessage(BOARD_CREATED_MSG);
			jsonMessage.setError(false);
		} catch (InvalidDBInputException e) {
			jsonMessage.setMessage(DATABASE_COULD_NOT_LOAD_BOARD_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}
	}

	private void getUsersLikeName(HttpServletRequest req, HttpServletResponse resp) {
		// get dummy data
		String name = req.getParameter("get_users_like_name");

		List<User> users = null;
		try {
			users = userManagement.getUsersLikeName(name);
		} catch (DataBaseNotAvailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DataBaseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// prepare response type
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		// prepare data
		GsonBuilder gb = new GsonBuilder();
		Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
		String jsonToSend = json.toJson(users);
		log(jsonToSend);
		// send JSON back to client
		try {
			resp.getWriter().println(jsonToSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getUsersBoards(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String username = UserManagementUtil.getValueByCookieName(req.getCookies(), "username");
		User user = null;
		JSONMessage jsonMessage = new JSONMessage();
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		try {
			user = userManagement.getUserByName(username);
			List<Board> foundBoards = null;
			foundBoards = kanban.getUsersBoards(user);
			// prepare data
			GsonBuilder gb = new GsonBuilder();
			Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
			String boardsAsJson = json.toJson(foundBoards);
			log("Send boards to server: " + boardsAsJson);
			// send JSON back to client
			resp.getWriter().println(boardsAsJson);

		} catch (UserDoesNotExistException e) {
			jsonMessage = new JSONMessage(USER_DOES_NOT_EXIST_ERROR);
			jsonMessage.setError(true);
			sendJSONResponse(resp, jsonMessage);
			return;
		} catch (DataBaseNotAvailableException e) {
			jsonMessage = new JSONMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
			jsonMessage.setError(true);
			sendJSONResponse(resp, jsonMessage);
			return;
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": " + e.getMessage());
			sendJSONResponse(resp, jsonMessage);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		super.doPost(req, resp);

	}

	private void sendJSONResponse(HttpServletResponse resp, JSONMessage jsonMessage) {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		GsonBuilder gb = new GsonBuilder();
		Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
		String jsonResponseString = json.toJson(jsonMessage);
		log(jsonResponseString);
		try {
			resp.getWriter().println(jsonResponseString);
		} catch (IOException e) {
			log("Schwerer Serverfehler: ", e);
		}
	}

	public String getUsernameFromCookie(HttpServletRequest req) {
		return UserManagementUtil.getValueByCookieName(req.getCookies(), UsermanagementCookieNames.USERNAME);
	}
}
