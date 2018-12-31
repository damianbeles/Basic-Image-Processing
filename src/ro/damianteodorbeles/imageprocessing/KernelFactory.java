package ro.damianteodorbeles.imageprocessing;

public class KernelFactory {

	public IKernel BOX_BLUR() {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return new float[] { 1f / 9, 1f / 9, 1f / 9, 1f / 9, 1f / 9, 1f / 9, 1f / 9, 1f / 9, 1f / 9 };
			}

			@Override
			public int getWidth() {
				return 3;
			}

			@Override
			public int getHeight() {
				return 3;
			}

			@Override
			public int[] getOrigin() {
				return new int[] { 1, 1 };
			}
		};
	}

	public IKernel GAUSSIAN_BLUR() {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return new float[] { 1f / 16, 2f / 16, 1f / 16, 2f / 16, 4f / 16, 2f / 16, 1f / 16, 2f / 16, 1f / 16 };
			}

			@Override
			public int getWidth() {
				return 3;
			}

			@Override
			public int getHeight() {
				return 3;
			}

			@Override
			public int[] getOrigin() {
				return new int[] { 1, 1 };
			}

		};
	}

	public IKernel EDGE_DETECTION() {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return new float[] { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
			}

			@Override
			public int getWidth() {
				return 3;
			}

			@Override
			public int getHeight() {
				return 3;
			}

			@Override
			public int[] getOrigin() {
				return new int[] { 1, 1 };
			}

		};
	}

	public IKernel SHARPEN() {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return new float[] { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
			}

			@Override
			public int getWidth() {
				return 3;
			}

			@Override
			public int getHeight() {
				return 3;
			}

			@Override
			public int[] getOrigin() {
				return new int[] { 1, 1 };
			}

		};
	}

	public IKernel EMBOSS() {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return new float[] { -2, -1, 0, -1, 1, 1, 0, 1, 2 };
			}

			@Override
			public int getWidth() {
				return 3;
			}

			@Override
			public int getHeight() {
				return 3;
			}

			@Override
			public int[] getOrigin() {
				return new int[] { 1, 1 };
			}

		};
	}

	public IKernel CUSTOM_NORMALIZED_KERNEL(final float[] kernel, final int width, final int height,
			final int[] origin) {
		return new IKernel() {

			@Override
			public float[] getKernel() {
				return kernel;
			}

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public int getHeight() {
				return height;
			}

			@Override
			public int[] getOrigin() {
				return origin;
			}

		};
	}

	public IKernel CUSTOM_UNNORMALIZED_KERNEL(final float[] kernel, final int width, final int height,
			final int[] origin) {
		return CUSTOM_NORMALIZED_KERNEL(Normalize(kernel), width, height, origin);
	}

	public IKernel CUSTOM_GENERATED_KERNEL(final int originValue, final int otherValue, final int width,
			final int height, final int[] origin) {
		float[] kernel = new float[width * height];
		for (int npos = 0; npos < kernel.length; ++npos)
			kernel[npos] = otherValue;
		kernel[origin[0] * width + origin[1]] = originValue;
		return CUSTOM_UNNORMALIZED_KERNEL(kernel, width, height, origin);
	}

	private float[] Normalize(final float[] kernel) {
		float sum = 0;

		for (int npos = 0; npos < kernel.length; ++npos) {
			sum += kernel[npos];
		}

		for (int npos = 0; npos < kernel.length; ++npos) {
			kernel[npos] /= sum;
		}

		return kernel;
	}
}