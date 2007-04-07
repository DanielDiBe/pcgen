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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.cdom.enumeration.AbilityNature;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.util.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with FEAT Token
 */
public class FeatToken extends AbstractToken implements PCTemplateLstToken
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.addFeatString(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		return parseFeat(context, template, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
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

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		List<PCGraphGrantsEdge> edgeList = new ArrayList<PCGraphGrantsEdge>();

		String token = tok.nextToken();
		while (true)
		{
			CDOMCategorizedSingleRef<Ability> ability =
					context.ref.getCDOMReference(ABILITY_CLASS,
						AbilityCategory.FEAT, token);
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), obj,
						ability);
			edge.setAssociation(AssociationKey.ABILITY_NATURE,
				AbilityNature.NORMAL);
			edgeList.add(edge);

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
					+ "PRExxx tags in FEAT:?)");
				return false;
			}
			for (PCGraphGrantsEdge edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					ABILITY_CLASS);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		HashMapToList<Set<Prerequisite>, CDOMCategorizedSingleRef<Ability>> m =
				new HashMapToList<Set<Prerequisite>, CDOMCategorizedSingleRef<Ability>>();
		for (PCGraphEdge edge : edges)
		{
			AbilityNature an =
					edge.getAssociation(AssociationKey.ABILITY_NATURE);
			if (!AbilityNature.NORMAL.equals(an))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of NORMAL AbilityNature");
				return null;
			}
			CDOMCategorizedSingleRef<Ability> ab =
					(CDOMCategorizedSingleRef<Ability>) edge.getSinkNodes()
						.get(0);
			if (!AbilityCategory.FEAT.equals(ab.getCDOMCategory()))
			{
				context.addWriteMessage("Abilities awarded by "
					+ getTokenName() + " must be of CATEGORY FEAT");
				return null;
			}
			m.addToListFor(
				new HashSet<Prerequisite>(edge.getPrerequisiteList()), ab);
		}

		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(TokenUtilities.REFERENCE_SORTER);

		Set<String> list = new TreeSet<String>();

		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			List<CDOMCategorizedSingleRef<Ability>> abilities =
					m.getListFor(prereqs);
			set.clear();
			set.addAll(abilities);
			String ab = ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				TreeSet<String> prereqSet = new TreeSet<String>();
				for (Prerequisite p : prereqs)
				{
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, p);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
							+ e);
						return null;
					}
					prereqSet.add(swriter.toString());
				}
				ab =
						ab + Constants.PIPE
							+ StringUtil.join(prereqSet, Constants.PIPE);
			}
			list.add(ab);
		}
		return list.toArray(new String[list.size()]);
	}
}
