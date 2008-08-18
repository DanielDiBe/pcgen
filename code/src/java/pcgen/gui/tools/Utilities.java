/*
 * Utilities.java
 *
 * Copyright 2002, 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Created on August 18th, 2002.
 */
package pcgen.gui.tools; // hm.binkley.gui;


import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.ResourceManager.Icons;

/**
 * <code>Utilities</code>.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 198 $
 *
 * @see SwingConstants
 */
final class Utilities implements SwingConstants
{
	/** Up direction. */
	public static final int UP = 14;

	/** Down direction. */
	public static final int DOWN = 15;

	/** Beginning (far left) direction. */
	public static final int BEGINNING = 16;

	/** End (far right) direction. */
	public static final int END = 17;

	/** Icons for New item. */
	public static final ImageIcon NEW_ICON = ResourceManager.getImageIcon(Icons.New16);

	/** Icons for Close item. */
	public static final ImageIcon CLOSE_ICON = ResourceManager.getImageIcon(Icons.Close16);

	/** Icons for Center item. */
	public static final ImageIcon CENTER_ICON = ResourceManager.getImageIcon(Icons.Stop16);

	/** Icons for Flip item. */
	public static final ImageIcon FLIP_ICON = ResourceManager.getImageIcon(Icons.Refresh16);

	/** Icons for Reset item. */
	public static final ImageIcon RESET_ICON = ResourceManager.getImageIcon(Icons.Redo16);

	/** Icons for Locked item. */
	public static final ImageIcon LOCK_ICON = ResourceManager.getImageIcon(Icons.Bookmarks16);

	/** Icons for Up item. */
	public static final ImageIcon UP_ICON = ResourceManager.getImageIcon(Icons.Up16);

	/** Icons for Left item. */
	public static final ImageIcon LEFT_ICON = ResourceManager.getImageIcon(Icons.Back16);

	/** Icons for Down item. */
	public static final ImageIcon DOWN_ICON = ResourceManager.getImageIcon(Icons.Down16);

	/** Icons for Right item. */
	public static final ImageIcon RIGHT_ICON = ResourceManager.getImageIcon(Icons.Forward16);

	/** Icons for Top item. */
	public static final ImageIcon TOP_ICON = ResourceManager.getImageIcon(Icons.UUp16);

	/** Icons for Beginning item. */
	public static final ImageIcon BEGINNING_ICON = ResourceManager.getImageIcon(Icons.BBack16);

	/** Icons for Bottom item. */
	public static final ImageIcon BOTTOM_ICON = ResourceManager.getImageIcon(Icons.DDown16);

	/** Icons for End item. */
	public static final ImageIcon END_ICON = ResourceManager.getImageIcon(Icons.FForward16);

	private Utilities()
	{
		super();
	}

	/**
	 * Work around bug in W32; it returns false even on right-mouse
	 * clicks.
	 *
	 * @param e <code>MouseEvent</code>, the event
	 *
	 * @return <code>boolean</code>, the condition
	 */
	static boolean isRightMouseButton(MouseEvent e)
	{
		return e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e);
	}

	/**
	 * <code>isShiftLeftMouseButton</code> detects SHIFT-BUTTON1
	 * events for flipping pane shortcuts.
	 *
	 * @param e <code>MouseEvent</code>, the event
	 *
	 * @return <code>boolean</code>, the condition
	 */
	static boolean isShiftLeftMouseButton(MouseEvent e)
	{
		return ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) && e.isShiftDown();
	}

}
