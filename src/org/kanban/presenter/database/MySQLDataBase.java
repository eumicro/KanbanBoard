package org.kanban.presenter.database;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.kanban.model.Board;
import org.kanban.model.User;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.database.exceptions.InvalidDBInputException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.usermanagement.exceptions.InvalidUserDataException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyExistsException;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;

public class MySQLDataBase implements DataBaseInterface {

	private static MySQLDataBase instance;

	private EntityManager dbManager;
	private EntityManagerFactory dbManagerFactory;
	private Logger log;

	private MySQLDataBase() throws DataBaseErrorException {
		log = Logger.getAnonymousLogger();
		try {
			dbManagerFactory = Persistence.createEntityManagerFactory("Kanban");
			dbManager = dbManagerFactory.createEntityManager();
		} catch (Exception e) {
			throw new DataBaseErrorException(e);
		}

	}

	public static MySQLDataBase getInstance()
			throws DataBaseNotAvailableException, DataBaseErrorException {
		return instance == null ? instance = new MySQLDataBase() : instance;
	}

	public synchronized List<Board> getUsersBoards(User user)
			throws DataBaseNotAvailableException, DataBaseErrorException {

		try {
			connect();
			Query query = dbManager.createNamedQuery(
					"Board.findBoardsByUserId", Board.class);
			query.setParameter("userid", user.getId());

			try {

				@SuppressWarnings("unchecked")
				List<Board> foundBoards = (List<Board>) query.getResultList();

				return foundBoards;
			} catch (NoResultException e) {

				return null;
			}

		} catch (Exception e) {
			log.log(Level.WARNING,
					"Database error - return null: " + e.getMessage(), e);
			throw new DataBaseErrorException(e);

		} finally {
			disconnect();
		}
	}

	public Board createBoard(Board board) throws InvalidDBInputException,
			DataBaseErrorException {
		try {
			connect();
			dbManager.persist(board);
			dbManager.getTransaction().commit();
			return board;
		} catch (Exception e) {
			rollback();
			throw new InvalidDBInputException();
		} finally {
			disconnect();
		}

	}

	public Board createBoard(String title, String description, User owner)
			throws InvalidDBInputException, DataBaseNotAvailableException,
			UserDoesNotExistException, DataBaseErrorException {
		if (!isUser(owner.getName())) {
			throw new UserDoesNotExistException();
		} else {
			Board newBoard = new Board(title, description, owner);
			createBoard(newBoard);

		}

		return null;
	}

	public void deleteBoard(Board board) throws BoardDoesNotExistException,
			DataBaseNotAvailableException, DataBaseErrorException {
		try {
			connect();

			board = dbManager.merge(board);
			dbManager.remove(board);
			dbManager.getTransaction().commit();
		} catch (Exception e) {
			rollback();
			throw new DataBaseErrorException(e);
		} finally {
			disconnect();
		}

	}

	private void rollback() throws DataBaseErrorException {
		try {
			dbManager.getTransaction().rollback();
		} catch (Exception e) {
			throw new DataBaseErrorException(e);
		}

	}

	public void updateBoard(Board board) throws BoardDoesNotExistException,
			InvalidDBInputException, DataBaseNotAvailableException,
			DataBaseErrorException {

		try {
			connect();
			board = dbManager.merge(board);
			dbManager.persist(board);
			dbManager.getTransaction().commit();
		} catch (Exception e) {
			rollback();
			throw new InvalidDBInputException();
		} finally {
			disconnect();
		}

	}

	public synchronized User createUser(String username, String password)
			throws UserAlreadyExistsException, InvalidUserDataException,
			DataBaseErrorException {
		User newUser = null;

		try {
			connect();

			newUser = new User();
			newUser.setName(username);
			newUser.setHashedPassword(password);
			dbManager.persist(newUser);
			dbManager.getTransaction().commit();

			return newUser;

		} catch (EntityExistsException e) {
			rollback();
			log.log(Level.WARNING, "User already exists.");
			throw new UserAlreadyExistsException();
		} catch (Exception e) {
			rollback();
			log.log(Level.WARNING, "Datenbankfehler");
			throw new DataBaseErrorException(e);

		} finally {
			disconnect();
		}

	}

	public User getUserById(Long id) throws UserDoesNotExistException {
		try {
			connect();
			User user = dbManager.find(User.class, id);
			return user;
		} catch (Exception e) {
			throw new UserDoesNotExistException();
		} finally {
			disconnect();
		}

	}

