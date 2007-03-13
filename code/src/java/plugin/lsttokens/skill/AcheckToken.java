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
package plugin.lsttokens.skill;

import pcgen.base.util.Logging;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with ACHECK Token
 */
public class AcheckToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "ACHECK";
	}

	public boolean parse(Skill skill, String value)
	{
		skill.setACheck(value);
		return true;
	}

	public boolean parse(LoadContext context, Skill skill, String value)
		throws PersistenceLayerException
	{
		try
		{
			skill.put(ObjectKey.ARMOR_CHECK, SkillArmorCheck.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Misunderstood " + getTokenName() + ": " + value
				+ " is not a valid value");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		SkillArmorCheck sac = skill.get(ObjectKey.ARMOR_CHECK);
		if (sac == null)
		{
			return null;
		}
		return new String[]{sac.toString()};
	}
}
