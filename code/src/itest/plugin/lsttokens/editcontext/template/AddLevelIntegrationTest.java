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
package plugin.lsttokens.editcontext.template;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.AddLevelToken;

public class AddLevelIntegrationTest extends
		AbstractIntegrationTestCase<PCTemplate>
{

	static AddLevelToken token = new AddLevelToken();
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
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Rogue");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Rogue");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fighter|4");
		commit(modCampaign, tc, "Rogue|2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinClassOnly() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Rogue");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Rogue");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fighter|4");
		commit(modCampaign, tc, "Rogue|4");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinLevelOnly() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fighter|4");
		commit(modCampaign, tc, "Fighter|2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Fighter|2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Fighter|3");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
