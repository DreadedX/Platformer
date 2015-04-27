package com.mtgames.platformer.level.tiles;

class BaseSolidTile extends BaseTile {

	public BaseSolidTile(int id, String name, int size, boolean visible) {
		super(id, name, size, visible);
		this.solid = true;
	}
}
