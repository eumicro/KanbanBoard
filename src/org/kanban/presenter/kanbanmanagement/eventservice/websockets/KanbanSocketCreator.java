package org.kanban.presenter.kanbanmanagement.eventservice.websockets;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class KanbanSocketCreator implements WebSocketCreator {

	private static KanbanSocketCreator instance;

	@Override
	public Object createWebSocket(ServletUpgradeRequest req,
			ServletUpgradeResponse resp) {
		return new KanbanEventSocket();
	}

	public static KanbanSocketCreator getInstance() {

		return instance == null ? instance = new KanbanSocketCreator()
				: instance;
	}

}
