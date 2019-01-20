package org.kanban.presenter.kanbanmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kanban.model.Board;
import org.kanban.model.Station;
import org.kanban.model.Task;
import org.kanban.model.User;
import org.kanban.presenter.database.DataBaseInterface;
import org.kanban.presenter.database.MySQLDataBase;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.database.exceptions.InvalidDBInputException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardHasNoStationsException;
import org.kanban.presenter.kanbanmanagement.exceptions.StationDoesNotExist;
import org.kanban.presenter.kanbanmanagement.exceptions.StationIsNotEmptyException;
import org.kanban.presenter.kanbanmanagement.exceptions.TaskDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserIsAlreadyBoardUserException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserPermissionDeniedException;
import org.kanban.presenter.kanbanmanagement.history.BoardHistoryFactory;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.BoardChangedEvent;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardObserver;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanBoardsObservable;
import org.kanban.presenter.kanbanmanagement.kanbanobserver.KanbanEventType;
import org.kanban.presenter.kanbanmanagement.template.KanbanFromTemplateFactory;
import org.kanban.presenter.usermanagement.UserManagement;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;
import org.kanban.presenter.usermanagement.observer.IUserManagementObserver;

/**
 * manages kanban board business logic. uses Singleton pattern for initializing
 * instance. Implements KanbanBoardObserver interface for notification board
 * observers.
 * 
 * @author Eugen
 *
 */
public class KanbanManagement implements KanbanBoardsObservable, IUserManagementObserver {

	private static KanbanManagement instance;
	private List<KanbanBoardObserver> boardsObservers;
	private DataBaseInterface db;
	private BoardHistoryFactory histories;
	@SuppressWarnings("unused")
	private Logger log;

	private KanbanManagement() throws DataBaseNotAvailableException, DataBaseErrorException {
		log = Logger.getAnonymousLogger();
		db = MySQLDataBase.getInstance();
		boardsObservers = new ArrayList<KanbanBoardObserver>();
		histories = BoardHistoryFactory.getInstance();
		addObserverToBoards(histories);
		UserManagement.getInstance().addObserver(this);

	}

	/**
	 * Singleton instance of the KanbanManagement system
	 * 
	 * @return
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public static KanbanManagement getinstance() throws DataBaseNotAvailableException, DataBaseErrorException {
		return instance == null ? instance = new KanbanManagement() : instance;
	}

	/**
	 * creates new board with a new transient Board object as parameter.
	 * 
	 * @param newBoard
	 * @return
	 * @throws InvalidDBInputException
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public Board createBoard(Board newBoard)
			throws InvalidDBInputException, DataBaseNotAvailableException, DataBaseErrorException {

		Board board = db.createBoard(newBoard);
		try {
			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.CREATE_BOARD, board, board.getOwner()));
		} catch (BoardDoesNotExistException e) {
			// inpossible here...
		}

		return board;
	}

	/**
	 * get users boards if user exists and is a participant in a board.
	 * 
	 * @param user
	 * @return
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public List<Board> getUsersBoards(User user) throws DataBaseNotAvailableException, DataBaseErrorException {
		return db.getUsersBoards(user);
	}

	/**
	 * checks the parameters and delegates a transient board object to
	 * createBoard(Board board).
	 * 
	 * @param title
	 * @param description
	 * @param user
	 * @return
	 * @throws InvalidDBInputException
	 * @throws DataBaseNotAvailableException
	 * @throws UserDoesNotExistException
	 * @throws DataBaseErrorException
	 */
	public Board createBoard(String title, String description, User user, Integer tNumber)
			throws InvalidDBInputException, DataBaseNotAvailableException, UserDoesNotExistException,
			DataBaseErrorException {
		if (!db.isUser(user.getName())) {
			throw new UserDoesNotExistException();
		} else {
			Board board;
			if (tNumber >= 0) {
				board = KanbanFromTemplateFactory.getinstance().createBoardByTemplateNumber(tNumber);
				board.setOwner(user);
				if (!title.equals("")) {
					board.setTitle(title);
				}
				if (!description.equals("")) {
					board.setDescription(description);
				}
			} else {
				board = new Board(title, description, user);
			}

			return createBoard(board);
		}
	}

