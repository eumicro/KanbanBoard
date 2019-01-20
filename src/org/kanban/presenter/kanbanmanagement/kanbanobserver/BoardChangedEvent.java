package org.kanban.presenter.kanbanmanagement.kanbanobserver;

import java.io.Serializable;
import java.util.Date;

import org.kanban.model.Board;
import org.kanban.model.User;

import com.google.gson.annotations.Expose;

public class BoardChangedEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1960391879214921716L;
	@Expose
	private KanbanEventType type;
	@Expose
	private Board board;
	@Expose
	private User actor;
	@Expose
	private Object affected;
	@Expose
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public KanbanEventType getType() {
		return type;
	}

	public Board getBoard() {
		return board;
	}

	public void setEventType(KanbanEventType type) {
		this.type = type;
	}

	public KanbanEventType getEventType() {

		return type;
	}

	/**
	 * @param board
	 *            the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * @return the actor
	 */
	public User getActor() {
		return actor;
	}

	/**
	 * @param actor
	 *            the actor to set
	 */
	public void setActor(User actor) {
		this.actor = actor;
	}

	/**
	 * @return the affected
	 */
	public Object getAffected() {
		return affected;
	}

	/**
	 * @param affected
	 *            the affected to set
	 */
	public void setAffected(Object affected) {
		this.affected = affected;
	}
}
