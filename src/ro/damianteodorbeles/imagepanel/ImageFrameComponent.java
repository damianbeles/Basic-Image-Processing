package ro.damianteodorbeles.imagepanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageFrameComponent extends JPanel {
	
	private final BufferedImage image;
	
	public ImageFrameComponent(final BufferedImage image, final Dimension size) {
		final BufferedImage scaledImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		scaledImage.getGraphics().drawImage(image.getScaledInstance(size.width, size.height, BufferedImage.SCALE_SMOOTH), 0, 0, null);
		this.image = scaledImage;
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.drawImage(image, 0, 0, null);
	}
}
