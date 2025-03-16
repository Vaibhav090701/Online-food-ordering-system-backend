package com.foodie.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long eventId;
	private String imageUrl;
	private String location;
	private String eventName;
	private Date startDate;
	private Date endDate;
	
	@ManyToOne
	private Restaurant restaurant;

}
