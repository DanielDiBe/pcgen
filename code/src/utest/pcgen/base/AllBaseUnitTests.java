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
package pcgen.base;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

import pcgen.base.graph.command.BaseGraphCommandTestSuite;
import pcgen.base.graph.core.BaseGraphCoreTestSuite;
import pcgen.base.graph.edit.BaseGraphEditTestSuite;
import pcgen.base.graph.monitor.BaseGraphMonitorTestSuite;
import pcgen.base.graph.visitor.BaseGraphVisitorTestSuite;
import pcgen.base.util.BaseUtilTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BaseGraphCommandTestSuite.class,
	BaseGraphCoreTestSuite.class, BaseGraphEditTestSuite.class,
	BaseGraphMonitorTestSuite.class, BaseGraphVisitorTestSuite.class,
	BaseUtilTestSuite.class})
public class AllBaseUnitTests extends TestSuite
{
	// No contents, see annotations
}