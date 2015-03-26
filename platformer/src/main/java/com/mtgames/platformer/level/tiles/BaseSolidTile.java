package com.mtgames.platformer.level.tiles;

class BaseSolidTile extends BaseTile {

	public BaseSolidTile(int id, String name, int tileId) {
		super(id, name, tileId);
		this.solid = true;
	}
}
