import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ro.damianteodorbeles.imagepanel.ImageFrame;

public class MainActivity {

	static final int PREFERRED_WIDTH = 600;
	static final int PREFERRED_HEIGHT = 450;

	static BufferedImage getImageFromFileChooser() {
		BufferedImage image = null;

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg) | (*.gif) | (*.png)", "jpg", "gif", "png"));

		final int dialogResult = fileChooser.showOpenDialog(null);
		if(dialogResult == JFileChooser.APPROVE_OPTION) {
			try {
				image = ImageIO.read(fileChooser.getSelectedFile().getAbsoluteFile());
			}
			catch (IOException exception) {
				System.out.println("Couldn't read the file containing the image!");
			}
		}

		return image;
	}

	static BufferedImage processImage(final BufferedImage image) {
		return null;
	}

	public static void main(String args[]) {
		ImageFrame originalImageFrame = new ImageFrame(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		originalImageFrame.createImageWindow("Original Image", getImageFromFileChooser());

		ImageFrame processedImageFrame = new ImageFrame(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		processedImageFrame.createImageWindow("Processed Image", processImage(originalImageFrame.getImage()));
	}
}
