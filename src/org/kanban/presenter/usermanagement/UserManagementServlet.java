package org.kanban.presenter.usermanagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.database.exceptions.InvalidDBInputException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserPermissionDeniedException;
import org.kanban.presenter.usermanagement.exceptions.InvalidUserDataException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyExistsException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyloggedInException;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;
import org.kanban.shared.JSONMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class UserManagementServlet
 */
public class UserManagementServlet extends HttpServlet {
	private static final String INVALID_DB_INPUT_EXCEPTION = "Die Eingaben sind ungültig!";
	private static final String DATABASE_NOT_AVAILABLE_EXCEPTION = "Die Datenbank ist momentan nicht erreichbar!";
	private static final long serialVersionUID = 1L;
	private UserManagement userManagement;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserManagementServlet() {
		super();
		// do not delete it.
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		log("UsermanagementServlet contacted...");
		if (request.getParameter("create_user") != null) {
			log("createUser called...");
			createUser(request, response);
		}
		if (request.getParameter("is_user") != null) {
			log("isUser called...");
			isUser(request, response);
		}
		if (request.getParameter("login_user") != null) {
			log("loginUser called...");
			logIn(request, response);
		}
		if (request.getParameter("logout_user") != null) {
			logOut(request, response);
		}
		if (request.getParameter("get_username") != null) {
			getUsername(request, response);
		}
		if (request.getParameter("delete_user") != null) {
			deleteUser(request, response);
		}

	}

