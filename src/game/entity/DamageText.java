package game.entity;

import java.awt.Color;

import engine.Vec2D;
import engine.entity.FloatingText;
import engine.gamestate.PopulatedScrollingGame;

public class DamageText extends FloatingText {
	public DamageText(Vec2D pos, int damage, PopulatedScrollingGame game)
	{
		super(pos, ""+(-damage), damage > 0?Color.red:Color.green, 500, 20);
		init(game);
	}
}
