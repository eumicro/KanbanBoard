package org.kanban.presenter.kanbanmanagement.eventservice;

import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import org.kanban.model.Board;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.kanbanmanagement.KanbanManagement;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardObserver;

public abstract class UserAsBoardObserver implements KanbanBoardObserver {
	private Stack<BoardChangedEvent> changedEvents;
	private User user;
	@SuppressWarnings("unused")
	private Logger log;

	@Override
	public User getUser() {

		return user;
	}

	public UserAsBoardObserver(User user) {
		this.user = user;
		this.log = Logger.getAnonymousLogger();
		changedEvents = new Stack<BoardChangedEvent>();
	}

	@Override
	public List<Board> getBoards() throws DataBaseNotAvailableException,
			DataBaseErrorException {

		return KanbanManagement.getinstance().getUsersBoards(user);
	}

	@Override
	public boolean observesBoard(Board board)
			throws BoardDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException {
		return KanbanManagement.getinstance().getUsersBoards(user)
				.contains(board);
	}

	@Override
	public abstract void boardChanged(BoardChangedEvent e);

}
