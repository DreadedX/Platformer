package com.mtgames.platformer.debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

public class Console extends WindowAdapter implements WindowListener, ActionListener {
	private final JTextField inputField = new JTextField();

	public Console() {
		Dimension frameSize = new Dimension(300, 50);
		int x = frameSize.width / 2;
		int y = frameSize.height / 2;
		JFrame frame = new JFrame("Command Input");
		frame.setBounds(x, y, frameSize.width, frameSize.height);

		inputField.setEditable(true);
		inputField.setBackground(Color.DARK_GRAY);
		inputField.setForeground(Color.LIGHT_GRAY);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(inputField), BorderLayout.SOUTH);
		frame.setVisible(true);

		inputField.addActionListener(this);

		frame.addWindowListener(this);
	}

	@Override public synchronized void actionPerformed(ActionEvent evt) {
		Command.exec(inputField.getText());
		inputField.setText("");
	}
}