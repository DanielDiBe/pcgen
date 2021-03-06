/*
 * PCStat.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on August 10, 2002, 11:58 PM
 */
package pcgen.core;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.facade.core.StatFacade;

/**
 * <code>PCStat</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class PCStat extends PObject implements StatFacade
{
	public String getAbb()
	{
		return get(StringKey.ABB);
	}

	public int getMinValue()
	{
		return getSafe(IntegerKey.MIN_VALUE);		
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder(30);
		sb.append("stat:").append(getAbb()).append(' ');
		sb.append("formula:").append(getSafe(FormulaKey.STAT_MOD)).append(' ');
		boolean rolled = getSafe(ObjectKey.ROLLED);
		if (!rolled)
		{
			sb.append(' ').append("rolled:").append(rolled);
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.StatFacade#getAbbreviation()
	 */
    @Override
	public String getAbbreviation()
	{
		return getAbb();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.StatFacade#getName()
	 */
    @Override
	public String getName()
	{
		return getDisplayName();
	}
}
