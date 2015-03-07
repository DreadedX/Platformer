package com.mtgames.firerpg.level;

import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.entities.Player;
import com.mtgames.firerpg.entities.enemies.BasicEnemy;

public class LevelLoader {
	
	private static String	x;
	private static String	y;
	private static String	id;
	private static String	width;
	private static String	height;
	private static int		realWidth;
	private static int		realHeight;
	private static byte[]	tiles;
	
	public LevelLoader(Level level, InputHandler input, String path) throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		GZIPInputStream gzis = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(path));
		
		Document document = builder.parse(gzis);
		
		NodeList tileList = document.getDocumentElement().getElementsByTagName("tile");
		NodeList entityList = document.getDocumentElement().getElementsByTagName("entity");
		height = document.getDocumentElement().getAttribute("height");
		width = document.getDocumentElement().getAttribute("width");
		
		realHeight = Integer.parseInt(height);
		realWidth = Integer.parseInt(width);
		
		tiles = new byte[realWidth * realHeight];
		
		for (int i = 0; i < realWidth * realHeight; i++) {
			tiles[i] = 1;
		}
		
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
						break;
					
					case 1:
						level.addEntity(new BasicEnemy(level, realX, realY));
						break;
				}
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