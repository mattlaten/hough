import java.awt.image.BufferedImage;
import java.io.File;
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
}
