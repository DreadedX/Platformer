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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

@SuppressWarnings({ "serial" }) public class Game extends Canvas implements Runnable {

	private static final boolean FPSUNLOCK = true;
	private static final int     TPS       = 60;
	public static final  int     WIDTH     = 608;
	public static final  int     HEIGHT    = WIDTH / 4 * 3;
	private static final String  NAME      = "Platformer";

	private static int scale;
	private final BufferedImage     image   = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private final int[]             pixels  = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private       AffineTransform   tx      = AffineTransform.getRotateInstance(Math.toRadians(Math.random() - 0.5), WIDTH / 2, HEIGHT / 2);
	private       AffineTransformOp op      = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
	private       boolean           running = false;
	private       int               fps     = 0;
	private Screen screen;

	public static InputHandler input;
	public static Level        level;

	public static boolean shakeCam = false;

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
			Game.scale = Integer.parseInt(args[0]);
		} else {
			Game.scale = 2;
		}

		if (args.length > 1) {
			if (Objects.equals(args[1], "debug")) {
				Debug.priority = Debug.DEBUG;
				Debug.debug = true;
			}
			if (Objects.equals(args[1], "log")) {
				Debug.priority = Debug.INFO;
			}
		}

		new Game().start();
	}

	private void init() {
		screen = new Screen();
		input = new InputHandler(this);
		level = new Level();

		//		Initialize command system
		Command.set(level, input, screen);
//		Load debug level
		Command.exec("load debug_level");

		if (Debug.debug) {
			new Console();
		}
	}

	private synchronized void start() {
		running = true;
		Thread main = new Thread(this);
		main.setName(NAME + " - Main");
		main.start();
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

	private void tick() {
		level.tick();

		if (shakeCam) {
			tx = AffineTransform.getRotateInstance(Math.toRadians(Math.random() - 0.5), WIDTH / 2, HEIGHT / 2);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		}
	}

	private void render() {
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

		level.renderLights(screen);

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
		if (shakeCam) {
			g.drawImage(op.filter(image, null), 0, 0, getWidth(), getHeight(), null);
		} else {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
		g.dispose();
		bs.show();
	}
}
