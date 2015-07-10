package com.mtgames.platformer.scripting.interfaces;

import com.mtgames.platformer.entities.AdvancedEntity;

public interface EntityInterface {
	void init(AdvancedEntity entity);
	void tick(AdvancedEntity entity);
	void render(AdvancedEntity entity);
}
