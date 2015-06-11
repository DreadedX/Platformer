from com.mtgames.platformer.scripting.interfaces import InitInterface
from com.mtgames.utils import Debug

class Init(InitInterface):
    def init(self):
        Debug.log("Jython successfully initialized", Debug.INFO)
        return True
