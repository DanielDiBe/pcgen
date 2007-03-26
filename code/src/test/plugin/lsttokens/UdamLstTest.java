package plugin.lsttokens;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class UdamLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new UdamLst();
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
	public void testInvalidNotEnoughValues() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1,2,3,4,5,6,7,8"));
	}

	@Test
	public void testInvalidTooManyValues() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"1,2,3,4,5,6,7,8,9,0"));
	}

	@Test
	public void testInvalidEmptyValue1() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, ",2,3,4,5,6,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue2() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,,3,4,5,6,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue3() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,,4,5,6,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue4() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,,5,6,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue5() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,4,,6,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue6() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,4,5,,7,8,9"));
	}

	@Test
	public void testInvalidEmptyValue7() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,4,5,6,,8,9"));
	}

	@Test
	public void testInvalidEmptyValue8() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,4,5,6,7,,9"));
	}

	@Test
	public void testInvalidEmptyValue9() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "1,2,3,4,5,6,7,8,"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("1,2,3,4,5,6,7,8,9");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		this.runRoundRobin("1,2,3,4*form,5*form,6,7*form,8,9");
	}
}
