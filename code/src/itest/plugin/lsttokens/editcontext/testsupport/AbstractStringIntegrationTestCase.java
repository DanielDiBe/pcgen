package plugin.lsttokens.editcontext.testsupport;

import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractStringIntegrationTestCase<T extends PObject>
		extends AbstractIntegrationTestCase<T>
{

	public abstract boolean isClearLegal();
	
	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Languedoc-Roussillon");
		commit(modCampaign, tc, "Nieder�sterreich");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinSame() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Finger Lakes");
		commit(modCampaign, tc, "Finger Lakes");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinDotClear() throws PersistenceLayerException
	{
		if (isClearLegal())
		{
			verifyCleanStart();
			TestContext tc = new TestContext();
			commit(testCampaign, tc, "Finger Lakes");
			commit(modCampaign, tc, ".CLEAR");
			completeRoundRobin(tc);
		}
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Nieder�sterreich");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Yarra Valley");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
