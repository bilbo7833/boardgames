package com.arena.game;

public class Equipment {
	private MeleeWeapon meleeWeapon;
	private RangedWeapon rangedWeapon;
	private Armor armor;
	
	public Equipment() {
		this.meleeWeapon = null;
		this.rangedWeapon = null;
		this.armor = null;
		return;
	}
	
	public Equipment(MeleeWeapon meleeWeapon, RangedWeapon rangedWeapon, Armor armor) {
		this.meleeWeapon = meleeWeapon;
		this.rangedWeapon = rangedWeapon;
		this.armor = armor;
		return;
	}
	
	public MeleeWeapon getMeleeWeapon() {
		return this.meleeWeapon;
	}
	
	public void setMeleeWeapon(MeleeWeapon meleeWeapon) {
		this.meleeWeapon = meleeWeapon;
		return;
	}
	
	public RangedWeapon getRangedWeapon() {
		return rangedWeapon;
	}

	public void setRangedWeapon(RangedWeapon rangedWeapon) {
		this.rangedWeapon = rangedWeapon;
	}
	
	public Armor getArmor() {
		return this.armor;
	}
	
	public void setArmor(Armor armor) {
		this.armor = armor;
		return;
	}
}
