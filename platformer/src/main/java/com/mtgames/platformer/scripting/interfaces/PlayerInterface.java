package com.mtgames.platformer.scripting.interfaces;

import com.mtgames.platformer.entities.Player;

public interface PlayerInterface {

	public void init(Player player);
	public void tick(Player player);
}
