package com.mtgames.platformer.level.tiles;

public class BaseTileSolid extends BaseTile {

	public BaseTileSolid(String name, int size, int part, boolean visible) {
		super(name, size, part, visible);
		this.solid = true;
	}

	public BaseTileSolid(String name, int size, int part) {
		super(name, size, part);
		this.solid = true;
	}

	public BaseTileSolid(String name, boolean visible) {
		super(name, visible);
		this.solid = true;
	}

	public BaseTileSolid(String name) {
		super(name);
		this.solid = true;
	}
}
