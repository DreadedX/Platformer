package com.mtgames.firerpg.level;

import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public class BaseTile extends Tile {
	
	protected int	tileId;
	
	public BaseTile(int id, int x, int y, int levelColour) {
		super(id, false, false, levelColour);
		this.tileId = x + y * 32;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId);
	}
}
