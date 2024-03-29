import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;


public class Accumulator {
	public static final int BLACK = (255 << 24 | 0 << 16 | 0 << 8 | 0);
	public static final int RED = (255 << 24 | 255 << 16 | 0 << 8 | 0);
	public static final int WHITE = (255 << 24 | 255 << 16 | 255 << 8 | 255);
	public static final int GRAY = (255 << 24 | 8 << 16 | 8 << 8 | 8);
	
	static Logger log = new Logger(Accumulator.class);
	
	BufferedImage original;
	BufferedImage input;
	int width;
	int height;
	int [][][] acc;
	int lower;
	int upper;
	int paddedwidth;
	int paddedheight;
	BufferedImage accImage;
	BufferedImage circles;
	BufferedImage overlay;
	
	
	public Accumulator(BufferedImage original, BufferedImage input, int lower, int upper) {
		this.original = original;
		this.input = input;
		this.width = input.getWidth();
		this.height = input.getHeight();
		this.lower = lower;
		this.upper = upper;
		this.paddedwidth = width+2*upper;
		this.paddedheight = height+2*upper;
		acc = new int[upper-lower][paddedheight][paddedwidth];
	}
	
	public BufferedImage buildAccumulator() {	
		log.info("Building accumulator...");
		if (accImage == null) {
			accImage = new BufferedImage(paddedwidth, paddedheight, BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < paddedheight; y++) {
				for (int x = 0; x < paddedwidth; x++) {
					accImage.setRGB(x, y, BLACK);
				}
			}
			
			//go through radii and points and draw circles if point is "on"
			for (int r = 0; r < upper - lower; r++) {
				int radius = r + lower;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (input.getRGB(x, y) == WHITE) { 
							// we want to draw the circle
							drawCircle(x+upper, y+upper, radius);
						}
					}
				}
			}
			
			//summing across all radii
			int sum [][] = new int [paddedheight][paddedwidth];
			for (int y = 0; y < paddedheight; y++) {
				for (int x = 0; x < paddedwidth; x++) {
					for (int r = 0; r < upper - lower; r++) {
						sum[y][x] += acc[r][y][x];
					}
				}
			}
			
			//finding max
			int maxVal = 0;
			for (int y = 0; y < paddedheight; y++) {
				for (int x = 0; x < paddedwidth; x++) {
					maxVal = Math.max(maxVal, sum[y][x]);
				}
			}
			
			//normalizing and drawing image
			for (int y = 0; y < paddedheight; y++) {
				for (int x = 0; x < paddedwidth; x++) {
					int intensity = 255*sum[y][x]/maxVal;
					Color colour = new Color(intensity, intensity, intensity);
					accImage.setRGB(x, y, colour.getRGB());
				}
			}

		}
		log.info("Accumulator built successfully!");
		return accImage;
	}
	
	public void detect() {
		if (accImage == null) {
			log.err("Accumulator not built yet");
		}
		log.info("Detecting circles...");
		circles = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				circles.setRGB(x, y, BLACK);
				overlay.setRGB(x,y, original.getRGB(x, y));
			}
		}
		
		for (int r = 0; r < upper - lower; r++) {
			int radius = r + lower;
			for (int y = 0; y < paddedheight; y++) {
				for (int x = 0; x < paddedwidth; x++) {
					//compute percent of circle in image here
					double vvis = 1.0;
					double hvis = 1.0;
					int xcenter = x-upper;
					int ycenter = y-upper;
					
					if (Math.abs(xcenter) <= radius+2) {
						hvis = (xcenter+radius)*Math.PI/4/(2*radius);
					}
					if (Math.abs(ycenter) <= radius+2) {
						vvis = (ycenter+radius)*Math.PI/4/(2*radius);
					}
					if (Math.abs(width-xcenter) <= radius+2) {
						hvis = (width-xcenter+radius)*Math.PI/4/(2*radius);
					}
					if (Math.abs(height-ycenter) <= radius+2) {
						vvis = (height-ycenter+radius)*Math.PI/4/(2*radius);
					}
					//detect center of circle given how much of circle is in image
					if (acc[r][y][x] > (vvis == 0 ? 1 : vvis)*(hvis == 0 ? 1 : hvis)*1.8*Math.PI*radius) {
						if (xcenter >= 0 && ycenter >= 0 && xcenter < width && ycenter < height) {
							overlay.setRGB(xcenter, ycenter, RED);
							overlayCircle(xcenter, ycenter, radius);
						}
					}
				}
			}
		}
		log.info("Circles detected!");
		//must build circles and overlay images
		
	}
	
	public void overlayCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			overlayPixel(x + x0, y + y0, radius);
			overlayPixel(y + x0, x + y0, radius);
			overlayPixel(-x + x0, y + y0, radius);
			overlayPixel(-y + x0, x + y0, radius);
			overlayPixel(-x + x0, -y + y0, radius);
			overlayPixel(-y + x0, -x + y0, radius);
			overlayPixel(x + x0, -y + y0, radius);
			overlayPixel(y + x0, -x + y0, radius);

			y++;
			if (radiusError < 0)
				radiusError += 2 * y + 1;
			else {
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}
	}
	
	private void overlayPixel(int x, int y, int radius) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			overlay.setRGB(x, y, RED);
			circles.setRGB(x, y, RED);
		}
	}
	
	public void drawCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
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
		acc[radius-lower][y][x]++;
	}
}
