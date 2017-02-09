package com.excelsecu.androidx2j.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScreenFrame extends JFrame {
	private static final long serialVersionUID = 6819222900970457455L;
	private JPanel mainPanel = new JPanel();
	private JButton addButton = new JButton();
	private JButton leftButton = new JButton();
	private JButton rightButton = new JButton();
	private JLabel label = new JLabel();
	private JTextField field = new JTextField();
	private DefaultListModel leftModel = new DefaultListModel();
	private DefaultListModel rightMOdel = new DefaultListModel();
	private JList leftList = new JList(leftModel);
	private JList rightList = new JList(rightMOdel);

	public ScreenFrame(String title) {
		setTitle(title);
		setPreferredSize(new Dimension(600, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initComponent();
		addData();
		pack();
		setVisible(true);
	}

	private void initComponent() {

		label.setText("添加选项：");
		addButton.setText("添加");
		leftList.setPreferredSize(new Dimension(150, 150));
		rightList.setPreferredSize(leftList.getPreferredSize());
		leftButton.setText("左");
		rightButton.setText("右");
		mainPanel.setBorder(BorderFactory.createTitledBorder("左右选择框"));
		mainPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0; // 0行0列
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 0;
		mainPanel.add(label, c);

		c.gridx++;
		c.weightx = 1;
		mainPanel.add(field, c);

		c.gridx++;
		c.weightx = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		// c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(addButton, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(leftList, c);

		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(leftButton, c);

		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(rightButton, c);

		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(rightList, c);

		this.getContentPane().add(mainPanel);
	}

	private void addData() {
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addItem();
			}
		});

		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leftItem();
			}
		});

		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightItem();
			}
		});
	}

	private void addItem() {
		if (field.getText() != null && !field.getText().equals("")) {
			((DefaultListModel) leftList.getModel())
					.addElement(field.getText());
			field.setText("");
		}
	}

	private void leftItem() {
		if (rightList.getSelectedIndex() != -1) {
			Object o = rightList.getSelectedValue();
			((DefaultListModel) rightList.getModel()).remove(rightList
					.getSelectedIndex());
			((DefaultListModel) leftList.getModel()).addElement(o);
		}
	}

	private void rightItem() {
		if (leftList.getSelectedIndex() != -1) {
			Object o = leftList.getSelectedValue();
			((DefaultListModel) leftList.getModel()).remove(leftList
					.getSelectedIndex());
			((DefaultListModel) rightList.getModel()).addElement(o);
		}
	}
}