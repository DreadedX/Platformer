from com.mtgames.platformer.scripting.interfaces import PlayerInterface

class Player(PlayerInterface):
    def __init__(self):
        self.input = 0
        self.maxHealth = 0

    def init(self, player):
        print player.getProperties().getSpeed()
        self.input = player.getProperties().getInput()
        self.maxHealth = player.getProperties().getMaxHealth()

    def tick(self, player):
        # print("x: {}, y: {}".format(player.x, player.y))
        # GODMODE
        player.life = self.maxHealth

        if self.input.isPressed(57):
            print "9 is pressed"
