package ro.damianteodorbeles.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilterEngine {

	private final int numberOfThreadsToProcessOn = 4;

	private BufferedImage copyImage(BufferedImage image) {
		BufferedImage returnImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);
		returnImage.getGraphics().drawImage(image, 0, 0, null);
		return returnImage;
	}

	private BufferedImage createBufferedImageFromByteArray(final byte[] pixels, final int imageWidth,
			final int imageHeight) {
		BufferedImage returnImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		returnImage.setData(
				Raster.createRaster(returnImage.getSampleModel(), new DataBufferByte(pixels, pixels.length), null));
		return returnImage;
	}

	private class Processor {

		public void applyNegative(final int startRow, final int endRow, final int startCol, final int endCol,
				final int imageWidth, byte[] pixels, byte[] newPixels) {
			for (int row = startRow; row < endRow; ++row) {
				for (int col = startCol; col < endCol; ++col) {
					int pos = row * 4 * imageWidth + col * 4;

					newPixels[pos] = (byte) 0xFF;
					for (int pixelNumber = 1; pixelNumber < 4; ++pixelNumber)
						newPixels[pos + pixelNumber] = (byte) (255 - (int) (pixels[pos + pixelNumber] & 0xFF));
				}
			}
		}

		public void applyGrayscale(final int startRow, final int endRow, final int startCol, final int endCol,
				final int imageWidth, byte[] pixels, byte[] newPixels) {
			for (int row = startRow; row < endRow; ++row) {
				for (int col = startCol; col < endCol; ++col) {
					int pos = row * 4 * imageWidth + col * 4;

					final float blue = ((int) pixels[pos + 1]) & 0xFF;
					final float green = ((int) pixels[pos + 2]) & 0xFF;
					final float red = ((int) pixels[pos + 3]) & 0xFF;
					byte gray = (byte) (0.299f * red + 0.587f * green + 0.114f * blue);

					newPixels[pos] = (byte) 0xFF;
					for (int pixelNumber = 1; pixelNumber < 4; ++pixelNumber)
						newPixels[pos + pixelNumber] = gray;
				}
			}
		}

		public void applyColorFilter(final int startRow, final int endRow, final int startCol, final int endCol,
				final int imageWidth, byte[] pixels, byte[] newPixels, ColorFilter colorFilter) {
			int offset;
			if (colorFilter == ColorFilter.BLUE)
				offset = 1;
			else if (colorFilter == ColorFilter.GREEN)
				offset = 2;
			else
				offset = 3;

			for (int row = startRow; row < endRow; ++row) {
				for (int col = startCol; col < endCol; ++col) {
					int pos = row * 4 * imageWidth + col * 4;

					newPixels[pos] = (byte) 0xFF;
					for (int pixelNumber = 1; pixelNumber < 4; ++pixelNumber)
						newPixels[pos + pixelNumber] = (byte) 0;
					newPixels[pos + offset] = pixels[pos + offset];
				}
			}
		}

		public void applyKernel(final int startRow, final int endRow, final int startCol, final int endCol,
				final int imageWidth, byte[] pixels, byte[] newPixels, final IKernel kernel) {
			final float[] kernelMatrix = kernel.getKernel();
			final int kernelWidth = kernel.getWidth();
			final int kernelHeight = kernel.getHeight();
			final int[] origin = kernel.getOrigin();

			final int startFromRow = startRow - origin[0];
			final int endAtRow = endRow - origin[0];
			final int startFromCol = startCol - origin[1];
			final int endAtCol = endCol - origin[1];

			for (int row = startFromRow; row < endAtRow; ++row) {
				for (int col = startFromCol; col < endAtCol; ++col) {
					float redSum = 0;
					float greenSum = 0;
					float blueSum = 0;
					for (int npos = 0; npos < kernelMatrix.length; ++npos) {
						int relativeRow = row + (kernelWidth - npos / kernelWidth - 1);
						int relativeCol = col + (kernelWidth - npos % kernelWidth - 1);
						if (relativeRow < startRow)
							relativeRow += kernelHeight;
						else if (relativeRow >= endRow)
							relativeRow -= kernelHeight;
						if (relativeCol < startCol)
							relativeCol += kernelWidth;
						else if (relativeCol >= endCol)
							relativeCol -= kernelWidth;

						int pos = (relativeRow * 4 * imageWidth) + (relativeCol * 4) + 1;
						blueSum += kernelMatrix[npos] * ((int) pixels[pos++] & 0xFF);
						greenSum += kernelMatrix[npos] * ((int) pixels[pos++] & 0xFF);
						redSum += kernelMatrix[npos] * ((int) pixels[pos] & 0xFF);
					}

					int pos = ((row + origin[0]) * 4 * imageWidth) + ((col + origin[1]) * 4);
					newPixels[pos++] = (byte) 0xFF;
					newPixels[pos++] = (byte) blueSum;
					newPixels[pos++] = (byte) greenSum;
					newPixels[pos] = (byte) redSum;
				}
			}
		}
	};

	public BufferedImage processImage(final BufferedImage image, Filter filter) {
		BufferedImage imageToProcess = copyImage(image);
		final int imageWidth = imageToProcess.getWidth();
		final int imageHeight = imageToProcess.getHeight();
		final byte[] pixels = ((DataBufferByte) imageToProcess.getRaster().getDataBuffer()).getData();

		final byte[] newPixels = new byte[pixels.length];

		ExecutorService processImageMultithread = Executors.newFixedThreadPool(numberOfThreadsToProcessOn);
		CompletionService<Void> processImageMultithreadCompletition = new ExecutorCompletionService<Void>(
				processImageMultithread);

		for (int threadIndex = 0; threadIndex < numberOfThreadsToProcessOn; ++threadIndex) {
			final int indexer = threadIndex;
			processImageMultithreadCompletition.submit(new Callable<Void>() {
				public Void call() {
					final int startRow = indexer * (imageHeight / numberOfThreadsToProcessOn);
					final int endRow = (indexer + 1) * (imageHeight / numberOfThreadsToProcessOn);
					final int startCol = 0;
					final int endCol = imageWidth;

					switch (filter) {
					case BOX_BLUR:
						new Processor().applyKernel(startRow, endRow, startCol, endCol, imageWidth, pixels, newPixels,
								new KernelFactory().BOX_BLUR());
						break;
					case GAUSSIAN_BLUR:
						new Processor().applyKernel(startRow, endRow, startCol, endCol, imageWidth, pixels, newPixels,
								new KernelFactory().GAUSSIAN_BLUR());
						break;
					case EDGE_DETECTION:
						new Processor().applyKernel(startRow, endRow, startCol, endCol, imageWidth, pixels, newPixels,
								new KernelFactory().EDGE_DETECTION());
						break;
					case SHARPEN:
						new Processor().applyKernel(startRow, endRow, startCol, endCol, imageWidth, pixels, newPixels,
								new KernelFactory().SHARPEN());
						break;
					case EMBOSS:
						new Processor().applyKernel(startRow, endRow, startCol, endCol, imageWidth, pixels, newPixels,
								new KernelFactory().EMBOSS());
						break;
					case BLUE_COLORING:
						new Processor().applyColorFilter(startRow, endRow, startCol, endCol, imageWidth, pixels,
								newPixels, ColorFilter.BLUE);
						break;
					case GREEN_COLORING:
						new Processor().applyColorFilter(startRow, endRow, startCol, endCol, imageWidth, pixels,
								newPixels, ColorFilter.GREEN);
						break;
					case RED_COLORING:
						new Processor().applyColorFilter(startRow, endRow, startCol, endCol, imageWidth, pixels,
								newPixels, ColorFilter.RED);
						break;
					case GRAYSCALE:
						new Processor().applyGrayscale(startRow, endRow, startCol, endCol, imageWidth, pixels,
								newPixels);
						break;
					case NEGATIVE:
						new Processor().applyNegative(startRow, endRow, startCol, endCol, imageWidth, pixels,
								newPixels);
						break;
					default:
						break;
					}
					return null;
				}
			});
		}

		for (int threadNumber = 0; threadNumber < numberOfThreadsToProcessOn; ++threadNumber) {
			try {
				processImageMultithreadCompletition.take();
			} catch (InterruptedException e) {
				System.out.println("Exception caught while processing the image on multiple threads.");
			}
		}

		return createBufferedImageFromByteArray(newPixels, imageWidth, imageHeight);
	}
}