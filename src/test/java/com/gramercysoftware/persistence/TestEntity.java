package com.gramercysoftware.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name= "test_table")
public class TestEntity implements java.io.Serializable {
	private static final long serialVersionUID = -245468210340920653L;

	public enum Type { FOO, BAR, }
	
	private Integer id;
	private Type type;
    private String message;    
    private boolean isAvailable;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "type", length = 10, nullable = false)
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	@Column(name = "message", length = 50, nullable = true)
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Column(name = "isAvailable", nullable = false)
	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	@Override
	public String toString() {
		return "TestEntity [id=" + id + ", isAvailable=" + isAvailable
				+ ", message=" + message + ", type=" + type + "]";
	}
}
