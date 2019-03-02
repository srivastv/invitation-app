package com.varun.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Location service for location related functionalities.
 * 
 * @author varun
 *
 */
public class LocationService
{
    static Logger LOG = Logger.getLogger(LocationService.class.getName());

    /**
     * Method to get the users from given file whose location is within the given
     * radius from base location.
     * 
     * @param baseLatitude
     *            The latitude of the base location.
     * @param baseLongitude
     *            The longitude of the base location.
     * @param radius
     *            The radius value in KM
     * @param customerFile
     *            The customer file name to be looked in class path.
     * @return Sorted set of {@link User} with location within the radius from base
     *         location.
     * @throws IOException
     */
    public Set<User> getUsersWithinRadius(double baseLatitude, double baseLongitude, double radius, String customerFile)
            throws IOException
    {
        Set<User> allUsers = getUsersFromFile(customerFile);
        return getUsersWithinRadius(baseLatitude, baseLongitude, radius, allUsers);
    }

    /**
     * Parses user json string into {@link User} object.
     *
     * @param jsonRecord
     *            json string representing a user entry.
     * @return Instance of {@link User}
     */
    public User parseUserJsonEntry(String jsonRecord)
    {
        if (StringUtils.isEmpty(jsonRecord))
        {
            LOG.warn("Empty json record");
            return null;
        }

        try
        {
            JSONObject json = new JSONObject(jsonRecord);
            String name = json.getString("name");
            String latitudeString = json.getString("latitude");
            String longitudeString = json.getString("longitude");
            int id = json.getInt("user_id");

            if (StringUtils.isBlank(name) || StringUtils.isBlank(latitudeString) || StringUtils.isBlank(longitudeString)
                    || id <= 0)
            {
                LOG.warn("Invalid user json entry. Once of the mandatory user attribute is missing : " + jsonRecord);
                return null;
            }

            double latitude = Double.valueOf(latitudeString);
            double longitude = Double.valueOf(longitudeString);

            return new User(id, name, latitude, longitude);

        }
        catch (JSONException ex)
        {
            LOG.warn("Invalid user json entry : " + jsonRecord);
            return null;
        }
        catch (NumberFormatException ex)
        {
            LOG.warn("Invalid latitude or longitude value in user json entry : " + jsonRecord);
            return null;
        }
    }

    /**
     * Finds distance in KM between two given locations.
     *
     * @param latitude1
     *            latitude of the first location
     * @param longitude1
     *            longitude of the first location
     * @param latitude2
     *            latitude of the second location
     * @param longitude2
     *            longitude of the second location
     * 
     * @return distance in KM between the two locations
     */
    public double getDistanceBetweenTwoLocation(double latitude1, double longitude1, double latitude2,
            double longitude2)
    {
        // formula as per wikipedia
        double radiusOfEarth = 6371; // Radius of the earth in KM
        double latitudeDiff = degreesToRadians(latitude2 - latitude1);
        double longitudeDiff = degreesToRadians(longitude2 - longitude1);
        double a = Math.sin(latitudeDiff / 2) * Math.sin(latitudeDiff / 2) + Math.cos(degreesToRadians(latitude1))
                * Math.cos(degreesToRadians(latitude2)) * Math.sin(longitudeDiff / 2) * Math.sin(longitudeDiff / 2);

        double centralAngle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = radiusOfEarth * centralAngle;
        return distance;
    }

    /**
     * Reads the json user entries from given file and returns set of User objects.
     *
     * @param fileName
     *            Name of the file containing user entry. This will be looked for in
     *            classpath
     * @return Set of User
     * @throws IOException
     */
    public Set<User> getUsersFromFile(String fileName) throws IOException
    {
        if (StringUtils.isBlank(fileName))
        {
            throw new FileNotFoundException("File name is null or empty");
        }

        InputStream stream = App.class.getResourceAsStream(fileName);

        if (stream == null)
        {
            throw new FileNotFoundException("File not found in classpath: " + fileName);
        }

        Set<User> usersFromFile = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line = reader.readLine();

            while (line != null)
            {
                User user = parseUserJsonEntry(line);
                if (user != null)
                {
                    usersFromFile.add(user);
                }
                line = reader.readLine();
            }
            reader.close();
        }

        return usersFromFile;
    }

    /**
     * Method to filter and return users from given set of users whose location is
     * within the given radius from base location.
     * 
     * @param baseLatitude
     *            The latitude of the base location.
     * @param baseLongitude
     *            The longitude of the base location.
     * @param radius
     *            The radius value in KM
     * @param users
     *            Set of all users.
     * @return Sorted set of {@link User} with location within the radius from base
     *         location.
     */
    private Set<User> getUsersWithinRadius(double baseLatitude, double baseLongitude, double radius, Set<User> users)
    {
        if (radius < 0)
        {
            LOG.warn("Negative radius value provided");
            return Collections.emptySet();
        }

        Set<User> selectedUsers = new TreeSet<>();

        for (User user : users)
        {
            double distance = getDistanceBetweenTwoLocation(baseLatitude, baseLongitude, user.getLatitude(),
                    user.getLongitude());

            if (distance < radius)
            {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
    }

    private double degreesToRadians(double degrees)
    {
        return degrees * Math.PI / 180;
    }
}
