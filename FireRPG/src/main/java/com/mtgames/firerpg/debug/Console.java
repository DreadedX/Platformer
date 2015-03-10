package com.mtgames.firerpg.debug;

//
//A simple Java Console for your application (Swing version)
//Requires Java 1.1.5 or higher
//
//Disclaimer the use of this source is at your own risk. 
//
//Permision to use and distribute into your own applications
//
//RJHM van den Bergh , rvdb@comweb.nl
//
//Modified by: Tim Huizinga

import com.mtgames.firerpg.level.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

public class Console extends WindowAdapter implements WindowListener, ActionListener, Runnable {
	public static String              lines   = "";
	private final ScriptEngineManager manager = new ScriptEngineManager();
	private final ScriptEngine        engine  = manager.getEngineByName("JavaScript");

	private final JFrame           frame      = new JFrame("Java Console");
	private final JTextArea        textArea   = new JTextArea();
	private final JTextField       inputField = new JTextField();
	private final PipedInputStream pin        = new PipedInputStream();
	private final PipedInputStream pin2       = new PipedInputStream();
	private       boolean quit;
	private final Thread  reader;
	private final Thread  reader2;

	private final Level level;

	public Console(Level level) {
		// create all components and add them
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(screenSize.width / 4, screenSize.height / 2);
		//        int x = frameSize.width / 2 + screenSize.width / 2;
		int x = frameSize.width / 2;
		int y = frameSize.height / 2;
		frame.setBounds(x, y, frameSize.width, frameSize.height);

		textArea.setEditable(false);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setForeground(Color.LIGHT_GRAY);

		inputField.setEditable(true);

		JButton button = new JButton("clear");

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.getContentPane().add(new JScrollPane(inputField), BorderLayout.SOUTH);
		//		frame.getContentPane().add(button,BorderLayout.EAST);
		frame.setVisible(true);

		inputField.addActionListener(this);

		frame.addWindowListener(this);
		button.addActionListener(this);

		try {
			PipedOutputStream pout = new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout, true));
		} catch (IOException | SecurityException io) {
			textArea.append("Couldn't redirect STDOUT to this console\n" + io.getMessage());
		}

		try {
			PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2, true));
		} catch (IOException | SecurityException io) {
			textArea.append("Couldn't redirect STDERR to this console\n" + io.getMessage());
		}

		quit = false; // signals the Threads that they should exit

		// Starting two seperate threads to read from the PipedInputStreams
		//
		reader = new Thread(this);
		reader.setDaemon(true);
		reader.start();
		//
		reader2 = new Thread(this);
		reader2.setDaemon(true);
		reader2.start();

		this.level = level;
	}

	public synchronized void windowClosed(WindowEvent evt) {
		quit = true;
		this.notifyAll(); // stop all threads
		try {
			reader.join(1000);
			pin.close();
		} catch (Exception ignored) {
		}
		try {
			reader2.join(1000);
			pin2.close();
		} catch (Exception ignored) {
		}
		System.exit(0);
	}

	public synchronized void windowClosing(WindowEvent evt) {
		frame.setVisible(false); // default behaviour of JFrame
		frame.dispose();
	}

	public synchronized void actionPerformed(ActionEvent evt) {
		try {
			engine.eval("function load(path) { level = path; print(path); }");
			engine.eval(inputField.getText());
			Debug.msg(Debug.DEBUG, "1");
			level.load(engine.get("level") + "");
			Debug.msg(Debug.DEBUG, "2");
		} catch (ScriptException e) {
			e.printStackTrace();
		}

		inputField.setText("");
	}

	public synchronized void run() {
		try {
			while (Thread.currentThread() == reader) {
				try {
					this.wait(100);
				} catch (InterruptedException ignored) {
				}
				if (pin.available() != 0) {
					String input = this.readLine(pin);
					textArea.append(input);

					lines = "";
					lines = input;

					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
				if (quit)
					return;
			}

			while (Thread.currentThread() == reader2) {
				try {
					this.wait(100);
				} catch (InterruptedException ignored) {
				}
				if (pin2.available() != 0) {
					String input = this.readLine(pin2);
					textArea.append(input);

					lines = "";
					lines = input;

					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
				if (quit)
					return;
			}
		} catch (Exception e) {
			textArea.append("\nConsole reports an Internal error.");
			textArea.append("The error is: " + e);
		}
	}

	synchronized String readLine(PipedInputStream in) throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte b[] = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
		return input;
	}
}