package ro.damianteodorbeles.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

public class FilterEngine {
	
	public BufferedImage applyKernel(final BufferedImage image, final IKernel kernel) {
		BufferedImage imageToProcess = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		imageToProcess.getGraphics().drawImage(image, 0, 0, null);

		final double[] kernelMatrix = kernel.getKernel();
		final int kernelWidth = kernel.getWidth();
		final int kernelHeight = kernel.getHeight();
		final int[] origin = kernel.getOrigin();

		final int imageWidth = imageToProcess.getWidth();
		final int imageHeight = imageToProcess.getHeight();
		final byte[] pixels = ((DataBufferByte) imageToProcess.getRaster().getDataBuffer()).getData();

	    final byte[] newPixels = new byte[pixels.length];

		for (int row = 0; row < imageHeight - kernelHeight; ++row) {
			for (int col = 0; col < imageWidth - kernelWidth; ++col) {
				double redSum = 0;
				double greenSum = 0;
				double blueSum = 0;

				for (int npos = 0; npos < kernelMatrix.length; ++npos) {
					final int relativeRow = row + (kernelWidth - npos / kernelWidth - 1);
					final int relativeCol = col + (kernelHeight - npos % kernelHeight - 1);

					int pos = (relativeRow * 4 * imageWidth) + (relativeCol * 4) + 1;
					redSum += kernelMatrix[npos] * ((int)pixels[pos++] & 0xFF);
					greenSum += kernelMatrix[npos] * ((int)pixels[pos++] & 0xFF);
					blueSum += kernelMatrix[npos] * ((int)pixels[pos] & 0xFF);
				}

				int pos = ((row + origin[0]) * 4 * imageWidth) + ((col + origin[1]) * 4);
				newPixels[pos++] = (byte)0xFF;
				newPixels[pos++] = (byte)redSum;
				newPixels[pos++] = (byte)greenSum;
				newPixels[pos] = (byte)blueSum;
			}
		}

	    BufferedImage processedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR_PRE);
	    processedImage.setData(Raster.createRaster(processedImage.getSampleModel(), new DataBufferByte(newPixels, newPixels.length), null));
	    return processedImage;
	}
}