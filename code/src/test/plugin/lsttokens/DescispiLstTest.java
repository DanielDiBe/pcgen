package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class DescispiLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new DescispiLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(Boolean.FALSE);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertTrue(primaryProf.isDescPI());
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Boolean val)
		throws PersistenceLayerException
	{
		assertEquals(val, Boolean.valueOf(primaryProf.isDescPI()));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, Boolean.valueOf(primaryProf.isDescPI()));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, Boolean.valueOf(primaryProf.isDescPI()));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, Boolean.valueOf(primaryProf.isDescPI()));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, Boolean.valueOf(primaryProf.isDescPI()));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertTrue(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertFalse(primaryProf.isDescPI());
		// We're nice enough to be case insensitive here...
		assertTrue(token.parse(primaryContext, primaryProf, "YeS"));
		assertTrue(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "Yes"));
		assertTrue(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "No"));
		assertFalse(primaryProf.isDescPI());
		// And we also allow single characters
		assertTrue(token.parse(primaryContext, primaryProf, "Y"));
		assertTrue(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "N"));
		assertFalse(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "y"));
		assertTrue(primaryProf.isDescPI());
		assertTrue(token.parse(primaryContext, primaryProf, "n"));
		assertFalse(primaryProf.isDescPI());
	}

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
