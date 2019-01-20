package org.kanban.presenter.kanbanmanagement.eventservice.websockets;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.kanbanmanagement.KanbanManagement;
import org.kanban.presenter.kanbanmanagement.eventservice.UserAsBoardObserver;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardObserver;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanEventType;
import org.kanban.presenter.usermanagement.UserManagement;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebSocket
public class KanbanEventSocket {

	private static KanbanEventSocket instance;
	private Session session;
	private ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);
	private User user;
	private KanbanBoardObserver userObserver;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public KanbanEventSocket() {
		super();

	}

	/**
	 * called when the socket connection with the browser is established
	 * 
	 * @param session
	 */
	@OnWebSocketConnect
	public void handleConnect(Session session) {
		this.session = session;
		log.info("Connection opened with session: " + session.getLocalAddress());

	}

	/**
	 * called when the connection closed
	 * 
	 * @param statusCode
	 * @param reason
	 */
	@OnWebSocketClose
	public void handleClose(int statusCode, String reason) {
		log.info("Connection closed with statusCode=" + statusCode
				+ ", reason=" + reason);
		if (user != null) {
			try {
				KanbanManagement.getinstance().removeObserver(
						KanbanManagement.getinstance().getObserverByUser(user));
			} catch (DataBaseNotAvailableException e) {
				log.info(e.getMessage());
			} catch (DataBaseErrorException e) {
				log.info(e.getMessage());
			}
		}
		stop();
	}

	/**
	 * called when a message received from the browser
	 * 
	 * @param message
	 */
	@OnWebSocketMessage
	public void registerUser(String username) {
		try {
			user = UserManagement.getInstance().getUserByName(username);
			userObserver = KanbanManagement.getinstance().getObserverByUser(
					user);
			log.info("register user '" + user.getName() + "'.");
			if (userObserver == null) {
				userObserver = new UserAsBoardObserver(user) {

					@Override
					public void boardChanged(BoardChangedEvent e) {
						log.info("board changed called for user '"
								+ user.getName() + "'.");
						if (e.getEventType().equals(
								KanbanEventType.UNINVITE_USER)) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {

							}
						}
						notifyObserver(e);

					}

				};
				KanbanManagement.getinstance()
						.addObserverToBoards(userObserver);
			}
		} catch (UserDoesNotExistException e) {
			log.info(e.getMessage());
		} catch (DataBaseNotAvailableException e) {
			log.info(e.getMessage());
		} catch (DataBaseErrorException e) {
			log.info(e.getMessage());
		}

	}

	/**
	 * called in case of an error
	 * 
	 * @param error
	 */
	@OnWebSocketError
	public void handleError(Throwable error) {
		log.info(error.getMessage());
	}

	/**
	 * sends message to browser
	 * 
	 * @param e2
	 * 
	 * @param message
	 */
	private void notifyObserver(BoardChangedEvent e2) {

		try {
			if (session.isOpen()) {
				GsonBuilder gb = new GsonBuilder();
				Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
				String jsonResponseString = json.toJson(e2);
				log.info("send event: " + jsonResponseString);
				session.getRemote().sendString(jsonResponseString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * closes the socket
	 */
	private void stop() {
		try {
			session.disconnect();
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}

}
