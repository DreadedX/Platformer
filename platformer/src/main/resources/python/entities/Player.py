from com.mtgames.platformer.scripting.interfaces import PlayerInterface
from com.mtgames.platformer.gfx.shaders import TextureLoader

# from com.mtgames.utils import Debug

class Player(PlayerInterface):
    def __init__(self):
        self.input = 0
        self.maxHealth = 0
        self.armor = TextureLoader.loadTexture("/assets/graphics/entities/player/armor.png")

    def init(self, player):
        # Debug.log("Player speed: {}".format(player.getProperties().getSpeed()), Debug.DEBUG)
        self.input = player.getProperties().getInput()
        self.maxHealth = player.getProperties().getMaxHealth()

    def tick(self, player):
        self.godmode(player)

        if self.input.isPressed(57):
            print "9 is pressed"
            self.input.unset(57)

    def render(self, player, screen):
        if player.movingDir == 0:
            direction = True
        else:
            direction = False
        screen.renderEntity(player.x, player.y, self.armor, 16, direction)

    def godmode(self, player):
        player.life = self.maxHealth
        player.staggerTime = 0



