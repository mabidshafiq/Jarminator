package net.sf.jarminator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Simple 'in progress' dialog.
 */
public class LoadDialog extends JDialog {

	JPanel loadPanel;
	JLabel loadLabel;

	private WorkingThread work;

	public LoadDialog(String root, String name) {
		super(Jarminator.frame, "Jarmination in progress...", true);

		loadPanel = new JPanel();
		GridBagLayout gbLoad = new GridBagLayout();
		loadPanel.setLayout(gbLoad);

		loadLabel = new JLabel("");
		loadPanel.add(loadLabel);
		gbLoad.setConstraints(loadLabel,
				new GridBagConstraints(0, 0, 1, 1, 1, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH, 
						new Insets(5, 5, 5, 5), 0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setContentPane(loadPanel);

		setSize(200, 80);
        setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Dimension size = this.getSize();
		Dimension parentSize = Jarminator.frame.getSize();
		Point parentLocation = Jarminator.frame.getLocation();
		setLocation(parentLocation.x + (parentSize.width - size.width) / 2, parentLocation.y + (parentSize.height - size.height) / 2);
		loadLabel.setHorizontalAlignment(JLabel.CENTER);

		addWindowListener(new WindowAdapter() {
				/**
				 * Invoked when a window is in the process of being closed.
				 * The close operation can be overridden at this point.
				 */
				public void windowClosing(WindowEvent e) {
					int result = JOptionPane.showConfirmDialog(LoadDialog.this, "Do you want to quit the examination?", "Info", JOptionPane.YES_NO_OPTION);
					if (result == 0) {
						work.quit();
						dispose();
					}
				}
			}
		);
		work = new WorkingThread(LoadDialog.this, root, name);
		work.start();
		pack();
		show();
	}
}
