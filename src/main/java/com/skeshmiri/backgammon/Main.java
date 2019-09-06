package com.skeshmiri.backgammon;

import javax.swing.SwingUtilities;
import com.skeshmiri.backgammon.gui.MainWindow;

public class Main {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				// not neccessary to create new object so reference/var is not stored
            	// MainWindow mw = new MainWindow();
				new MainWindow();
			}
        });
	}
}
