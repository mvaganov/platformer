package engine.entity;

public abstract class Enemy extends Mortal {
	
	protected int damage;
	
	/**
	 * initialize in here instead of the constructor. reflection is used, 
	 * so different constructors might be called than you expect.
	 */
	public abstract void init();
	
	public int getDamage() { return damage; }
}
