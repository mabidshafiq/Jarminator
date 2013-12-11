package net.sf.jarminator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * GUI stuff of Jarminator frame.
 */
public class JarminatorFrame extends JFrame {

	JPanel mainPanel;
	JPanel topPanel;
	JLabel rootLabel;
	JTextField rootTextField;
	JButton browseButton;
	JButton loadButton;
	JTextField filterTextField;
	JLabel filterLabel;
	JButton applyButton;
	JTabbedPane tabbedPane;
	JPanel jarPanel;
	JTree jarsTree;
	JPanel classesPanel;
	JTree classesTree;
	JPanel duplicateClassesPanel;
	JTree duplicateClassesTree;
	JPanel aboutPanel;
	JLabel aboutLabel;
	JLabel statusLabel;

	JarminatorEvents events = new JarminatorEvents(this);

	public JarminatorFrame() {
		setTitle("Jarminator");

		mainPanel = new JPanel();
		GridBagLayout gbMain = new GridBagLayout();
		GridBagConstraints gbcMain = new GridBagConstraints();
		mainPanel.setLayout(gbMain);

		topPanel = new JPanel();
		GridBagLayout gbTop = new GridBagLayout();
		GridBagConstraints gbcTop = new GridBagConstraints();
		topPanel.setLayout(gbTop);

		rootLabel = new JLabel("Source");
		gbcTop.gridx = 0;
		gbcTop.gridy = 0;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 0;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbcTop.insets = new Insets(0, 0, 0, 4);
		gbTop.setConstraints(rootLabel, gbcTop);
		topPanel.add(rootLabel);

		rootTextField = new JTextField();
		rootTextField.setColumns(1);
		gbcTop.gridx = 1;
		gbcTop.gridy = 0;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 1;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbTop.setConstraints(rootTextField, gbcTop);
		topPanel.add(rootTextField);

		browseButton = new JButton("Browse...");
		browseButton.setMargin(new Insets(0, 4, 0, 4));
		browseButton.setRolloverEnabled(true);
		gbcTop.gridx = 2;
		gbcTop.gridy = 0;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 0;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbcTop.insets = new Insets(0, 5, 0, 0);
		gbTop.setConstraints(browseButton, gbcTop);
		topPanel.add(browseButton);

		loadButton = new JButton("Load");
		loadButton.setActionCommand("Apply");
		loadButton.setBackground(new Color(221, 221, 221));
		loadButton.setMargin(new Insets(0, 4, 0, 4));
		loadButton.setRolloverEnabled(true);
		gbcTop.gridx = 3;
		gbcTop.gridy = 0;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 0;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbTop.setConstraints(loadButton, gbcTop);
		topPanel.add(loadButton);

		filterTextField = new JTextField();
		gbcTop.gridx = 1;
		gbcTop.gridy = 1;
		gbcTop.gridwidth = 2;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 1;
		gbcTop.weighty = 1;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbcTop.insets = new Insets(2, 0, 4, 0);
		gbTop.setConstraints(filterTextField, gbcTop);
		topPanel.add(filterTextField);

		filterLabel = new JLabel("Filter");
		gbcTop.gridx = 0;
		gbcTop.gridy = 1;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 0;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbTop.setConstraints(filterLabel, gbcTop);
		topPanel.add(filterLabel);

		applyButton = new JButton("Apply");
		applyButton.setMargin(new Insets(0, 4, 0, 4));
		gbcTop.gridx = 3;
		gbcTop.gridy = 1;
		gbcTop.gridwidth = 1;
		gbcTop.gridheight = 1;
		gbcTop.fill = GridBagConstraints.BOTH;
		gbcTop.weightx = 0;
		gbcTop.weighty = 0;
		gbcTop.anchor = GridBagConstraints.NORTH;
		gbTop.setConstraints(applyButton, gbcTop);
		topPanel.add(applyButton);
		gbcMain.gridx = 0;
		gbcMain.gridy = 0;
		gbcMain.gridwidth = 1;
		gbcMain.gridheight = 1;
		gbcMain.fill = GridBagConstraints.BOTH;
		gbcMain.weightx = 1;
		gbcMain.weighty = 0;
		gbcMain.anchor = GridBagConstraints.CENTER;
		gbcMain.insets = new Insets(6, 5, 0, 5);
		gbMain.setConstraints(topPanel, gbcMain);
		mainPanel.add(topPanel);

		tabbedPane = new JTabbedPane();

		jarPanel = new JPanel();
		GridBagLayout gbJar = new GridBagLayout();
		GridBagConstraints gbcJar = new GridBagConstraints();
		jarPanel.setLayout(gbJar);

		jarsTree = new JTree();
		jarsTree.setForeground(new Color(0, 0, 0));
		JScrollPane scpJars = new JScrollPane(jarsTree);
		gbcJar.gridx = 0;
		gbcJar.gridy = 0;
		gbcJar.gridwidth = 20;
		gbcJar.gridheight = 13;
		gbcJar.fill = GridBagConstraints.BOTH;
		gbcJar.weightx = 1;
		gbcJar.weighty = 1;
		gbcJar.anchor = GridBagConstraints.NORTH;
		gbJar.setConstraints(scpJars, gbcJar);
		jarPanel.add(scpJars);
		tabbedPane.addTab("Jars", jarPanel);

		classesPanel = new JPanel();
		GridBagLayout gbClass = new GridBagLayout();
		GridBagConstraints gbcClass = new GridBagConstraints();
		classesPanel.setLayout(gbClass);
		
		duplicateClassesPanel = new JPanel();
		GridBagLayout gbDuplicateClass = new GridBagLayout();
		GridBagConstraints gbcDuplicateClass = new GridBagConstraints();
		duplicateClassesPanel.setLayout(gbDuplicateClass);
		

		classesTree = new JTree();
		classesTree.setForeground(new Color(0, 0, 0));
		JScrollPane scpClasses = new JScrollPane(classesTree);
		gbcClass.gridx = 0;
		gbcClass.gridy = 0;
		gbcClass.gridwidth = 20;
		gbcClass.gridheight = 13;
		gbcClass.fill = GridBagConstraints.BOTH;
		gbcClass.weightx = 1;
		gbcClass.weighty = 1;
		gbcClass.anchor = GridBagConstraints.NORTH;
		gbClass.setConstraints(scpClasses, gbcClass);
		classesPanel.add(scpClasses);
		tabbedPane.addTab("Classes", classesPanel);

		
		//duplicate classes
		duplicateClassesTree = new JTree();
		duplicateClassesTree.setForeground(new Color(0, 0, 0));
		JScrollPane scpDuplicateClasses = new JScrollPane(duplicateClassesTree);
		gbcDuplicateClass.gridx = 0;
		gbcDuplicateClass.gridy = 0;
		gbcDuplicateClass.gridwidth = 20;
		gbcDuplicateClass.gridheight = 13;
		gbcDuplicateClass.fill = GridBagConstraints.BOTH;
		gbcDuplicateClass.weightx = 1;
		gbcDuplicateClass.weighty = 1;
		gbcClass.anchor = GridBagConstraints.NORTH;
		gbDuplicateClass.setConstraints(scpDuplicateClasses, gbcClass);
		duplicateClassesPanel.add(scpDuplicateClasses);
		tabbedPane.addTab("Duplicate Classes", duplicateClassesPanel);

		
		
		
		aboutPanel = new JPanel();
		GridBagLayout gbAbout = new GridBagLayout();
		GridBagConstraints gbcAbout = new GridBagConstraints();
		aboutPanel.setLayout(gbAbout);

		aboutLabel = new JLabel("");
		gbcAbout.gridx = 0;
		gbcAbout.gridy = 0;
		gbcAbout.gridwidth = 20;
		gbcAbout.gridheight = 13;
		gbcAbout.fill = GridBagConstraints.BOTH;
		gbcAbout.weightx = 1;
		gbcAbout.weighty = 1;
		gbcAbout.anchor = GridBagConstraints.NORTH;
		gbAbout.setConstraints(aboutLabel, gbcAbout);
		aboutPanel.add(aboutLabel);
		tabbedPane.addTab("About", aboutPanel);
		gbcMain.gridx = 0;
		gbcMain.gridy = 1;
		gbcMain.gridwidth = 1;
		gbcMain.gridheight = 1;
		gbcMain.fill = GridBagConstraints.BOTH;
		gbcMain.weightx = 1;
		gbcMain.weighty = 1;
		gbcMain.anchor = GridBagConstraints.NORTH;
		gbMain.setConstraints(tabbedPane, gbcMain);
		mainPanel.add(tabbedPane);

		statusLabel = new JLabel("Status");
		statusLabel.setForeground(new Color(0x30, 0x30, 0x30));
		gbcMain.gridx = 0;
		gbcMain.gridy = 2;
		gbcMain.gridwidth = 1;
		gbcMain.gridheight = 1;
		gbcMain.fill = GridBagConstraints.BOTH;
		gbcMain.weightx = 1;
		gbcMain.weighty = 0;
		gbcMain.anchor = GridBagConstraints.SOUTH;
		gbcMain.insets = new Insets(5, 5, 5, 5);
		gbMain.setConstraints(statusLabel, gbcMain);
		mainPanel.add(statusLabel);

		loadButton.setText("<html>&nbsp; &nbsp;<b>Load</b>&nbsp; &nbsp;");
        aboutLabel.setHorizontalAlignment(JLabel.CENTER);
		aboutLabel.setVerticalAlignment(JLabel.TOP);
        aboutLabel.setText("<html><center><font face='Verdana,Arial'><font size='6' color='#000066'><br><br><b>Jarminator<br>" +
		        "<font size='3'>java JARs examinator<br>freeware, " + Jarminator.VERSION +
		        "<font size='4' color='#993333'><br><br><br>coded by <br>&lt;igor.spasic@gmail.com&gt;" +
		        "<font size='4'><br><br><br>Customized by : Muhammad Abid <br>&lt;mabidshafiq@gmail.com&gt;"
        		);
        
		statusLabel.setText("<html><b>Jarminator " + Jarminator.VERSION);


		// frame settings
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 500);
		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		setVisible(true);
		setIconImage(Util.createImageIcon("/gfx/jar.gif").getImage());

		setContentPane(mainPanel);
	}
	

}