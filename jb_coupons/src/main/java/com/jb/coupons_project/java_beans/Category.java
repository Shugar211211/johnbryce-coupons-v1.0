package com.jb.coupons_project.java_beans;

public enum Category {
	
	ARTS("arts and crafts"),
	AUTOMOTIVE("cars and auto accessories"),
	BABY("babies and kids"),
	BEAUTY("beauty and personal care"),
	BOOKS("books and reading"),
	COMPUTERS("computers and accessories"),
	CLOTHING("clothing"),
	ELECTRONICS("home and industrial electronics and equipment"),
	FASHION("fashion and accessories"),
	FINANCE("investments, finance, and insurance products"),
	FOOD("food and cooking"),
	HEALTH("health and personal care products"),
	HOME("home and garden"),
	LIFESTYLE("lifestyle, leisure, and recreational products"),
	MOVIES("movies and television"),
	MUSIC("music"),
	OUTDOORS("outdoors"),
	PETS("pets"),
	RESTAURANTS("restaurants"),
	SHOES("shoes and footwear"),
	SOFTWARE("software"),
	SPORTS("sport gear and equipment"),
	TRAVEL("travel and vacation deals"),
	TOOLS("diy and tools"),
	VIDEOGAMES("video games");
	
	// Category description
	private String description;
	
	/**
	 * Constructor - sets category description.
	 * @param description - category description
	 */
	private Category (String description) {
		this.description = description;
	}
	
	/**
	 * Getter for category description.
	 * @return category description
	 */
	public String getCategoryDescription() {
		return description;
	}
	
	/**
	 * Getter for category title
	 * @param description
	 * @return category
	 */
	public static Category getCategoryTitle(String description)
	{
		if(description.equals("arts and crafts")) return Category.ARTS;
		if(description.equals("cars and auto accessories")) return Category.AUTOMOTIVE;
		if(description.equals("beauty and personal care")) return Category.BABY;
		if(description.equals("arts and crafts")) return Category.BEAUTY;
		if(description.equals("books and reading")) return Category.BOOKS;
		if(description.equals("computers and accessories")) return Category.COMPUTERS;
		if(description.equals("clothing")) return Category.CLOTHING;
		if(description.equals("home and industrial electronics and equipment")) return Category.ELECTRONICS;
		if(description.equals("fashion and accessories")) return Category.FASHION;
		if(description.equals("investments, finance, and insurance products")) return Category.FINANCE;
		if(description.equals("food and cooking")) return Category.FOOD;
		if(description.equals("health and personal care products")) return Category.HEALTH;
		if(description.equals("home and garden")) return Category.HOME;
		if(description.equals("lifestyle, leisure, and recreational products")) return Category.LIFESTYLE;
		if(description.equals("movies and television")) return Category.MOVIES;
		if(description.equals("music")) return Category.MUSIC;
		if(description.equals("outdoors")) return Category.OUTDOORS;
		if(description.equals("pets")) return Category.PETS;
		if(description.equals("restaurants")) return Category.RESTAURANTS;
		if(description.equals("shoes and footwear")) return Category.SHOES;
		if(description.equals("software")) return Category.SOFTWARE;
		if(description.equals("sport gear and equipment")) return Category.SPORTS;
		if(description.equals("travel and vacation deals")) return Category.TRAVEL;
		if(description.equals("diy and tools")) return Category.TOOLS;
		if(description.equals("video games")) return Category.VIDEOGAMES;
		
		else return null;
	}
}
