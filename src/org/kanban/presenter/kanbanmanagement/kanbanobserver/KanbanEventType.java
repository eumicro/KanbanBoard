package org.kanban.presenter.kanbanmanagement.kanbanobserver;

import com.google.gson.annotations.SerializedName;

public enum KanbanEventType {
	@SerializedName("CREATE_BOARD")
	CREATE_BOARD, @SerializedName("CREATE_STATION")
	CREATE_STATION, @SerializedName("CREATE_TASK")
	CREATE_TASK, @SerializedName("EDIT_BOARD")
	EDIT_BOARD, @SerializedName("EDIT_STATION")
	EDIT_STATION, @SerializedName("EDIT_TASK")
	EDIT_TASK, @SerializedName("DELETE_BOARD")
	DELETE_BOARD, @SerializedName("DELETE_STATION")
	DELETE_STATION, @SerializedName("DELETE_TASK")
	DELETE_TASK, @SerializedName("INVITE_USER")
	INVITE_USER, @SerializedName("UNINVITE_USER")
	UNINVITE_USER, @SerializedName("MOVE_TASK")
	MOVE_TASK, @SerializedName("MOVE_STATION")
	MOVE_STATION, @SerializedName("ASSIGN_USER_TO_TASK")
	ASSIGN_USER_TO_TASK
}