	public void updateUser(User user) throws UserDoesNotExistException,
			DataBaseErrorException {

		try {
			connect();
			dbManager.refresh(user);
			dbManager.getTransaction().commit();
		} catch (Exception e) {
			rollback();
			throw new UserDoesNotExistException();

		} finally {
			disconnect();
		}

	}

	public void deleteUser(User user) throws UserDoesNotExistException,
			DataBaseErrorException {

		try {
			connect();
			user = dbManager.merge(user);
			dbManager.remove(user);
			dbManager.getTransaction().commit();
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			rollback();
			throw new UserDoesNotExistException();
		} finally {
			disconnect();
		}

	}

	public List<User> getUsersOfBoard(Long board_id)
			throws BoardDoesNotExistException {

		try {
			connect();
			Query query = dbManager.createNamedQuery("Board.findUsersOfBoard",
					User.class);
			query.setParameter("board_id", board_id);

			try {

				@SuppressWarnings("unchecked")
				List<User> foundUsers = (List<User>) query.getResultList();

				return foundUsers;
			} catch (NoResultException e) {

				return null;
			}

		} catch (Exception e) {

			throw new BoardDoesNotExistException();

		} finally {
			disconnect();

		}
	}

	public User getUserByName(String username)
			throws UserDoesNotExistException, DataBaseNotAvailableException,
			DataBaseErrorException {

		try {
			connect();
			Query query = dbManager.createNamedQuery("User.findByName",
					User.class);
			query.setParameter("username", username);

			User foundUser = (User) query.getSingleResult();

			return foundUser;

		} catch (NoResultException e) {
			throw new UserDoesNotExistException();
		} catch (Exception e) {
			throw new DataBaseErrorException(e);
		} finally {
			disconnect();
		}
	}

	public List<User> getUsersLikeName(String name)
			throws DataBaseNotAvailableException, DataBaseErrorException {

		try {
			connect();
			Query query = dbManager.createNamedQuery("User.findBeginsWithName");
			query.setParameter("username", name + "%");
			@SuppressWarnings("unchecked")
			List<User> result = (List<User>) query.getResultList();
			return result;
		} catch (Exception e) {

			throw new DataBaseErrorException(e);
		} finally {
			disconnect();
		}
	}

	public boolean isUser(String username) throws InvalidDBInputException,
			DataBaseNotAvailableException, DataBaseErrorException {
		try {
			User user = getUserByName(username);
			if (user != null) {
				return true;
			} else {
				return false;
			}

		} catch (UserDoesNotExistException e) {
			return false;
		}
	}

	public boolean checkLogIn(String username, String password)
			throws UserDoesNotExistException, InvalidDBInputException,
			DataBaseNotAvailableException, DataBaseErrorException {

		try {
			connect();
			Query query = dbManager.createNamedQuery("User.checkLogin",
					String.class);
			query.setParameter("username", username);

			String userPassword = (String) query.getSingleResult(); // returns
																	// count of
																	// found
																	// users.

			if (userPassword.equals(password)) { // if one user found

				return true;
			} else {
				if (!isUser(username)) {

					throw new UserDoesNotExistException();
				}

				return false;
			}
		} catch (Exception e) {
			if (e instanceof NoResultException) {
				throw new UserDoesNotExistException();
			} else {

				throw new DataBaseErrorException(e);
			}

		} finally {
			disconnect();
		}

	}

	private synchronized void connect() {
		dbManagerFactory = Persistence.createEntityManagerFactory("Kanban");
		dbManager = dbManagerFactory.createEntityManager();
		dbManager.getTransaction().begin();
	}

	private synchronized void disconnect() {

		if (dbManager.isOpen()) {
			dbManager.close();
		}
		if (dbManagerFactory.isOpen()) {
			dbManagerFactory.close();
		}

	}

	public Board getBoardById(Long board_id) throws BoardDoesNotExistException,
			DataBaseNotAvailableException, DataBaseErrorException {

		try {
			connect();
			Query query = dbManager.createNamedQuery("Board.findById",
					Board.class);
			query.setParameter("board_id", board_id);

			Board foundBoard = (Board) query.getSingleResult();

			return foundBoard;

		} catch (NoResultException e) {

			throw new BoardDoesNotExistException();

		} catch (Exception e) {

			throw new DataBaseErrorException(e);
		} finally {
			disconnect();
		}
	}

}
