package org.kanban.presenter.undoredomanager.interfaces;

import org.kanban.presenter.undoredomanager.exceptions.NoRedoStatesAvailableException;
import org.kanban.presenter.undoredomanager.exceptions.NoUndoStatesAvailableException;

public interface UndoRedoManager<TState> {
	public TState undo() throws NoUndoStatesAvailableException;

	public void pushState(TState state);

	public TState redo() throws NoRedoStatesAvailableException;
}
