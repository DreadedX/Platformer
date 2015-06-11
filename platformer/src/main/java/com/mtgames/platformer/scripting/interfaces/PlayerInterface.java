package com.mtgames.platformer.scripting.interfaces;

import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.gfx.Screen;

public interface PlayerInterface {
	void init(Player player);
	void tick(Player player);
	void render(Player player, Screen screen);
}
