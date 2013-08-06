import java.awt.image.BufferedImage;


public class Main {
	static String filename = "";
	static Logger log = new Logger(Main.class);
	
	public static void main (String [] args) {
		if (args.length > 0) {
			filename = args[0];
		} else {
			filename = "data/testseq100000.gif";
		}
		BufferedImage image = FileIO.read(filename);
		log.info(image.getHeight());
		log.info(image.getWidth());
		
		//CircleDetector detect = new CircleDetector(image);
		BufferedImage out = EdgeDetector.detect(image);
		FileIO.write("output/test.gif", out);
		
		BufferedImage thresh = EdgeDetector.threshold(out, 127);
		FileIO.write("output/test2.gif", thresh);
		
		//Accumulator acc = new Accumulator(out, 10, 15);
		//BufferedImage accImage = acc.process();
		//FileIO.write("output/test3.gif", accImage);
		 /*
	    JFrame TheFrame = new JFrame("¼v¹³¡G¼e " + width + " °ª " + height);   
	   
	    JLabel TheLabel = new JLabel(new ImageIcon(outImg));   
	    TheFrame.getContentPane().add(TheLabel);   
	     
	    
	      TheFrame.setSize(600, 600);   
	       
	      TheFrame.addWindowListener(new WindowAdapter() {   
	          public void windowClosing(WindowEvent e) {   
	            System.exit(0);                 
	          }   
	        });           
	      TheFrame.setVisible(true);   
	      */
		
	}
}
