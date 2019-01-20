package org.kanban.presenter.usermanagement;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kanban.model.User;
import org.kanban.presenter.database.DataBaseInterface;
import org.kanban.presenter.database.MySQLDataBase;
import org.kanban.presenter.database.exceptions.DataBaseErrorException;
import org.kanban.presenter.database.exceptions.DataBaseNotAvailableException;
import org.kanban.presenter.database.exceptions.InvalidDBInputException;
import org.kanban.presenter.kanbanmanagement.exceptions.BoardDoesNotExistException;
import org.kanban.presenter.kanbanmanagement.exceptions.UserPermissionDeniedException;
import org.kanban.presenter.usermanagement.exceptions.InvalidUserDataException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyExistsException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyLoggedOutException;
import org.kanban.presenter.usermanagement.exceptions.UserAlreadyloggedInException;
import org.kanban.presenter.usermanagement.exceptions.UserDoesNotExistException;
import org.kanban.presenter.usermanagement.observer.IUserManagementObservable;
import org.kanban.presenter.usermanagement.observer.IUserManagementObserver;

/**
 * user management...
 * 
 * @author Eugen
 *
 */
public class UserManagement implements IUserManagementObservable {
	private static UserManagement instance;
	private DataBaseInterface db;
	private List<User> onlineUsers;
	private static Logger log;
	private List<IUserManagementObserver> observer;

	private UserManagement() throws DataBaseNotAvailableException, DataBaseErrorException {
		onlineUsers = new ArrayList<User>();
		observer = new ArrayList<IUserManagementObserver>();
		db = MySQLDataBase.getInstance();
		log = Logger.getAnonymousLogger();
	}

	/**
	 * Singleton
	 * 
	 * @return singleton instance
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public static UserManagement getInstance() throws DataBaseNotAvailableException, DataBaseErrorException {
		return instance == null ? instance = new UserManagement() : instance;
	}

	/**
	 * adds user in a server side user list.
	 * 
	 * @param username
	 * @param pass
	 * @throws UserDoesNotExistException
	 * @throws UserAlreadyloggedInException
	 * @throws DataBaseNotAvailableException
	 * @throws InvalidDBInputException
	 * @throws InvalidUserDataException
	 * @throws DataBaseErrorException
	 */
	public void logIn(String username, String pass) throws UserDoesNotExistException, UserAlreadyloggedInException,
			DataBaseNotAvailableException, InvalidDBInputException, InvalidUserDataException, DataBaseErrorException {

		if (!isValidInput(username, pass)) {
			throw new InvalidUserDataException();
		}
		// try to log in
		String hashedPassword = getHashedPassword(pass);

		if (db.checkLogIn(username, hashedPassword)) { // checks if user exists
														// and throws
														// UserDoesNotExist if
														// not.
			if (isLoggedIn(username)) {
				// throw new UserAlreadyloggedInException();
				// nothing to do...
				return;
			} else {
				// get user from Database throws UserDoesNotExist if user does
				// not
				// exists (here redundandt)
				User user = db.getUserByName(username);
				onlineUsers.add(user);
			}

		} else {
			throw new InvalidDBInputException();
		}

	}

	/**
	 * 
	 * @param username
	 * @throws UserDoesNotExistException
	 * @throws UserAlreadyLoggedOutException
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public void logOut(User user) throws UserDoesNotExistException, UserAlreadyLoggedOutException,
			DataBaseNotAvailableException, DataBaseErrorException {
		if (onlineUsers.contains(user)) {
			onlineUsers.remove(user);
			return;
		} else {
			try {
				if (db.isUser(user.getName())) {
					throw new UserAlreadyLoggedOutException();
				} else {
					throw new UserDoesNotExistException();
				}
			} catch (InvalidDBInputException e) {
				// should not happen here...
				log.log(Level.WARNING, "Method error!", e);
			}
		}
	}

	public User createUser(String username, String password) throws UserAlreadyExistsException,
			InvalidUserDataException, InvalidDBInputException, DataBaseNotAvailableException, DataBaseErrorException {

		// check input
		if (!isValidInput(username, password)) {
			throw new InvalidUserDataException();
		}
		// check if user exists
		if (db.isUser(username)) {
			throw new UserAlreadyExistsException();
		}
		// hash the password with md5
		String md5password = getHashedPassword(password);
		// create user in model

		return db.createUser(username, md5password);

	}

	private boolean isValidInput(String username, String password) {
		// check user input..
		// && username.matches("^[a-zA-Z0-9]+")
		return username.length() >= 4 && username.length() < 32 && password.length() >= 2 && password.length() < 32;

	}

	public void deleteUser(String username, String password)
			throws UserDoesNotExistException, InvalidUserDataException, DataBaseNotAvailableException,
			DataBaseErrorException, UserPermissionDeniedException, BoardDoesNotExistException, InvalidDBInputException {
		User user = getUserByName(username);
		if (!user.getHashedPassword().equals(getHashedPassword(password))) {
			throw new UserPermissionDeniedException();
		}

		beforeUserDelete(user);
		db.deleteUser(user);
	}

	/**
	 * 
	 * @param user
	 * @return boolean
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public boolean isLoggedIn(User user)
			throws UserDoesNotExistException, DataBaseNotAvailableException, DataBaseErrorException {
		if (user == null) {
			return false;
		}
		if (isLoggedIn(user.getName())) {
			return true;
		} else {
			try {
				if (!db.isUser(user.getName())) {
					throw new UserDoesNotExistException();
				}
			} catch (InvalidDBInputException e) {
				// it should not happen here...
				log.log(Level.WARNING, "impossible error!", e);
			}
		}
		return false;
	}

	public static String getHashedPassword(String pass) {
		try {
			return md5(pass);
		} catch (NoSuchAlgorithmException e) {

			// it can not be handelt...
			log.log(Level.WARNING, "MD5-Algorithm error!", e);
			return null;
		}
	}

	/**
	 * local check if user is in the online list.
	 * 
	 * @param name
	 * @return
	 */
	private boolean isLoggedIn(String name) {
		log.info("isLoggedin called...");
		for (User user : onlineUsers) {
			if (user.getName().equals(name)) {
				log.info("User '" + name + "' is logged in. return true!");
				return true;
			}
		}
		log.info("User '" + name + "' is not logged in. return false!");
		return false;
	}

