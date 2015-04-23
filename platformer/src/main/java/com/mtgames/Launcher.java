package com.mtgames;

import com.mtgames.platformer.Game;
import com.mtgames.utils.LauncherBase;

import java.io.IOException;

public class Launcher extends LauncherBase {

	public static void main(String[] args) throws IOException, NoSuchFieldException {
		launcher();

		Game.main(args);
	}
}
