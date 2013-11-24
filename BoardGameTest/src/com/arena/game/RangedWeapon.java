package com.arena.game;

public class RangedWeapon extends Weapon {
	// basic attributes
	private float minRange;
	private float shortRange;
	
	public RangedWeapon(String name, int basicDamage, int damageRange,
			int chriticalHitModifier, int load, float minRange, float shortRange) {
		super(name, basicDamage, damageRange, chriticalHitModifier, load);
		this.setMinRange(minRange);
		this.setShortRange(shortRange);
	}

	public float getMinRange() {
		return minRange;
	}

	public void setMinRange(float minRange) {
		this.minRange = minRange;
	}

	public float getShortRange() {
		return shortRange;
	}

	public void setShortRange(float shortRange) {
		this.shortRange = shortRange;
	}
}
