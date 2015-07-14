from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.scripting import Jython

class Trigger(EntityInterface):
    def __init__(self):
        pass

    def init(self, entity):
        pass

    def tick(self, entity):
        if entity.hasCollidedEntity("player"):
            entity.life = 0

            name = entity.getProperties().getExecute().split("/")

            Jython.execute("%s/triggers/%s.py" % (name[0], name[1]))

    def render(self, entity):
        pass
