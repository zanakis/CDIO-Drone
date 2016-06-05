package de.yadrone.apps.paperchase;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.video.ImageListener;

public class ObjectDetection implements ImageListener {

	private void setImage(final BufferedImage image)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
//				repaint();
			}
		});
	}
	
		public void imageUpdated(BufferedImage image)
		{
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);			 
			 byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			 Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
			 Mat gray = new Mat();
			 frame.put(0, 0, data);

			 Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY );
			 Imgproc.GaussianBlur(gray, gray ,new Size(9, 9), 2, 2 );

			 Mat circles = new Mat();
			 Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, gray.rows()/8, 200, 100, 0, 0);

			 if (circles.cols() > 0){
			    for (int x = 0; x < circles.cols(); x++) 
			        {
			        double vCircle[] = circles.get(0,x);

			        if (vCircle == null)
			            break;

			        Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			        int radius = (int)Math.round(vCircle[2]);

			        
			        Imgproc.circle(frame, pt, radius, new Scalar(0,255,0), 1);
			        Imgproc.circle(frame, pt, 3, new Scalar(0,0,255), 1);
			        
			        
			        System.out.println( 26 * 1000 * frame.height()/ ((radius*2)* 23));
			        }
			 }	
			 MatOfByte buffer = new MatOfByte();
				Imgcodecs.imencode(".png", frame, buffer);
				InputStream in = new ByteArrayInputStream(buffer.toArray());
				BufferedImage img;
				try {
					img = ImageIO.read(in);
					setImage(img);
				} catch (IOException e) {
					e.printStackTrace();
				}
		};
}
