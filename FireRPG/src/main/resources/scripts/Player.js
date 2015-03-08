var x = 0;
var xa = 0;
var y = 0;
var ya = 0;

function init() {
	JUMPWAIT = 2;
	JUMPSPEED = 5;
	DASHSPEED = 5;
	DASHWAIT = 72;
	STAGGERLENGTH = 20;
    MAXHEALTH = 100;

	speed = 2;
	xMin = -5;
	xMax = 4;
	yMin = -8;
	yMax = 7;
}
	
function tick() {
  // DEMO: This creates a wind effect
  // if (x >= 400) {
  //  xa = xa + 1;
  // }
  
  // DEMO: Teleport back to start 
  // if (x >= 507) {
	// x = 22;
	// y = -104 + y;
  // }
  
//  DEMO: God mode
//  health = MAXHEALTH;
}
