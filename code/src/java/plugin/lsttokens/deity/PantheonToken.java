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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.util.Logging;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Class deals with PANTHEON Token
 */
public class PantheonToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "PANTHEON";
	}

	public boolean parse(Deity deity, String value)
	{
		if (value.length() > 0)
		{
			String[] pantheons = value.split("\\|");
			List<String> pantheonList = CoreUtility.arrayToList(pantheons);
			deity.setPantheonList(pantheonList);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (value.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (value.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			deity.addToListFor(ListKey.PANTHEON, Pantheon.getConstant(tok
				.nextToken()));
		}
		return true;
	}

	public String unparse(LoadContext context, Deity deity)
	{
		List<Pantheon> pantheons = deity.getListFor(ListKey.PANTHEON);
		if (pantheons == null || pantheons.isEmpty())
		{
			return null;
		}
		StringBuilder sb =
				new StringBuilder().append(getTokenName()).append(':');
		boolean needPipe = false;
		for (Pantheon rp : pantheons)
		{
			if (needPipe)
			{
				sb.append(Constants.PIPE);
			}
			needPipe = true;
			sb.append(rp);
		}
		return sb.toString();
	}
}
