/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with SYMBOL Token
 */
public class SymbolToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "SYMBOL";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setHolyItem(value);
		return true;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		deity.put(StringKey.HOLY_ITEM, value);
		return true;
	}

	public String unparse(LoadContext context, Deity deity)
	{
		String holyItem = deity.get(StringKey.HOLY_ITEM);
		if (holyItem == null)
		{
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			holyItem).toString();
	}
}
