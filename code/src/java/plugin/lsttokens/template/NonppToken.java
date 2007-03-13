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
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with NONPP Token
 */
public class NonppToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "NONPP";
	}

	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			template.setNonProficiencyPenalty(Integer.parseInt(value));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			int nonpp = Integer.parseInt(value);
			if (nonpp > 0)
			{
				Logging.errorPrint("Non-Proficiency Penalty must be "
					+ "less than or equal to zero: " + value);
				return false;
			}
			template.put(IntegerKey.NONPP, Integer.valueOf(nonpp));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Non-Proficiency Penalty must be a number "
				+ "less than or equal to zero: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer nonpp = pct.get(IntegerKey.NONPP);
		if (nonpp == null)
		{
			return null;
		}
		if (nonpp.intValue() > 0)
		{
			context.addWriteMessage("Non-Proficiency Penalty must be "
				+ "less than or equal to zero: " + nonpp);
			return null;
		}
		return new String[]{nonpp.toString()};
	}

}
