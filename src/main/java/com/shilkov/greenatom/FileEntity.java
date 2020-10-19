package com.shilkov.greenatom;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity // This tells Hibernate to make a table out of this class
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "filetable")
public class FileEntity {
	@Id
	@Column(name = "id")
	@SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "fileuser")
	private String fileuser;
	
	@Column(name = "date")
	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFileuser() {
		return fileuser;
	}

	public void setFileuser(String fileuser) {
		this.fileuser = fileuser;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
