package pcgen.rules;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.facade.CampaignFacade;

public interface RulesFacade
{

	public <T extends CDOMObject> Collection<T> getAll(Class<T> cl);
	
	public void clearRules();
	
	public boolean loadRules(Collection<CampaignFacade> collection);
}
