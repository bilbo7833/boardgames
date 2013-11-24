package com.arena.game;

public class Armor {
	// description
	private String name;
	private String description;
	
	// basic attributes
	private int armor;
	private int load;
	
	public Armor(String name, int armor, int load) {
		this.setName(name);
		this.setArmor(armor);
		this.setLoad(load);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}
}
