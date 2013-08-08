import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main extends JFrame {
	static String filename = "";
	static Logger log = new Logger(Main.class);
	
	public Main() {
		//GUI stuff
		super("Hough Transform Circle Detector");
		filename = "../data/test0.gif";
		BufferedImage original = null;
		BufferedImage edges = null;
		BufferedImage threshold = null;
		BufferedImage accumulator = null;
		BufferedImage circles = null;
		BufferedImage overlay = null;
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF Images", "gif");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			filename = chooser.getSelectedFile().getAbsolutePath();
		}
		
		original = FileIO.read(filename);
		log.info("Original Image Height: " + original.getHeight());
		log.info("Orignal Image Width: " + original.getWidth());
		
		edges = EdgeDetector.detect(original);
		FileIO.write("../output/edges.gif", edges);
		
		threshold = EdgeDetector.threshold(edges, 127);
		FileIO.write("../output/filtered-edges.gif", threshold);
		
		Accumulator acc = new Accumulator(original, edges, 10, 60);
		accumulator = acc.buildAccumulator();
		acc.detect();
		circles = acc.circles;
		overlay = acc.overlay;
		FileIO.write("../output/overlay.gif", overlay);
		FileIO.write("../output/circles.gif", circles);
		FileIO.write("../output/accumulator.gif", accumulator);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Original", makeImagePanel(original, "Original Image"));
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);   

		tabbedPane.addTab("Edges", makeImagePanel(edges, "Edges Image"));
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		tabbedPane.addTab("Threshold", makeImagePanel(threshold, "Thresholded Edges Image"));
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
	   
		tabbedPane.addTab("Accumulator", makeImagePanel(accumulator, "Accumulator Image"));
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		
		tabbedPane.addTab("Circles", makeImagePanel(circles, "Circles Image"));
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		
		tabbedPane.addTab("Overlay", makeImagePanel(overlay, "Overlay Image"));
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		
	    this.getContentPane().add(tabbedPane);   
	     
		this.setSize(600, 600);
		this.setLocation(100, 100);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				log.info("Goodbye!");
				System.exit(0);
			}
		});
		this.setVisible(true); 
	}
	
	public static void main (String [] args) {
		log.info("Starting up...");
		new Main();
	}
	
	public void saveImage(BufferedImage img)	{
		if(img == null)	return;
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF Images", "gif");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			// save the image
			FileIO.write(chooser.getSelectedFile().getAbsolutePath(), img);
		}
	}
	
	protected JComponent makeImagePanel(final BufferedImage image, String name) {
		JLabel label;
		if (image != null) {
			 label = new JLabel(new ImageIcon(image));
		} else {
			label = new JLabel("No data");
	        label.setHorizontalAlignment(JLabel.CENTER);
		}
        JPanel panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);
        JButton save = new JButton("Save " + name); 
        save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveImage(image);
			}
        });
        panel.add(save, BorderLayout.SOUTH);
        return panel;
    }
}
