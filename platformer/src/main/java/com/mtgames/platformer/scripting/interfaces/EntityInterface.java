package com.mtgames.platformer.scripting.interfaces;

import com.mtgames.platformer.entities.AdvancedEntity;
import com.mtgames.platformer.gfx.Screen;

public interface EntityInterface {
	void init(AdvancedEntity entity);
	void tick(AdvancedEntity entity);
	void render(AdvancedEntity entity, Screen screen);
}
