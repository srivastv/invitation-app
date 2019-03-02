package com.varun.app;

import java.io.IOException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for LocationService class.
 * 
 * @author varun
 */
public class LocationServiceTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public LocationServiceTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(LocationServiceTest.class);
    }

    public void testFindDistanceBetweenPoints()
    {
        LocationService service = new LocationService();
        assertEquals(82.6949261163998,
                service.getDistanceBetweenTwoLocation(53.339428, -6.257664, 54.080556, -6.361944));

        assertEquals(0.0, service.getDistanceBetweenTwoLocation(53.339428, -6.257664, 53.339428, -6.257664));
    }

    public void testGetUsersFromInvalidFile()
    {
        LocationService service = new LocationService();
        try
        {
            service.getUsersFromFile("non_existing_file_name");
            fail("Expected exception not thrown");
        }
        catch (IOException e)
        {
            // pass, do nothing
        }
    }

    public void testGetUsersFromFile()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersFromFile("/customer.txt");
            assertEquals(32, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersFromFileWithDuplicates()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersFromFile("/customer_with_duplicates.txt");
            assertEquals(32, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersFromFileWithInvalidEntries()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersFromFile("/customer_with_invalid_entries.txt");
            assertEquals(32, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersFromFileWithEmptyFileName()
    {
        LocationService service = new LocationService();
        try
        {
            service.getUsersFromFile("");
            fail("Expected exception not thrown");

        }
        catch (IOException e)
        {
            // pass
        }
    }

    public void testParseInvalidUserJson()
    {
        LocationService service = new LocationService();
        assertNull(service.parseUserJsonEntry(null));
        assertNull(service.parseUserJsonEntry(""));

        // missing id
        assertNull(
                service.parseUserJsonEntry("{'latitude': '52.2559432', 'name': 'varun', 'longitude': '-7.1048927'}"));

        // negative id
        assertNull(service.parseUserJsonEntry(
                "{'latitude': '52.2559432', 'user_id': -9, 'name': 'varun', 'longitude': '-7.1048927'}"));

        // missing latitude
        assertNull(service.parseUserJsonEntry("{'user_id': 9, 'name': 'varun', 'longitude': '-7.1048927'}"));

        // mising longitude
        assertNull(service.parseUserJsonEntry("{'latitude': '52.2559432', 'user_id': 9, 'name': 'varun'}"));

        // missing name
        assertNull(service.parseUserJsonEntry("{'latitude': '52.2559432', 'user_id': 9, 'longitude': '-7.1048927'}"));

        // invalid latitude value
        assertNull(service
                .parseUserJsonEntry("{'latitude': 'hello', 'user_id': 9, 'name': 'varun', 'longitude': '-7.1048927'}"));

        // invalid longitude value
        assertNull(service
                .parseUserJsonEntry("{'latitude': '52.2559432', 'user_id': 9, 'name': 'varun', 'longitude': 'hello'}"));

        // invalid json
        assertNull(service.parseUserJsonEntry(
                "{'latitude': '52.2559432', 'user_id': 9 'name': 'varun', 'longitude': '-7.1048927'"));

    }

    public void testParseValidUserJson()
    {
        LocationService service = new LocationService();

        User u = service.parseUserJsonEntry(
                "{'latitude': '52.2559432', 'user_id': 9, 'name': 'varun', 'longitude': '-7.1048927'}");

        assertEquals("varun", u.getName());
        assertEquals(52.2559432, u.getLatitude());
        assertEquals(-7.1048927, u.getLongitude());
        assertEquals(9, u.getId());
    }

    public void testGetUsersWithinRadius()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersWithinRadius(53.339428, -6.257664, 100, "/customer.txt");
            assertEquals(16, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersWithinRadiusWithNegativeRadius()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersWithinRadius(53.339428, -6.257664, -100, "/customer.txt");
            assertEquals(0, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testDuplicateEntriesInfile()
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> users = service.getUsersWithinRadius(53.339428, -6.257664, 100, "/customer_with_duplicates.txt");
            assertEquals(16, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersWithinRadiusForUserInside()
    {
        LocationService service = new LocationService();
        User userInRadius = new User(31, "Alan Behan", 53.1489345, -6.8422408);

        try
        {
            Set<User> users = service.getUsersWithinRadius(53.339428, -6.257664, 100, "/customer.txt");
            assertTrue(users.contains(userInRadius));
            assertEquals(16, users.size());

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }

    public void testGetUsersWithinRadiusForUserOutside()
    {
        LocationService service = new LocationService();
        User userOutSideRadius = new User(1, "Alice Cahill", 51.92893, -10.27699);

        try
        {
            Set<User> users = service.getUsersWithinRadius(53.339428, -6.257664, 100, "/customer.txt");
            assertEquals(16, users.size());

            assertFalse(users.contains(userOutSideRadius));

        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
    }
}
