package com.mtgames.firerpg;

import com.mtgames.firerpg.debug.Console;
import com.mtgames.firerpg.debug.Debug;
import com.mtgames.firerpg.gfx.Background;
import com.mtgames.firerpg.gfx.Font;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.gfx.SpriteSheet;
import com.mtgames.firerpg.gfx.gui.Hud;
import com.mtgames.firerpg.gfx.gui.Text;
import com.mtgames.firerpg.level.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Objects;

@SuppressWarnings({ "serial" }) class Game extends Canvas implements Runnable {

	private static final boolean FPSUNLOCK = true;
	private static final int     TPS       = 60;
	private static final int     WIDTH     = 304;
	private static final int     HEIGHT    = WIDTH / 4 * 3;
	private static final String  NAME      = "FireRPG";

	private static boolean debug = false;
	private static int scale;
	private final BufferedImage image         = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private final int[]         pixels        = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private       boolean       running       = false;
	private       int           tickCount     = 0;
	private       int           fps           = 0;
	private final String[]      consoleBuffer = { "", "", "", "" };
	private Screen screen;

	private InputHandler input;
	private Background   background1;
	private Background   background2;
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
		if (args.length != 0) {
			scale = Integer.parseInt(args[0]);
		} else {
			scale = 4;
		}

		if (args.length > 1) {
			new Console();
			Debug.priority = Debug.INFO;
			System.out.println(Debug.priority + " " + Debug.SCRIPT);
		}

		new Game().start();
	}

	void init() {
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		background1 = new Background("/forest1.png");
		background2 = new Background("/forest2.png");
		level = new Level("levels/debug_level.map", "scripts/Level.js", input);
	}

	synchronized void start() {
		running = true;
		new Thread(this).start();
	}

	public synchronized void stop() {
		running = false;
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
				Debug.msg(Debug.INFO, frames + " Frames, " + ticks + " Ticks");
				frames = 0;
				ticks = 0;
			}
		}
	}

	void tick() {
		tickCount++;
		level.tick();

		debug = input.debug.isPressed();

		//        Render console ingame
		if (!Objects.equals(Console.lines, consoleBuffer[0])) {
			consoleBuffer[3] = consoleBuffer[2];
			consoleBuffer[2] = consoleBuffer[1];
			consoleBuffer[1] = consoleBuffer[0];
			consoleBuffer[0] = Console.lines;
		}
	}

	void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		int xOffset = level.entities.get(0).x + 30 - (screen.width / 2);
		int yOffset = level.entities.get(0).y - 10 - (screen.height / 2);

		screen.renderBackground(background1, 2);
		screen.renderBackground(background2, 4);

		level.renderTiles(screen, xOffset, yOffset);

		level.renderParticles(screen);

		level.renderEntities(screen);

		Hud.render(screen);

		/* Debug text */
		if (debug) {
			Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1);
			Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 9);
		}

		if (input.message.isPressed()) {
			Text.textBox(screen, "Mission:", "It is your mission to just mess around a bit in this world and try to debug the game!");
		}

		Font.render(consoleBuffer[0], screen, screen.xOffset, screen.height + screen.yOffset - 8);
		Font.render(consoleBuffer[1], screen, screen.xOffset, screen.height + screen.yOffset - 16);
		Font.render(consoleBuffer[2], screen, screen.xOffset, screen.height + screen.yOffset - 24);
		Font.render(consoleBuffer[3], screen, screen.xOffset, screen.height + screen.yOffset - 32);

		for (int y = 0; y < screen.height; y++) {
			System.arraycopy(screen.pixels, y * screen.width, pixels, y * WIDTH, screen.width);
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
}
