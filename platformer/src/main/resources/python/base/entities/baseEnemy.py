from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.level import Level
from com.mtgames.platformer.gfx import Screen
from com.mtgames.platformer.gfx.lwjgl import TextureLoader
from com.mtgames.platformer.entities import LightSource

class BaseEnemy(EntityInterface):
    def __init__(self):
        self.speed = 0
        self.jumpSpeed = 0

        self.lightSource = 0

        self.ya = 0

        self.textureID = TextureLoader.loadTextureArray("/assets/graphics/entities/base/baseEnemy", 6)

    def init(self, entity):
        self.speed = entity.getProperties().getSpeed()
        self.jumpSpeed = entity.getProperties().getJumpSpeed()

        self.lightSource = LightSource(entity.x, entity.y, entity.getProperties())
        Level.addLightSource(self.lightSource)

    def tick(self, entity):
        xa = 0

        if entity.x > Level.entities.get(0).x + 20:
            entity.movingDir = 0

        if entity.x < Level.entities.get(0).x - 20:
            entity.movingDir = 1

        if not entity.hasCollided(3, -16) and entity.hasCollided(3,0):
            self.ya = -self.jumpSpeed
            xa += self.speed
        elif not entity.hasCollided(-3, -16) and entity.hasCollided(-3, 0):
            self.ya = -self.jumpSpeed
            xa -= self.speed
        # elif entity.hasCollided(-3, 0):
        #     entity.movingDir = 1
        #     xa += self.speed
        # elif entity.hasCollided(3, 0):
        #     entity.movingDir = 0
        #     xa -= self.speed
        else:
            if entity.movingDir == 0:
                xa -= self.speed

            if entity.movingDir == 1:
                xa += self.speed

        entity.move(xa, self.ya)
        self.ya = entity.gravity(self.ya)
        self.lightSource.move(entity.x, entity.y)

    def render(self, entity):
        xtile = 0
        flipx = False

        if entity.movingDir == 0:
            flipx = True

        if entity.isJumping:
            xtile = 4

        xtile += entity.animationFrame

        Screen.renderEntity(entity.x, entity.y, self.textureID[xtile], 32, flipx)
