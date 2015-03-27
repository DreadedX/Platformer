package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.debug.Console;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.Hud;
import com.mtgames.platformer.gfx.gui.Text;
import com.mtgames.platformer.level.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

@SuppressWarnings({ "serial" }) class Game extends Canvas implements Runnable {

	private static final boolean FPSUNLOCK = true;
	private static final int     TPS       = 60;
	private static final int     WIDTH     = 608;
	private static final int     HEIGHT    = WIDTH / 4 * 3;
	private static final String  NAME      = "FireRPG";

	private static int scale;
	private final BufferedImage image   = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private final int[]         pixels  = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private       boolean       running = false;
	private       int           fps     = 0;
	private Screen screen;

	private InputHandler input;
	private Level        level;

	private Game() {
		setMinimumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
		setMaximumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
		setPreferredSize(new Dimension(WIDTH * scale, HEIGHT * scale));

		JFrame frame = new JFrame(NAME);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			scale = Integer.parseInt(args[0]);
		} else {
			scale = 2;
		}

		if (args.length > 1) {
			if (Objects.equals(args[1], "debug")) {
				Debug.priority = Debug.INFO;
				Debug.priority = Debug.DEBUG;
				Debug.debug = true;
			}
		}

		new Game().start();
	}

	void init() {
		screen = new Screen(WIDTH, HEIGHT);
		input = new InputHandler(this);
		level = new Level("scripts/Level.js", input);

//		Initialize command system
		Command.set(level, input, screen);
//		Load debug level
		Command.exec("load debug_level");

		if (Debug.debug) {
			new Console();
		}

	}

	synchronized void start() {
		running = true;
		new Thread(this).start();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000d / TPS;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		long lastTimerShort = System.currentTimeMillis();
		double delta = 0;

		init();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = FPSUNLOCK;

			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			// try{
			// Thread.sleep(2);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			if (shouldRender) {
				frames++;
				render();
			}

			/* Determine current fps */
			if (System.currentTimeMillis() - lastTimerShort >= 100) {
				lastTimerShort += 100;
				fps = (int) ((frames) / ((System.currentTimeMillis() - lastTimer) / 1000d) + 0.5d);
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				Debug.log(frames + " Frames, " + ticks + " Ticks", Debug.INFO);
				frames = 0;
				ticks = 0;
			}
		}
	}

	void tick() {
		level.tick();
	}

	void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		int xOffset = screen.width / 2;
		int yOffset = screen.height / 2;

		if (level.entities.size() > 0) {
			xOffset = level.entities.get(0).x + 30 - (screen.width / 2);
			yOffset = level.entities.get(0).y - 10 - (screen.height / 2);
		}

		level.renderBackground(screen);

		level.renderTiles(screen, xOffset, yOffset);

		level.renderParticles(screen);

		level.renderEntities(screen);

		screen.renderLighting();

		Hud.render(screen);

		/* Debug text */
		if (input.debug.isPressed() && Debug.debug) {
			Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1);
			Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 9);
		}

		if (input.message.isPressed()) {
			Text.textBox(screen, "Debug text:", "ABCDEFGHIJKLMNOPQRSTUVWXYZ      abcdefghijklmnopqrstuvwxyz      0123456789.,:;'\"!?$%()-=+/*[]");
		}

		for (int y = 0; y < screen.height; y++) {
			System.arraycopy(screen.pixels, y * screen.width, pixels, y * WIDTH, screen.width);
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
}
