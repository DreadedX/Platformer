var x;
var xa;

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
	// this function get called every tick
  if (x >= 300) {
    // xa = -50;
    x = 299;
  }
}
