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
package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class CRTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static CrToken token = new CrToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testBadInputNegative() throws PersistenceLayerException
	{
		try
		{
			boolean parse = getToken().parse(primaryContext, primaryProf, "-1");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testBadInputZero() throws PersistenceLayerException
	{
		try
		{
			boolean parse = getToken().parse(primaryContext, primaryProf, "0");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testRoundRobinFraction() throws PersistenceLayerException
	{
		runRoundRobin("1/3");
	}

	@Test
	public void testRoundRobinFractionFormula()
		throws PersistenceLayerException
	{
		runRoundRobin("1/Formula");
	}

	@Test
	public void testRoundRobinFractionFormulaNegative()
		throws PersistenceLayerException
	{
		runRoundRobin("1/-Formula");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("Formula");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

	// @Test
	// public void testEmpty()
	// {
	// //Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	// }

}
