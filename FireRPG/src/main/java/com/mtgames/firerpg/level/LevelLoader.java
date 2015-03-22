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

		String id;
		String x = null;
		String y = null;
		for (int i = 0; i < tileList.getLength(); i++) {

			Node node = tileList.item(i);
			if (node instanceof Element) {
				id = node.getAttributes().getNamedItem("id").getNodeValue();

				NodeList childNodes = node.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node cNode = childNodes.item(j);

					// Identifying the child tag of employee encountered.
					if (cNode instanceof Element) {
						String content = cNode.getLastChild().getTextContent().trim();
						switch (cNode.getNodeName()) {
							case "x":
								x = content;
								break;
							case "y":
								y = content;
								break;
						}
					}
				}
				int realX = Integer.parseInt(x);
				int realY = Integer.parseInt(y);
				int realId = Integer.parseInt(id);
				tiles[realX + realY * realWidth] = (byte) realId;
			}
		}

		for (int i = 0; i < entityList.getLength(); i++) {

			Node node = entityList.item(i);
			if (node instanceof Element) {
				id = node.getAttributes().getNamedItem("id").getNodeValue();

				NodeList childNodes = node.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node cNode = childNodes.item(j);

					// Identifying the child tag of employee encountered.
					if (cNode instanceof Element) {
						String content = cNode.getLastChild().getTextContent().trim();
						switch (cNode.getNodeName()) {
							case "x":
								x = content;
								break;
							case "y":
								y = content;
								break;
						}
					}
				}
				int realX = Integer.parseInt(x);
				int realY = Integer.parseInt(y);
				int realId = Integer.parseInt(id);
				switch (realId) {
					case 0:
						level.addEntity(new Player(level, realX, realY, input));
						Debug.log(Debug.LEVEL, "Added Player: " + realX + " " + realY);
						break;

					case 1:
						level.addEntity(new BasicEnemy(level, realX, realY));
						Debug.log(Debug.LEVEL, "Added BasicEnemy: " + realX + " " + realY);
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

				level.addBackground(new Background("/" + name + ".png", speed));
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