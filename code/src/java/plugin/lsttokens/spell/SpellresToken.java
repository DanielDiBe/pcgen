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
package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLRES Token
 */
public class SpellresToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "SPELLRES";
	}

	public boolean parse(Spell spell, String value)
	{
		spell.setSpellResistance(value);
		return true;
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (value.equalsIgnoreCase("NO"))
		{
			spell.put(ObjectKey.CAN_BE_RESISTED, Boolean.FALSE);
		}
		else if (value.equalsIgnoreCase("YES"))
		{
			spell.put(ObjectKey.CAN_BE_RESISTED, Boolean.TRUE);
		}
		else
		{
			Logging.errorPrint("Did not understand " + getTokenName()
				+ " value: " + value);
			Logging.errorPrint("Must be YES or NO");
			return false;
		}
		return true;
	}

	public String unparse(LoadContext context, Spell spell)
	{
		Boolean resistable = spell.get(ObjectKey.CAN_BE_RESISTED);
		if (resistable == null)
		{
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			resistable.booleanValue() ? "YES" : "NO").toString();
	}
}
