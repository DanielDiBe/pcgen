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

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class DefineLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "DEFINE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		try
		{
			String varName = tok.nextToken();
			String defineFormula = tok.nextToken();
			obj.addVariable(anInt, varName, defineFormula);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		int barLoc = value.indexOf('|');
		if (barLoc == -1 || barLoc != value.lastIndexOf('|'))
		{
			Logging.errorPrint(getTokenName()
				+ " must be of Format: varName|varFormula");
			return false;
		}
		Formula f = FormulaFactory.getFormulaFor(value.substring(barLoc + 1));
		/*
		 * FIXME Looks like variables need special treatment, so they can be unparsed
		 */
		obj.put(FormulaKey.getConstant(value.substring(0, barLoc)), f);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
