package org.kanban.presenter.usermanagement.observer;

import org.kanban.model.User;

public interface IUserManagementObservable {
	public void addObserver(IUserManagementObserver o);

	public void removeObserver(IUserManagementObserver o);

	public void onUserLogout(User u);

	public void onUserLogin(User u);

	public void onUserCreate(User u);

	public void beforeUserDelete(User u);
}
