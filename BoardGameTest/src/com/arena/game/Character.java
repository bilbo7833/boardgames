package com.arena.game;

public class Character {
	private String name;
	private String description;
	
	private PrimaryAttributes primaryAttributes;
	private SecondaryAttributes secondaryAttributes;
	
	private int experiencePoints = 0;
	private int level = 1;
	
	private Equipment equipment;
	
	public Character(String name, int strength, int toughness, int agility, int willPower) {
		this.setName(name);
		
		this.primaryAttributes = new PrimaryAttributes(strength, toughness, agility, willPower);
		this.secondaryAttributes = new SecondaryAttributes(this.primaryAttributes);
		this.equipment = new Equipment();
		return;
	}

	public int getExperiencePoints() {
		return this.experiencePoints;
	}	
	
	public void setExperiencePoints(int experiencePoints) {
		this.experiencePoints = experiencePoints;
		this.updateLevel();
		return;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public PrimaryAttributes getPrimaryAttributes() {
		return this.primaryAttributes;
	}
	
	public void setPrimaryAttributes(int strength, int toughness, int agility, int willPower) {
		this.primaryAttributes = new PrimaryAttributes(strength, toughness, agility, willPower);
		return;
	}
	
	public SecondaryAttributes getSecondaryAttributes() {
		return this.secondaryAttributes;
	}
	
	public void updateSecondaryAttributes() {
		this.secondaryAttributes = new SecondaryAttributes(this.primaryAttributes);
		return;
	}
	
	public Equipment getEquipment() {
		return this.equipment;
	}
	
	public void setEquipment(MeleeWeapon meleeWeapon, RangedWeapon rangedWeapon, Armor armor) {
		this.equipment = new Equipment(meleeWeapon, rangedWeapon, armor);
		return;
	}

	public void addExperiencePoints(int experiencePoints) {
		this.experiencePoints += experiencePoints;
		return;
	}
	
	private void updateLevel() {
		this.level = (int) Math.floor(0.5 + Math.sqrt(0.25 + 0.4 * this.experiencePoints));
		return;
	}
	
	
	/*
	 * Melee
	 */
	private float getMeleeSkillBonus() {
		return (float) 0.5 * this.secondaryAttributes.getMeleeSkill() / 100;
	}
	
	private float getMeleeEvasionPenalty() {
		return (float) -0.25 * this.primaryAttributes.getAgility() / 100;
	}
	
	public boolean meleeHit(Character opponent) {
		float chanceToHit = 0.5f;
		chanceToHit += this.getMeleeSkillBonus();
		chanceToHit += opponent.getMeleeEvasionPenalty();
		return (Math.random() <= chanceToHit);
	}
	
	public boolean meleeCriticalHit(Character opponent) {
		float chanceToHit = 0.05f;
		chanceToHit += 0.05 * Math.max(0, (2 * this.secondaryAttributes.getMeleeSkill()
				- opponent.primaryAttributes.getToughness()) / 100);
		return (Math.random() <= chanceToHit);
	}
	
	/*
	 * Ranged Combat
	 */
	private float getRangedCombatSkillBonus() {
		return (float) 0.5 * this.secondaryAttributes.getRangedCombatSkill() / 100;
	}	
	
	private float getRangedCombatEvasionPenalty() {
		return (float) -0.1 * this.primaryAttributes.getAgility() / 100;
	}	
	
	private float getRangedCombatRangePenalty(float range) {
		float shortRange = this.equipment.getRangedWeapon().getShortRange();
		return -0.25f * Math.max(0, (range - shortRange) / shortRange);
	}
	
	private float getRangedCombatMovementPenalty(float movement) {
		return -0.25f * (movement / this.getMovingRange());
	}
	
	public boolean rangedCombatHit(Character opponent, float range, float movement) {
		float chanceToHit = 0.5f;
		chanceToHit += this.getRangedCombatSkillBonus() + this.getRangedCombatRangePenalty(range)
				+ this.getRangedCombatEvasionPenalty() + this.getRangedCombatMovementPenalty(movement);
		return (Math.random() <= chanceToHit);
	}
	
	public boolean rangedCombatCriticalHit(Character opponent) {
		float chanceToHit = 0.05f;
		chanceToHit *= Math.max(0f, (2 * this.secondaryAttributes.getRangedCombatSkill()
				- opponent.primaryAttributes.getToughness()) / 100);
		return (Math.random() <= chanceToHit);
	}
	
	public float getLoad() {
		return this.equipment.getMeleeWeapon().getLoad() + this.equipment.getArmor().getLoad();
	}
	
	
	/*
	 * Movement
	 */
	public float getAgilityBonus() {
		return this.primaryAttributes.getAgility() / 100;
	}
	
	public float getArmorPenalty() {
		return -Math.max(0, (this.getLoad() - this.secondaryAttributes.getLoadCapacity()) / 100);
	}
	
	public float getMovingRange() {
		float basicRange = 2f;
		return basicRange + this.getAgilityBonus() + this.getArmorPenalty();
	}
	
	public float getRunningRange() {
		float runningRange = 1f;
		float runningBonus  = 0.5f * ((float) Math.random());
		return runningRange + runningBonus + this.getAgilityBonus() + 0.5f * this.getArmorPenalty();
	}
	
	public float getChargingRange() {
		float basicRange = (float) (1.5 + 0.5 * Math.random());
		return basicRange + this.getAgilityBonus() + 0.5f * this.getArmorPenalty();
	}	

	
	/*
	 * Damage
	 */
	private float getArmorReduction() {
		return - this.equipment.getArmor().getArmor();
	}
	
	private float getStrengthModifier() {
		int minStrength = this.equipment.getMeleeWeapon().getMinStrength();
		int basicDamage = this.equipment.getMeleeWeapon().getBasicDamage();
		return ((this.primaryAttributes.getStrength() < minStrength) ? 2 : 0.5f)
				* (this.primaryAttributes.getStrength() / minStrength - 1) * basicDamage;
	}
	
	private float getChargeBonus(float chargingRange) {
		return chargingRange * this.equipment.getMeleeWeapon().getCriticalHitMultiplier();
	}
	
	public float getMeleeDamage(Character opponent, float chargingRange) {
		float basicDamage = this.equipment.getMeleeWeapon().getBasicDamage();
		float damageRange = this.equipment.getMeleeWeapon().getDamageRange();
		float strengthModifier = this.getStrengthModifier();
		float criticalHitMultiplier = (this.meleeCriticalHit(opponent) ?
				this.equipment.getMeleeWeapon().getCriticalHitMultiplier() : 1);
		return Math.max(0, basicDamage * criticalHitMultiplier + damageRange
				+ strengthModifier + opponent.getArmorReduction() + this.getChargeBonus(chargingRange));
	}
	
	public float getRangedCombatDamage(Character opponent) {
		float basicDamage = this.equipment.getRangedWeapon().getBasicDamage();
		float damageRange = this.equipment.getRangedWeapon().getDamageRange();
		float criticalHitMultiplier = (this.meleeCriticalHit(opponent) ?
				this.equipment.getRangedWeapon().getCriticalHitMultiplier() : 1);
		return Math.max(0, basicDamage * criticalHitMultiplier
				+ damageRange + opponent.getArmorReduction());
	}
}

