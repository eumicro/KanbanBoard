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
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

import com.google.gson.annotations.Expose;

@Entity
public class Station implements Serializable {
	/**
	 * 
	 */
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
	private Short position;
	@Expose
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(referencedColumnName = "ID", name = "STATION_ID")
	@PrivateOwned

	private List<Task> tasks;

	public Station() {
		// for Serializable
	}

	public Station(String title, String description) {
		this.title = title;
		this.description = description;
		this.tasks = new ArrayList<Task>();
	}

	public Station(Station station) {
		this(station.getTitle(), station.getDescription());
		List<Task> copyTasks = new ArrayList<Task>();
		// copy tasks
		for (Task task : tasks) {
			copyTasks.add(new Task(task));
		}
		this.tasks = new ArrayList<Task>(copyTasks);
	}

	public void addTask(Task task) {
		this.tasks.add(task);
	}

	public void removeTask(Task task) {
		this.tasks.remove(task);
	}

	public boolean containsTask(Task task) {
		return tasks.contains(task);
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

	public Short getPosition() {
		return position;
	}

	public void setPosition(Short position) {
		this.position = position;
	}

	public Task getTaskById(Long task_id) {
		for (Task task : tasks) {
			if (task.getId().equals(task_id)) {
				return task;
			}
		}
		return null;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public boolean containsTasks() {
		return tasks.size() > 0;
	}

	public void moveTask(Task task, int position) {
		this.tasks.remove(task);
		this.tasks.add(position, task);
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
		if (!(obj instanceof Station)) {
			return false;
		}
		Station other = (Station) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
