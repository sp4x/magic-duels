package jmegraphic.hud;

import jmegraphic.GraphicObject;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapFontLoader;
import com.jmex.angelfont.BitmapText;
import com.jmex.angelfont.Rectangle;


/*
 * TESTO DA MOSTRARE A VIDEO
 */
public class Notification extends HudObject {
	BitmapText txt;
	float expireTime;

	public Notification(String name) {
		super(name);
		BitmapFont fnt = BitmapFontLoader.loadDefaultFont();

        txt = new BitmapText(fnt, false);
        Rectangle box = new Rectangle(10, -10, display.getWidth() - 20,
                        display.getHeight() - 20);
        txt.setBox(box);
        txt.setSize(32);
        txt.setDefaultColor(ColorRGBA.green.clone());
        
        this.width = box.width;
        this.height = box.height;
        this.getLocalTranslation().x = -width/2;
        this.getLocalTranslation().y = height/2;
        this.attachChild(txt);
        
        this.expireTime = -1;
	}
	
	public void setExpireTime(float time) {
		expireTime = time + Timer.getTimer().getTimeInSeconds();
	}
	
	public void setText(String text) {
		txt.setText(text);
	}
	
	public void setColour(ColorRGBA col) {
		txt.setDefaultColor(col);
	}

	@Override
	public void update() {
		txt.update();
	}
	
	@Override
	public boolean toRemove() {
		if (expireTime == -1)
			return false;
		float currentTime = Timer.getTimer().getTimeInSeconds();
		return (currentTime>=expireTime);
	}

}
