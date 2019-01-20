package org.kanban.presenter.kanbanmanagement.history;

import java.util.ArrayList;
import java.util.List;

import org.kanban.model.Board;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.undoredomanager.interfaces.UndoRedoManagerSimpleStack;

public class BoardHistory {
	private Board b;
	private List<BoardChangedEvent> history;
	private UndoRedoManagerSimpleStack<BoardChangedEvent> undoRedoManager;
	public final Integer MAX_STEPS;

	public BoardHistory(Board b, Integer max_steps) {
		this.MAX_STEPS = max_steps;
		this.b = b;
		this.history = new ArrayList<BoardChangedEvent>();
	}

	public void pushState(BoardChangedEvent e) {
		history.add(0, e);
		if (history.size() > MAX_STEPS) {
			history = history.subList(history.size() - MAX_STEPS, history.size());
		}

	}

	public List<BoardChangedEvent> getEventHistory() {
		return history;
	}

	public BoardChangedEvent getStep(int i) {
		return history.get(i);
	}

}
