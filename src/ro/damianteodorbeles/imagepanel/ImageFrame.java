package ro.damianteodorbeles.imagepanel;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageFrame extends JPanel {

	private Dimension size;
	private BufferedImage image;

	public ImageFrame(Dimension size) {
		setSize(size);
	}

	public void setSize(final Dimension size) {
		this.size = size;
	}

	public Dimension getSize() {
		return new Dimension(this.size);
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public JFrame createImageWindow(final String title, final BufferedImage image) {
		this.image = image;

		final JFrame frame = new JFrame(title);
		frame.add(new ImageFrameComponent(image, size));
		frame.setResizable(false);
		frame.setSize(this.size);
		frame.setVisible(true);

		return frame;
	}
}
