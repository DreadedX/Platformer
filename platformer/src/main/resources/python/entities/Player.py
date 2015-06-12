from com.mtgames.platformer.scripting.interfaces import PlayerInterface
from com.mtgames.platformer.gfx.lwjgl import TextureLoader
from com.mtgames.platformer.settings import Settings
from com.mtgames.platformer.debug import Command
from com.mtgames.platformer.gfx.gui import GUI

from java.lang import Math
from com.sun.javafx.geom import Vec3f

class Player(PlayerInterface):
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


    def init(self, player):
        self.input = player.getProperties().getInput()
        self.speed = player.getProperties().getSpeed()
        self.maxHealth = player.getProperties().getMaxHealth()
        self.jumpWaitMax = player.getProperties().getJumpWait()
        self.jumpSpeed = player.getProperties().getJumpSpeed()
        self.dashSpeed = player.getProperties().getDashSpeed()
        self.dashWaitMax = player.getProperties().getDashWait()
        self.staggerLength = player.getProperties().getStaggerLength()

        player.life = self.maxHealth


    def tick(self, player):
        self.isStaggered = self.staggerTime != 0

        if not self.isStaggered:
            if player.hasCollided(0, 1) and self.jumpWait > self.jumpWaitMax:
                self.canJump = True
                self.jumpWait = 0

            if player.hasCollided(0, 1) and not self.canJump:
                self.jumpWait += 1

            if not player.hasCollided(0, 1):
                self.canJump = False

            if self.input.isPressed(Settings.KEY_JUMP) and self.canJump and player.isAlive():
                self.ya = -self.jumpSpeed
                self.canJump = False
                player.animationFrame = 0

            if self.input.isPressed(Settings.KEY_DASH) and self.canDash and player.isAlive():
                self.xaDash = self.dashSpeed
                self.canDash = False
                self.dashDeplete = True
                player.animationFrame = 0

            if self.input.isPressed(Settings.KEY_LEFT) and player.isAlive() and not self.isDashing:
                self.xa -= self.speed

            if self.input.isPressed(Settings.KEY_RIGHT) and player.isAlive() and not self.isDashing:
                self.xa += self.speed

            if self.input.isPressed(Settings.KEY_TORCH) and player.isAlive():
                Command.execute("light torch %d %d {\"colour\":%d}" % (player.x, player.y, int((Math.random() * 0xffffff))))
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

        player.move(self.xa, self.ya)
        self.dash(player)
        self.ya = player.gravity(self.ya)

        if self.isDashing:
            for _ in range(10):
                Command.execute("light dashParticle %d %d" % (player.x, player.y))

        if player.hasCollidedEntity("BaseEnemy"):
            player.life -= 1
            self.staggerTime = self.staggerLength


    def render(self, player, screen):
        xtile = 0
        flipx = False

        if player.movingDir == 0:
            flipx = True

        if player.isJumping and not self.isDashing and not self.isStaggered:
            xtile = 4
        elif self.isDashing:
            xtile = 6
            player.animationFrame = 0
        elif self.isStaggered:
            xtile = 7
            player.animationFrame = 0

        xtile += player.animationFrame

        screen.renderEntity(player.x, player.y, self.textureID[xtile], 16, flipx)

        GUI.add(lambda : GUI.progressBar(80, 13, 16, 150, float(player.life) / self.maxHealth, Vec3f(0.4, 0.1, 0.1)))
        dashratio = float(self.dashWait)/self.dashWaitMax
        if dashratio == 1:
            colour = Vec3f(0.1, 0.1, 0.5)
        else:
            colour = Vec3f(0.1, 0.1, 0.4)

        GUI.add(lambda : GUI.progressBar(screen.width-80, 12, 16, 150, dashratio, colour))

        # TODO: Temp death code
        if not player.isAlive():
            GUI.add(lambda : GUI.textBox("You died!", ""))


    def dash(self, player):
        if self.dashWait >= self.dashWaitMax:
            self.dashWait = self.dashWaitMax
        else:
            self.dashWait += 1

        self.canDash = self.dashWait == self.dashWaitMax and not player.hasCollided(0, 1)

        if self.dashDeplete:
            self.dashWait -= 5

        if self.dashWait <= 0:
            self.dashDeplete = False

        if self.xaDash == 0:
            return

        if player.hasCollided(-1, 0) or player.hasCollided(1, 0):
            self.staggerTime = self.staggerLength
            self.dashDeplete = False
            self.xaDash = 0
            player.life -= 10

        self.ya = 0
        player.gravityWait = 0

        if player.movingDir == 0:
            self.xa = -self.xaDash * self.speed
        else:
            self.xa = self.xaDash * self.speed

        if self.dashTime > 2:
            self.xaDash -= 1
            self.dashTime = 0

        self.dashTime += 1
