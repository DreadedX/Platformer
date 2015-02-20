package com.mtgames.firerpg;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	public InputHandler(Game game) {
		game.addKeyListener(this);
	}

	public class Key {
		private int numTimesPressed = 0;
		private boolean pressed = false;

		public int getNumTimesPressed() {
			return numTimesPressed;
		}

		public boolean isPressed() {
			return pressed;
		}

		public void toggle(boolean isPressed) {
			pressed = isPressed;
			if (isPressed)
				numTimesPressed++;
		}
	}

	public Key debug = new Key();
	public Key message = new Key();
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key space = new Key();
	public Key shift = new Key();

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_F3:
			debug.toggle(!debug.isPressed());
			break;

		case KeyEvent.VK_M:
			message.toggle(!message.isPressed());
			break;

		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			up.toggle(true);
			break;

		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			down.toggle(true);
			break;

		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			left.toggle(true);
			break;

		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			right.toggle(true);
			break;

		case KeyEvent.VK_SPACE:
			space.toggle(true);
			break;

		case KeyEvent.VK_SHIFT:
			shift.toggle(true);
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			up.toggle(false);
			break;

		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			down.toggle(false);
			break;

		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			left.toggle(false);
			break;

		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			right.toggle(false);
			break;

		case KeyEvent.VK_SPACE:
			space.toggle(false);
			break;

		case KeyEvent.VK_SHIFT:
			shift.toggle(false);
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}
}
