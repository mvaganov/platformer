package engine;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioPlayer {

	public static boolean soundsAllowedToPlay = true;
	
	Clip clip;
	String name;
	public String getName(){return name;} 
	public AudioPlayer(String s) {
		name = s;
		try{
			InputStream is = getClass().getResourceAsStream(s);
			if(is == null){
				System.err.println("no audio resource: \""+s+"\"");
			}else{
				AudioInputStream ais = AudioSystem.getAudioInputStream(is);
						AudioFormat baseFormat = ais.getFormat();
						AudioFormat decodeFormat = new AudioFormat(
								AudioFormat.Encoding.PCM_SIGNED,
								baseFormat.getSampleRate(),
								16,
								baseFormat.getChannels(),
								baseFormat.getChannels() * 2,
								baseFormat.getSampleRate(),
								false
								);
				AudioInputStream dais = AudioSystem.getAudioInputStream(
						decodeFormat, ais);
				clip = AudioSystem.getClip();
				clip.open(dais);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	public boolean isPlaying(){return clip.isActive();}
	public void play() {
		play(false);
	}
	public void adjustGain(float gain){
		if(clip == null) return;
		if(gain != 0)
		{
			FloatControl gainControl = 
				    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(gain); // Reduce volume by gain decibels.
		}
	}
	public void play(boolean looped) {
		if(!soundsAllowedToPlay)
			return;
		if(clip == null) return;
		stop();
		if(looped)
		{
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop() {
		if(clip == null) return;
		if(clip.isRunning())
			clip.stop();
	}
	
	public void close() {
		if(clip == null) return;
		stop();
		clip.close();
	}
}
