package com.mtgames.firerpg.level;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.debug.Debug;
import com.mtgames.firerpg.entities.Player;
import com.mtgames.firerpg.entities.enemies.BasicEnemy;
import com.mtgames.firerpg.gfx.Background;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.zip.GZIPInputStream;

class LevelLoader {

	private static int    realWidth;
	private static int    realHeight;
	private static byte[] tiles;

	public LevelLoader(Level level, InputHandler input, String path) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();

		GZIPInputStream gzis = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(path));

		Document document = builder.parse(gzis);

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

		for (int i = 0; i < entityList.getLength(); i++) {

			Node node = entityList.item(i);
			if (node instanceof Element) {
				id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
				x = Integer.parseInt(node.getAttributes().getNamedItem("x").getNodeValue());
				y = Integer.parseInt(node.getAttributes().getNamedItem("y").getNodeValue());

				switch (id) {
					case 0:
						level.addEntity(new Player(level, x, y, input));
						Debug.log(Debug.LEVEL, "Added Player: " + x + " " + y);
						break;

					case 1:
						level.addEntity(new BasicEnemy(level, x, y));
						Debug.log(Debug.LEVEL, "Added BasicEnemy: " + x + " " + y);
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

				level.addBackground(new Background("/backgrounds/" + name + ".png", speed));
				Debug.log(Debug.LEVEL, "Added layer: " + name + " " + speed);
			}

		}
	}

	public byte[] loadTiles() {
		return tiles;
	}

	public int getWidth() {
		return realWidth;
	}

	public int getHeight() {
		return realHeight;
	}

}