package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.opengl.TextureLoader;
import com.mtgames.utils.Debug;
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

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

@SuppressWarnings({ "serial" }) public class Game extends Canvas implements Runnable {

	private static final boolean FPSUNLOCK = true;
	private static final int     TPS       = 60;
	public static final  int     WIDTH     = 608;
	public static final  int     HEIGHT    = WIDTH / 4 * 3;
	private static final String  NAME      = "Platformer";

	public static int scale;
	private final BufferedImage     image   = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private final int[]             pixels  = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private       AffineTransform   tx      = AffineTransform.getRotateInstance(Math.toRadians(Math.random() - 0.5), WIDTH / 2, HEIGHT / 2);
	private       AffineTransformOp op      = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
	private       boolean           running = false;
	private       int               fps     = 0;
	private Screen screen;

	public static InputHandler input;
	public static Level        level;

	public static  boolean shakeCam = false;
	private static boolean debug    = false;

	private int xOffset;
	private int yOffset;

	public static boolean paused = false;

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback   keyCallback;
	private long              window;

	//	private Game() {
	//		setMinimumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
	//		setMaximumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
	//		setPreferredSize(new Dimension(WIDTH * scale, HEIGHT * scale));
	//
	//		JFrame frame = new JFrame(NAME);
	//
	//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	//		frame.setLayout(new BorderLayout());
	//
	//		frame.add(this, BorderLayout.CENTER);
	//		frame.pack();
	//
	//		frame.setResizable(true);
	//		frame.setLocationRelativeTo(null);
	//		frame.setVisible(true);
	//	}

	public static void main(String[] args) {
		if (Integer.getInteger("com.mtgames.scale") != null) {
			Game.scale = Integer.getInteger("com.mtgames.scale");
		} else {
			Game.scale = 1;
		}

		if (Integer.getInteger("com.mtgames.debug") == 0) {
			debug = true;
		}

		new Game().start();
	}

	private void init() {
		screen = new Screen();
		input = new InputHandler();
		level = new Level();

		xOffset = screen.width / 2;
		yOffset = screen.height / 2;

		//		Initialize command system
		Command.set(level, screen);
//		Load debug level
		Command.exec("load opengl");

		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		window = glfwCreateWindow(WIDTH * scale, HEIGHT * scale, "Hello World!", NULL, NULL);
		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(window, keyCallback = new InputHandler());

		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
		glfwShowWindow(window);
	}

	private synchronized void start() {
		running = true;
		Thread main = new Thread(this);
		main.setName(NAME + " - Main");
		main.start();
	}

	public void run() {
		Debug.log("LWGJL verion: " + Sys.getVersion(), Debug.DEBUG);

		try {
			init();
			loop();

			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			glfwTerminate();
			errorCallback.release();
		}

		System.exit(0);
	}

	private void loop() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000d / TPS;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		long lastTimerShort = System.currentTimeMillis();
		double delta = 0;

		GLContext.createFromCurrent();
		glMatrixMode(GL11.GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH * scale, HEIGHT * scale, 0, 1, -1);
		glMatrixMode(GL11.GL_MODELVIEW);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_TEXTURE_2D);

		Debug.log("OpenGL version: " + glGetString(GL_VERSION), Debug.DEBUG);

		while (glfwWindowShouldClose(window) == GL_FALSE) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = FPSUNLOCK;

			while (delta >= 1) {
				if (!paused) {
					tick();
				}
				ticks++;
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

//		if (shakeCam) {
//			tx = AffineTransform.getRotateInstance(Math.toRadians(Math.random() - 0.5), WIDTH / 2, HEIGHT / 2);
//			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
//		}
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (level.entities.size() > 0) {
			if (level.entities.get(0).x > xOffset + screen.width/2 + 50) {
				xOffset = level.entities.get(0).x - (screen.width/2 + 50);
			}

			if (level.entities.get(0).x < xOffset + screen.width/2 - 50) {
				xOffset = level.entities.get(0).x - (screen.width/2 - 50);
			}

			if (level.entities.get(0).y > yOffset + screen.height/2 + 50) {
				yOffset = level.entities.get(0).y - (screen.height/2 + 50);
			}

			if (level.entities.get(0).y < yOffset + screen.height/2 - 50) {
				yOffset = level.entities.get(0).y - (screen.height/2 - 50);
			}
		}

		if (input.isPressed(GLFW_KEY_P)) {
			Command.exec("pause");
			input.set(GLFW_KEY_P, false);
		}

//		screen.render(16, 16, TextureLoader.loadImage("/assets/graphics/tiles/bigBlock1.png"), 1, true, 1);
//		screen.render(32, 16, TextureLoader.loadImage("/assets/graphics/tiles/bigBlock1.png"), 1, true, 1);
		level.renderBackground(screen);
		level.renderTiles(screen, xOffset, yOffset);
		level.renderEntities(screen);

		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	private void renderOld() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		if (level.entities.size() > 0) {
			if (level.entities.get(0).x > xOffset + screen.width/2 + 50) {
				xOffset = level.entities.get(0).x - (screen.width/2 + 50);
			}

			if (level.entities.get(0).x < xOffset + screen.width/2 - 50) {
				xOffset = level.entities.get(0).x - (screen.width/2 - 50);
			}

			if (level.entities.get(0).y > yOffset + screen.height/2 + 50) {
				yOffset = level.entities.get(0).y - (screen.height/2 + 50);
			}

			if (level.entities.get(0).y < yOffset + screen.height/2 - 50) {
				yOffset = level.entities.get(0).y - (screen.height/2 - 50);
			}
		}

		level.renderBackground(screen);

		level.renderTiles(screen, xOffset, yOffset);

		level.renderParticles(screen);

		level.renderEntities(screen);

		level.renderLights(screen);

		screen.renderLighting();

		Hud.render(screen);

		/* Debug text */
		if (input.isPressed(GLFW_KEY_F3) && debug) {
			Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1);
			Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 9);
		}

//		Pausing the game TODO: should not be in render()
		if (input.isPressed(GLFW_KEY_P)) {
			Command.exec("pause");
			input.set(GLFW_KEY_P, false);
		}

		if (input.isPressed(GLFW_KEY_M)) {
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
