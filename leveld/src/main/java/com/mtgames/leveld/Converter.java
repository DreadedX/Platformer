package com.mtgames.leveld;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

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
	private static String parent;
	private static BufferedImage image;

	public static void main(String[] args) {
		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".png & .json files", "png", "json");
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(Paths.get(".").toAbsolutePath().normalize().toFile());

		int returnVal = fc.showOpenDialog(null);

		File path;
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

		parent = path.getParent();

		if (extension.equals("png")) {
			try {
				image = ImageIO.read(new File(path.toString()));
				width = image.getWidth();
				height = image.getHeight();

				convertPNG();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (extension.equals("json")) {
			String archiveLocation = new File(parent).getParent() + "/" + new File(new File(parent).getParent()).getName();

			try {
				File tar = new File(parent + ".tar");
				if (tar.exists() && !tar.isDirectory()) {
					tar.delete();
				}

				File folder = new File(parent);

				File[] filesToTar=folder.listFiles((dir, name) -> name.endsWith(".json"));

				TarOutputStream tarOut = new TarOutputStream(new FileOutputStream(archiveLocation + ".tar"));

				assert filesToTar != null;
				for (File f:filesToTar) {
					try {
						tarOut.putNextEntry(new TarEntry(f, f.getName()));
						BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

						int count;
						byte data[] = new byte[2048];
						while((count = origin.read(data)) != -1) {
							tarOut.write(data, 0, count);
						}

						tarOut.flush();
						origin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				InputStream archive = new FileInputStream(new File(archiveLocation + ".tar"));
				GZIPOutputStream gzOut = new GZIPOutputStream(new FileOutputStream(archiveLocation + ".tgz"));

				byte[] buffer = new byte[1024];
				int len;
				while ((len = archive.read(buffer)) != -1) {
					gzOut.write(buffer, 0, len);
				}
				gzOut.close();
				archive.close();

				new File(archiveLocation + ".tar").delete();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void convertPNG() {
		new File(parent + "/data").mkdir();
		int[] tileColours = image.getRGB(0, 0, width, height, null, 0, width);
		int id;
		String json;

		json = "{\n"
				+ "  \"width\": " + width +",\n"
				+ "  \"height\": " + height +",\n"
				+ "  \"background\": {\n"
				+ "    \"layer1\": {\n"
				+ "      \"name\": \"forest1\",\n"
				+ "      \"speed\": 2\n"
				+ "    },\n"
				+ "    \"layer2\":{\n"
				+ "      \"name\": \"forest2\",\n"
				+ "      \"speed\": 4\n"
				+ "    }\n"
				+ "  },\n"
				+ "  \"tiles\": [\n";

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x != 0) {
					json += ",";
				}

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

				json += id;
			}
		}
		json += "  ]\n"
				+ "}";

		PrintWriter out;
		try {
			out = new PrintWriter(parent + "/data/tiles.json");
			out.println(json);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}