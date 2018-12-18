package ro.damianteodorbeles.imageprocessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FilterEngine {

	public BufferedImage applyKernel(final BufferedImage image, final IKernel kernel) {
		BufferedImage imageToProcess = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_4BYTE_ABGR_PRE);
		imageToProcess.getGraphics().drawImage(image, 0, 0, null);

		final float[] kernelMatrix = kernel.getKernel();
		final int kernelWidth = kernel.getWidth();
		final int kernelHeight = kernel.getHeight();
		final int[] origin = kernel.getOrigin();

		final int imageWidth = imageToProcess.getWidth();
		final int imageHeight = imageToProcess.getHeight();

		if (imageWidth < kernelWidth || imageHeight < kernelHeight) {
			System.out.println("Can't process the image with the given kernel. Kernel too big.");
			return null;
		}

		final byte[] pixels = ((DataBufferByte) imageToProcess.getRaster().getDataBuffer()).getData();

		final byte[] newPixels = new byte[pixels.length];

		ExecutorService applyKernelOnFourThreads = Executors.newFixedThreadPool(4);
		CompletionService<Void> applyKernelOnFourThreadsCompletition = new ExecutorCompletionService<Void>(
				applyKernelOnFourThreads);

		for (int threadIndex = 0; threadIndex < 4; ++threadIndex) {
			final int indexer = threadIndex;
			applyKernelOnFourThreadsCompletition.submit(new Callable<Void>() {
				public Void call() {
					final int startRow = indexer * (imageHeight >> 2);
					final int endRow = (indexer + 1) * (imageHeight >> 2);
					final int startCol = 0;
					final int endCol = imageWidth;

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
								redSum += kernelMatrix[npos] * ((int) pixels[pos++] & 0xFF);
								greenSum += kernelMatrix[npos] * ((int) pixels[pos++] & 0xFF);
								blueSum += kernelMatrix[npos] * ((int) pixels[pos] & 0xFF);
							}

							int pos = ((row + origin[0]) * 4 * imageWidth) + ((col + origin[1]) * 4);
							newPixels[pos++] = (byte) 0xFF;
							newPixels[pos++] = (byte) redSum;
							newPixels[pos++] = (byte) greenSum;
							newPixels[pos] = (byte) blueSum;
						}
					}
					return null;
				}
			});
		}

		int numberOfThreadsCompleted = 0;
		while (numberOfThreadsCompleted < 4) {
			try {
				Future<Void> result = applyKernelOnFourThreadsCompletition.take();
				result.get();
				numberOfThreadsCompleted++;
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Exception caught while processing the image on multiple threads.");
			}
		}

		BufferedImage processedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		processedImage.setData(Raster.createRaster(processedImage.getSampleModel(),
				new DataBufferByte(newPixels, newPixels.length), null));
		return processedImage;
	}
}