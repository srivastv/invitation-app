package com.varun.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for User class.
 * 
 * @author varun
 */
public class UserTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public UserTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(UserTest.class);
    }

    public void testUserSorting()
    {
        User user1 = new User(1, "varun", 12345, 6);
        User user2 = new User(2, "john", 12345.5, 6.2);

        assertTrue(user1.compareTo(user2) < 0);
        assertTrue(user1.compareTo(user1) == 0);
    }

    public void testUserEquality()
    {
        User user1 = new User(1, "varun", 12345, 6);
        User user2 = new User(2, "varun", 12345, 6);

        assertFalse(user1.equals(user2));
        assertTrue(user1.equals(user1));
    }
}
