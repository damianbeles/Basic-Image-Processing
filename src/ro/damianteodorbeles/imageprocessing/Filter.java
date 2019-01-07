package ro.damianteodorbeles.imageprocessing;

public enum Filter {
	BOX_BLUR("BOX BLUR"),
	GAUSSIAN_BLUR("GAUSSIAN BLUR"),
	EDGE_DETECTION("EDGE DETECTION"),
	SHARPEN("SHARPEN"),
	EMBOSS("EMBOSS"),
	RED_COLORING("RED COLORING"),
	GREEN_COLORING("GREEN COLORING"),
	BLUE_COLORING("BLUE COLORING"),
	GRAYSCALE("GRAYSCALE"),
	NEGATIVE("NEGATIVE");
	
	Filter(String label) {
		this.label = label;
	}
	
	private final String label;
	
	public String getLabel() {
		return label;
	}
}
