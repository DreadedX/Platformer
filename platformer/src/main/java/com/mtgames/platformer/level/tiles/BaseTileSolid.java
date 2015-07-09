package com.mtgames.platformer.level.tiles;

public class BaseTileSolid extends BaseTile {

	public BaseTileSolid(String name, boolean visible, boolean external) {
		super(name, visible, external);
		this.solid = true;
	}

	public BaseTileSolid(String name, boolean external) {
		super(name, external);
		this.solid = true;
	}
}
