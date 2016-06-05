//package application;
//
//
//import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import javax.imageio.ImageIO;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//
//
//public class Javaforsoeg {
//		
//		private double hueSt = 43.61;
//		private double hueSp = 180;
//		private double SST = 43.29;
//		private double SSP = 200;
//		private double vst = 0;
//		private double vsp = 129.59;
//		
//			
//		/**
//		 * The action triggered by pushing the button on the GUI
//		 */
//		
//		private BufferedImage grabFrame()
//		{
//			Mat frame = new Mat();
//			if (this.capture.isOpened())
//			{
//				try
//				{
//					this.capture.read(frame);
//					if (!frame.empty())
//					{
//						Mat blurredImage = new Mat();
//						Mat hsvImage = new Mat();
//						Mat mask = new Mat();
//						Mat morphOutput = new Mat();
//						
//						Imgproc.blur(frame, blurredImage, new Size(7, 7));
//						
//						Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
//						
//						Scalar minValues = new Scalar(hueSt, SST,
//								vst);
//						Scalar maxValues = new Scalar(hueSp, SSP,
//								vsp);
//						
//						Core.inRange(hsvImage, minValues, maxValues, mask);
//						
//						Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
//						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
//						
//						Imgproc.erode(mask, morphOutput, erodeElement);
//						Imgproc.erode(mask, morphOutput, erodeElement);
//						
//						Imgproc.dilate(mask, morphOutput, dilateElement);
//						Imgproc.dilate(mask, morphOutput, dilateElement);
//						
//						
//						frame = this.findAndDrawBalls(morphOutput, frame);
//						BufferedImage imageToShow = mat2Image(frame);
//
//					}
//					
//				}
//				catch (Exception e)
//				{
//					System.err.print("ERROR");
//					e.printStackTrace();
//				}
//			}
//			
//			return imageToShow;
//		}
//		private Mat findAndDrawBalls(Mat maskedImage, Mat frame)
//		{
//			List<MatOfPoint> contours = new ArrayList<>();
//			Mat hierarchy = new Mat();
//			Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//			if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
//			{
//				for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
//				{
//					Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
//				}
//			}
//			
//			return frame;
//		}
//
//		
//		private BufferedImage mat2Image(Mat frame) throws IOException
//		{
//			MatOfByte buffer = new MatOfByte();
//			Imgcodecs.imencode(".png", frame, buffer);
//			InputStream in = new ByteArrayInputStream(buffer.toArray());
//			BufferedImage img = ImageIO.read(in);
//			return img;
//		}
//		
//	
//}