	/**
	 * gets a user if the id equals to the parameter. fast search in the online
	 * list first, then in the data base...
	 * 
	 * @param id
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws DataBaseNotAvailableException
	 */
	public User getUserById(Long id) throws UserDoesNotExistException, DataBaseNotAvailableException {
		// search in the online list first
		for (User onlineUser : onlineUsers) {
			if (onlineUser.getId().equals(id)) {
				return onlineUser;
			}
		}
		// then in the db
		return db.getUserById(id);
	}

	public User getUserByName(String name)
			throws UserDoesNotExistException, DataBaseNotAvailableException, DataBaseErrorException {
		// search in the online list first

		// then in the db
		return getOnlineUserByName(name) != null ? getOnlineUserByName(name) : db.getUserByName(name);
	}

	/**
	 * gets all users with names similar to the parameter 'String name' (e.g.
	 * begins with name%)
	 * 
	 * @param name
	 * @return
	 * @throws DataBaseNotAvailableException
	 * @throws DataBaseErrorException
	 */
	public List<User> getUsersLikeName(String name) throws DataBaseNotAvailableException, DataBaseErrorException {
		return db.getUsersLikeName(name);
	}

	/**
	 * checks if the given user name exists..
	 * 
	 * @param username
	 * @return
	 * @throws DataBaseNotAvailableException
	 * @throws InvalidDBInputException
	 * @throws DataBaseErrorException
	 */
	public boolean isUser(String username)
			throws DataBaseNotAvailableException, InvalidDBInputException, DataBaseErrorException {
		return db.isUser(username);
	}

	/**
	 * gets all logged in users.
	 * 
	 * @return List<User>
	 */
	public List<User> getOnlineUsers() {
		return onlineUsers;
	}

	/**
	 * md5 algorithm
	 * 
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String md5(String input) throws NoSuchAlgorithmException {

		String md5 = null;

		if (null == input) {
			return null;
		}

		// Create MessageDigest object
		MessageDigest digest = MessageDigest.getInstance("MD5");

		// Update input string in message digest
		digest.update(input.getBytes(), 0, input.length());

		// Converts message digest value in base 16 (hex)
		md5 = new BigInteger(1, digest.digest()).toString(16);

		return md5;
	}

	public void logOut(String username) throws UserDoesNotExistException, DataBaseNotAvailableException,
			UserAlreadyLoggedOutException, DataBaseErrorException {
		User user = getUserByName(username);
		logOut(user);
	}

	public User getOnlineUserByName(String username) {
		for (User user : onlineUsers) {
			if (user.getName().equals(username)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public void addObserver(IUserManagementObserver o) {
		observer.add(o);

	}

	@Override
	public void removeObserver(IUserManagementObserver o) {
		observer.remove(o);

	}

	@Override
	public void onUserLogout(User u) {
		for (IUserManagementObserver iUserManagementObserver : observer) {
			iUserManagementObserver.onUserLogout(u);
		}

	}

	@Override
	public void onUserLogin(User u) {
		for (IUserManagementObserver iUserManagementObserver : observer) {
			iUserManagementObserver.onUserLogin(u);
		}
	}

	@Override
	public void onUserCreate(User u) {
		for (IUserManagementObserver iUserManagementObserver : observer) {
			iUserManagementObserver.onUserCreate(u);
		}
	}

	@Override
	public void beforeUserDelete(User u) {
		for (IUserManagementObserver iUserManagementObserver : observer) {
			iUserManagementObserver.beforeUserDelete(u);
		}

	}
}
