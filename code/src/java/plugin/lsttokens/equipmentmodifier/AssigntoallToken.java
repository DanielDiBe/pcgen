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
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with ASSIGNTOALL token
 */
public class AssigntoallToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "ASSIGNTOALL";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setAssignment(value);
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		if (value.equalsIgnoreCase("NO"))
		{
			mod.put(ObjectKey.ASSIGN_TO_ALL, Boolean.FALSE);
		}
		else if (value.equalsIgnoreCase("YES"))
		{
			mod.put(ObjectKey.ASSIGN_TO_ALL, Boolean.TRUE);
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

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Boolean stacks = mod.get(ObjectKey.ASSIGN_TO_ALL);
		if (stacks == null)
		{
			return null;
		}
		return new String[]{stacks.booleanValue() ? "YES" : "NO"};
	}
}
