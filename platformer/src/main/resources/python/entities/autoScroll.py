from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.level import Level

class AutoScroll(EntityInterface):
    def __init__(self):
       self.speed = 0

    def init(self, entity):
        self.speed = entity.getProperties().getSpeed()

    def tick(self, entity):
        entity.x += self.speed

        entity.y = Level.entities.get(1).y

    def render(self, entity):
        pass
