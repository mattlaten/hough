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
		
	}
}
