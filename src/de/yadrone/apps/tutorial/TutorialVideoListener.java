package de.yadrone.apps.tutorial;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
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

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.DroneCommand;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;

public class TutorialVideoListener extends JFrame
{
	
	
	private BufferedImage image = null;

	public TutorialVideoListener(final IARDrone drone)
	{
		super("YADrone Tutorial");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		setBounds(100, 100, 800, 600);
		setVisible(true);

		drone.getVideoManager().addImageListener(new ImageListener() {
			public void imageUpdated(BufferedImage newImage)
			{
				image = newImage;
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						repaint();
					}
				});
			}	
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);


			}
		});
		


		// close the 
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) 
			{
				drone.stop();
				System.exit(0);
			}
		});
	}
	
	private void setImage(final BufferedImage image)
	{
		this.image = image;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				repaint();
			}
		});
	}
	double distanceToObject;
	private IARDrone drone;
	private ImageListener imageListener = new ImageListener() {
		public void imageUpdated(BufferedImage image)
		{
			
			 byte[] pixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			 Mat frame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
			 Mat gray = new Mat();
			 frame.put(0, 0, pixel);
				
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
			        
			        distanceToObject = 4.45 * 750 * frame.height()/ ((radius*2)*3.17);
			        
			        System.out.println(distanceToObject);
			        
			        }
			  
				
			 setImage(mat2img(frame));
					
			 }
		}
	};
	
	public Double getDistToObject() {
		return distanceToObject;
	}
	
	public BufferedImage mat2img(Mat input){
	 Mat mat = input;
	 byte[] data = new byte[mat.rows()*mat.cols()*(int)(mat.elemSize())];
	 mat.get(0, 0, data);
	 if (mat.channels() == 3) {
	 for (int i = 0; i < data.length; i += 3) {
	 byte temp = data[i];
	 data[i] = data[i + 2];
	 data[i + 2] = temp;
	 }
	 }
	 BufferedImage img = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
	 img.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
		
		return img;	
	}
	
	public ImageListener getvideo(){
		return imageListener;

	}


	public synchronized void paint(Graphics g)
	{
		if (image != null)
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}
}
