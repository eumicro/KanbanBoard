package org.kanban.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries(value={
		@NamedQuery(name="User.findAll",query="SELECT u FROM User u"),
		@NamedQuery(name="User.checkLogin",query="SELECT u.hashedPassword FROM User u WHERE u.name=:username"),
		@NamedQuery(name="User.findByName",query="SELECT u FROM User u WHERE u.name=:username"),
		@NamedQuery(name="User.findBeginsWithName",query="SELECT u FROM User u WHERE u.name LIKE :username"),
		@NamedQuery(name="User.findById",query="SELECT u FROM User u WHERE u.id=:userid")
		
})
public class User implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2185624364229132314L;
	
	@Expose
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Expose
	@Column(unique=true,nullable=false,updatable=false)
	private String name;
	// will not be read by gson
	@Column(name = "PASSWORD",nullable=false)
	private String hashedPassword;

	public User() {
		// for Serializable
	}
	public User(String name, String hashedPassword) {
		this.name = name;
		this.hashedPassword = hashedPassword;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long userId) {
		this.id = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		
		return "{id:"+id+",name:"+name+"}";
	}
}
