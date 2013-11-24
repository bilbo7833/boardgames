package com.arena.game;

public class Weapon {
	// description
	private String name;
	private String description;
	
	// basic attributes
	private int basicDamage;
	private int damageRange;
	private int criticalHitMultiplier;
	private int load;
	
	public Weapon(String name, int basicDamage, int damageRange, int criticalHitMultiplier, int load) {
		this.setName(name);
		this.setBasicDamage(basicDamage);
		this.setDamageRange(damageRange);
		this.setCriticalHitMultiplier(criticalHitMultiplier);
		this.setLoad(load);
		return;
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

	public int getBasicDamage() {
		return basicDamage;
	}

	public void setBasicDamage(int basicDamage) {
		this.basicDamage = basicDamage;
	}

	public int getDamageRange() {
		return damageRange;
	}

	public void setDamageRange(int damageRange) {
		this.damageRange = damageRange;
	}

	public int getCriticalHitMultiplier() {
		return criticalHitMultiplier;
	}

	public void setCriticalHitMultiplier(int criticalHitMultiplier) {
		this.criticalHitMultiplier = criticalHitMultiplier;
	}

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}
}
