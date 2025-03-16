package com.foodie.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EventDTO {
	
	private String imageUrl;
	private String location;
	private String eventName;
	private Date startDate;
	private Date endDate;
	private long restaurentId;

}
