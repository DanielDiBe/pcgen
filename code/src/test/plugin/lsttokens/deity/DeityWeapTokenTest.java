/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deity;

import org.junit.Test;

import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.DeityLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractListTokenTestCase;

public class DeityWeapTokenTest extends AbstractListTokenTestCase<Deity, WeaponProf>
{
	static DeityweapToken token = new DeityweapToken();
	static DeityLoader loader = new DeityLoader();

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<WeaponProf> getTargetClass()
	{
		return WeaponProf.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public LstObjectFileLoader<Deity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Deity> getToken()
	{
		return token;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(WeaponProf.class, one);
	}

	@Test
	public void dummyTest()
	{
		//Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

}
