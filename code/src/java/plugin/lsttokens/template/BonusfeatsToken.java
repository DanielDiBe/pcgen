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

import java.util.Set;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Slot;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements PCTemplateLstToken
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getTokenName()
	{
		return "BONUSFEATS";
	}

	// number of additional feats to spend
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			int featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Invalid integer in " + getTokenName()
					+ ": must be greater than zero");
				return false;
			}
			template.setBonusInitialFeats(featCount);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": must be an integer (greater than zero)");
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Invalid integer in " + getTokenName()
					+ ": must be greater than zero");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
				+ value);
			return false;
		}

		context.graph.addSlotIntoGraph(getTokenName(), template, ABILITY_CLASS,
			FormulaFactory.getFormulaFor(featCount));

		return true;
	}

	public String unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					Slot.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context.addWriteMessage("Invalid Slot Count " + links.size()
				+ " associated with " + getTokenName()
				+ ": Only one Slot allowed.");
			return null;
		}
		PCGraphEdge edge = links.iterator().next();
		Slot<Ability> slot = (Slot<Ability>) edge.getSinkNodes().get(0);
		if (!slot.getSlotClass().equals(ABILITY_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		return new StringBuilder().append(getTokenName()).append(':').append(
			slot.toLSTform()).toString();
		// return new StringBuilder().append(getTokenName()).append(':').append(
		// slot.toLSTform()).toString();
	}
}
