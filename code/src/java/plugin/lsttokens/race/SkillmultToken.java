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
package plugin.lsttokens.race;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Race;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLMULT Token
 */
public class SkillmultToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "SKILLMULT";
	}

	public boolean parse(Race race, String value)
	{
		try
		{
			race.setInitialSkillMultiplier(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			Integer i = Integer.valueOf(value);
			if (i.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName()
					+ " must be a positive integer");
				return false;
			}
			race.put(IntegerKey.INITIAL_SKILL_MULT, i);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String unparse(LoadContext context, Race race)
	{
		Integer sp = race.get(IntegerKey.INITIAL_SKILL_MULT);
		if (sp == null)
		{
			return null;
		}
		if (sp.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':')
			.append(sp).toString();
	}
}