	private void deleteUser(HttpServletRequest request, HttpServletResponse resp) {
		String username = UserManagementUtil.getValueByCookieName(
				request.getCookies(), UsermanagementCookieNames.USERNAME);
		String password = request.getParameter("delete_user");
		JSONMessage jsonMessage = new JSONMessage();
		try {
			UserManagement.getInstance().deleteUser(username, password);
			jsonMessage.setError(false);
			jsonMessage.setMessage(JSONMessage.USER_DELETED_MSG);
		} catch (UserDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(JSONMessage.USER_DOES_NOT_EXIST_ERROR);
		} catch (InvalidUserDataException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(JSONMessage.INVALID_USER_DATA_EXCEPTION);
		} catch (DataBaseNotAvailableException | DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage
					.setMessage(JSONMessage.DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (UserPermissionDeniedException e) {
			jsonMessage.setError(true);
			jsonMessage
					.setMessage(JSONMessage.USER_PERMISSION_DENIED_EXCEPTION);
		} catch (BoardDoesNotExistException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(JSONMessage.BOARD_DOES_NOT_EXIST_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(JSONMessage.INVALID_DB_INPUT_EXCEPTION);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}
	}

	private void isUser(HttpServletRequest request, HttpServletResponse resp) {
		String username = request.getParameter("is_user");
		JSONMessage jsonMessage = new JSONMessage();
		log("isUser(" + username + ") called...");
		try {
			Boolean isUser = userManagement.isUser(username);
			jsonMessage.setError(false);
			jsonMessage.setMessage(String.valueOf(isUser));
			log("isUser(" + username + ") = " + isUser + " returned.");
		} catch (DataBaseNotAvailableException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION);
		} catch (InvalidDBInputException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(INVALID_DB_INPUT_EXCEPTION);
		} catch (DataBaseErrorException e) {
			jsonMessage.setError(true);
			jsonMessage.setMessage(DATABASE_NOT_AVAILABLE_EXCEPTION + ": "
					+ e.getInternalException().getMessage());
		} catch (Exception e) {
			jsonMessage.setError(true);
			jsonMessage
					.setMessage("Ein Problem mit dem Servlet ist aufgetreten: "
							+ e.getMessage());
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}

	}

	private void getUsername(HttpServletRequest request,
			HttpServletResponse response) {
		String username = UserManagementUtil.getValueByCookieName(
				request.getCookies(), UsermanagementCookieNames.USERNAME);
		JSONMessage jsonMessage = null;
		if (username != null) {
			jsonMessage = new JSONMessage(username);
			sendJSONResponse(response, jsonMessage);
		}

	}

	private void logOut(HttpServletRequest request, HttpServletResponse response) {
		String successMsg = "Abmeldung erfolgreich. Sie werden weitergeleitet!";
		String errorMsg = "Etwas ist schief gelaufen! Sie werden weitergeleitet.";
		String username = UserManagementUtil.getValueByCookieName(
				request.getCookies(), UsermanagementCookieNames.USERNAME);
		JSONMessage jsonMessage = null;
		if (username != null) {

			try {
				userManagement = UserManagement.getInstance();
				userManagement.logOut(username);
				Cookie unsetUserCookie = UserManagementUtil.getUnsetCookie(
						request.getCookies(),
						UsermanagementCookieNames.USERNAME);
				response.addCookie(unsetUserCookie);
				jsonMessage = new JSONMessage(successMsg);
				jsonMessage.setError(false);
			} catch (Exception e) {
				jsonMessage = new JSONMessage(errorMsg);
				jsonMessage.setError(true);
			} finally {
				sendJSONResponse(response, jsonMessage);
			}
		}

	}

	private void createUser(HttpServletRequest request,
			HttpServletResponse response) {
		String username = request.getParameter("create_user");
		String password = request.getParameter("password");
		User user = null;
		// prepare JSON stuff
		JSONMessage jsonMessage = null;
		// END prepare JSON stuff
		try {
			userManagement = UserManagement.getInstance();
			user = userManagement.createUser(username, password);
			jsonMessage = new JSONMessage("Der Benutzer '" + user.getName()
					+ "' wurde erfolgreich erstellt.");
			jsonMessage.setError(false);
		} catch (UserAlreadyExistsException e) {
			jsonMessage = new JSONMessage("Dieser Benutzer existiert bereits!");
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage = new JSONMessage(
					"Die Datenbank ist nicht erreichbar, bitte später versuchen!");
			jsonMessage.setError(true);
		} catch (InvalidUserDataException e) {
			jsonMessage = new JSONMessage(
					"Benutzerverwaltung: Die Registrierungsdaten sind ungültig!");
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e) {
			jsonMessage = new JSONMessage(
					"Datenbank: Die Registrierungsdaten sind ungültig!");
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage = new JSONMessage(
					"Datenbankfehler: Die Datenbank scheint nicht erreichbar zu sein. Bitte versuchen Sie es später noch ein mal!");
			jsonMessage.setError(true);
		} finally {
			sendJSONResponse(response, jsonMessage);
		}

	}

	private void logIn(HttpServletRequest req, HttpServletResponse resp) {

		String username = req.getParameter("login_user");
		String password = req.getParameter("password");
		log("Login method called for user '" + username + "'.");
		// prepare JSON stuff
		JSONMessage jsonMessage = null;
		// end prepare JSON stuff
		try {
			userManagement = UserManagement.getInstance();
			userManagement.logIn(username, password);
			Cookie userCookey = new Cookie(UsermanagementCookieNames.USERNAME,
					String.valueOf(username));
			resp.addCookie(userCookey);
			jsonMessage = new JSONMessage("Willkommen '" + username
					+ "' Sie werden weitergeleitet!");
			jsonMessage.setError(false);
		} catch (UserDoesNotExistException e) {
			jsonMessage = new JSONMessage("Der Benutzer '" + username
					+ "' existiert nicht!");
			jsonMessage.setError(true);
		} catch (UserAlreadyloggedInException e) {
			jsonMessage = new JSONMessage("Der Benutzer '" + username
					+ "' ist bereits eingeloggt!");
			jsonMessage.setError(true);
		} catch (DataBaseNotAvailableException e) {
			jsonMessage = new JSONMessage(
					"Datenbankfehler: Die Datenbank scheint nicht erreichbar zu sein. Bitte versuchen Sie es später noch ein mal!");
			jsonMessage.setError(true);
		} catch (InvalidDBInputException e1) {
			jsonMessage = new JSONMessage("Datenbank: Die Angaben '" + username
					+ "' '" + password + "' sind ungültig!");
			jsonMessage.setError(true);
		} catch (InvalidUserDataException e) {
			jsonMessage = new JSONMessage("Benutzerverwaltung: Die Angaben '"
					+ username + "' '" + password + "' sind ungültig!");
			jsonMessage.setError(true);
		} catch (DataBaseErrorException e) {
			jsonMessage = new JSONMessage(
					"Datenbankfehler: Die Datenbank scheint nicht erreichbar zu sein. Bitte versuchen Sie es später noch ein mal!");
			jsonMessage.setError(true);
		} finally {
			sendJSONResponse(resp, jsonMessage);
		}
	}

	private void sendJSONResponse(HttpServletResponse resp,
			JSONMessage jsonMessage) {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
