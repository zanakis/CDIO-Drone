package de.yadrone.apps.tutorial;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ObjectDetection {
		@FXML
		private Button cameraButton;
		@FXML
		private ImageView originalFrame;
		@FXML
		private ImageView maskImage;
		@FXML
		private ImageView morphImage;
		@FXML
		private Slider hueStart;
		@FXML
		private Slider hueStop;
		@FXML
		private Slider saturationStart;
		@FXML
		private Slider saturationStop;
		@FXML
		private Slider valueStart;
		@FXML
		private Slider valueStop;
		@FXML
		private Label hsvCurrentValues;
		private ScheduledExecutorService timer;
		private VideoCapture capture = new VideoCapture();
		private boolean cameraActive;
		private ObjectProperty<String> hsvValuesProp;
			
		@FXML
		private void startCamera()
		{
			// bind a text property with the string containing the current range of
			// HSV values for object detection
			hsvValuesProp = new SimpleObjectProperty<String>();
			this.hsvCurrentValues.textProperty().bind(hsvValuesProp);
					
			// set a fixed width for all the image to show and preserve image ratio
			this.imageViewProperties(this.originalFrame, 400);
			this.imageViewProperties(this.maskImage, 200);
			this.imageViewProperties(this.morphImage, 200);
			
			if (!this.cameraActive)
			{
				this.capture.open(0);
				 
				if (this.capture.isOpened())
				{
					this.cameraActive = true;
					
					Runnable frameGrabber = new Runnable() {
						
						@Override
						public void run()
						{
							Image imageToShow = grabFrame();
							originalFrame.setImage(imageToShow);
						}
					};
					
					this.timer = Executors.newSingleThreadScheduledExecutor();
					this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
					this.cameraButton.setText("Stop Camera");
				}
				else
				{
					System.err.println("Failed to open the camera connection...");
				}
			}
			else
			{
				this.cameraActive = false;
				this.cameraButton.setText("Start Camera");
				
				try
				{
					this.timer.shutdown();
					this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException e)
				{
					System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
				}
				
				this.capture.release();
			}
		}
		private Image grabFrame()
		{
			Image imageToShow = null;
			Mat frame = new Mat();
			if (this.capture.isOpened())
			{
				try
				{
					this.capture.read(frame);
					if (!frame.empty())
					{
						Mat blurredImage = new Mat();
						Mat hsvImage = new Mat();
						Mat mask = new Mat();
						Mat morphOutput = new Mat();
						
						Imgproc.blur(frame, blurredImage, new Size(7, 7));
						
						Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
						
						Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
								this.valueStart.getValue());
						Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
								this.valueStop.getValue());
						
						String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
								+ "\tSaturation range: " + minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: "
								+ minValues.val[2] + "-" + maxValues.val[2];
						this.onFXThread(this.hsvValuesProp, valuesToPrint);
						
						Core.inRange(hsvImage, minValues, maxValues, mask);
						this.onFXThread(this.maskImage.imageProperty(), this.mat2Image(mask));
						
						Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
						Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
						
						Imgproc.erode(mask, morphOutput, erodeElement);
						Imgproc.erode(mask, morphOutput, erodeElement);
						
						Imgproc.dilate(mask, morphOutput, dilateElement);
						Imgproc.dilate(mask, morphOutput, dilateElement);
						
						this.onFXThread(this.morphImage.imageProperty(), this.mat2Image(morphOutput));
						
						frame = this.findAndDrawBalls(morphOutput, frame);
						
						imageToShow = mat2Image(frame);
					}
					
				}
				catch (Exception e)
				{
					System.err.print("ERROR");
					e.printStackTrace();
				}
			}
			
			return imageToShow;
		}
		private Mat findAndDrawBalls(Mat maskedImage, Mat frame)
		{
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
			if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
			{
				for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
				{
					Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
				}
			}
			
			return frame;
		}
		private void imageViewProperties(ImageView image, int dimension)
		{
			image.setFitWidth(dimension);
			image.setPreserveRatio(true);
		}
		
		private Image mat2Image(Mat frame)
		{
			MatOfByte buffer = new MatOfByte();
			Imgcodecs.imencode(".png", frame, buffer);
			return new Image(new ByteArrayInputStream(buffer.toArray()));
		}
		
		private <T> void onFXThread(final ObjectProperty<T> property, final T value)
		{
			Platform.runLater(new Runnable() {
				
				@Override
				public void run()
				{
					property.set(value);
				}
			});
		}
}
