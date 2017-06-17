package com.sickworm.androidx2j.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.sickworm.androidx2j.ContentConverter;

public class MainEntry {
	
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    final JFrame window = new JFrame("Android XML to Java Code");
                    window.setPreferredSize(new Dimension(1280, 800));
                    window.pack();
                    window.setLocationRelativeTo(null);
                    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    
                    GridBagLayout gridBagLayout = new GridBagLayout();
                    gridBagLayout.columnWidths = new int[]{1, 1};
                    gridBagLayout.rowHeights = new int[]{1, 0};
                    gridBagLayout.columnWeights = new double[]{1.0, 1.0};
                    gridBagLayout.rowWeights = new double[]{1, 0};
                    window.getContentPane().setLayout(gridBagLayout);
                    
                    JPanel srcPanel = new JPanel();
                    GridBagConstraints gbc_srcPanel = new GridBagConstraints();
                    gbc_srcPanel.fill = GridBagConstraints.BOTH;
                    gbc_srcPanel.insets = new Insets(0, 0, 5, 5);
                    gbc_srcPanel.gridx = 0;
                    gbc_srcPanel.gridy = 0;
                    window.getContentPane().add(srcPanel, gbc_srcPanel);
                    GridBagLayout gbl_srcPanel = new GridBagLayout();
                    gbl_srcPanel.rowWeights = new double[]{0.0, 1.0};
                    gbl_srcPanel.columnWeights = new double[]{1.0, 0.0};
                    gbl_srcPanel.rowHeights = new int[] {30, 0};
                    gbl_srcPanel.columnWidths = new int[] {1};
                    srcPanel.setLayout(gbl_srcPanel);
                    
                    JButton srcPathBrowseButton = new JButton("Browse");
                    srcPathBrowseButton.setToolTipText("Hi");
                    
                    final JTextField srcPathTextField = new JTextField();
                    srcPathTextField.setToolTipText("Hey");
                    srcPathTextField.setColumns(30);
                    GridBagConstraints gbc_srcPathTextField = new GridBagConstraints();
                    gbc_srcPathTextField.fill = GridBagConstraints.BOTH;
                    gbc_srcPathTextField.insets = new Insets(5, 5, 5, 5);
                    gbc_srcPathTextField.gridx = 0;
                    gbc_srcPathTextField.gridy = 0;
                    srcPanel.add(srcPathTextField, gbc_srcPathTextField);
                    GridBagConstraints gbc_srcPathBrowseButton = new GridBagConstraints();
                    gbc_srcPathBrowseButton.fill = GridBagConstraints.HORIZONTAL;
                    gbc_srcPathBrowseButton.insets = new Insets(5, 0, 5, 0);
                    gbc_srcPathBrowseButton.gridx = 1;
                    gbc_srcPathBrowseButton.gridy = 0;
                    srcPanel.add(srcPathBrowseButton, gbc_srcPathBrowseButton);
                    
                    JScrollPane srcScrollPane = new JScrollPane();
                    srcScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    srcScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    GridBagConstraints gbc_srcScrollPane = new GridBagConstraints();
                    gbc_srcScrollPane.gridwidth = 2;
                    gbc_srcScrollPane.fill = GridBagConstraints.BOTH;
                    gbc_srcScrollPane.insets = new Insets(0, 5, 0, 0);
                    gbc_srcScrollPane.gridx = 0;
                    gbc_srcScrollPane.gridy = 1;
                    gbc_srcScrollPane.weighty = 0;
                    srcPanel.add(srcScrollPane, gbc_srcScrollPane);
                    
                    final JTextArea srcTextArea = new JTextArea();
                    srcScrollPane.setViewportView(srcTextArea);
                    
                    
                    
                    JPanel destPanel = new JPanel();
                    GridBagConstraints gbc_destPanel = new GridBagConstraints();
                    gbc_destPanel.fill = GridBagConstraints.BOTH;
                    gbc_destPanel.insets = new Insets(0, 0, 5, 0);
                    gbc_destPanel.gridx = 1;
                    gbc_destPanel.gridy = 0;
                    window.getContentPane().add(destPanel, gbc_destPanel);
                    GridBagLayout gbl_destPanel = new GridBagLayout();
                    gbl_destPanel.rowWeights = new double[]{0.0, 1.0};
                    gbl_destPanel.columnWeights = new double[]{1.0, 0.0};
                    gbl_destPanel.rowHeights = new int[] {30, 0};
                    gbl_destPanel.columnWidths = new int[] {1};
                    destPanel.setLayout(gbl_destPanel);
                    
                    
                    JButton destPathBrowseButton = new JButton("Browse");
                    destPathBrowseButton.setToolTipText("Hi");
                    destPathBrowseButton.addActionListener(new ActionListener() {
                    	public void actionPerformed(ActionEvent e) {
                    	}
                    });
                    
