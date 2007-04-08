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
package plugin.lsttokens.pcclass;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class VisibleTokenTest extends AbstractTokenTestCase<PCClass>
{

	static VisibleToken token = new VisibleToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidOutput()
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryProf.put(ObjectKey.VISIBILITY, Visibility.QUALIFY);
		assertNull(token.unparse(primaryContext, primaryProf));
		assertFalse(primaryContext.getWriteMessageCount() == 0);
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(Visibility.NO, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.NO);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "Always"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		// Note case sensitivity
		assertFalse(token.parse(primaryContext, primaryProf, "Yes"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		// assertTrue(token.parse(primaryContext, primaryProf, "DISPLAY"));
		// assertEquals(Visibility.DISPLAY,
		// primaryProf.get(ObjectKey.VISIBILITY));
		// assertTrue(token.parse(primaryContext, primaryProf, "EXPORT"));
		// assertEquals(Visibility.EXPORT,
		// primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(Visibility.YES, primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(Visibility.NO, primaryProf.get(ObjectKey.VISIBILITY));
	}

	//
	// @Test
	// public void testRoundRobinDisplay() throws PersistenceLayerException
	// {
	// runRoundRobin("DISPLAY");
	// }
	//
	// @Test
	// public void testRoundRobinExport() throws PersistenceLayerException
	// {
	// runRoundRobin("EXPORT");
	// }

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinNo() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}

}
