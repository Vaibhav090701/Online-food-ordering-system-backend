package com.foodie.request;

import java.util.Date;

import lombok.Data;

@Data
public class CreateEventRequest {

	private String imageUrl;
	private String location;
	private String eventName;
	private Date startDate;
	private Date endDate;
	private long restaurentId;

}