	/**
	 * deletes an existing board if user is owner and a board with given id
	 * exists.
	 * 
	 * @param board_id
	 * @param user
	 * @throws BoardDoesNotExistException
	 * @throws UserPermissionDeniedException
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 * @throws InvalidDBInputException
	 * @throws TaskDoesNotExistException
	 */
	public void deleteBoard(Long board_id, User user) throws BoardDoesNotExistException, UserPermissionDeniedException,
			DataBaseNotAvailableException, DataBaseErrorException, TaskDoesNotExistException, InvalidDBInputException {
		Board board = db.getBoardById(board_id);

		if (!board.isOwner(user)) {
			throw new UserPermissionDeniedException();
		} else {
			deleteBoard(board);

		}
	}

	private void deleteBoard(Board board) throws BoardDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException, UserPermissionDeniedException, TaskDoesNotExistException, InvalidDBInputException {
		addBoardEvent(
				new BoardChangedEventImpl(board, KanbanEventType.DELETE_BOARD, new Board(board), board.getOwner()));

		db.deleteBoard(board);
	}

	/**
	 * edits an existing board by given input parameters if user has edit
	 * rights.
	 * 
	 * @param title
	 * @param description
	 * @param board_id
	 * @param user
	 * @throws BoardDoesNotExistException
	 * @throws UserPermissionDeniedException
	 * @throws DataBaseNotAvailableException
	 * @throws InvalidDBInputException
	 * @throws DataBaseErrorException
	 */
	public void editBoard(String title, String description, Long board_id, User user)
			throws BoardDoesNotExistException, UserPermissionDeniedException, DataBaseNotAvailableException,
			InvalidDBInputException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getOwner().equals(user)) {
			throw new UserPermissionDeniedException();
		} else {
			board.setTitle(title);
			board.setDescription(description);
			db.updateBoard(board);
			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.EDIT_BOARD, board, board.getOwner()));
		}
	}

	/**
	 * ad a new station to an existing board if user is owner.
	 * 
	 * @param title
	 * @param description
	 * @param position
	 * @param board_id
	 * @param user
	 * @throws UserPermissionDeniedException
	 * @throws BoardDoesNotExistException
	 * @throws DataBaseNotAvailableException
	 * @throws InvalidDBInputException
	 * @throws DataBaseErrorException
	 */
	public void addStationToBoard(String title, String description, Short position, Long board_id, User user)
			throws UserPermissionDeniedException, BoardDoesNotExistException, DataBaseNotAvailableException,
			InvalidDBInputException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getOwner().equals(user)) {
			throw new UserPermissionDeniedException();
		} else {
			Station station = new Station();
			station.setTitle(title);
			station.setDescription(description);
			station.setPosition(position);
			board.addStation(station);
			db.updateBoard(board);
			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.CREATE_STATION, station, board.getOwner()));
		}
	}

	public void editStation(String title, String description, Long station_id, Long board_id, User user)
			throws StationDoesNotExist, UserPermissionDeniedException, BoardDoesNotExistException,
			InvalidDBInputException, DataBaseNotAvailableException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);

		if (!board.getOwner().equals(user)) {
			throw new UserPermissionDeniedException();
		} else {
			Station station = board.getStationById(station_id);
			if (station == null) {
				throw new StationDoesNotExist();
			}
			station.setTitle(title);
			station.setDescription(description);
			db.updateBoard(board);
			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.EDIT_STATION, station, board.getOwner()));
		}
	}

	public void deleteStation(Long station_id, Long board_id, User user)
			throws StationDoesNotExist, UserPermissionDeniedException, BoardDoesNotExistException,
			DataBaseNotAvailableException, InvalidDBInputException, StationIsNotEmptyException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getOwner().equals(user)) {
			throw new UserPermissionDeniedException();
		} else {
			Station station = board.getStationById(station_id);
			if (station == null) {
				throw new StationDoesNotExist();
			} else if (station.containsTasks()) {
				throw new StationIsNotEmptyException();
			} else {
				board.getStations().remove(station);
				db.updateBoard(board);
				addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.DELETE_STATION, new Station(station),
						board.getOwner()));
			}

		}
	}

	public void addTaskToBoard(String title, String description, User reporter, User assignee, String color,
			Long board_id)
					throws BoardDoesNotExistException, DataBaseNotAvailableException, UserPermissionDeniedException,
					InvalidDBInputException, DataBaseErrorException, BoardHasNoStationsException {
		Board board = db.getBoardById(board_id);
		if (!board.getUsers().contains(reporter)) {
			throw new UserPermissionDeniedException();
		} else if (board.getStations().size() == 0) {
			throw new BoardHasNoStationsException();
		} else {

			Task newTask = new Task();
			newTask.setTitle(title);
			newTask.setDescription(description);
			newTask.setColor(color);
			newTask.setReporter(reporter);
			newTask.setAssignee(assignee);
			board.addTask(newTask);
			db.updateBoard(board);
			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.CREATE_TASK, newTask, reporter));
		}
	}

	public void editTask(String title, String description, String color, User editor, User assignee, Long task_id,
			Long board_id)
					throws BoardDoesNotExistException, DataBaseNotAvailableException, UserPermissionDeniedException,
					InvalidDBInputException, TaskDoesNotExistException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getUsers().contains(editor)) {
			throw new UserPermissionDeniedException();
		} else {
			Task editedTask = board.getTaskById(task_id);
			if (editedTask == null) {
				throw new TaskDoesNotExistException();
			} else {
				editedTask.setTitle(title);
				editedTask.setDescription(description);
				editedTask.setColor(color);
				editedTask.setAssignee(assignee);
				editedTask.setUpdatedDate();
				db.updateBoard(board);
				addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.EDIT_TASK, editedTask, editor));
			}

		}
	}

	public void deleteTask(Long task_id, Long board_id, User user)
			throws UserPermissionDeniedException, BoardDoesNotExistException, DataBaseNotAvailableException,
			TaskDoesNotExistException, InvalidDBInputException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getUsers().contains(user)) {
			throw new UserPermissionDeniedException();
		} else {
			Task task = board.getTaskById(task_id);
			if (task == null) {
				throw new TaskDoesNotExistException();
			} else {
				board.removeTask(task);
				db.updateBoard(board);
				addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.DELETE_TASK, new Task(task), user));
			}

		}
	}

	public void moveTask(Long task_id, Long station_id, Long board_id, User user)
			throws BoardDoesNotExistException, DataBaseNotAvailableException, UserPermissionDeniedException,
			TaskDoesNotExistException, StationDoesNotExist, InvalidDBInputException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		Task task = board.getTaskById(task_id);
		Station station = board.getStationById(station_id);
		if (task == null) {
			throw new TaskDoesNotExistException();
		}
		if (station == null) {
			throw new StationDoesNotExist();
		}
		if (!board.isUser(user)) {
			throw new UserPermissionDeniedException();
		} else {
			board.moveTaskToStation(task, station);
			db.updateBoard(board);

			addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.MOVE_TASK, task, user));
		}

	}

	public List<User> getUsersOfBoard(Long board_id) throws BoardDoesNotExistException, DataBaseNotAvailableException {

		return db.getUsersOfBoard(board_id);

	}

	public void inviteUserToBoard(Long board_id, User invitingUser, User beInvitedUser) throws InvalidDBInputException,
			DataBaseNotAvailableException, UserPermissionDeniedException, BoardDoesNotExistException,
			UserDoesNotExistException, UserIsAlreadyBoardUserException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.isOwner(invitingUser)) {
			throw new UserPermissionDeniedException();
		}
		if (board.isUser(beInvitedUser)) {
			throw new UserIsAlreadyBoardUserException();
		}
		board.addUser(beInvitedUser);
		db.updateBoard(board);
		addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.INVITE_USER, beInvitedUser, invitingUser));

	}

	/**
	 * remove user after setting owner as reporter and assignee instead of
	 * deleted user
	 * 
	 * @param board_id
	 * @param unInvitingUser
	 * @param beUnInvitedUser
	 * @throws BoardDoesNotExistException
	 * @throws DataBaseNotAvailableException
	 * @throws UserPermissionDeniedException
	 * @throws InvalidDBInputException
	 * @throws UserDoesNotExistException
	 * @throws DataBaseErrorException
	 */
	public void unInviteUserFromBoard(Long board_id, User unInvitingUser, User beUnInvitedUser)
			throws BoardDoesNotExistException, DataBaseNotAvailableException, UserPermissionDeniedException,
			InvalidDBInputException, UserDoesNotExistException, DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.isOwner(unInvitingUser) || board.isOwner(beUnInvitedUser)) {
			throw new UserPermissionDeniedException();
		} else if ((!db.isUser(beUnInvitedUser.getName())) || (!db.isUser(unInvitingUser.getName()))) {
			throw new UserDoesNotExistException();
		} else {
			// remove user after setting owner as reporter and assignee instead
			// of deleted user
			for (Task task : board.getTasks()) {
				if (task.getReporter().equals(beUnInvitedUser)) {
					task.setReporter(board.getOwner());
				}
				if (task.getAssignee().equals(beUnInvitedUser)) {
					task.setAssignee(board.getOwner());
				}
			}
			// notify user before uninvite
			addBoardEvent(
					new BoardChangedEventImpl(board, KanbanEventType.UNINVITE_USER, beUnInvitedUser, unInvitingUser));
			// uninvite
			board.removeUser(beUnInvitedUser);
			db.updateBoard(board);
			// updateBoard(board);
		}
	}

	public void assignUserToTask(Long board_id, Long task_id, User refererUser, User assignedUser)
			throws BoardDoesNotExistException, InvalidDBInputException, DataBaseNotAvailableException,
			TaskDoesNotExistException, UserDoesNotExistException, UserPermissionDeniedException,
			DataBaseErrorException {
		Board board = db.getBoardById(board_id);
		if (!board.getUsers().contains(refererUser)) {
			throw new UserPermissionDeniedException();
		} else if ((!db.isUser(refererUser.getName())) || (!db.isUser(assignedUser.getName()))) {
			throw new UserDoesNotExistException();
		} else {
			Task task = board.getTaskById(task_id);
			if (task == null) {
				throw new TaskDoesNotExistException();
			} else {
				User oldAssigneUser = task.getAssignee();
				board.assignUserToTask(task, assignedUser);
				db.updateBoard(board);
				List<Object> affectedObjects = new ArrayList<Object>();
				affectedObjects.add(task);
				affectedObjects.add(oldAssigneUser);
				affectedObjects.add(assignedUser);

				addBoardEvent(new BoardChangedEventImpl(board, KanbanEventType.ASSIGN_USER_TO_TASK, affectedObjects,
						refererUser));
			}

		}
	}

	// private void updateBoard(Board board) throws BoardDoesNotExistException,
	// DataBaseNotAvailableException, InvalidDBInputException,
	// DataBaseErrorException {
	// db.updateBoard(board);
	// addBoardEvent(new BoardChangedEvent() {
	// @Override
	// public Board getBoard() {
	// return board;
	// }
	//
	// @Override
	// public KanbanEventType getEventType() {
	// return KanbanEventType.EDIT;
	// }
	// });
	//
	// }

	public void addObserverToBoards(KanbanBoardObserver l) {
		if (boardsObservers.contains(l)) {
			return;
		} else {
			boardsObservers.add(l);
		}

	}

	public void removeObserver(KanbanBoardObserver l) {
		if (!boardsObservers.contains(l)) {
			return;
		} else {
			boardsObservers.remove(l);
		}

	}

	public void addBoardEvent(BoardChangedEvent e)
			throws BoardDoesNotExistException, DataBaseNotAvailableException, DataBaseErrorException {
		for (KanbanBoardObserver kanbanBoardListener : boardsObservers) {

			if (kanbanBoardListener.observesBoard(e.getBoard())) {
				kanbanBoardListener.boardChanged(e);
			}

		}

	}

	public KanbanBoardObserver getObserverByUser(User user) {
		for (KanbanBoardObserver kanbanBoardObserver : boardsObservers) {
			User u = kanbanBoardObserver.getUser();
			if (u != null && u.equals(user)) {
				return kanbanBoardObserver;
			}
		}
		return null;
	}

	/**
	 * 
	 * @author Eugen
	 *
	 */
	private class BoardChangedEventImpl extends BoardChangedEvent {
		private static final long serialVersionUID = 3590081332337541061L;

		/**
		 * 
		 * @param board
		 * @param type
		 * @param affectedObject
		 * @param user
		 */
		public BoardChangedEventImpl(Board board, KanbanEventType type, Object affectedObject, User user) {
			setDate(new Date());
			setEventType(type);
			setBoard(board);
			setAffected(affectedObject);
			setActor(user);
		}

	}

	public List<BoardChangedEvent> getBoardHistoryById(Long board_id)
			throws BoardDoesNotExistException, DataBaseNotAvailableException, DataBaseErrorException {
		Board b = db.getBoardById(board_id);

		return histories.getHistory(b).getEventHistory();
	}

	@Override
	public void onUserLogout(User u) {
		// nothing todo

	}

	@Override
	public void onUserLogin(User u) {
		// nothing todo

	}

	@Override
	public void onUserCreate(User u) {
		// nothing todo

	}

	@Override
	public void beforeUserDelete(User u) {
		List<Board> boards;
		try {
			boards = KanbanManagement.getinstance().getUsersBoards(u);
			// delete users boards or uninvite them from foreign ones
			for (Board board : boards) {
				if (board.isOwner(u)) {
					deleteBoard(board.getId(), u);
				} else {
					unInviteUserFromBoard(board.getId(), board.getOwner(), u);
				}
			}
		} catch (Exception e1) {
			log.log(Level.WARNING, "Something went wrong while deleting user boards");
		}

	}
}
