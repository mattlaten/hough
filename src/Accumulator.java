import java.awt.image.BufferedImage;


public class Accumulator {
	BufferedImage input;
	int width;
	int height;
	int [][][] grid;
	int lower;
	int upper;
	BufferedImage output;
	
	
	public Accumulator(BufferedImage input, int lower, int upper) {
		this.input = input;
		this.width = input.getWidth();
		this.height = input.getHeight();
		this.lower = lower;
		this.upper = upper;
		grid = new int[upper-lower][height][width];
	}
	
	public BufferedImage process() {
		
		int[] pixels = new int[width * height];
		input.getRaster().getPixels(0, 0, width, height, pixels);
	
		if (output == null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < height; x++) {
					if (pixels[y*width+x] == 1) { // we want to draw the circle
						for (int r = 0; r < upper - lower; r++) {
							int radius = r + lower;
							for (int yy = 0; yy < height; yy++) {
								for (int xx = 0; xx < width; xx++) {
									// draw circle around point in image
									drawCircle(xx, yy, radius);
								}
							}
						}
					}
				}
			}
		}
		return output;
	}
	
	public void drawCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			System.out.println(x + " >= " + y);
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
		if (x >= 0 && y >= 0 && x < width && y < height)
			grid[radius-lower][y][x]++;
	}
}
