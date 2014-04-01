package engine;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import engine.imagemap.SpriteResource;
import main.GameApp;

public class Res {
	private static Res rm = new Res();
	private HashMap<String, Object> resources = new HashMap<String, Object>();
	
	private Object safeGet(String name, @SuppressWarnings("rawtypes") Class t) {
		if(name == null || name.length() == 0)
			return null;
		Object o = resources.get(name);
		if(o != null)
		{
			if(o.getClass() != t)
				throw new IllegalStateException(
						"have object \""+name+"\", but it isn't type \'"+
				t+"\' as expected");
		} else try {
			if(t == AudioPlayer.class) {
				o = new AudioPlayer(name);
			} else if(t == BufferedImage.class){
				if(GameApp.DEBUG && !GameApp.isApplet){
					String n = "./resources"+name;
					System.out.println(n);
					o = ImageIO.read(new File(n).toURI().toURL());
					return o;
				}else{
					o = ImageIO.read(getClass().getResourceAsStream(name));
				}
			} else if(t == SpriteResource.class){
				o = new SpriteResource(name);
			}
		}catch(Exception e){e.printStackTrace();}
		if(o != null){
			resources.put(name, o);
		}
		return o;
	}
	
	public static void unload(String s) {
		rm.resources.remove(s);
	}
	
	public static AudioPlayer getSound(String s){
		return (AudioPlayer)rm.safeGet(s, AudioPlayer.class);
	}
	public static BufferedImage getImage(String s){
		return (BufferedImage)rm.safeGet(s, BufferedImage.class);
	}
	public static SpriteResource getSprite(String s){
		return (SpriteResource)rm.safeGet(s, SpriteResource.class);
	}
	
	public static String getLine(BufferedReader br) throws IOException{
		String str = br.readLine();
		if(str == null)
			return null;
		int commentIndex = str.indexOf("//");
		if(commentIndex >= 0){
			str = str.substring(0, commentIndex);
		}
		return str;
	}
	public static int getLineNumber(BufferedReader br) throws IOException{
		return Integer.parseInt(getLine(br).trim());
	}
	public static String[] getLineTokens(BufferedReader br) throws IOException
	{
		String line = getLine(br);
		if(line != null)
			return line.split( "\\s+");
		return null;
	}
	private static boolean isValidIntegerToken(String token){
		return token != null && token.length() != 0;
	}
	public static int[] getLineNumbers(BufferedReader br) throws IOException
	{
		String[] tokens = getLineTokens(br);
		if(tokens == null)
			return null;
		int validTokens = 0;
		for(int i=0;i<tokens.length;++i)
		{
			if(isValidIntegerToken(tokens[i])){
				validTokens++;
			}else{
				tokens[i] = null;
			}
		}
		int[] nums = new int[validTokens];
		validTokens = 0;
		for(int i=0;i<tokens.length;++i){
			if(isValidIntegerToken(tokens[i])){
				nums[validTokens]=Integer.parseInt(tokens[i]);
				validTokens++;
			}
		}
		return nums;
	}
}
