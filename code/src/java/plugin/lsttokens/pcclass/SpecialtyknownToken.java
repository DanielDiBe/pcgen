/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.SpellProgressionInfo;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCClassLevelLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken implements PCClassLstToken,
		PCClassLevelLstToken
{

	public String getTokenName()
	{
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer st = new StringTokenizer(value, ",");
		List<String> list = new ArrayList<String>(st.countTokens());

		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}

		pcclass.addSpecialtyKnown(level, list);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value,
		int level)
	{
		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);

		List<String> knownList = new ArrayList<String>(st.countTokens());
		while (st.hasMoreTokens())
		{
			knownList.add(st.nextToken());
		}

		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		sp.setSpecialtyKnown(level, knownList);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc, int level)
	{
		if (!pcc.hasCDOMSpellProgression())
		{
			return null;
		}
		SpellProgressionInfo sp = pcc.getCDOMSpellProgression();
		List<String> list = sp.getSpecialtyKnownForLevel(level);
		if (list == null || list.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(list, Constants.COMMA)};
	}
}
