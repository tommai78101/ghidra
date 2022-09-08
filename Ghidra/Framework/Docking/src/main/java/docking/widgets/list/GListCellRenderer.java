/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docking.widgets.list;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

import javax.swing.*;

import docking.widgets.AbstractGCellRenderer;
import generic.theme.GThemeDefaults.Colors.Palette;

/**
 * Provides a common implementation of a list renderer, for use in both JList and JComboBox.
 * <p>
 * HTML rendering defaults to disabled.  See {@link #setHTMLRenderingEnabled(boolean)}.
 *
 * @param <E> the element-type this list models.
 */
public class GListCellRenderer<E> extends AbstractGCellRenderer implements ListCellRenderer<E> {

	/**
	 * Returns a new ListCellRenderer that maps the list's data instance to a string used in the cell.
	 * <p>
	 * Use this if you only need to provide a way to get the string value from the type being shown
	 * in the list.
	 * 
	 * @param cellToTextMappingFunction a function that maps your custom type to a string value
	 * @return new GListCellRenderer instance
	 */
	public static <E> GListCellRenderer<E> createDefaultCellTextRenderer(
			Function<E, String> cellToTextMappingFunction) {
		return new GListCellRenderer<>() {
			@Override
			protected String getItemText(E value) {
				return cellToTextMappingFunction.apply(value);
			}
		};
	}

	/**
	 * Constructs a new GListCellRenderer.
	 */
	public GListCellRenderer() {

		// lists don't need alternation for rows, as they don't use long columnar data
		setShouldAlternateRowBackgroundColors(false);
	}

	/**
	 * Return the cell renderer text
	 * @param value Cell object value
	 * @return A string interpretation of value; generated by calling value.toString()
	 */
	protected String getItemText(E value) {
		return value == null ? "" : value.toString();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
			boolean isSelected, boolean hasFocus) {

		setText(getItemText(value));
		setHorizontalAlignment(
			value instanceof Number ? SwingConstants.RIGHT : SwingConstants.LEFT);
		ListModel<? extends E> model = list.getModel();
		configureFont(list, model, index);

		if (isSelected) {
			setForeground(list.getSelectionForeground());
			setBackground(list.getSelectionBackground());
			setOpaque(true);
		}
		else {
			setForegroundColor(list, model, value);

			JList.DropLocation dropLocation = list.getDropLocation();
			// @formatter:off
			boolean isDropRow = (dropLocation != null && 
								dropLocation.isInsert() &&
								dropLocation.getIndex() == index);
			// @formatter:on
			if (isDropRow) {
				setBackground(Palette.CYAN);
			}
			else {
				setBackground(getOSDependentBackgroundColor(list, index));
			}
		}

		setBorder(hasFocus ? focusBorder : noFocusBorder);
		return this;
	}

	protected void setForegroundColor(JList<? extends E> list, ListModel<? extends E> model,
			Object value) {
		setForeground(list.getForeground());
	}

	protected void configureFont(JList<? extends E> list, ListModel<? extends E> model, int index) {
		setFont(defaultFont);
	}

	/**
	 * Returns the width, height necessary to display the largest element in this list.
	 * <p>
	 * Useful for setting a JList's fixed cell width and height to the actual necessary size.
	 * <p>
	 * NOTE: the items and the renderer must be in plain text mode, not HTML rendering mode.
	 * 
	 * @param list the JList that uses this cell renderer
	 * @param items the items to measure
	 * @param minWidth the minimum width that can be returned
	 * @param minHeight the minimum height that can be returned
	 * @return a new Dimension containing a width and height value necessary to display the largest
	 * element in the list
	 */
	public Dimension computePlainTextListCellDimensions(JList<? extends E> list, List<E> items,
			int minWidth, int minHeight) {
		configureFont(list, list.getModel(), 0);
		FontMetrics metrics = getFontMetrics(getFont());
		int maxWidth = minWidth;
		for (E item : items) {
			String text = getItemText(item).toString();
			maxWidth = Math.max(maxWidth, metrics.stringWidth(text));
		}
		return new Dimension(maxWidth, Math.max(metrics.getHeight(), minHeight));
	}
}
