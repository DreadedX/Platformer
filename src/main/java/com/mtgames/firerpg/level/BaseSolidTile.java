package com.mtgames.firerpg.level;

public class BaseSolidTile extends BaseTile {
	
	public BaseSolidTile(int id, int x, int y, int levelColour) {
		super(id, x, y, levelColour);
		this.solid = true;
	}
}
