package pcgen.base.util;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class DoubleKeyMapTest extends TestCase
{

	private static final char CONST_G = 'G';
	private static final char CONST_F = 'F';
	private static final char CONST_D = 'D';
	private static final char CONST_B = 'B';
	private static final char CONST_A = 'A';
	DoubleKeyMap<Integer, Double, Character> dkm;

	@Before
	public void setUp()
	{
		dkm = new DoubleKeyMap<Integer, Double, Character>();
	}

	public void populate()
	{
		dkm.put(Integer.valueOf(1), Double.valueOf(1), CONST_A);
		dkm.put(Integer.valueOf(1), Double.valueOf(2), CONST_B);
		dkm.put(Integer.valueOf(1), Double.valueOf(3), 'C');
		dkm.put(Integer.valueOf(2), Double.valueOf(1), CONST_D);
		dkm.put(Integer.valueOf(2), Double.valueOf(2), 'E');
		dkm.put(null, Double.valueOf(3), CONST_F);
		dkm.put(Integer.valueOf(3), null, CONST_G);
		dkm.put(Integer.valueOf(5), Double.valueOf(6), null);
	}

	@Test
	public void testPutGet()
	{
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		populate();
		assertEquals(Character.valueOf('A'), dkm.get(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('B'), dkm.get(Integer.valueOf(1), Double.valueOf(2)));
		assertEquals(Character.valueOf('C'), dkm.get(Integer.valueOf(1), Double.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(1), Double.valueOf(0)));
		assertEquals(Character.valueOf('D'), dkm.get(Integer.valueOf(2), Double.valueOf(1)));
		assertEquals(Character.valueOf('E'), dkm.get(Integer.valueOf(2), Double.valueOf(2)));
		assertEquals(Character.valueOf('F'), dkm.get(null, Double.valueOf(3)));
		assertEquals(Character.valueOf('G'), dkm.get(Integer.valueOf(3), null));
		assertNull(dkm.get(Integer.valueOf(2), Double.valueOf(3)));
		assertNull(dkm.get(Integer.valueOf(4), Double.valueOf(0)));
		assertNull(dkm.get(Integer.valueOf(1), null));
		assertNull(dkm.get(null, Double.valueOf(1)));
	}

	@Test
	public void testContainsKey()
	{
		assertFalse(dkm.containsKey(Integer.valueOf(4)));
		populate();
		assertTrue(dkm.containsKey(Integer.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(4)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(2), Double.valueOf(1)));
		assertTrue(dkm.containsKey(Integer.valueOf(2), Double.valueOf(2)));
		assertFalse(dkm.containsKey(Integer.valueOf(2), Double.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(3), Double.valueOf(0)));
		assertFalse(dkm.containsKey(Integer.valueOf(1), null));
		assertFalse(dkm.containsKey(null, Double.valueOf(1)));
		assertTrue(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(3), null));
	}

	@Test
	public void testRemove()
	{
		assertNull(dkm.remove(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		assertEquals(Character.valueOf('A'), dkm.remove(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertNull(dkm.remove(Integer.valueOf(1), Double.valueOf(1)));
		assertEquals(Character.valueOf('F'), dkm.remove(null, Double.valueOf(3)));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertNull(dkm.remove(null, Double.valueOf(3)));
		assertEquals(Character.valueOf('G'), dkm.remove(Integer.valueOf(3), null));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertNull(dkm.remove(Integer.valueOf(3), null));
		assertEquals(Character.valueOf('B'), dkm.remove(Integer.valueOf(1), Double.valueOf(2)));
		assertTrue(dkm.containsKey(Integer.valueOf(1)));
		assertEquals(Character.valueOf('C'), dkm.remove(Integer.valueOf(1), Double.valueOf(3)));
		assertFalse(dkm.containsKey(Integer.valueOf(1)));
	}

	@Test
	public void testGetKeySet()
	{
		Set<Integer> s = dkm.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = dkm.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = dkm.getKeySet();
		assertEquals(5, s3.size());
		assertTrue(s3.contains(Integer.valueOf(1)));
		assertTrue(s3.contains(Integer.valueOf(2)));
		assertTrue(s3.contains(Integer.valueOf(3)));
		assertTrue(s3.contains(Integer.valueOf(5)));
		assertTrue(s3.contains(null));
	}

	@Test
	public void testGetSecondaryKeySet()
	{
		Set<Double> s = dkm.getSecondaryKeySet(Integer.valueOf(4));
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(Double.valueOf(-5));
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Double> s2 = dkm.getSecondaryKeySet(Integer.valueOf(4));
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = dkm.getSecondaryKeySet(Integer.valueOf(1));
		assertEquals(3, s3.size());
		assertTrue(s3.contains(Double.valueOf(1)));
		assertTrue(s3.contains(Double.valueOf(2)));
		assertTrue(s3.contains(Double.valueOf(3)));
		Set<Double> s4 = dkm.getSecondaryKeySet(Integer.valueOf(3));
		assertEquals(1, s4.size());
		assertTrue(s4.contains(null));
		Set<Double> s5 = dkm.getSecondaryKeySet(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains(Double.valueOf(3)));
	}

	@Test
	public void testClearIsEmpty()
	{
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		populate();
		assertFalse(dkm.isEmpty());
		assertEquals(5, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.put(null, Double.valueOf(3), 'F');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.put(Integer.valueOf(3), null, 'G');
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
		dkm.put(Integer.valueOf(5), Double.valueOf(6), null);
		assertFalse(dkm.isEmpty());
		assertEquals(1, dkm.firstKeyCount());
		dkm.clear();
		assertTrue(dkm.isEmpty());
		assertEquals(0, dkm.firstKeyCount());
	}

	@Test
	public void testValues()
	{
		Set<Character> s = dkm.values(Integer.valueOf(4));
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add('Q');
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Character> s2 = dkm.values(Integer.valueOf(4));
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate();
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Character> s3 = dkm.values(Integer.valueOf(1));
		assertEquals(3, s3.size());
		assertTrue(s3.contains('A'));
		assertTrue(s3.contains('B'));
		assertTrue(s3.contains('C'));
		Set<Character> s4 = dkm.values(Integer.valueOf(3));
		assertEquals(1, s4.size());
		assertTrue(s4.contains('G'));
		Set<Character> s5 = dkm.values(null);
		assertEquals(1, s5.size());
		assertTrue(s5.contains('F'));
		Set<Character> s6 = dkm.values(Integer.valueOf(5));
		assertEquals(1, s6.size());
		assertTrue(s6.contains(null));
	}

	@Test
	public void testRemoveValue()
	{
		assertFalse(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		populate();
		assertTrue(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertFalse(dkm.removeValue(Integer.valueOf(1), 'A'));
		assertFalse(dkm.containsKey(Integer.valueOf(1), Double.valueOf(1)));
		assertTrue(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertFalse(dkm.removeValue(null, 'F'));
		assertFalse(dkm.containsKey(null, Double.valueOf(3)));
		assertTrue(dkm.containsKey(Integer.valueOf(3), null));
		assertTrue(dkm.removeValue(Integer.valueOf(3), 'G'));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertFalse(dkm.removeValue(Integer.valueOf(3), 'G'));
		assertFalse(dkm.containsKey(Integer.valueOf(3), null));
		assertTrue(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
		assertTrue(dkm.removeValue(Integer.valueOf(5), null));
		assertFalse(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
		assertFalse(dkm.removeValue(Integer.valueOf(5), null));
		assertFalse(dkm.containsKey(Integer.valueOf(5), Double.valueOf(6)));
	}
}
