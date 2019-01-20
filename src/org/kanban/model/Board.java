package org.kanban.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;

import org.eclipse.persistence.annotations.PrivateOwned;

import com.google.gson.annotations.Expose;

@Entity
// select * from it15g01.BOARD b, it15g01.BOARD_USER bu WHERE bu.USER_ID="12"
// AND b.ID=bu.BOARD_ID;
@NamedQueries(value = {
		@NamedQuery(name = "Board.findBoardsByUserId", query = "SELECT b FROM Board b join b.users u WHERE u.id=:userid"),
		@NamedQuery(name = "Board.findById", query = "SELECT b FROM Board b WHERE b.id=:board_id"),
		@NamedQuery(name = "Board.findUsersOfBoard", query = "SELECT u FROM Board b join b.users u WHERE b.id=:board_id") })
public class Board implements Serializable {
	private static final long serialVersionUID = 4431119791543302205L;

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Expose
	private String title;
	@Expose
	private String description;
	@Expose
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "owner_user_id")
	private User owner;

	@Expose
	@ManyToMany
	// (cascade={CascadeType.MERGE,CascadeType.REFRESH})
	@JoinTable(name = "BOARD_USER", joinColumns = @JoinColumn(name = "BOARD_ID") , inverseJoinColumns = @JoinColumn(name = "USER_ID") )
	private List<User> users;
	@Expose
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(referencedColumnName = "id", name = "board_id")
	@PrivateOwned

	private List<Station> stations;

	public Board() {
		this("", "", null);
	}

	public Board(User user) {
		this("Kein Titel", "Keine Beschreibung", user);
	}

	public Board(String title, String description, User owner) {
		this.title = title;
		this.description = description;
		this.owner = owner;
		this.users = new ArrayList<User>();
		this.stations = new ArrayList<Station>();
		if (owner != null) {
			this.users.add(owner);
		}

	}

	public Board(Board board) {
		this(board.getTitle(), board.getDescription(), board.getOwner());
		this.stations = new ArrayList<Station>();
		for (Station station : board.getStations()) {
			stations.add(new Station(station));
		}
		this.users = new ArrayList<User>(board.getUsers());
	}

	public void addStation(Station station) {
		this.stations.add(station);
		station.setPosition((short) this.stations.indexOf(station));
	}

	public void removeStation(Station station) {
		this.stations.remove(station);
	}

	public void addUser(User newUser) {
		this.users.add(newUser);
	}

	public void removeUser(User user) {
		this.users.remove(user);
	}

	public boolean isUser(User user) {
		for (User boardUser : users) {
			if (user.equals(boardUser)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOwner(User user) {
		return user.equals(owner);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Station getStationById(Long station_id) {
		for (Station station : stations) {
			if (station.getId().equals(station_id)) {
				return station;
			}
		}
		return null;
	}

	public Task getTaskById(Long task_id) {
		for (Station station : stations) {
			if (station.getTaskById(task_id) != null) {
				return station.getTaskById(task_id);
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Board)) {
			return false;
		}
		Board other = (Board) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.users.remove(this.owner);
		this.owner = owner;
		this.users.add(this.owner);

	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public void addTask(Task newTask) {
		getStationByPosition((short) 0).addTask(newTask);
		newTask.setUpdatedDate();

	}

	private Station getStationByPosition(Short pos) {
		for (Station station : stations) {
			if (station.getPosition().equals(pos)) {
				return station;
			}
		}
		return null;
	}

	public void removeTask(Task task) {
		for (Station station : stations) {
			if (station.getTaskById(task.getId()) != null) {
				station.removeTask(task);
			}
		}

	}

	public void assignUserToTask(Task task, User assignee) {
		Task foundTask = getTaskById(task.getId());
		if (foundTask != null) {
			task.setAssignee(assignee);
		}
		task.setUpdatedDate();

	}

	public void moveTaskToStation(Task task, Station newStation) {
		for (Station oldStation : stations) {
			if (oldStation.containsTask(task)) {
				oldStation.removeTask(task);
				Station newStationInBoard = getStationById(newStation.getId());
				newStationInBoard.addTask(task);
				task.setUpdatedDate();
				return;
			}
		}
	}

	public void moveStation(Station station, int position) {
		this.stations.remove(station);
		this.stations.add(position, station);
		station.setPosition(Short.valueOf((short) position));
	}

	public List<Task> getTasks() {
		List<Task> allTasks = new ArrayList<Task>();
		for (Station station : stations) {
			allTasks.addAll(station.getTasks());
		}
		return allTasks;
	}

	@PreUpdate
	public void onUpdate() {

	}

}
