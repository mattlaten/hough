import java.awt.image.BufferedImage;


public class EdgeDetector {
	BufferedImage image;
	
	public static final int BLACK = (255 << 24 | 0 << 16 | 0 << 8 | 0);
	public static final int WHITE = (255 << 24 | 255 << 16 | 255 << 8 | 255);
	static Logger log = new Logger(EdgeDetector.class);
	
	public EdgeDetector(BufferedImage image) {
		this.image = image;
	}
	
	public static BufferedImage detect(BufferedImage input) {
		log.info("Detecting edges...");
		int i, j;
		int Gx[][], Gy[][], G[][];
		int width = input.getWidth();
		int height = input.getHeight();
		int[] pixels = new int[width * height];
		int[][] output = new int[width][height];
		input.getRaster().getPixels(0, 0, width, height, pixels);

		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				output[i][j] = (input.getRGB(i, j) & 0xff0000) >> 16;
			}
		}

		Gx = new int[width][height];
		Gy = new int[width][height];
		G = new int[width][height];

		BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		log.info("Applying Sobel Filter...");
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (i == 0 || i == width - 1 || j == 0 || j == height - 1)
					Gx[i][j] = Gy[i][j] = G[i][j] = 0; // Image boundary cleared
				else {
					//apply sobel filter to image
					Gx[i][j] = output[i + 1][j - 1] + 2 * output[i + 1][j]
							+ output[i + 1][j + 1] - output[i - 1][j - 1] - 2
							* output[i - 1][j] - output[i - 1][j + 1];
					Gy[i][j] = output[i - 1][j + 1] + 2 * output[i][j + 1]
							+ output[i + 1][j + 1] - output[i - 1][j - 1] - 2
							* output[i][j - 1] - output[i + 1][j - 1];
					
					Gx[i][j] = Math.max(Math.min(Math.abs(Gx[i][j]), 255), 0);
					Gy[i][j] = Math.max(Math.min(Math.abs(Gy[i][j]), 255), 0);
					
					G[i][j] = (int) Math.sqrt((Gx[i][j])*(Gx[i][j]) + (Gy[i][j])*(Gy[i][j]));
					G[i][j] = Math.max(Math.min(Math.abs(G[i][j]), 255), 0);
				
					outImg.setRGB(i, j, (255 << 24 | G[i][j] << 16 | G[i][j] << 8 | G[i][j]));
				}
			}
		}
		log.info("Edges Detected!");
		return outImg;
	}
	
	public static BufferedImage threshold(BufferedImage input, int value) {
		log.info("Thresholding edges...");
		int width = input.getWidth();
		int height = input.getHeight();
		BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				outImg.setRGB(i, j, (((input.getRGB(i, j) & 0xff0000) >> 16) > value ? WHITE : BLACK));
			}
		}
		log.info("Edges thresholded!");
		return outImg;
	}
}
