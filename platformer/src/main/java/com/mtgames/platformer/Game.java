package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.level.tiles.Tile;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.InitInterface;
import com.mtgames.platformer.settings.Settings;
import com.mtgames.utils.Debug;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static com.mtgames.platformer.settings.Settings.*;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings({ "serial" }) public class Game implements Runnable {

	private static final boolean FPSUNLOCK = true;
	private static final int     TPS       = 60;
	public static final  int     WIDTH     = 608;
	public static final  int     HEIGHT    = WIDTH / 4 * 3;
	private static final String  NAME      = "Platformer";

	public static int scale;
	private int fps = 0;

	public static boolean editor = false;

	protected static boolean debug      = false;
	protected static boolean showDebug  = false;
	public static    boolean lightDebug = false;

	private int xOffset;
	private int yOffset;

	public static int mx;
	public static int my;

	private static int tile  = 3;
	private static int layer = 0;

	private Tile[] tilesSort = new Tile[Tile.tiles.length];

	public static boolean paused = false;

	private        GLFWErrorCallback       errorCallback;
	private        GLFWKeyCallback         keyCallback;
	private        GLFWMouseButtonCallback mouseButtonCallback;
	private static long                    window;

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
		new Input();

		new Settings();

		xOffset = Screen.width / 2;
		yOffset = Screen.height / 2;

		//		Load debug level
		Command.queue("load base/debug_level");
		//		Command.exec("load white");
		Command.execute();

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

		glfwSetKeyCallback(window, keyCallback = Input.keyCallback);
		glfwSetMouseButtonCallback(window, mouseButtonCallback = Input.mouseButtonCallback);

		ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(window, (GLFWvidmode.width(videoMode) - (WIDTH * scale)) / 2, (GLFWvidmode.height(videoMode) - (HEIGHT * scale)) / 2);
//		THIS IS ONLY HERE TO MAKE DEBUGGING EASIER ON TWO MONITORS
		if (debug) {
			glfwSetWindowPos(window, (GLFWvidmode.width(videoMode) - (WIDTH * scale)) / 2 + 1920, (GLFWvidmode.height(videoMode) - (HEIGHT * scale)) / 2);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
//		glfwSwapInterval(1);
		glfwShowWindow(window);

		Debug.log("Initializing jython", Debug.INFO);
		InitInterface ii = (InitInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.InitInterface", "Init.py");
		if (!ii.init()) {
			Debug.log("Failed to initialize jython", Debug.ERROR);
		}

		if (editor) {
			Command.queue("lighting");
			Command.execute();
		}
	}

	protected synchronized void start() {
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
			mouseButtonCallback.release();
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

		Screen.initLight();

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
				tick();
				Command.execute();
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

				glfwSwapBuffers(window);
				glfwPollEvents();
				glFlush();
			}

			if (Input.isPressed(KEY_PAUSE)) {
				Command.queue("pause");
				Input.unset(KEY_PAUSE);
			}

			if (Input.isPressed(KEY_DEBUG) && debug) {
				showDebug = !showDebug;
				Input.unset(KEY_DEBUG);
			}

			/* Determine current fps */
			if (System.currentTimeMillis() - lastTimerShort >= 100) {
				lastTimerShort += 100;
				fps = (int) ((frames) / ((System.currentTimeMillis() - lastTimer) / 1000d) + 0.5d);
			}

			DoubleBuffer mxRaw = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer myRaw = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, mxRaw, myRaw);

			mxRaw.rewind();
			myRaw.rewind();

			mx = (int) mxRaw.get();
			my = (int) myRaw.get();

			mx = mx / scale;
			my = my / scale;

			if (mx < 0) {
				mx = 0;
			}
			if (my < 0) {
				my = 0;
			}
			if (mx > WIDTH) {
				mx = WIDTH;
			}
			if (my > HEIGHT) {
				my = HEIGHT;
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				Debug.log(frames + " Frames, " + ticks + " Ticks", Debug.INFO);
				frames = 0;
				ticks = 0;
			}
		}
	}

	protected void tick() {
		if (!paused) {
			Level.tick();
		}

		if (editor) {
			if (!Input.isPressed(KEY_TILE_SELECT) && !paused) {
				if (Input.isPressed(KEY_TILE_PLACE)) {
					if (layer == 0) {
						Level.tiles0[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = tile;
					}
					if (layer == 1) {
						Level.tiles[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = tile;
					}
				}
				if (Input.isPressed(KEY_TILE_REMOVE)) {
					if (layer == 0) {
						Level.tiles0[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = 1;
					}
					if (layer == 1) {
						Level.tiles[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = 1;
					}
				}
			}

			if (Input.isPressed(KEY_LAYER0)) {
				layer = 0;
				Level.renderLayer0 = true;
				Level.renderLayer = false;
			} else if (Input.isPressed(KEY_LAYER1)) {
				layer = 1;
				Level.renderLayer0 = false;
				Level.renderLayer = true;
			} else {
				Level.renderLayer0 = true;
				Level.renderLayer = true;
			}

			if (!paused	&& Input.isPressed(KEY_TILE_SELECT)) {
				tilesSort = new Tile[Tile.tiles.length];

				System.arraycopy(Tile.tiles, 0, tilesSort, 0, tilesSort.length);

				Arrays.sort(tilesSort, Tile.TileNameComparator);
			}
		}
	}

	protected void render() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (Level.entities.size() > 0) {
			if (Level.entities.get(0).x > xOffset + Screen.width /2 + 50) {
				xOffset = Level.entities.get(0).x - (Screen.width /2 + 50);
			}

			if (Level.entities.get(0).x < xOffset + Screen.width / 2 - 50) {
				xOffset = Level.entities.get(0).x - (Screen.width / 2 - 50);
			}

			if (Level.entities.get(0).y > yOffset + Screen.height /2 + 50) {
				yOffset = Level.entities.get(0).y - ((Screen.height / 2) + 50);
			}

			if (Level.entities.get(0).y < yOffset + Screen.height / 2 - 50) {
				yOffset = Level.entities.get(0).y - (Screen.height /2 - 50);
			}
		}

		Level.renderBackground();
		Level.renderTiles(xOffset, yOffset);
		Level.renderEntities();

		Screen.renderLightFBO();

		if (Input.isPressed(KEY_MESSAGE)) {
//			GUI.textBox("The fox", "The quick brown fox jumps over the lazy dog.");
			GUI.textBox(Level.name, Level.description + "|By: " + Level.author);
		}

		GUI.render();

		if (paused) {
			Font.render("Paused", Screen.xOffset + Screen.width - 49, Screen.yOffset + 1);
			GUI.buttonText(WIDTH / 2, 199, "Resume", new Vec3f(0.1f, 0.5f, 0.1f), () -> Command.queue("pause"));
			GUI.buttonText(WIDTH / 2, 214, "Restart", new Vec3f(0.1f, 0.5f, 0.5f), () -> {
				Command.queue("reload");
				Command.queue("pause");
			});
			GUI.buttonText(WIDTH / 2, 229, "Quit", new Vec3f(0.5f, 0.5f, 0.1f), () -> Command.queue("exit"));
			Screen.drawRectangle(0, 0, WIDTH, HEIGHT, new Vec4f(1.0f, 1.0f, 1.0f, 0.1f));
		}

		if (showDebug) {
			Font.render("fps: " + fps, Screen.xOffset + 1, Screen.yOffset + 1);
			Font.render("x: " + Level.entities.get(0).x + " y: " + Level.entities.get(0).y, Screen.xOffset + 1, Screen.yOffset + 11);
			Font.render("mx: " + mx + " my: " + my, Screen.xOffset + 1, Screen.yOffset + 21);
		}

		GUI.render();

		if (editor) {
			int mxBox = (mx + Screen.xOffset) >> 4;
			int myBox = (my + Screen.yOffset) >> 4;

			if (!paused && !Input.isPressed(KEY_TILE_SELECT)) {
				Tile.tiles[tile].render(mxBox << 4, myBox << 4);
				Screen.drawRectangle((mxBox << 4) - Screen.xOffset, (myBox << 4) - Screen.yOffset, (mxBox << 4) + 16 - Screen.xOffset,
						(myBox << 4) + 16 - Screen.yOffset, new Vec4f(1.0f, 1.0f, 1.0f, 0.3f));
				if (!showDebug) {
					Font.render(Tile.tiles[tile].getName(), Screen.xOffset + 1, Screen.yOffset + 1);
				}
				Font.render("Layer " + layer, Screen.width -56+ Screen.xOffset, Screen.yOffset + 1);
			}

			if (!paused	&& Input.isPressed(KEY_TILE_SELECT)) {
				for (int i = 0; i < tilesSort.length; i++) {
					if (tilesSort[i] == null) {
						return;
					}
					final int finalI = i;
					GUI.button(8 + 16 * (finalI), 8, 16, 16, () -> tilesSort[finalI].render(16 * (finalI) + Screen.xOffset, Screen.yOffset), () -> tile = tilesSort[finalI].getId());
				}
			}

			if (paused) {
				GUI.buttonText(WIDTH / 2, 184, "Export", new Vec3f(0.2f, 0.2f, 0.7f), () -> Command.queue("export"));
			}

			GUI.render();
		}
	}
}
