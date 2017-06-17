package com.sickworm.androidx2j.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sickworm.androidx2j.ContentConverter;

public class ScreenFrame extends JFrame {
	private JTextField srcPathField = new JTextField();
	private JTextField desPathField = new JTextField();
	private JButton srcPathBrowse = new JButton();
	private JButton desPathBrowse = new JButton();
	
	private JTextArea srcTextField = new JTextArea();
	private JTextArea destTextField = new JTextArea();
	private JScrollPane srcScroll = new JScrollPane(srcTextField);
	private JScrollPane destScroll = new JScrollPane(destTextField);

	private JButton runButton = new JButton();
	
	private static final long serialVersionUID = 6819222900970457455L;
	private JPanel mainPanel = new JPanel();

	public ScreenFrame(String title) {
		setTitle(title);
		setPreferredSize(new Dimension(1280, 800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initComponent();
		addListener();
		pack();
		setVisible(true);
	}

	private void initComponent() {
		srcPathBrowse.setText("Browse");
		desPathBrowse.setText("Browse");
		runButton.setText("Translate");
		GridBagLayout g = new GridBagLayout();
		mainPanel.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		mainPanel.add(srcPathField, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 0;
		mainPanel.add(srcPathBrowse, c);


		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		mainPanel.add(desPathField, c);

		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 0;
		mainPanel.add(desPathBrowse, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(srcScroll, c);
		

		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(destScroll, c);

		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		c.weighty = 0;
		mainPanel.add(runButton, c);
		
		
		
		this.getContentPane().add(mainPanel);
		
	}

	private void addListener() {
		srcPathBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser=new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(ScreenFrame.this) == JFileChooser.APPROVE_OPTION) {
					srcPathField.setText(chooser.getSelectedFile().toString());
				}
			}
		});

		desPathBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser=new JFileChooser();
				
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(ScreenFrame.this) == JFileChooser.APPROVE_OPTION) {
					desPathField.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = new ContentConverter().convertXMLToJavaCode(srcTextField.getText());
				destTextField.setText(result);
			}
		});
	}
}