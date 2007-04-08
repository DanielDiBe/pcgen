/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.add;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMCompoundReference;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Restriction;
import pcgen.cdom.base.Slot;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.restriction.GroupRestriction;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class ClassSkillsToken implements AddLstToken
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	public boolean parse(PObject target, String value, int level)
	{
		if (!target.getClass().equals(PCClass.class))
		{
			Logging
				.errorPrint("ADD:CLASSSKILLS is only valid in Class LST files");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		target.addAddList(level, getTokenName() + "(" + items + ")"
			+ countString);
		return true;
	}

	public String getTokenName()
	{
		return "CLASSSKILLS";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in ADD:" + getTokenName()
						+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in ADD:" + getTokenName()
					+ ": " + countString);
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (items.charAt(0) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with , see: " + value);
			return false;
		}
		if (items.charAt(items.length() - 1) == ',')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with , see: " + value);
			return false;
		}
		if (items.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		/*
		 * BUG FIXME This slot actually belongs to the Class' SkillList not
		 * to the Class itself...
		 */
		Slot<Skill> slot =
				context.graph.addSlotIntoGraph(getTokenName(), obj,
					SKILL_CLASS, FormulaFactory.getFormulaFor(count));
		CDOMCompoundReference<Skill> cr =
				new CDOMCompoundReference<Skill>(SKILL_CLASS, getTokenName()
					+ " items");
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<Skill> ref =
					TokenUtilities.getObjectReference(context, SKILL_CLASS,
						token);
			if (ref == null)
			{
				return false;
			}
			cr.addReference(ref);
		}

		/*
		 * BUG FIXME How do I get the slink out of this Slot to be allows and not
		 * grants?
		 */
		slot.addSinkRestriction(new GroupRestriction<Skill>(SKILL_CLASS, cr));
		slot.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);

		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
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
		Slot<Skill> slot = (Slot<Skill>) edge.getSinkNodes().get(0);
		if (!slot.getSlotClass().equals(SKILL_CLASS))
		{
			context.addWriteMessage("Invalid Slot Type associated with "
				+ getTokenName() + ": Type cannot be "
				+ slot.getSlotClass().getSimpleName());
			return null;
		}
		String slotCount = slot.toLSTform();
		String result;
		List<Restriction<?>> restr = slot.getSinkRestrictions();
		if (restr.size() != 1)
		{
			context.addWriteMessage("Slot for " + getTokenName()
				+ " must have only one restriction");
			return null;
		}
		Restriction<?> res = restr.get(0);
		if ("1".equals(slotCount))
		{
			result = res.toLSTform();
		}
		else
		{
			result = slotCount + "|" + res.toLSTform();
		}
		return new String[]{result};
	}
}
