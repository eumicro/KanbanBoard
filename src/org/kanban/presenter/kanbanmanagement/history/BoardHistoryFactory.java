package org.kanban.presenter.kanbanmanagement.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kanban.model.Board;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardObserver;

public class BoardHistoryFactory implements KanbanBoardObserver {
	private static final int MAX_STEPS = 40;
	private Map<Board, BoardHistory> histories;
	private static BoardHistoryFactory instance;

	private BoardHistoryFactory() {
		histories = new HashMap<Board, BoardHistory>();
	}

	public static BoardHistoryFactory getInstance() {
		return instance == null ? instance = new BoardHistoryFactory()
				: instance;
	}

	public BoardHistory getHistory(Board b) {
		BoardHistory h = histories.get(b);
		if (h == null) {
			h = new BoardHistory(b, MAX_STEPS);
			histories.put(b, h);
		}
		return h;
	}

	@Override
	public User getUser() {
		return null;
	}

	@Override
	public List<Board> getBoards() throws DataBaseNotAvailableException,
			DataBaseErrorException {
		return null;
	}

	@Override
	public boolean observesBoard(Board board)
			throws BoardDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException {
		return true;
	}

	@Override
	public void boardChanged(BoardChangedEvent e) {
		BoardHistory h = getHistory(e.getBoard());
		h.pushState(e);

	}
}
