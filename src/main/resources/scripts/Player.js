var x = 0;
var xa = 0;

function init() {
	JUMPWAIT = 2;
	JUMPSPEED = 5;
	DASHSPEED = 5;
	DASHWAIT = 72;
	STAGGERLENGTH = 20;

	speed = 2;
	xMin = -5;
	xMax = 4;
	yMin = -8;
	yMax = 7;
  print("This is a test");
}
	
function tick() {
  
  // DEMO: This creates a wind effect
  if (x >= 400) {
   xa = xa + 1;
    // x = -1+x;
  }
}