                    final JTextField destPathTextField = new JTextField();
                    destPathTextField.setToolTipText("Hey");
                    destPathTextField.setColumns(30);
                    GridBagConstraints gbc_destPathTextField = new GridBagConstraints();
                    gbc_destPathTextField.fill = GridBagConstraints.BOTH;
                    gbc_destPathTextField.insets = new Insets(5, 5, 5, 5);
                    gbc_destPathTextField.gridx = 0;
                    gbc_destPathTextField.gridy = 0;
                    destPanel.add(destPathTextField, gbc_destPathTextField);
                    GridBagConstraints gbc_destPathBrowseButton = new GridBagConstraints();
                    gbc_destPathBrowseButton.fill = GridBagConstraints.HORIZONTAL;
                    gbc_destPathBrowseButton.insets = new Insets(5, 0, 5, 0);
                    gbc_destPathBrowseButton.gridx = 1;
                    gbc_destPathBrowseButton.gridy = 0;
                    destPanel.add(destPathBrowseButton, gbc_destPathBrowseButton);
                    
                    JScrollPane destScrollPane = new JScrollPane();
                    destScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    destScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    GridBagConstraints gbc_destScrollPane = new GridBagConstraints();
                    gbc_destScrollPane.gridwidth = 2;
                    gbc_destScrollPane.fill = GridBagConstraints.BOTH;
                    gbc_destScrollPane.insets = new Insets(0, 5, 0, 0);
                    gbc_destScrollPane.gridx = 0;
                    gbc_destScrollPane.gridy = 1;
                    gbc_destScrollPane.weighty = 0;
                    destPanel.add(destScrollPane, gbc_destScrollPane);
                    
                    final JTextArea destTextArea = new JTextArea();
                    destScrollPane.setViewportView(destTextArea);
                    
                    JPanel actionPanel = new JPanel();
                    GridBagConstraints gbc_actionPanel = new GridBagConstraints();
                    gbc_actionPanel.anchor = GridBagConstraints.EAST;
                    gbc_actionPanel.gridwidth = 2;
                    gbc_actionPanel.insets = new Insets(5, 5, 5, 5);
                    gbc_actionPanel.gridx = 0;
                    gbc_actionPanel.gridy = 1;
                    window.getContentPane().add(actionPanel, gbc_actionPanel);
                    GridBagLayout gbl_actionPanel = new GridBagLayout();
                    gbl_actionPanel.columnWidths = new int[] {1};
                    gbl_actionPanel.rowHeights = new int[] {1};
                    gbl_actionPanel.columnWeights = new double[]{Double.MIN_VALUE};
                    gbl_actionPanel.rowWeights = new double[]{0.0};
                    actionPanel.setLayout(gbl_actionPanel);
                    
                    JButton btnTranslateButton = new JButton("Translate");
                    GridBagConstraints gbc_btnTranslateButton = new GridBagConstraints();
                    gbc_btnTranslateButton.insets = new Insets(0, 0, 0, 0);
                    gbc_btnTranslateButton.gridx = 1;
                    gbc_btnTranslateButton.gridy = 0;
                    actionPanel.add(btnTranslateButton, gbc_btnTranslateButton);
                    

                    srcPathBrowseButton.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent e) {
            				JFileChooser chooser=new JFileChooser();
            				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            				if(chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
            					srcPathTextField.setText(chooser.getSelectedFile().toString());
            				}
            			}
            		});

            		destPathBrowseButton.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent e) {
            				JFileChooser chooser=new JFileChooser();
            				
            				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            				if(chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
            					destPathTextField.setText(chooser.getSelectedFile().toString());
            				}
            			}
            		});
            		
            		btnTranslateButton.addActionListener(new ActionListener() {
            			public void actionPerformed(ActionEvent e) {
            				String result = new ContentConverter().convertXMLToJavaCode(srcTextArea.getText());
            				destTextArea.setText(result);
            			}
            		});
            		
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
