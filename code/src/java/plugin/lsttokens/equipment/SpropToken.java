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
package plugin.lsttokens.equipment;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpecialProperty;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.Equipment;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * Deals with SPROP token
 */
public class SpropToken extends AbstractToken implements EquipmentLstToken
{

	@Override
	public String getTokenName()
	{
		return "SPROP";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addSpecialProperty(pcgen.core.SpecialProperty.createFromLst(value));
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (value == null || value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + ": line minimally requires "
				+ getTokenName() + ":<text>");
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

		String firstToken = tok.nextToken();

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			context.graph.unlinkChildNodesOfClass(getTokenName(), eq,
				SpecialProperty.class);
			firstToken = tok.nextToken();
		}

		if (Constants.LST_DOT_CLEAR.equals(firstToken))
		{
			Logging.errorPrint(getTokenName()
				+ " tag confused by redundant '.CLEAR'" + value);
			return false;
		}

		SpecialProperty sa = new SpecialProperty(firstToken);
		/*
		 * CONSIDER TODO This is another issue with this system - it is linked
		 * in before it's determined if it's really valid... :(
		 */
		context.graph.linkObjectIntoGraph(getTokenName(), eq, sa);

		if (!tok.hasMoreTokens())
		{
			// No variables, we're done!
			return true;
		}

		String token = tok.nextToken();

		while (true)
		{
			/*
			 * FIXME This is the ONLY Token fixed so far for a leading pre:
			 * Yarra Valley|PRELEVEL:4|Rheinhessen
			 * 
			 * This check needs to be universal in all the tokens that do this
			 * trailing PRE check
			 */
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				Logging.errorPrint(getTokenName()
					+ " tag confused by '.CLEAR' as a " + "middle token: "
					+ value);
				return false;
			}
			sa.addVariable(FormulaFactory.getFormulaFor(token));

			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			sa.addPrerequisite(prereq);
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		// if (obj instanceof PCClass) {
		// sa.setSASource("PCCLASS=" + obj.getKeyName() + "|" + level);
		// }

		return true;
	}

	public String unparse(LoadContext context, Equipment eq)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), eq,
					SpecialProperty.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		boolean needSpacer = false;
		for (PCGraphEdge edge : edges)
		{
			SpecialProperty sp = (SpecialProperty) edge.getSinkNodes().get(0);
			if (needSpacer)
			{
				sb.append('\t');
			}
			needSpacer = true;
			sb.append(getTokenName()).append(':').append(sp.getPropertyName());
			int variableCount = sp.getVariableCount();
			for (int i = 0; i < variableCount; i++)
			{
				sb.append(Constants.PIPE).append(sp.getVariable(i));
			}
			List<Prerequisite> prereqs = sp.getPrerequisiteList();
			if (prereqs != null && !prereqs.isEmpty())
			{
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
					sb.append(Constants.PIPE).append(swriter.toString());
				}
			}
		}
		return sb.toString();
	}
}