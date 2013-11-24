package com.arena.game;

public class MeleeWeapon extends Weapon {
	// basic attributes
	private int minStrength;
	
	public MeleeWeapon(String name, int basicDamage, int damageRange, int chriticalHitMultiplier,
			int load, int minStrength) {
		super(name, basicDamage, damageRange, chriticalHitMultiplier, load);
		this.minStrength = minStrength;
		return;
	}

	public int getMinStrength() {
		return minStrength;
	}

	public void setMinStrength(int minStrength) {
		this.minStrength = minStrength;
	}
}
