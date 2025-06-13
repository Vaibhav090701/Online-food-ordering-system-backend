package com.foodie.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryDTO {

	private long id;

	private String categoryDescription ;
	private String categoryImage;
	private String categoryName;
	
	private boolean deleted;

}
