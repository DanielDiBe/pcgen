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
package plugin.lsttokens.auto;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ConditionalChoiceActor;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class EquipToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, ChooseResultActor
{

	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "EQUIP";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		String equipItems;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		/*
		 * CONSIDER There is the ability to consolidate this PREREQ processing
		 * into AutoLst.java (since it's the same across AUTO SubTokens)
		 */
		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			equipItems = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			equipItems = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.log(Logging.LST_ERROR, "Unresolved Prerequisite in " + value
						+ " in " + getFullName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.log(Logging.LST_ERROR, "Error generating Prerequisite "
						+ value.substring(openBracketLoc + 1,
								value.length() - 1) + " in " + getFullName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', equipItems))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(equipItems, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(aProf))
			{
				ChooseResultActor cra;
				if (prereq == null)
				{
					cra = this;
				}
				else
				{
					ConditionalChoiceActor cca = new ConditionalChoiceActor(
							this);
					cca.addPrerequisite(prereq);
					cra = cca;
				}
				context.obj.addToList(obj, ListKey.CHOOSE_ACTOR, cra);
			}
			else
			{
				CDOMReference<Equipment> ref = TokenUtilities
						.getTypeOrPrimitive(context, EQUIPMENT_CLASS, aProf);
				if (ref == null)
				{
					return false;
				}
				context.obj.addToList(obj, ListKey.EQUIPMENT,
						new QualifiedObject<CDOMReference<Equipment>>(ref,
								prereq));
				// apo.setAssociation(AssociationKey.EQUIPMENT_NATURE,
				// EquipmentNature.AUTOMATIC);
				// apo.setAssociation(AssociationKey.QUANTITY, INTEGER_ONE);
				// TODO Need to account for output index??
				// newEq.setOutputIndex(aList.size());
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();

		Changes<ChooseResultActor> listChanges = context.getObjectContext()
				.getListChanges(obj, ListKey.CHOOSE_ACTOR);

		// TODO remove not supported?

		Changes<QualifiedObject<CDOMReference<Equipment>>> changes = context.obj
				.getListChanges(obj, ListKey.EQUIPMENT);
		Collection<QualifiedObject<CDOMReference<Equipment>>> added = changes
				.getAdded();
		HashMapToList<List<Prerequisite>, CDOMReference<Equipment>> m = new HashMapToList<List<Prerequisite>, CDOMReference<Equipment>>();
		if (added != null)
		{
			for (QualifiedObject<CDOMReference<Equipment>> qo : added)
			{
				m.addToListFor(qo.getPrerequisiteList(), qo.getObject(null));
			}
		}
		Collection<ChooseResultActor> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			for (ChooseResultActor cra : listAdded)
			{
				if (cra.getSource().equals(getTokenName()))
				{
					try
					{
						list.add(cra.getLstFormat());
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
				}
			}
		}
		for (List<Prerequisite> prereqs : m.getKeySet())
		{
			String ab = ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
					Constants.PIPE);
			if (prereqs != null && !prereqs.isEmpty())
			{
				if (prereqs.size() > 1)
				{
					context.addWriteMessage("Error: "
							+ obj.getClass().getSimpleName()
							+ " had more than one Prerequisite for "
							+ getFullName());
					return null;
				}
				Prerequisite p = prereqs.get(0);
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, p);
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				ab = ab + '[' + swriter.toString() + ']';
			}
			list.add(ab);
		}
		if (list.isEmpty())
		{
			// Empty indicates no Token
			return null;
		}

		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		Equipment e = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(EQUIPMENT_CLASS, o);
		if (e != null)
		{
			pc.addAssoc(obj, AssociationListKey.EQUIPMENT, e);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		Equipment e = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(EQUIPMENT_CLASS, o);
		if (e != null)
		{
			pc.removeAssoc(obj, AssociationListKey.EQUIPMENT, e);
		}
	}

	public String getSource()
	{
		return getTokenName();
	}

	public String getLstFormat()
	{
		return "%LIST";
	}
}
