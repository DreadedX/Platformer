package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.level.Level;
import com.mtgames.utils.Debug;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings({ "serial" }) public class Game implements Runnable {

	protected static final boolean FPSUNLOCK = true;
	protected static final int     TPS       = 60;
	public static final    int     WIDTH     = 608;
	public static final    int     HEIGHT    = WIDTH / 4 * 3;
	protected static final String  NAME      = "Platformer";

	public static int scale;
	protected int fps = 0;
	public static Screen screen;

	public static InputHandler input;
	public static Level        level;

	protected static boolean debug      = false;
	protected static boolean showDebug  = false;
	public static    boolean lightDebug = false;

	private int xOffset;
	private int yOffset;

	public static boolean paused = false;

	protected GLFWErrorCallback errorCallback;
	protected GLFWKeyCallback   keyCallback;
	protected long              window;

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

	protected void init() {
		screen = new Screen();
		input = new InputHandler();
		level = new Level();

		xOffset = screen.width / 2;
		yOffset = screen.height / 2;

		//		Initialize command system
		Command.set(level, screen);
		//		Load debug level
		Command.exec("load debug_level");
		//		Command.exec("load white");

		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		window = glfwCreateWindow(WIDTH * scale, HEIGHT * scale, NAME, NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(window, keyCallback = new InputHandler());

		ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(window, (GLFWvidmode.width(videoMode) - WIDTH) / 2, (GLFWvidmode.height(videoMode) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
//		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	private synchronized void start() {
		Thread main = new Thread(this);
		main.setName(NAME + " - Main");
		main.start();
	}

	public void run() {
		Debug.log("LWGJL version: " + Sys.getVersion(), Debug.DEBUG);

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

		glClearColor (0.0f, 0.0f, 0.0f, 0.5f);
		glClearDepth (1.0f);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH);
		glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		screen.initLight();

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH * scale, HEIGHT * scale, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Font.init();

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

			if (input.isPressed(GLFW_KEY_P)) {
				Command.exec("pause");
				input.set(GLFW_KEY_P, false);
			}

			if (input.isPressed(GLFW_KEY_F3) && debug) {
				showDebug = !showDebug;
				input.set(GLFW_KEY_F3, false);
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
	}

	protected void render() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (level.entities.size() > 0) {
			if (level.entities.get(0).x > xOffset + screen.width/2 + 50) {
				xOffset = level.entities.get(0).x - (screen.width/2 + 50);
			}

			if (level.entities.get(0).x < xOffset + screen.width / 2 - 50) {
				xOffset = level.entities.get(0).x - (screen.width / 2 - 50);
			}

			if (level.entities.get(0).y > yOffset + screen.height/2 + 50) {
				yOffset = level.entities.get(0).y - (screen.height / 2 + 50);
			}

			if (level.entities.get(0).y < yOffset + screen.height / 2 - 50) {
				yOffset = level.entities.get(0).y - (screen.height/2 - 50);
			}
		}

		level.renderBackground();
		level.renderTiles(xOffset, yOffset);
		level.renderEntities();

		screen.renderLightFBO(screen, level);

		GUI.render();

		if (showDebug) {
			Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1);
			Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 9);
		}

		if (input.isPressed(GLFW_KEY_M)) {
			GUI.add(() -> GUI.textBox("Debug text:", "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+~*[] ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+~*[] ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+~*[] ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+~*[] "));
		}

		glfwSwapBuffers(window);
		glfwPollEvents();
		glFlush();
	}
}
