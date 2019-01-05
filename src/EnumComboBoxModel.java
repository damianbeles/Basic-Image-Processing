import java.util.EnumSet;

import javax.swing.DefaultComboBoxModel;

import ro.damianteodorbeles.imageprocessing.Filter;

@SuppressWarnings("serial")
public class EnumComboBoxModel extends DefaultComboBoxModel<Filter> {
	public EnumComboBoxModel() {
		super();
		for (final Filter _E : EnumSet.allOf(Filter.class)) {
			this.addElement(_E);
		}
	}
}
