from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.gfx import Screen
from com.mtgames.utils import Debug

from com.sun.javafx.geom import Vec4f

class Trigger(EntityInterface):
    def __init__(self):
        pass

    def init(self, entity):
        pass

    def tick(self, entity):
        if entity.hasCollidedEntity("player"):
           Debug.log(entity.getProperties().getExecute(), Debug.DEBUG)
           entity.life = 0

    def render(self, entity):
        Screen.drawRectangle(entity.x+entity.xMin-Screen.xOffset, entity.y+entity.yMin-Screen.yOffset, entity.x+entity.xMax-Screen.xOffset, entity.y+entity.yMax-Screen.yOffset, Vec4f(1, 1, 1, 1))

