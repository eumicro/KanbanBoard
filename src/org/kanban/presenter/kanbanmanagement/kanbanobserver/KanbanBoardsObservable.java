package org.kanban.presenter.kanbanmanagement.kanbanobserver;

import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;

public interface KanbanBoardsObservable {
	public void addObserverToBoards(KanbanBoardObserver listener);

	public void removeObserver(KanbanBoardObserver listeener);

	public void addBoardEvent(BoardChangedEvent e)
			throws BoardDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException;

	public KanbanBoardObserver getObserverByUser(User user);
}
