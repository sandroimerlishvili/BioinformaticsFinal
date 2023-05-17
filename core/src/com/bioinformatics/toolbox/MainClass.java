package com.bioinformatics.toolbox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class MainClass extends Game {

	// screens

	protected MainScreen mainScreen;
	protected ProteinScreen proteinScreen;

	// screen values

	public final static int MAINMENU = 0;
	public final static int PROTEIN = 1;

	// audio

	protected Music mainMusic;
	protected Sound clickSound;
	protected  Sound errorSound;

	// dnaSequence

	String dnaSequence;


	public void create() {

		Assets.loadTextures();
		Assets.loadAudio();
		Assets.manager.finishLoading();

		mainScreen = new MainScreen(this);
		setScreen(mainScreen);


	}

	public void changeScreen(int screen) {

		switch(screen) {

			case MAINMENU:

				proteinScreen = null;
				mainScreen = new MainScreen(this);
				this.setScreen(mainScreen);
				break;

			case PROTEIN:

				if (proteinScreen == null) {

					mainScreen = null;
					proteinScreen = new ProteinScreen(this, dnaSequence);
					this.setScreen(proteinScreen);
					break;

				} else {

					this.setScreen(proteinScreen);
					break;

				}

		}

	}



}
