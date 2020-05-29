package com.mygdx.breakout.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.breakout.Breakout;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Breakout";
		config.width = 1280;
		config.height = 1020;
		new LwjglApplication(new Breakout(), config);
	}
}
