/*
 * DomainLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class DomainLoader extends LstObjectFileLoader<Domain>
{
	/** Creates a new instance of DomainLoader */
	public DomainLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public void parseLine(Domain domain, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(DomainLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			DomainLstToken token = (DomainLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, domain, value);
				if (!token.parse(domain, value))
				{
					Logging.errorPrint("Error parsing domain "
						+ domain.getDisplayName() + ':' + source.getURI()
						+ ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(domain, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal obj info '" + colString + "' in "
					+ source.getURI());
			}
		}

		completeObject(source, domain);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	@Override
	protected Domain getObjectKeyed(String aKey)
	{
		return Globals.getDomainKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(Domain objToForget)
	{
		Globals.getDomainList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addDomain((Domain) pObj);
	}

	@Override
	public void parseToken(LoadContext context, Domain domain, String key, String value, CampaignSourceEntry source) throws PersistenceLayerException {
		DomainLstToken token = TokenStore.inst().getToken(DomainLstToken.class,
				key);

		if (token == null) {
			if (!PObjectLoader.parseTag(context, domain, key, value)) {
				Logging.errorPrint("Illegal domain Token '" + key + "' for "
						+ domain.getDisplayName() + " in " + source.getURI()
						+ " of " + source.getCampaign() + ".");
			}
		} else {
			LstUtils.deprecationCheck(token, domain, value);
			if (!token.parse(context, domain, value)) {
				Logging.errorPrint("Error parsing token " + key + " in domain "
						+ domain.getDisplayName() + ':' + source.getURI() + ':'
						+ value + "\"");
			}
		}
	}

	@Override
	public Class<Domain> getLoadClass() {
		return Domain.class;
	}
}
