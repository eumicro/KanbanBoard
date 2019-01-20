package org.kanban.presenter.kanbanmanagement.template;

import org.kanban.model.Board;

public abstract class KanbanFactory {

	public abstract Board createBoard(BoardTemplate t);
}
