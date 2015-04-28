package com.mtgames.platformer.level.tiles;

class BaseSolidTile extends BaseTile {

	public BaseSolidTile(int id, String name, int size, int part, boolean visible) {
		super(id, name, size, part, visible);
		this.solid = true;
	}

	public BaseSolidTile(int id, String name, int size, int part) {
		super(id, name, size, part);
		this.solid = true;
	}

	public BaseSolidTile(int id, String name, boolean visible) {
		super(id, name, visible);
		this.solid = true;
	}

	public BaseSolidTile(int id, String name) {
		super(id, name);
		this.solid = true;
	}
}
