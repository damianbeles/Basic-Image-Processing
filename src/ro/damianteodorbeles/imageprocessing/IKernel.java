package ro.damianteodorbeles.imageprocessing;

public interface IKernel {
	
	public double [] getKernel();
	public int getWidth();
	public int getHeight();
	public int [] getOrigin();
}
