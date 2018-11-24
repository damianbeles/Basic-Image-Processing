package ro.damianteodorbeles.imageprocessing;


public class KernelFactory {
	
	public IKernel BOX_BLUR() {
		return new IKernel() {

			@Override
			public double[] getKernel() {
				return new double[] {
					1.0/9, 1.0/9, 1.0/9,
					1.0/9, 1.0/9, 1.0/9,
					1.0/9, 1.0/9, 1.0/9
				};
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
			public double[] getKernel() {
				return new double[] {
						1.0/16, 2.0/16, 1.0/16,
						2.0/16, 4.0/16, 2.0/16,
						1.0/16, 2.0/16, 1.0/16
				};
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
			public double[] getKernel() {
				return new double[] {
					-1, -1, -1,
					-1,  8, -1,
					-1, -1, -1
				};
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
			public double[] getKernel() {
				return new double[] {
					 0, -1,  0,
					-1,  5, -1,
					 0, -1,  0
				};
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
			public double[] getKernel() {
				return new double[] {
						-2, -1, 0,
						-1,  1, 1,
						 0,  1, 2
				};
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
	
	public IKernel CUSTOM_NORMALIZED_KERNEL(final double[] kernel, final int width, final int height, final int[] origin) {
		return new IKernel() {

			@Override
			public double[] getKernel() {
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
	
	public IKernel CUSTOM_UNNORMALIZED_KERNEL(final double[] kernel, final int width, final int height, final int[] origin) {
		return CUSTOM_NORMALIZED_KERNEL(Normalize(kernel), width, height, origin);
	}
	
	public IKernel CUSTOM_GENERATED_KERNEL(final int originValue, final int otherValue, final int width, final int height, final int[] origin) {
		double[] kernel = new double[width * height];
		for (int npos = 0; npos < kernel.length; ++npos)
			kernel[npos] = otherValue;
		kernel[origin[0] * width + origin[1]] = originValue;
		return CUSTOM_UNNORMALIZED_KERNEL(kernel, width, height, origin);
	}
	
	private double[] Normalize(final double[] kernel) {
		int sum = 0;
		
		for (int npos = 0; npos < kernel.length; ++npos) {
			sum += kernel[npos];
		}
		
		for (int npos = 0; npos < kernel.length; ++npos) {
			kernel[npos] /= (double)sum;
		}
		
		return kernel;
	}
}