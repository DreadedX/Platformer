from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.scripting import Jython
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
            entity.life = 0

            name = entity.getProperties().getExecute().split("/")

            Jython.execute("%s/triggers/%s.py" % (name[0], name[1]))

            # name = "python.demo.triggers.%s" % entity.getProperties().getExecute()
            # try:
                # exec "import %s" % name
                # exec "%s.main()" % name
            # except ImportError:
            #     Debug.log("The package %s does not exist" % name, Debug.DEBUG)
            # except SyntaxError:
            #     Debug.log("%s is not a valid package name" % name, Debug.DEBUG)

    def render(self, entity):
        Screen.drawRectangle(entity.x+entity.xMin-Screen.xOffset, entity.y+entity.yMin-Screen.yOffset, entity.x+entity.xMax-Screen.xOffset, entity.y+entity.yMax-Screen.yOffset, Vec4f(1, 1, 1, 1))
