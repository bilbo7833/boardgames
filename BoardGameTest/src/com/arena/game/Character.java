package com.arena.game;

public class Character {
	// description
	private String name;
	private String description;

	// basic attributes
	private int strength;
	private int toughness;
	private int agility;
	private int willPower;
	
	// secondary attributes
	private float initiative;
	private float meleeSkill;
	private float rangedWeaponSkill;
	private float loadCapacity;
	private int hitPoints;
	
	// experience
	private int experiencePoints = 0;
	private int level = 1;
	
	// equipment
	private MeleeWeapon meleeWeapon = null;
	private RangedWeapon rangedWeapon = null;
	private Armor armor = null;

	
	public Character(String name, int strength, int toughness, int agility, int willPower) {
		this.setName(name);	
	
		this.setStrength(strength);
		this.setToughness(toughness);
		this.setAgility(agility);
		this.setWillPower(willPower);
		
		this.setInitiative(agility);
		this.setMeleeSkill((strength + agility) / 2);
		this.setRangedWeaponSkill((agility + willPower) /2);
		this.setLoadCapacity((strength + toughness) / 2);
		this.setHitPoints(toughness * 2);
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
	
	public MeleeWeapon getMeleeWeapon() {
		return this.meleeWeapon;
	}
	
	public void setMeleeWeapon(MeleeWeapon meleeWeapon) {
		this.meleeWeapon = meleeWeapon;
		return;
	}
	
	public Armor getArmor() {
		return this.armor;
	}
	
	public void setArmor(Armor armor) {
		this.armor = armor;
		return;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public float getRangedWeaponSkill() {
		return rangedWeaponSkill;
	}

	public void setRangedWeaponSkill(float rangedWeaponSkill) {
		this.rangedWeaponSkill = rangedWeaponSkill;
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
	
	public RangedWeapon getRangedWeapon() {
		return rangedWeapon;
	}

	public void setRangedWeapon(RangedWeapon rangedWeapon) {
		this.rangedWeapon = rangedWeapon;
	}

	public void addExperiencePoints(int experiencePoints) {
		this.setExperiencePoints(this.getExperiencePoints() + experiencePoints);
		return;
	}
	
	private void updateLevel() {
		this.setLevel((int) Math.floor(0.5 + Math.sqrt(0.25 + 0.4 * this.experiencePoints)));
		return;
	}
	
	private float getCombatSkillBonus() {
		return (float) 0.5 * this.meleeSkill / 100;
	}
	
	private float getRangedCombatSkillBonus() {
		return (float) 0.5 * this.rangedWeaponSkill / 100;
	}
	
	private float getMeleeEvasionPenalty() {
		return (float) -0.25 * this.agility / 100;
	}
	
	private float getRangedCombatEvasionPenalty() {
		return (float) -0.1 * this.agility / 100;
	}
	
	public boolean meleeHit(Character opponent) {
		float chanceToHit = 0.5f;
		chanceToHit += this.getCombatSkillBonus();
		chanceToHit += opponent.getMeleeEvasionPenalty();
		return (Math.random() <= chanceToHit);
	}
	
	public boolean meleeCriticalHit(Character opponent) {
		float chanceToHit = 0.05f;
		chanceToHit += 0.05 * Math.max(0, (2 * this.meleeSkill - opponent.toughness) / 100);
		return (Math.random() <= chanceToHit);
	}
	
	private float getRangedCombatRangePenalty(float range) {
		float shortRange = this.getRangedWeapon().getShortRange();
		return 0.25f * Math.max(0, (range - shortRange) / shortRange);
	}
	
	private float getRangedCombatMovementPenalty(float movement) {
		return 0.25f * (movement / this.getMovingRange());
	}
	
	public boolean rangedCombatHit(Character opponent, float range, float movement) {
		float chanceToHit = 0.5f;
		chanceToHit += this.getRangedCombatSkillBonus() + this.getRangedCombatRangePenalty(range)
				+ this.getRangedCombatEvasionPenalty() + this.getRangedCombatMovementPenalty(movement);
		return (Math.random() <= chanceToHit);
	}
	
	public float getLoad() {
		return this.getMeleeWeapon().getLoad() + this.getArmor().getLoad();
	}
	
	public float getAgilityBonus() {
		return this.agility / 100;
	}
	
	public float getArmorPenalty() {
		return -Math.max(0, (this.getLoad() - this.getLoadCapacity()) / 100);
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
	
	private float getArmorReduction() {
		return - this.getArmor().getArmor();
	}
	
	private float getStrengthModifier() {
		int minStrength = this.getMeleeWeapon().getMinStrength();
		int basicDamage = this.getMeleeWeapon().getBasicDamage();
		return ((this.strength < minStrength) ? 2 : 0.5f) * (this.strength / minStrength - 1) * basicDamage;
	}
	
	private float getChargeBonus(float chargingRange) {
		return chargingRange * this.getMeleeWeapon().getCriticalHitMultiplier();
	}
	
	public float getMeleeDamage(Character opponent, float chargingRange) {
		float basicDamage = this.getMeleeWeapon().getBasicDamage();
		float damageRange = this.getMeleeWeapon().getDamageRange();
		float strengthModifier = this.getStrengthModifier();
		float criticalHitMultiplier = (this.meleeCriticalHit(opponent) ?
				this.getMeleeWeapon().getCriticalHitMultiplier() : 1);
		return Math.max(0, basicDamage * criticalHitMultiplier + damageRange
				+ strengthModifier + opponent.getArmorReduction() + this.getChargeBonus(chargingRange));
	}
}

