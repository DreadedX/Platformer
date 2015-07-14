from python import player

class Player(player.Player):
    def tick(self, entity):
        super(Player, self).tick(entity)
        entity.life = self.maxHealth
        self.staggerTime = 0
