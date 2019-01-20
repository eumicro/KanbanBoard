package org.kanban.presenter.usermanagement.observer;

import org.kanban.model.User;

public interface IUserManagementObserver {

	public void onUserLogout(User u);

	public void onUserLogin(User u);

	public void onUserCreate(User u);

	public void beforeUserDelete(User u);
}
