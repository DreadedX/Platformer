package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public abstract class Particle extends Entity {
	protected int		life;
	protected Screen	screen;
	protected int		x;
	protected int		y;
	
	public int			id	= level.particles.size();
	
	public Particle(Level level, int x, int y, int life) {
		super(level);
		this.life = life;
		this.x = x;
		this.y = y;
	}
	
	public void tick() {
		life--;
	}
	
	public boolean isAlive() {
		if (life >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean isOnScreen(Screen screen) {
		if (x - screen.xOffset < 0 || x - screen.xOffset >= screen.width || y - screen.yOffset < 0 || y - screen.yOffset >= screen.height) {
			return false;
		}
		
		return true;
	}
}
