/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;

public class QualifyChooser<T> implements ChoiceSet<T>
{

	private Formula count;

	private Formula max;

	public static <T extends PObject> QualifyChooser<T> getQualifyChooser(Class<T> cl)
	{
		return new QualifyChooser<T>(cl);
	}

	public QualifyChooser(Class<T> cl)
	{
		super();
		if (cl == null)
		{
			throw new IllegalArgumentException("Choice Class cannot be null");
		}
	}

	public Formula getMaxSelections()
	{
		return max;
	}

	public Formula getCount()
	{
		return count;
	}

	public Set<T> getSet()
	{
		return set;
	}

	@Override
	public String toString()
	{
		return count.toString() + '<' + max.toString() + Constants.PIPE
			+ StringUtil.join(set, Constants.PIPE);
	}

	@Override
	public int hashCode()
	{
		return count.hashCode() + max.hashCode() * 23;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof QualifyChooser))
		{
			return false;
		}
		if (o == this)
		{
			return true;
		}
		QualifyChooser<?> cs = (QualifyChooser) o;
		return max == cs.max && count == cs.count && set.equals(cs.set);
	}

	public void setCount(Formula choiceCount)
	{
		// if (choiceCount <= 0)
		// {
		// throw new IllegalArgumentException(
		// "Count for ChoiceSet must be >= 1");
		// }
		count = choiceCount;
	}

	public void setMaxSelections(Formula maxSelected)
	{
		// if (maxSelected <= 0)
		// {
		// throw new IllegalArgumentException(
		// "Max Selected for ChoiceSet must be >= 1");
		// }
		max = maxSelected;
	}

	// public boolean validate()
	// {
	// if (max < count)
	// {
	// Logging
	// .errorPrint("Nonsensical ChoiceSet Max Selected must be >= Count");
	// return false;
	// }
	// return true;
	// }
}
