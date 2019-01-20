package org.kanban.presenter.undoredomanager.interfaces;

import java.util.EmptyStackException;
import java.util.Stack;

import org.kanban.presenter.undoredomanager.exceptions.NoRedoStatesAvailableException;
import org.kanban.presenter.undoredomanager.exceptions.NoUndoStatesAvailableException;

public class UndoRedoManagerSimpleStack<M> implements UndoRedoManager<M> {
	private Stack<M> redoStates;
	private Stack<M> undoStates;
	private final Integer MAX_STEPS;

	@Override
	public M undo() throws NoUndoStatesAvailableException {
		try {
			M m = undoStates.pop();
			redoStates.push(m);
			return m;
		} catch (Exception e) {
			throw new NoUndoStatesAvailableException();
		}

	}

	@Override
	public void pushState(M state) {

		undoStates.push(state);

	}

	public UndoRedoManagerSimpleStack(Integer max_steps) {
		MAX_STEPS = max_steps;
		undoStates = new Stack<M>();
		redoStates = new Stack<M>();
	}

	@Override
	public M redo() throws NoRedoStatesAvailableException {
		M m = null;
		try {
			m = redoStates.pop();
			undoStates.push(m);
		} catch (EmptyStackException e) {
			throw new NoRedoStatesAvailableException();
		}

		return m;
	}

}
