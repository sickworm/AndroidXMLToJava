package com.excelsecu.androidx2j.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.CardLayout;

public class MainEntry {
	
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CopyOfScreenFrame window = new CopyOfScreenFrame("Android XML to Java Code");
                    window.getContentPane().setLayout(new CardLayout(0, 0));
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
