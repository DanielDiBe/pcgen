/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject July 22, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.prereq;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a utility class related to PreReq objects.
 */
public final class PrerequisiteUtilities
{
	/**
	 * Private Constructor
	 */
	private PrerequisiteUtilities()
	{
		// Don't allow instantiation of utility class
	}

	public static final String preReqHTMLStringsForList(final PlayerCharacter aPC, final PObject aObj, final List<Prerequisite> aList, final boolean includeHeader)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(aList.size() * 20);

		final List<Prerequisite> newList = new ArrayList<Prerequisite>();
		int iter = 0;

		for ( Prerequisite prereq : aList )
		{
			newList.clear();

			newList.add(prereq);

			if (iter++ > 0)
			{
				pString.append(" and ");
			}

			final String bString = PrereqHandler.toHtmlString(newList);

			final boolean flag;


			if (aObj instanceof Equipment)
			{
				flag = PrereqHandler.passesAll(newList, (Equipment) aObj, aPC);
			}
			else
			{
				flag = PrereqHandler.passesAll(newList, aPC, null);
			}

			if (!flag)
			{
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlStart());
				pString.append("<i>");
			}

			final StringTokenizer aTok = new StringTokenizer(bString, "&<>", true);

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();

				if (aString.equals("<"))
				{
					pString.append("&lt;");
				}
				else if (aString.equals(">"))
				{
					pString.append("&gt;");
				}
				else if (aString.equals("&"))
				{
					pString.append("&amp;");
				}
				else
				{
					pString.append(aString);
				}
			}

			if (!flag)
			{
				pString.append("</i>");
				pString.append(SettingsHandler.getPrereqFailColorAsHtmlEnd());
			}
		}

		if (pString.toString().indexOf('<') >= 0)
		{
			// seems that ALIGN and STAT have problems in
			// HTML display, so wrapping in <font> tag.
			pString.insert(0, "<font>");
			pString.append("</font>");

			if (includeHeader)
			{
				if (pString.toString().indexOf('<') >= 0)
				{
					pString.insert(0, "<html>");
					pString.append("</html>");
				}
			}
		}

		return pString.toString();
	}


	/**
	 * Build the LST syntax to represent the list of prerequisites.
	 * 
	 * @param preReqs The list of prerequisites.
	 * @param separator The character to separate each prereq from each other.
	 */
	public static String getPrerequisitePCCText(final List preReqs, final String separator)
	{
		final StringBuffer sBuff = new StringBuffer();
		if ((preReqs != null) && (preReqs.size() > 0)) {
			final StringWriter writer = new StringWriter();
			final PrerequisiteWriter preReqWriter = new PrerequisiteWriter();
			for (Iterator preReqIter = preReqs.iterator(); preReqIter.hasNext();) {
				final Prerequisite preReq = (Prerequisite) preReqIter.next();
				try {
					preReqWriter.write(writer, preReq);
				} catch (PersistenceLayerException e) {
					Logging.errorPrint("Failed to encode prereq: ", e);
				}
				if (preReqIter.hasNext()) {
					writer.write(separator);
				}
			}
			sBuff.append(separator);
			sBuff.append(writer.toString());
		}
		return sBuff.toString();
	}
	
}
