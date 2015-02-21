package com.mtgames.firerpg;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.mtgames.firerpg.gfx.Background;
import com.mtgames.firerpg.gfx.Font;
import com.mtgames.firerpg.gfx.gui.Text;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.gfx.SpriteSheet;
import com.mtgames.firerpg.level.Level;

@SuppressWarnings({ "serial" })
public class Game extends Canvas implements Runnable {
	
	public static final boolean	FPSUNLOCK	= true;
	public static final int		TPS			= 60;
	public static final int		WIDTH		= 256;
	public static final int		HEIGHT		= WIDTH / 4 * 3;
	public static final String	NAME		= "FireRPG";
	
	private JFrame				frame;
	
	public boolean				debug		= false;
	public boolean				running		= false;
	public int					tickCount	= 0;
	public int					fps			= 0;
	public static int			scale;
	
	private BufferedImage		image		= new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[]				pixels		= ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	private Screen				screen;
	public InputHandler			input;
	public Background			background1;
	public Background			background2;
	public Level				level;
	
	public Game() {
		setMinimumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
		setMaximumSize(new Dimension(WIDTH * scale, HEIGHT * scale));
		setPreferredSize(new Dimension(WIDTH * scale, HEIGHT * scale));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void init() {
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		background1 = new Background("/forest1.png");
		background2 = new Background("/forest2.png");
		level = new Level("levels/debug_level.map", input);
	}
	
	public synchronized void start() {
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
				System.out.println(frames + " Frames, " + ticks + " Ticks, " + level.particles.size() + " Particles");
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick() {
		tickCount++;
		level.tick();
		
		debug = input.debug.isPressed();
		
	}
	
	public void render() {
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
		
		/* Debug text */
		if (debug) {
			Font.render("fps: " + fps, screen, screen.xOffset + 1, screen.yOffset + 1);
			Font.render("x: " + level.entities.get(0).x + " y: " + level.entities.get(0).y, screen, screen.xOffset + 1, screen.yOffset + 9);
		}
		
		/* Hud.render(screen, 10, 7); */
		
		if (input.message.isPressed()) {
			Text.textBox(screen, "Mission:", "It is your mission to just mess around a bit in this world and try to debug the game!");
		}
		
		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				int colourCode = screen.pixels[x + y * screen.width];
				pixels[x + y * WIDTH] = colourCode;
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		if (args.length != 0) {
		scale = Integer.parseInt(args[0]);
		} else {
			scale = 4;
		}
		new Game().start();
	}
}
