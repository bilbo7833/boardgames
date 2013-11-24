package com.arena.game;

public class PrimaryAttributes {
	private int strength;
	private int toughness;
	private int agility;
	private int willPower;
	
	public PrimaryAttributes(int strength, int toughness, int agility, int willPower) {
		this.strength = strength;
		this.toughness = toughness;
		this.agility = agility;
		this.willPower = willPower;
		return;
	}
	
	public int getWillPower() {
		return willPower;
	}

	public void setWillPower(int willPower) {
		this.willPower = willPower;
	}

	public int getAgility() {
		return agility;
	}

	public void setAgility(int agility) {
		this.agility = agility;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
}
