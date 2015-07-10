from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.gfx import Screen
from com.mtgames.platformer.entities import LightSource
from com.mtgames.platformer.settings import Settings

from com.sun.javafx.geom import Vec4f

class FreeCamera(EntityInterface):
    def __init__(self):
        self.input = 0
        self.speed = 4

        self.lightSource = 0

    def init(self, entity):
        self.input = entity.getProperties().getInput()

        self.lightSource = LightSource(entity.x, entity.y, entity.getProperties())
        # entity.getProperties().getLevel().addLightSource(self.lightSource)

    def tick(self, entity):
        if self.input.isPressed(Settings.KEY_MOD):
            self.speed = 8
        else:
            self.speed = 4

        if self.input.isPressed(Settings.KEY_LEFT):
            entity.x -= self.speed

        if self.input.isPressed(Settings.KEY_RIGHT):
            entity.x += self.speed

        if self.input.isPressed(Settings.KEY_UP):
            entity.y -= self.speed

        if self.input.isPressed(Settings.KEY_DOWN):
            entity.y += self.speed

        # self.lightSource.move(entity.x, entity.y)

    def render(self, entity):
        Screen.drawRectangle(entity.x-4-Screen.xOffset, entity.y-4-Screen.yOffset, entity.x+4-Screen.xOffset, entity.y+4-Screen.yOffset, Vec4f(0, 0, 0, 0.5))
