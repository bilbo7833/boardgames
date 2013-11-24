package com.arena.game;

public class SampleCharacters {
	
	// characters
	public Character lionel;
	public Character ronan;
	
	// weapons
	public MeleeWeapon axe;
	public MeleeWeapon shortSword;
	public RangedWeapon bow;
	
	// armor
	public Armor lightLeatherArmor;
	public Armor chainMail;
	
	
	public void sampleCharacters() {
		// create characters
		this.lionel = new Character("Lionel 'the Lion'", 50, 50, 40, 40);
		this.ronan = new Character("Ronan 'le bow'", 30, 45, 65, 50);
		
		// set experience points
		this.lionel.setExperiencePoints(500);
		this.ronan.setExperiencePoints(600);
		
		// create equipment and equip characters
		this.axe = new MeleeWeapon("Axe", 25, 10, 3, 50, 50);
		this.chainMail = new Armor("Chain Mail", 40, 40);
		this.lionel.getEquipment().setMeleeWeapon(this.axe);
		this.lionel.getEquipment().setArmor(this.chainMail);
		
		this.shortSword = new MeleeWeapon("Short Sword", 10, 5, 2, 20, 20);
		this.bow = new RangedWeapon("Bow", 20, 5, 2, 20, 0.5f, 5.0f);
		this.lightLeatherArmor = new Armor("Light Leather Armor", 20, 20);
		this.ronan.getEquipment().setMeleeWeapon(this.shortSword);
		this.ronan.getEquipment().setRangedWeapon(this.bow);
		this.ronan.getEquipment().setArmor(this.lightLeatherArmor);
	}
}
