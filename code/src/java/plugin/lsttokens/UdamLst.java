/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 * 
 */
public class UdamLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "UDAM";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.addUdamList(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			obj.removeListFor(ListKey.UDAM);
		}
		else
		{
			final StringTokenizer tok =
					new StringTokenizer(value, Constants.COMMA);
			if (tok.countTokens() != 9)
			{
				Logging.errorPrint(getTokenName()
					+ " requires 9 comma separated values");
				return false;
			}
			if (obj.containsListFor(ListKey.UDAM))
			{
				Logging.errorPrint(obj.getDisplayName() + " already has "
					+ getTokenName() + " set.");
				Logging.errorPrint("  It will be redefined, "
					+ "but you should be using " + getTokenName() + ":.CLEAR");
				obj.removeListFor(ListKey.UDAM);
			}

			while (tok.hasMoreTokens())
			{
				obj.addToListFor(ListKey.UDAM, tok.nextToken());
			}
		}
		return true;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = obj.getListFor(ListKey.UDAM);
		if (list == null || list.isEmpty())
		{
			return null;
		}
		if (list.size() != 9)
		{
			//Error
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName()).append(':');
		boolean needComma = false;
		for (String item : list)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			sb.append(item);
			needComma = true;
		}
		return sb.toString();
	}
}
