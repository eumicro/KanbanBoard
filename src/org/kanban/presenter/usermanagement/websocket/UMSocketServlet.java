package org.kanban.presenter.usermanagement.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "UserManagementSocketServlet", urlPatterns = { "/usermanagementsocket" })
public class UMSocketServlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(UserManagementSocket.class);
	}

}
