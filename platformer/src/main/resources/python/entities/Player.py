from com.mtgames.platformer.scripting.interfaces import EntityInterface
from com.mtgames.platformer.gfx.lwjgl import TextureLoader
from com.mtgames.platformer.settings import Settings
from com.mtgames.platformer.debug import Command
from com.mtgames.platformer.gfx.gui import GUI

from com.mtgames.platformer.entities import LightSource

from java.lang import Math
from com.sun.javafx.geom import Vec3f

class Player(EntityInterface):
    def __init__(self):
        self.input = 0
        self.speed = 0
        self.maxHealth = 0
        self.jumpWaitMax = 0
        self.jumpSpeed = 0
        self.dashSpeed = 0
        self.dashWaitMax = 0
        self.staggerLength = 0

        self.xa = 0
        self.ya = 0
        self.xaDash = 0
        self.jumpWait = 0
        self.dashWait = 0
        self.dashTime = 0
        self.staggerTime = 0

        self.canJump = False
        self.canDash = True
        self.isStaggered = False
        self.isDashing = False
        self.dashDeplete = False

        self.textureID = TextureLoader.loadTextureArray("/assets/graphics/entities/player", 8)

        self.lightSource = 0


    def init(self, entity):
        entity.collide = True
        entity.persistent = True

        self.input = entity.getProperties().getInput()
        self.speed = entity.getProperties().getSpeed()
        self.maxHealth = entity.getProperties().getMaxHealth()
        self.jumpWaitMax = entity.getProperties().getJumpWait()
        self.jumpSpeed = entity.getProperties().getJumpSpeed()
        self.dashSpeed = entity.getProperties().getDashSpeed()
        self.dashWaitMax = entity.getProperties().getDashWait()
        self.staggerLength = entity.getProperties().getStaggerLength()

        entity.life = self.maxHealth

        self.lightSource = LightSource(entity.x, entity.y, entity.getProperties())
        entity.getProperties().getLevel().addLightSource(self.lightSource)


    def tick(self, entity):
        self.isStaggered = self.staggerTime != 0

        if not self.isStaggered:
            if entity.hasCollided(0, 1) and self.jumpWait > self.jumpWaitMax:
                self.canJump = True
                self.jumpWait = 0

            if entity.hasCollided(0, 1) and not self.canJump:
                self.jumpWait += 1

            if not entity.hasCollided(0, 1):
                self.canJump = False

            if self.input.isPressed(Settings.KEY_JUMP) and self.canJump and entity.isAlive():
                self.ya = -self.jumpSpeed
                self.canJump = False
                entity.animationFrame = 0

            if self.input.isPressed(Settings.KEY_DASH) and self.canDash and entity.isAlive():
                self.xaDash = self.dashSpeed
                self.canDash = False
                self.dashDeplete = True
                entity.animationFrame = 0

            if self.input.isPressed(Settings.KEY_LEFT) and entity.isAlive() and not self.isDashing:
                self.xa -= self.speed

            if self.input.isPressed(Settings.KEY_RIGHT) and entity.isAlive() and not self.isDashing:
                self.xa += self.speed

            if self.input.isPressed(Settings.KEY_TORCH) and entity.isAlive():
                Command.queue("light torch %d %d {\"colour\":%d}" % (entity.x, entity.y, int((Math.random() * 0xffffff))))
                self.input.unset(Settings.KEY_TORCH)

        else:
            self.staggerTime -=1
            self.ya = 0

        self.isDashing = self.xaDash != 0

        if self.xa > 1:
            self.xa -= 2
        elif self.xa < -1:
            self.xa += 2
        elif self.xa == 1 or self.xa == -1:
            self.xa = 0

        if self.xa > self.speed and not self.isDashing:
            self.xa = self.speed
        elif self.xa < -self.speed and not self.isDashing:
            self.xa = -self.speed

        entity.move(self.xa, self.ya)
        self.dash(entity)
        self.ya = entity.gravity(self.ya)

        if self.isDashing:
            for _ in range(10):
                Command.queue("light dashParticle %d %d" % (entity.x, entity.y))

        if entity.hasCollidedEntity("baseEnemy"):
            entity.life -= 1
            self.staggerTime = self.staggerLength


        self.lightSource.move(entity.x, entity.y)


    def render(self, entity, screen):
        xtile = 0
        flipx = False

        if entity.movingDir == 0:
            flipx = True

        if entity.isJumping and not self.isDashing and not self.isStaggered:
            xtile = 4
        elif self.isDashing:
            xtile = 6
            entity.animationFrame = 0
        elif self.isStaggered:
            xtile = 7
            entity.animationFrame = 0

        xtile += entity.animationFrame

        screen.renderEntity(entity.x, entity.y, self.textureID[xtile], 16, flipx)

        GUI.add(lambda : GUI.progressBar(80, 13, 16, 150, float(entity.life) / self.maxHealth, Vec3f(0.4, 0.1, 0.1)))
        dashratio = float(self.dashWait)/self.dashWaitMax
        if dashratio == 1:
            colour = Vec3f(0.1, 0.1, 0.5)
        else:
            colour = Vec3f(0.1, 0.1, 0.4)

        GUI.add(lambda : GUI.progressBar(screen.width-80, 12, 16, 150, dashratio, colour))

        # TODO: Temp death code
        if not entity.isAlive():
            GUI.add(lambda : GUI.textBox("You died!", ""))


    def dash(self, entity):
        if self.dashWait >= self.dashWaitMax:
            self.dashWait = self.dashWaitMax
        else:
            self.dashWait += 1

        self.canDash = self.dashWait == self.dashWaitMax and not entity.hasCollided(0, 1)

        if self.dashDeplete:
            self.dashWait -= 5

        if self.dashWait <= 0:
            self.dashDeplete = False

        if self.xaDash == 0:
            return

        if entity.hasCollided(-1, 0) or entity.hasCollided(1, 0):
            self.staggerTime = self.staggerLength
            self.dashDeplete = False
            self.xaDash = 0
            entity.life -= 10

        self.ya = 0
        entity.gravityWait = 0

        if entity.movingDir == 0:
            self.xa = -self.xaDash * self.speed
        else:
            self.xa = self.xaDash * self.speed

        if self.dashTime > 2:
            self.xaDash -= 1
            self.dashTime = 0

        self.dashTime += 1
