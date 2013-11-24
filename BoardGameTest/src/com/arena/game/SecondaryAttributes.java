package com.arena.game;

public class SecondaryAttributes {
	private float initiative;
	private float meleeSkill;
	private float rangedCombatSkill;
	private float loadCapacity;
	private int hitPoints;
	
	public SecondaryAttributes(PrimaryAttributes primaryAttributes) {
		this.initiative = primaryAttributes.getAgility();
		this.meleeSkill = (primaryAttributes.getStrength() + primaryAttributes.getAgility()) / 2;
		this.rangedCombatSkill = (primaryAttributes.getAgility() + primaryAttributes.getWillPower()) / 2;
		this.loadCapacity = (primaryAttributes.getStrength() + primaryAttributes.getToughness()) / 2;
		this.hitPoints = primaryAttributes.getToughness() * 2;
		return;
	}
	
	public int getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public float getLoadCapacity() {
		return loadCapacity;
	}

	public void setLoadCapacity(float loadCapacity) {
		this.loadCapacity = loadCapacity;
	}

	public float getRangedCombatSkill() {
		return rangedCombatSkill;
	}

	public void setRangedCombatSkill(float rangedCombatSkill) {
		this.rangedCombatSkill = rangedCombatSkill;
	}

	public float getMeleeSkill() {
		return meleeSkill;
	}

	public void setMeleeSkill(float meleeSkill) {
		this.meleeSkill = meleeSkill;
	}

	public float getInitiative() {
		return initiative;
	}

	public void setInitiative(float initiative) {
		this.initiative = initiative;
	}
}
