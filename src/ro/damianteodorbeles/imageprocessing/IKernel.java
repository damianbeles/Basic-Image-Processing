package ro.damianteodorbeles.imageprocessing;

public interface IKernel {
	
	public float [] getKernel();
	public int getWidth();
	public int getHeight();
	public int [] getOrigin();
}
