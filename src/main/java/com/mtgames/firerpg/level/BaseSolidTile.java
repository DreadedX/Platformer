package com.mtgames.firerpg.level;

public class BaseSolidTile extends BaseTile {
	
	public BaseSolidTile(int id, int x, int y) {
		super(id, x, y);
		this.solid = true;
	}
}
