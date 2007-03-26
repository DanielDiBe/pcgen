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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ChoiceSet;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Kit;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class KitLst implements GlobalLstToken
{

	private static final Class<Kit> KIT_CLASS = Kit.class;

	public String getTokenName()
	{
		return "KIT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setKitString(anInt + "|" + value);
		}
		else
		{
			obj.setKitString("0|" + value);
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
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
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		ChoiceSet<CDOMSimpleSingleRef<Kit>> cl;
		try
		{
			int count = Integer.parseInt(tok.nextToken());
			if (count <= 0)
			{
				Logging.errorPrint("Count in " + getTokenName()
					+ " must be > 0");
				return false;
			}
			cl =
					new ChoiceSet<CDOMSimpleSingleRef<Kit>>(count, tok
						.countTokens());
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " parse error: first value must be a number");
			return false;
		}

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
					ChoiceSet.class);
				cl.clear();
			}
			else
			{
				cl.addChoice(context.ref.getCDOMReference(KIT_CLASS, tokText));
			}
		}
		context.graph.linkObjectIntoGraph(getTokenName(), obj, cl);

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					ChoiceSet.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>(edgeList.size());
		Set<CDOMReference<?>> set =
			new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);
		for (PCGraphEdge edge : edgeList)
		{
			StringBuilder sb = new StringBuilder();
			ChoiceSet<CDOMSimpleSingleRef<Kit>> cl =
					(ChoiceSet<CDOMSimpleSingleRef<Kit>>) edge.getSinkNodes()
						.get(0);
			sb.append(cl.getCount());
			set.clear();
			set.addAll(cl.getSet());
			for (CDOMReference<?> ref : set)
			{
				sb.append(Constants.PIPE).append(ref.getLSTformat());
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
