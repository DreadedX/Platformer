package com.mtgames.platformer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	public final Key debug   = new Key();
	public final Key reload  = new Key();
	public final Key message = new Key();
	public final Key up      = new Key();
	public final Key down    = new Key();
	public final Key left    = new Key();
	public final Key right   = new Key();
	public final Key space   = new Key();
	public final Key shift   = new Key();

	public final Key throwItem = new Key();

	public InputHandler(Game game) {
		game.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_F3:
				debug.toggle(!debug.isPressed());
				break;

			case KeyEvent.VK_R:
				reload.toggle(true);

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

			case KeyEvent.VK_Q:
				throwItem.toggle(true);
				break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_R:
				reload.toggle(false);

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

			case KeyEvent.VK_Q:
				throwItem.toggle(false);
				break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public class Key {
		private boolean pressed = false;

		public boolean isPressed() {
			return pressed;
		}

		public void toggle(boolean isPressed) {
			pressed = isPressed;
		}
	}
}
