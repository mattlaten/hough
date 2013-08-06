import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileIO {
	static Logger log = new Logger(FileIO.class);
	public static BufferedImage read(String filename) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filename));
		} catch (IOException e) {
			log.err(e.getMessage());
		}
		return image;
	}
	
	public static void write(String filename, BufferedImage image) {
		try {
		    FileOutputStream outFile = new FileOutputStream(filename);   
		    ImageIO.write(image,"gif",outFile);   
		} catch (IOException e) {
			log.err(e.getMessage());
		}
	}
}
