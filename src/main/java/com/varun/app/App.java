package com.varun.app;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This is the main program which invokes the LocationService to print user name
 * and ids of users within radius of 100km from given location.
 * 
 * @author varun
 *
 */
public class App
{
    private static final String DEFAULT_CUSTOMER_FILE = "/customer.txt";
    private static final double DEFAULT_BASE_LATITUDE = 53.339428;
    private static final double DEFAULT_BASE_LONGITUDE = -6.257664;
    private static final double DEFAULT_RADIUS = 100;

    static Logger LOG = Logger.getLogger(App.class.getName());

    public static void main(String[] args)
    {
        LocationService service = new LocationService();
        try
        {
            Set<User> usersToBeInvited = service.getUsersWithinRadius(DEFAULT_BASE_LATITUDE, DEFAULT_BASE_LONGITUDE,
                    DEFAULT_RADIUS, DEFAULT_CUSTOMER_FILE);

            printUsers(usersToBeInvited);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage());
        }
    }

    private static void printUsers(Set<User> users)
    {
        for (User user : users)
        {
            LOG.info("Name: " + user.getName() + " Id: " + user.getId());
        }
    }
}
