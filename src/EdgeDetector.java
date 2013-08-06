import java.awt.image.BufferedImage;


public class EdgeDetector {
	BufferedImage image;
	
	public static final int BLACK = (255 << 24 | 0 << 16 | 0 << 8 | 0);
	public static final int WHITE = (255 << 24 | 255 << 16 | 255 << 8 | 255);
	
	public EdgeDetector(BufferedImage image) {
		this.image = image;
	}
	
	public static BufferedImage detect(BufferedImage input) {
		int i, j;
		int Gx[][], Gy[][], G[][];
		int width = input.getWidth();
		int height = input.getHeight();
		int[] pixels = new int[width * height];
		int[][] output = new int[width][height];
		input.getRaster().getPixels(0, 0, width, height, pixels);

		int counter = 0;

		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				output[i][j] = (input.getRGB(i, j) & 0xff0000) >> 16;//pixels[counter];
				//System.out.print(output[i][j] + " ");		
				//counter++;
			}
			//System.out.println();
		}

		Gx = new int[width][height];
		Gy = new int[width][height];
		G = new int[width][height];

		BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (i == 0 || i == width - 1 || j == 0 || j == height - 1)
					Gx[i][j] = Gy[i][j] = G[i][j] = 0; // Image boundary cleared
				else {
					Gx[i][j] = output[i + 1][j - 1] + 2 * output[i + 1][j]
							+ output[i + 1][j + 1] - output[i - 1][j - 1] - 2
							* output[i - 1][j] - output[i - 1][j + 1];
					Gy[i][j] = output[i - 1][j + 1] + 2 * output[i][j + 1]
							+ output[i + 1][j + 1] - output[i - 1][j - 1] - 2
							* output[i][j - 1] - output[i + 1][j - 1];
					
					Gx[i][j] = Math.max(Math.min(Math.abs(Gx[i][j]), 255), 0);
					Gy[i][j] = Math.max(Math.min(Math.abs(Gy[i][j]), 255), 0);
					
					//G[i][j] = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
					G[i][j] = (int) Math.sqrt((Gx[i][j])*(Gx[i][j]) + (Gy[i][j])*(Gy[i][j]));
					G[i][j] = Math.max(Math.min(Math.abs(G[i][j]), 255), 0);
				
					outImg.setRGB(i, j, (255 << 24 | G[i][j] << 16 | G[i][j] << 8 | G[i][j]));
				}
			}
		}
		
		/*
		for (int ii = 0; ii < width; ii++) {
			for (int jj = 0; jj < height; jj++) {
				// System.out.println(counter);
				pixels[counter] = (int) G[ii][jj];
				counter++;
			}
		}
		*/
		return outImg;
	}
	
	public static BufferedImage threshold(BufferedImage input, int value) {
		int width = input.getWidth();
		int height = input.getHeight();
		BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				outImg.setRGB(i, j, (((input.getRGB(i, j) & 0xff0000) >> 16) > value ? WHITE : BLACK));
				//counter++;
			}
		}
		return outImg;
	}
	
	private static void writeOut(BufferedImage input, int[][] array, String name) {
		int width = input.getWidth();
		int height = input.getHeight();
		BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		int pixels[] = new int[width*height];
		int count = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[count] = array[i][j];
				count++;
			}
		}
		outImg.getRaster().setPixels(0, 0, width, height, pixels);
		FileIO.write(name, outImg);
	}
}
