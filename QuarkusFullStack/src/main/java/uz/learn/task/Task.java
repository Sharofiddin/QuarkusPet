package uz.learn.task;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import uz.learn.project.Project;
import uz.learn.user.User;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity{
	@Column(nullable = false)
	public String title;
	
	@Column(length = 100)
	public String description;
	
	public Integer priority;
	
	@ManyToOne(optional = false)
	public User user;
	
	@ManyToOne
	public Project project;
	
	@CreationTimestamp
	public ZonedDateTime created;
	
	@Version
	public int version;

}
