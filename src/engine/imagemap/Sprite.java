package engine.imagemap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import engine.Timer;
import engine.Vec2I;

public class Sprite {
	private SpriteResource src;
	private int animIndex;
	private int frameIndex;
	private int currentDelay;
	private int repetitions;
	private long timer;
	public Vec2I position = new Vec2I();
	public boolean flipped = false;
	public BufferedImage currentFrame;

	public SpriteResource getSource(){return src;}
	
	public Sprite(SpriteResource spriteData){
		this.src = spriteData;
		animIndex = -1;
	}
	
	public SpriteResource.AnimationDescription getAnimationDetails(){
		if(animIndex < 0)return null;
		return src.getAnimationData()[animIndex];
	}
	
	void refreshCurrentFrameData()
	{
		currentFrame = src.getTiles()[animIndex][frameIndex];
		currentDelay = src.getAnimationData()[animIndex].delay;
	}

	public void update(){
		timer += Timer.deltaTimeMS;
		if(timer > currentDelay) {
			timer -= currentDelay;
			frameIndex++;
			if(frameIndex >= src.getTiles()[animIndex].length){
				repetitions++;
				frameIndex = 0;
			}
			refreshCurrentFrameData();
		}
	}
	
	public void setState(int index)
	{
		if(index < 0 || index >= src.getTiles().length)return;
		SpriteResource.AnimationDescription srad;
		if(animIndex >= 0){
			srad = getAnimationDetails();
			if(srad.sound != null && srad.loop){
				srad.sound.stop();
			}
		}
		animIndex = index;
		frameIndex = 0;
		refreshCurrentFrameData();
		timer = 0;
		repetitions = 0;
		srad = getAnimationDetails();
		if(srad.sound != null){
			//System.out.println("playing "+srad.sound.getName());
			srad.sound.play(srad.loop);
		}
	}
	
	public int getCurrentState(){
		return animIndex;
	}
	
	public int getAnimationIndex(){return animIndex;}
	public int getFrameIndex(){return frameIndex;}
	public int getRepetitions(){return repetitions;}
	
	public void draw(Graphics2D g){
		Vec2I off = getAnimationDetails().offset;
		if(flipped) {
			int width = currentFrame.getWidth(),
				height = currentFrame.getHeight();
			g.drawImage(currentFrame,
					position.x -off.x,
					position.y +off.y, 
					-width, height, null);
		} else {
			g.drawImage(currentFrame, position.x+off.x, position.y+off.y, null);
		}
	}
}
