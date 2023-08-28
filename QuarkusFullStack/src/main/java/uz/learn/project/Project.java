package uz.learn.project;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import uz.learn.user.User;

@Entity
@Table(
		name = "projects",
		uniqueConstraints = {@UniqueConstraint(
				columnNames = {"name", "user_id"})
		}
	)

public class Project extends PanacheEntity{
	
	@Column(nullable = false)
	public String name;
	
	@ManyToOne(optional = false)
	public User user;
	
	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	public ZonedDateTime created;
	
	@Version
	public int version;

}
