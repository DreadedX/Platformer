package com.mtgames.png2xml;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class Converter {

	private static int width;
	private static int height;
	private static File path;
	private static String filename;
	private static BufferedImage image;

	public static void main(String[] args) {
		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".png & .xml files", "png", "xml");
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(Paths.get(".").toAbsolutePath().normalize().toFile());

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			path = fc.getSelectedFile();
		} else {
			return;
		}

		String extension = "";

		int i = path.toString().lastIndexOf('.');
		if (i >= 0) {
			extension = path.toString().substring(i + 1);
		}

		filename = path.getName().substring(0, path.getName().lastIndexOf('.'));
		new File("levels").mkdir();
		new File("levels/" + filename).mkdir();

		if(extension.equals("png")) {
			System.out.println("Converting " + path + " to " + filename + ".xml");

			try {
				image = ImageIO.read(new File(path.toString()));
				width = image.getWidth();
				height = image.getHeight();

//				System.out.println("Loaded: " + path);

				convertPNG();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(extension.equals("xml")) {
			System.out.println("Converting " + path + " to " + filename + ".map");

			try {
				InputStream in = new FileInputStream(path);
				GZIPOutputStream gzout = new GZIPOutputStream(new FileOutputStream("levels/" + filename + "/" + filename + ".map"));

				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) != -1) {
					gzout.write(buffer, 0, len);
				}
				gzout.close();
				in.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void convertPNG() {
		int[] tileColours = image.getRGB(0, 0, width, height, null, 0, width);
		int id;
		String xml;

		xml = "<map height='" + height + "' width='" + width + "'>\n";
		xml += "  <background>\n";
		xml += "    <layer name='forest1' speed='2'/>\n";
		xml += "    <layer name='forest2' speed='4'/>\n";
		xml += "  </background>\n";
		xml += "  <entities>\n";
		xml += "    <entity id='0' x='10' y='10'/>\n";
		xml += "  </entities>\n";
		xml += "  <tiles>\n";
		
//		System.out.println("Added: Necessary level information");
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				switch (tileColours[x + y * width]) {
				default:
					id = 1;
					break;

				case 0xff9966cc:
					id = 2;
					break;

				case 0xff666666:
					id = 3;
					break;

				case 0xffffcc01:
					id = 32;
					break;

				case 0xffffcc02:
					id = 33;
					break;

				case 0xffffcc03:
					id = 64;
					break;

				case 0xffffcc04:
					id = 65;
					break;

				case 0xff996631:
					id = 34;
					break;

				case 0xff996632:
					id = 35;
					break;

				case 0xff996633:
					id = 66;
					break;

				case 0xff996634:
					id = 67;
					break;

				}

				if (id != 1) {
					xml += "    <tile id='" + id + "' x='" + x + "' y='" + y + "'/>\n";
//					System.out.println("Added: " + id + " at " + x + ", " + y);
				}
			}
		}
		xml += "  </tiles>\n";
		xml += "</map>\n";
		
		PrintWriter out;
		try {
			out = new PrintWriter("levels/" + filename + "/" + filename + ".xml");
			out.println(xml);
			out.close();
			System.out.println("Converted " + path + " to " + filename + ".xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}