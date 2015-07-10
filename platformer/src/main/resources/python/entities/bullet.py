from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.entities import LightSource
from com.mtgames.platformer.settings import Properties

class Bullet(EntityInterface):
    def __init__(self):
        self.lightSource = 0
        self.speed = 0

    def init(self, entity):
        life = 100
        self.speed = entity.getProperties().getSpeed()*10

        properties = Properties("dashParticle")

        self.lightSource = LightSource(entity.x, entity.y, properties)
        entity.getProperties().getLevel().addLightSource(self.lightSource)

    def tick(self, entity):
        entity.move(self.speed, 0)
        self.lightSource.move(entity.x, entity.y)
        if entity.hasCollided(1, 0) or entity.hasCollided(-1, 0) or entity.hasCollidedEntity("baseEnemy"):
            entity.life = 0

    def render(self, entity, screen):
        pass
