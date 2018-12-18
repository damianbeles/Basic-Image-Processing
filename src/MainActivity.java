import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ro.damianteodorbeles.imagepanel.ImageFrame;
import ro.damianteodorbeles.imageprocessing.FilterEngine;
import ro.damianteodorbeles.imageprocessing.KernelFactory;

public class MainActivity {

	static final int PREFERRED_WIDTH = 600;
	static final int PREFERRED_HEIGHT = 450;

	static BufferedImage getImageFromFileChooser() {
		BufferedImage image = null;

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("Image Files (*.jpg) | (*.gif) | (*.png)", "jpg", "gif", "png"));

		final int dialogResult = fileChooser.showOpenDialog(null);
		if (dialogResult == JFileChooser.APPROVE_OPTION) {
			try {
				image = ImageIO.read(fileChooser.getSelectedFile().getAbsoluteFile());
			} catch (IOException exception) {
				System.out.println("Couldn't read the file containing the image!");
			}
		}

		return image;
	}

	static BufferedImage processImage(final BufferedImage image) {
		FilterEngine filterEngine = new FilterEngine();
		return filterEngine.applyKernel(image, new KernelFactory().EDGE_DETECTION());
	}

	public static void main(String args[]) {
		Dimension preferredDimension = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
		ImageFrame originalImageFrame = new ImageFrame(preferredDimension);
		originalImageFrame.createImageWindow("Original Image", getImageFromFileChooser());

		ImageFrame processedImageFrame = new ImageFrame(preferredDimension);
		processedImageFrame.createImageWindow("Processed Image", processImage(originalImageFrame.getImage()));
	}
}
