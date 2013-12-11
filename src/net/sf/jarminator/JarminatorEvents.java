package net.sf.jarminator;

import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Various events.
 */
public class JarminatorEvents {

	private String userDir = System.getProperty("user.dir");

	private JarminatorFrame frame;

	public JarminatorEvents(JarminatorFrame fr) {
		frame = fr;
	}

	private Icon openIcon = Util.createImageIcon("/gfx/open.gif");
	private Icon closedIcon = Util.createImageIcon("/gfx/close.gif");
	private Icon leafIcon = Util.createImageIcon("/gfx/leaf.gif");
	private Icon jarIcon = Util.createImageIcon("/gfx/jar.gif");
	private Icon classIcon = Util.createImageIcon("/gfx/class.gif");
	private Icon class2Icon = Util.createImageIcon("/gfx/class2.gif");
	private Color gray2 = new Color(0x666666);

	class JarTreeCellRenderer extends DefaultTreeCellRenderer {

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (value != null) {
				String val = value.toString();
				if (val != null) {
					if (val.endsWith(".jar")) {
						this.setIcon(jarIcon);
					} else if (val.endsWith(".class")) {
						Icon ico = classIcon;
						if ((leaf == true) && (value instanceof DoubleStringTreeNode)) {
							DoubleStringTreeNode n = (DoubleStringTreeNode) value;
							if (n.isDuplicate() == true) {
								ico = class2Icon;
								this.setForeground(gray2);
							}
						}
						this.setIcon(ico);
					} else if ((leaf == true) && (value instanceof DoubleStringTreeNode)) {
						DoubleStringTreeNode n = (DoubleStringTreeNode) value;
						if (n.isDuplicate() == true) {
							this.setForeground(gray2);
						}
					}
				}
			}
			return this;
		}
	}


	/*MouseListener mlJars = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			int selRow = frame.trJars.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = frame.trJars.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				if(e.getClickCount() == 1) {
					onTreeNodeClick_Jars(selPath);
				}
			}
		}
	};

	MouseListener mlClasses = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			int selRow = frame.trClasses.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = frame.trClasses.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				if(e.getClickCount() == 1) {
					onTreeNodeClick_Classes(selPath);
				}
			}
		}
	};*/

	TreeSelectionListener tslJars = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath selectedPath = e.getNewLeadSelectionPath();
			onTreeNodeSelect(selectedPath, 2);
		}
	};
	TreeSelectionListener tslClasses = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath selectedPath = e.getNewLeadSelectionPath();
			onTreeNodeSelect(selectedPath, 1);
		}
	};
	
	TreeSelectionListener tslDuplicateClasses = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath selectedPath = e.getNewLeadSelectionPath();
			onTreeNodeSelect(selectedPath, 1);
		}
	};

	// ---------------------------------------------------------------- init

	public void onCreate() {
        frame.jarsTree.setRootVisible(false);
		JarTreeCellRenderer renderer = new JarTreeCellRenderer();
		renderer.setOpenIcon(openIcon);
		renderer.setClosedIcon(closedIcon);
		renderer.setLeafIcon(leafIcon);
		frame.jarsTree.setCellRenderer(renderer);
		frame.jarsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(null)));
		//frame.trJars.addMouseListener(mlJars);
		frame.jarsTree.addTreeSelectionListener(tslJars);
		
		frame.classesTree.setRootVisible(false);
		frame.classesTree.setCellRenderer(renderer);
		frame.classesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(null)));
		//frame.trClasses.addMouseListener(mlClasses);
		frame.classesTree.addTreeSelectionListener(tslClasses);
		
		frame.duplicateClassesTree.setRootVisible(false);
		frame.duplicateClassesTree.setCellRenderer(renderer);
		frame.duplicateClassesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(null)));
		//frame.trClasses.addMouseListener(mlClasses);
		frame.duplicateClassesTree.addTreeSelectionListener(tslDuplicateClasses);
		
		initButtons();
	}

	private void initButtons() {
		frame.loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//onLoadApply(frame.tfRoot.getText(), null);
				System.out.println("<<< load button Clicked >>>");
				String root = frame.rootTextField.getText();
				if (root.length() != 0) {
					new LoadDialog(root, null);
				}
			}
		});
		frame.browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setApproveButtonText("Select");
				fc.setDialogTitle("Select root folders or JAR files");
				fc.setCurrentDirectory(new File(userDir));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fc.setFileFilter(filter);
				fc.setMultiSelectionEnabled(true);
				int returnVal = fc.showDialog(frame, null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					StringBuffer root = new StringBuffer();
					File[] selectedFiles = fc.getSelectedFiles();
					for (int i = 0; i < selectedFiles.length - 1; i++) {
						root.append(selectedFiles[i].getAbsoluteFile());
						root.append(';');
					}
					root.append(selectedFiles[selectedFiles.length - 1].getAbsoluteFile());

					String data = frame.rootTextField.getText().trim();
					if (data.indexOf(root.toString()) == -1) {
						if (data.length() != 0) {
							data += ';';
						}
						frame.rootTextField.setText(data + root);
					}
				}
			}
		});
		frame.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("<<< apply button Clicked >>>");
				String root = frame.rootTextField.getText();
				String filter = frame.filterTextField.getText();
				if (root.length() != 0) {
					if (filter.length() == 0) {
						filter = null;
					}
					//onLoadApply(null, filter);
					new LoadDialog(null, filter);
				}
			}
		});
	}

	// ---------------------------------------------------------------- browse button

	private SimpleFileFilter filter = new SimpleFileFilter("jar", "JAR Java Archives");

	// ---------------------------------------------------------------- on tree node select

	private void onTreeNodeSelect(TreePath selPath, int from) {
		if (selPath == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
		String leaf = "";
		if (from == 2) {
			leaf = selPath.getPathComponent(1).toString();
		}
		if (node.isLeaf() == true) {
			String[] s = (String[]) node.getUserObject();
			leaf = s[1];
		}
		StringBuffer sb = new StringBuffer("<html>");
		int total = selPath.getPathCount() - 1;
		for (int i = from; i < total; i++) {
			sb.append(selPath.getPathComponent(i)).append('/');
		}
		sb.append(selPath.getLastPathComponent());
		sb.append("<br>").append(leaf);
		frame.statusLabel.setText(sb.toString());
	}
}
