package engine.entity;

import java.awt.Graphics2D;

import engine.Timer;
import game.entity.DamageText;
import game.entity.HitParticle;

public abstract class Mortal extends MOB {
	protected int health, maxHealth;
	protected boolean flinching;
	protected long flinchTimerStart;
	protected int flinchDurationMS = 300;
	protected int flinchFlashSpeedMS = 50;

	public void hit(int damage) {
		if(shouldDispose || flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) shouldDispose = true;
		flinching = true;
		flinchTimerStart = Timer.currentTime;
		if(damage > 0 && health > 0)
		{
			HitParticle hit = new HitParticle(pos);
			hit.init(game);
			game.addEntity(hit, false);
		}
		if(damage != 0){
			game.addEntity(new DamageText(pos, damage, game), false);
		}
	}

	public int getFlinchingMS()
	{
		return (int)((Timer.currentTime - flinchTimerStart) / 1000000);
	}
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getFlichDurationMS() {return flinchDurationMS;}
	public void setFlichDurationMS(int ms) {flinchDurationMS = ms;}
	public int getFlinchFlashSpeedMS() {return flinchFlashSpeedMS;}
	public void setFlinchFlashSpeedMS(int ms) {flinchFlashSpeedMS = ms;}
	public boolean isFlinching(){
		if(flinching) {
			if(getFlinchingMS() > flinchDurationMS)
				flinching = false;
		}
		return flinching;
	}
	/** @return true if this should be redrawn, taking flinch into account */
	public boolean isFlinchShowing(){
		if(isFlinching()){
			return (getFlinchingMS() / flinchFlashSpeedMS % 2 == 0);
		}
		return true;
	}
	public void draw(Graphics2D g) {
		if(isFlinchShowing()){
			super.draw(g);
		}
	}
}
