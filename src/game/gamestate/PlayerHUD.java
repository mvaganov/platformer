package game.gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import engine.Res;
import game.entity.Player;

public class PlayerHUD {
	private Player player;
	
	private BufferedImage image;
	private Font font;
	
	public PlayerHUD(Player p)
	{
		player = p;
		try{
			image = Res.getImage("/hud/hud.gif");
			font = new Font("Arial", Font.PLAIN, 14);
		}catch(Exception e){ e.printStackTrace();}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, 0, 10, null);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(player.getHealth() + "/" + 
				player.getMaxHealth(), 30, 25);
		g.drawString((player.getFire()/100) + "/" + 
				(player.getMaxFire()/100), 30, 45);
	}
}
