Hough Transform Circle Detector - Matthew Laten (LTNMAT001)
===============================

Problem Approach
-------------------------------
In order to detect edges, I used a standard Sobel filter applied
over the entire input image. This extracted all the edges with 
some noise (as can be seen in output/edges.gif). Thus, I ran
thresholding over the image with threshold levels set to halfway.
The effects this made can be seen in output/threshold.gif.

This image then gets piped into the accumulator for the hough 
transform, and for each point that is "on" in the edge-detected
image, a circle is drawn into each radius-based slice 
of the 3d accumulator. The brightest points in this image 
correspond to the centers of circles that are fully contained
in the image. In fact, we expect the intesity to be near
the size of the circumfrence of the circle (2 x pi x r), so we
only need to search for points near that value in intensity. 
For circles partially present in the image, we multiply this
threshold by a constant factor, calculated by how much of the circle
lies inside the image, and validate potential centers against this
modified value. This logic can be seen in the Accumulator class,
under the detect method.

Once the circles are detected, the are written out to files and
overlayed on the original image. All output images can be seen
in the output directory.

Issues
-------------------------------
The accumulator seemed to work better with the non-thresholded
edge-detected image, so in the final version the non-thresholded
image was used for circle detection.

The circle detector works on images fully contained within the
image as well as circles with their centers contained within the
image, but if a center of a potential circle is outside the image,
such a circle is not picked up...

How to run
-------------------------------
1) Navigate to the "jars" directory in a shell
2) Execute the jar: "java -jar circle-detector.jar"
3) Select file you want to process in dialog; cancelling will run the circle detector on "../data/test0.gif"
4) Use arrow keys to navigate tabs
5) Click the "Save Image" button to save the currently viewed image



