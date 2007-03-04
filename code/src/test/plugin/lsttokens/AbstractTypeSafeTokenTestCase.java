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
package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.AbstractTokenTestCase;

public abstract class AbstractTypeSafeTokenTestCase<T extends PObject> extends
		AbstractTokenTestCase<T>
{

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Nieder�sterreich"));
		assertEquals(getConstant("Nieder�sterreich"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Finger Lakes"));
		assertEquals(getConstant("Finger Lakes"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf, "Rheinhessen"));
		assertEquals(getConstant("Rheinhessen"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Languedoc-Roussillon"));
		assertEquals(getConstant("Languedoc-Roussillon"), primaryProf
			.get(getObjectKey()));
		assertTrue(getToken()
			.parse(primaryContext, primaryProf, "Yarra Valley"));
		assertEquals(getConstant("Yarra Valley"), primaryProf
			.get(getObjectKey()));
	}

	public abstract Object getConstant(String string);

	public abstract ObjectKey<?> getObjectKey();

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Nieder�sterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}
}
