/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		Visibility vis;
		if (value.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (value.equals("EXPORT"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.getObjectContext().put(template, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Visibility vis = context.getObjectContext().getObject(template,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY_ONLY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.OUTPUT_ONLY))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for a PCTemplate");
			return null;
		}
		return new String[] { visString };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
