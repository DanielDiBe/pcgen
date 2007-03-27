/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;

public class LevelSkillPoints extends ConcretePrereqObject
{

	/*
	 * CONSIDER Should this be generic and allow N slots per level of anything,
	 * or are skills so unique that they deserve their own object?
	 */
	private final int points;

	public LevelSkillPoints(int i)
	{
		super();
		points = i;
	}

	public int getPoints()
	{
		return points;
	}

	@Override
	public int hashCode()
	{
		return points;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof LevelSkillPoints
			&& ((LevelSkillPoints) o).points == points;
	}
}
