package org.kanban.presenter.kanbanmanagement.eventservice.websockets;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "KanbanEventSocketServlet", urlPatterns = { "/eventsocket" })
public class KanbanEventSocketServlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(Long.MAX_VALUE);
		factory.setCreator(KanbanSocketCreator.getInstance());

	}

}
