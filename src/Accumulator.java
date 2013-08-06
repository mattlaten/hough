import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;


public class Accumulator {
	public static final int BLACK = (255 << 24 | 0 << 16 | 0 << 8 | 0);
	public static final int WHITE = (255 << 24 | 255 << 16 | 255 << 8 | 255);
	public static final int GRAY = (255 << 24 | 8 << 16 | 8 << 8 | 8);
	
	static Logger log = new Logger(Accumulator.class);
	
	BufferedImage input;
	int width;
	int height;
	int [][][] grid;
	int lower;
	int upper;
	BufferedImage accOutput;
	BufferedImage circles;
	BufferedImage overlay;
	
	
	public Accumulator(BufferedImage input, int lower, int upper) {
		this.input = input;
		this.width = input.getWidth();
		this.height = input.getHeight();
		this.lower = lower;
		this.upper = upper;
		grid = new int[upper-lower][height][width];
	}
	
	public BufferedImage buildAccumulator() {	
		if (accOutput == null) {
			accOutput = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					accOutput.setRGB(x, y, BLACK);
				}
			}
			
			//go through radii and points and draw circles if point is "on"
			for (int r = 0; r < upper - lower; r++) {
				int radius = r + lower;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (input.getRGB(x, y) == WHITE) { 
							// we want to draw the circle
							drawCircle(x, y, radius);
						}
					}
				}
			}
			
			//summing across all radii
			int sum [][] = new int [height][width];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					for (int r = 0; r < upper - lower; r++) {
						sum[y][x] += grid[r][y][x];
					}
				}
			}
			
			//finding max
			int maxVal = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					maxVal = Math.max(maxVal, sum[y][x]);
				}
			}
			
			//normalizing and drawing image
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int intensity = 255*sum[y][x]/maxVal;
					Color colour = new Color(intensity, intensity, intensity);
					accOutput.setRGB(x, y, colour.getRGB());
				}
			}

		}
		return accOutput;
	}
	
	public void detect() {
		if (accOutput == null) {
			log.err("Accumulator not built yet");
		}
		
	}
	
	public void drawCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			//System.out.println(x + " >= " + y);
			drawPixel(x + x0, y + y0, radius);
			drawPixel(y + x0, x + y0, radius);
			drawPixel(-x + x0, y + y0, radius);
			drawPixel(-y + x0, x + y0, radius);
			drawPixel(-x + x0, -y + y0, radius);
			drawPixel(-y + x0, -x + y0, radius);
			drawPixel(x + x0, -y + y0, radius);
			drawPixel(y + x0, -x + y0, radius);

			y++;
			if (radiusError < 0)
				radiusError += 2 * y + 1;
			else {
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}
	}
	
	private void drawPixel(int x, int y, int radius) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			grid[radius-lower][y][x]++;
		}
	}
}
