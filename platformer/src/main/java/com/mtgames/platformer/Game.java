package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.scripting.JythonFactory;
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
	public static Screen screen;

	public static InputHandler input;
	public static Level        level;

	protected static boolean debug      = false;
	private static   boolean showDebug  = false;
	public static    boolean lightDebug = false;

	private int xOffset;
	private int yOffset;

	public static int mx;
	public static int my;

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
		screen = new Screen();
		input = new InputHandler();
		level = new Level();

		new Settings();

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

		glfwSetKeyCallback(window, keyCallback = input.keyCallback);
		glfwSetMouseButtonCallback(window, mouseButtonCallback = input.mouseButtonCallback);

		ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(window, (GLFWvidmode.width(videoMode) - WIDTH) / 2, (GLFWvidmode.height(videoMode) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
//		glfwSwapInterval(1);
		glfwShowWindow(window);
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
				tick();
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

			if (input.isPressed(KEY_PAUSE)) {
				Command.exec("pause");
				input.unset(KEY_PAUSE);
			}

			if (input.isPressed(KEY_DEBUG) && debug) {
				showDebug = !showDebug;
				input.unset(KEY_DEBUG);
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
			level.tick();
		}
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

		if (input.isPressed(KEY_MESSAGE)) {
//			GUI.add(() -> GUI.textBox("Debug text:", "ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+/*[]"));
//			GUI.add(() -> GUI.textBox("Debug text:", "ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+/*[] |ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+/*[] |ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+/*[] |ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789 .,:;'\"!?$%()-=+/*[] |ABCDEFGHIJKLMNOPQRSTUVWXY abcdefghijklmnopqrstuvwxyz 0123456789| .,:;'\"!?$%()-=+/*[]"));
//			GUI.add(() -> GUI.textBox("Lorem Impsum", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui."));
			GUI.add(() -> GUI.textBox("The fox", "The quick brown fox jumps over the lazy dog."));
			GUI.add(() -> GUI.textBox(level.name, level.description + "|By: " + level.author));
		}

		GUI.render();

		if (paused) {
			GUI.add(() -> Font.render("Paused", screen, screen.xOffset + screen.width - 49, screen.yOffset + 1));
			GUI.add(() -> GUI.buttonText(WIDTH / 2, 199, "Resume", new Vec3f(0.1f, 0.5f, 0.1f), () -> Command.exec("pause")));
			GUI.add(() -> GUI.buttonText(WIDTH / 2, 214, "Restart", new Vec3f(0.1f, 0.5f, 0.5f), () -> {
				Command.exec("reload");
				Command.exec("pause");
			}));
			GUI.add(() -> GUI.buttonText(WIDTH / 2, 229, "Quit", new Vec3f(0.5f, 0.5f, 0.1f), () -> Command.exec("exit")));
			screen.drawRectangle(0, 0, WIDTH, HEIGHT, new Vec4f(1.0f, 1.0f, 1.0f, 0.1f));
		}

		if (showDebug) {
			GUI.add(() -> Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1));
			GUI.add(() -> Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 11));
			GUI.add(() -> Font.render("mx: " + mx + " my: " + my, screen, screen.xOffset + 1, screen.yOffset + 21));
		}

		GUI.render();
	}
}
