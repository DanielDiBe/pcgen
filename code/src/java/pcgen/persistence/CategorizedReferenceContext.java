/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import pcgen.base.util.TripleKeyMap;
import pcgen.base.util.TripleKeyMapToInstanceList;
import pcgen.cdom.base.CDOMCategorizedSingleRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class CategorizedReferenceContext
{

	private TripleKeyMapToInstanceList<Class, Category, String, CategorizedCDOMObject> duplicates =
			new TripleKeyMapToInstanceList<Class, Category, String, CategorizedCDOMObject>();

	private TripleKeyMap<Class, Category, String, CategorizedCDOMObject> active =
			new TripleKeyMap<Class, Category, String, CategorizedCDOMObject>();

	private TripleKeyMap<Class, Category, String, CDOMCategorizedSingleRef> referenced =
			new TripleKeyMap<Class, Category, String, CDOMCategorizedSingleRef>();

	public <T extends CategorizedCDOMObject<T>> void registerWithKey(
		Class<T> cl, Category<T> cat, T obj, String key)
	{
		if (active.containsKey(cl, cat, key))
		{
			duplicates.addToListFor(cl, cat, key, obj);
		}
		else
		{
			active.put(cl, cat, key, obj);
		}
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		CategorizedCDOMObject po = active.get(c, cat, val);
		if (po != null)
		{
			if (duplicates.containsListFor(c, cat, val))
			{
				Logging.errorPrint("Reference to Constructed "
					+ c.getSimpleName() + " " + val + " is ambiguous");
			}
			return (T) po;
		}
		return null;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T getConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		T obj = silentlyGetConstructedCDOMObject(c, cat, val);
		if (obj == null)
		{
			Logging.errorPrint("Someone expected " + c.getSimpleName() + " "
				+ cat + " " + val + " to exist.");
			Thread.dumpStack();
		}
		return obj;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> T constructCDOMObject(
		Class<T> c, String val)
	{
		if (val.equals(""))
		{
			throw new IllegalArgumentException("Cannot build Empty Name");
		}
		try
		{
			if (!CategorizedCDOMObject.class.isAssignableFrom(c))
			{
				throw new IllegalArgumentException(c.getSimpleName() + " "
					+ val);
			}
			T obj = c.newInstance();
			obj.setName(val);
			registerWithKey(c, obj.getCDOMCategory(), obj, val);
			return obj;
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException(c + " " + val);
	}

	public <T extends CategorizedCDOMObject<T>> void reassociateReference(
		String value, T obj)
	{
		String oldKey = obj.getKeyName();
		if (oldKey.equals(value))
		{
			Logging.errorPrint("Worthless Key change encountered: "
				+ obj.getDisplayName() + " " + oldKey);
		}
		Class<T> cl = (Class<T>) obj.getClass();
		Category<T> cat = obj.getCDOMCategory();
		if (active.get(cl, cat, oldKey).equals(obj))
		{
			List<CategorizedCDOMObject> list = duplicates.getListFor(cl, cat, oldKey);
			if (list == null)
			{
				// No replacement
				active.remove(cl, cat, oldKey);
			}
			else
			{
				CategorizedCDOMObject newActive = duplicates.getItemFor(cl, cat, oldKey, 0);
				duplicates.removeFromListFor(cl, cat, oldKey, newActive);
				active.put(cl, cat, oldKey, newActive);
			}
		}
		else
		{
			duplicates.removeFromListFor(cl, cat, oldKey, obj);
		}
		obj.setKeyName(value);
		registerWithKey(cl, cat, obj, value);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> void reassociateReference(
		Category<T> cat, T obj)
	{
		Category<T> oldCat = obj.getCDOMCategory();
		if (oldCat == null && cat == null || oldCat != null && oldCat.equals(cat))
		{
			Logging.errorPrint("Worthless Category change encountered: "
				+ obj.getDisplayName() + " " + oldCat);
		}
		Class<T> cl = (Class<T>) obj.getClass();
		String key = obj.getKeyName();
		if (active.get(cl, oldCat, key).equals(obj))
		{
			List<CategorizedCDOMObject> list = duplicates.getListFor(cl, oldCat, key);
			if (list == null)
			{
				// No replacement
				active.remove(cl, oldCat, key);
			}
			else
			{
				CategorizedCDOMObject newActive = duplicates.getItemFor(cl, oldCat, key, 0);
				duplicates.removeFromListFor(cl, oldCat, key, newActive);
				active.put(cl, oldCat, key, newActive);
			}
		}
		else
		{
			duplicates.removeFromListFor(cl, oldCat, key, obj);
		}
		obj.setCDOMCategory(cat);
		registerWithKey(cl, cat, obj, key);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> void forgetCDOMObjectKeyed(
		Class<T> cl, Category<T> cat, String forgetKey)
	{
		active.remove(cl, cat, forgetKey);
		duplicates.removeListFor(cl, cat, forgetKey);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> Collection<T> getConstructedCDOMObjects(
		Class<T> name, Category<T> cat)
	{
		return (Collection) active.values(name, cat);
	}

	public <T extends PObject & CategorizedCDOMObject<T>> boolean containsConstructedCDOMObject(
		Class<T> name, Category<T> cat, String key)
	{
		return active.containsKey(name, cat, key);
	}

	public <T extends CategorizedCDOMObject<T>> T cloneConstructedCDOMObject(
		Class<T> cl, T orig, String newKey)
	{
		try
		{
			T clone = cl.cast(((CDOMObject) orig).clone());
			clone.setName(newKey);
			clone.setKeyName(newKey);
			clone.setCDOMCategory(orig.getCDOMCategory());
			registerWithKey(cl, orig.getCDOMCategory(), clone, newKey);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			Logging.errorPrint(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
				cl.getName(), orig.getKeyName(), newKey));
		}
		return null;
	}

	public <T extends PObject & CategorizedCDOMObject<T>> CDOMCategorizedSingleRef<T> getCDOMReference(
		Class<T> c, Category<T> cat, String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
		if (val.equals(""))
		{
			throw new IllegalArgumentException("Cannot reference Empty Name");
		}
		if (val.startsWith("TYPE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ANY"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ALL"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("PRE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CHOOSE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMES="))
		{
			throw new IllegalArgumentException(val);
		}
		if (c.equals(Ability.class))
		{
			// FIXME This destroys ASSOCIATION information, so need to figure
			// out how to preserve it - needs to be stripped before it gets here
			// :(
			int parenLoc = val.indexOf("(");
			if (parenLoc != -1 && val.charAt(parenLoc - 1) != ' ')
			{
				val = val.substring(0, parenLoc);
			}
		}
		if (c.equals(PCClass.class))
		{
			if (val.startsWith("CLASS"))
			{
				throw new IllegalArgumentException(val);
			}
			else if (val.startsWith("SUB"))
			{
				throw new IllegalArgumentException(val);
			}
			else
			{
				try
				{
					Integer.parseInt(val);
					throw new IllegalArgumentException(val);
				}
				catch (NumberFormatException nfe)
				{
					// Want this!
				}
			}
		}

		CDOMCategorizedSingleRef<T> ref = referenced.get(c, cat, val);
		if (ref == null)
		{
			ref = new CDOMCategorizedSingleRef<T>(c, cat, val);
			referenced.put(c, cat, val, ref);
		}
		return ref;
	}

	public boolean validate()
	{
		boolean returnGood = true;
		for (Class key1 : duplicates.getKeySet())
		{
			for (Category key2 : duplicates.getSecondaryKeySet(key1))
			{
				for (String second : duplicates.getTertiaryKeySet(key1, key2))
				{
					if (SettingsHandler.isAllowOverride())
					{
						List<CategorizedCDOMObject> list =
								duplicates.getListFor(key1, key2, second);
						CategorizedCDOMObject good = active.get(key1, key2, second);
						for (int i = 0; i < list.size(); i++)
						{
							CategorizedCDOMObject dupe = list.get(i);
							// If the new object is more recent than the current
							// one, use the new object
							final Date origDate =
									good.getSourceEntry().getSourceBook()
										.getDate();
							final Date dupeDate =
									dupe.getSourceEntry().getSourceBook()
										.getDate();
							if ((dupeDate != null)
								&& ((origDate == null) || ((dupeDate
									.compareTo(origDate) > 0))))
							{
								duplicates.removeFromListFor(key1, key2,
									second, good);
								good = dupe;
							}
							else
							{
								duplicates.removeFromListFor(key1, key2,
									second, dupe);
							}
						}
						if (!good.equals(active.get(key1, key2, second)))
						{
							active.put(key1, key2, second, good);
						}
					}
					else
					{
						Logging.errorPrint("More than one "
							+ key1.getSimpleName() + " with key/name " + second
							+ " and category " + key2 + " was built");
						returnGood = false;
					}
				}
			}
		}
		for (Class key1 : active.getKeySet())
		{
			for (Category cat : active.getSecondaryKeySet(key1))
			{
				for (String s : active.getTertiaryKeySet(key1, cat))
				{
					String keyName = active.get(key1, cat, s).getKeyName();
					if (!keyName.equals(s))
					{
						Logging.errorPrint("Magical Key Change: " + s + " to "
							+ keyName);
						returnGood = false;
					}
				}
			}
		}
		return validateConstructed();
	}

	private boolean validateConstructed()
	{
		boolean returnGood = true;
		for (Class cl : referenced.getKeySet())
		{
			for (Category cat : referenced.getSecondaryKeySet(cl))
			{
				for (String s : referenced.getTertiaryKeySet(cl, cat))
				{
					if (!active.containsKey(cl, cat, s))
					{
						Logging.errorPrint("Unconstructed Reference: "
							+ cl.getSimpleName() + " " + cat + " " + s);
						returnGood = false;
					}
				}
			}
		}
		return returnGood;
	}

	public void clear()
	{
		duplicates.clear();
		active.clear();
		referenced.clear();
	}
}
