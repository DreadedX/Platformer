package com.mtgames.platformer.level;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.entities.AutoScoll;
import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.entities.enemies.BaseEnemy;
import com.mtgames.platformer.entities.particles.Torch;
import com.mtgames.platformer.gfx.Background;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

class LevelLoader {

	private static int    realWidth;
	private static int    realHeight;
	private static byte[] tiles;

	public LevelLoader(Level level, InputHandler input, String path, boolean external) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();

		GZIPInputStream gzis;
		if (external) {
			gzis = new GZIPInputStream(new FileInputStream(path));
		} else {
			gzis = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(path));
		}

		Document document = builder.parse(gzis);
		gzis.close();

		NodeList tileList = document.getDocumentElement().getElementsByTagName("tile");
		NodeList entityList = document.getDocumentElement().getElementsByTagName("entity");
		NodeList backgroundList = document.getDocumentElement().getElementsByTagName("layer");
		String height = document.getDocumentElement().getAttribute("height");
		String width = document.getDocumentElement().getAttribute("width");

		realHeight = Integer.parseInt(height);
		realWidth = Integer.parseInt(width);

		tiles = new byte[realWidth * realHeight];

		for (int i = 0; i < realWidth * realHeight; i++) {
			tiles[i] = 1;
		}

		int id;
		int x;
		int y;
		for (int i = 0; i < tileList.getLength(); i++) {

			Node node = tileList.item(i);
			if (node instanceof Element) {
				id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
				x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
				y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());

				tiles[x + y * realWidth] = (byte) id;
			}
		}

        String type;
		for (int i = 0; i < entityList.getLength(); i++) {

			Node node = entityList.item(i);
			if (node instanceof Element) {
				type = node.getAttributes().getNamedItem("id").getNodeValue();
				x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
				y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());

				switch (type) {
//                  TODO: Change id numbers to names to make it easier to understand
//                  TODO: Move the enity spawn code to seperate class so it can be called from other parts of the code
					case "0":
						level.addEntity(new Player(level, x, y, input));
						Debug.log("Added Player: " + x + " " + y, Debug.LEVEL);
						break;

					case "1":
						level.addEntity(new BaseEnemy(level, x, y));
						Debug.log("Added BaseEnemy: " + x + " " + y, Debug.LEVEL);
						break;

                    case "2":
                        level.addParticle(new Torch(level, x, y));

					case "99":
						level.addEntity(new AutoScoll(level, x, y));
						Debug.log("Added AutoScroll: " + x + " " + y, Debug.LEVEL);
						break;
				}
			}
		}

		String name;
		int speed;
		for (int i = 0; i < backgroundList.getLength(); i++) {

			Node node = backgroundList.item(i);
			if (node instanceof Element) {
				name = node.getAttributes().getNamedItem("name").getNodeValue();
				speed = Integer.parseInt(node.getAttributes().getNamedItem("speed").getNodeValue());

				level.addBackground(new Background("/graphics/backgrounds/" + name + ".png", speed));
				Debug.log("Added layer: " + name + " " + speed, Debug.LEVEL);
			}

		}
	}

	public byte[] getTiles() {
		return tiles;
	}

	public int getWidth() {
		return realWidth;
	}

	public int getHeight() {
		return realHeight;
	}

}