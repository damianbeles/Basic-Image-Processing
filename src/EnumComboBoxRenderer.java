import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import ro.damianteodorbeles.imageprocessing.Filter;

@SuppressWarnings("serial")
public class EnumComboBoxRenderer extends BasicComboBoxRenderer {
	
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof Filter) {
			Filter filter = (Filter) value;
			setText(filter.getLabel());
		}

		return this;
	}
}
