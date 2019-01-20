package org.kanban.presenter.usermanagement.websocket;

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
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardObserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebSocket
public class UserManagementSocket {

	private static UserManagementSocket instance;
	private Session session;
	private ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(1);
	private User user;
	private KanbanBoardObserver userObserver;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public UserManagementSocket() {
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
		stop();
	}

	/**
	 * called when a message received from the browser
	 * 
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(String rpc) {
		switch (rpc) {
		case "get_name":

			break;

		default:
			break;
		}

	}

	/**
	 * called in case of an error
	 * 
	 * @param error
	 */
	@OnWebSocketError
	public void handleError(Throwable error) {
		error.printStackTrace();
	}

	/**
	 * sends message to browser
	 * 
	 * @param message
	 */
	private void sendObjectAsJSON(Object obj) {

		try {
			if (session.isOpen()) {
				GsonBuilder gb = new GsonBuilder();
				Gson json = gb.excludeFieldsWithoutExposeAnnotation().create();
				String jsonResponseString = json.toJson(obj);
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
			e.printStackTrace();
		}
	}

}