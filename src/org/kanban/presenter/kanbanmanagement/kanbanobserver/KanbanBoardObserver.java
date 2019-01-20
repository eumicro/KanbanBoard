package org.kanban.presenter.kanbanmanagement.kanbanobserver;

import java.util.List;

import org.kanban.model.Board;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;

public interface KanbanBoardObserver {

	public User getUser();

	public List<Board> getBoards() throws DataBaseNotAvailableException,
			DataBaseErrorException;

	public boolean observesBoard(Board board)
			throws BoardDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException;

	public void boardChanged(BoardChangedEvent e);

}
