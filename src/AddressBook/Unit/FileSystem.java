package AddressBook.Unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;

/**
 * Created by
 * Originally by Ingrid Buckley Jan 2020
 * Edited by Ben Fulker and Armand Mohammed
 * Edited 4/22/20 by Paul Nicowski
 */
public class FileSystem {
    //This class has dependencies on AddressBook.java, File,
    /**
     * This method allows the address book application to read sqllite files
     * @param addressBook
     * @param file
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void readFile(AddressBook addressBook, File file) throws SQLException, FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        if(!file.canRead()){
            throw new FileNotFoundException();
        }
        //creates connection to sqlite and queries the table in the file
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        ResultSet rs = connection.createStatement().executeQuery("SELECT lastName, firstName, address, city, state, zip, phone FROM persons");
        addressBook.clear();  // Clear the current AddressBook contents
        while (rs.next()) {  // Iterate through all the records, adding them to the AddressBook
            Person p = new Person(rs.getString("firstName"), rs.getString("lastName"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zip"), rs.getString("phone"));
            addressBook.add(p);
        }
        //closes connection
        connection.close();
    }

    /**
     * Allows user to save current addressBook file displayed in the GUI
     * @param addressBook
     * @param file
     * @throws SQLException
     */
    public void saveFile(AddressBook addressBook, File file) throws SQLException {
        //creates connection to sqlite and queries the table in the file
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        Statement statement = connection.createStatement();

        // Create the table structure
        statement.execute("DROP TABLE IF EXISTS persons");
        statement.execute("CREATE TABLE persons (firstName TEXT, lastName TEXT, address TEXT, city TEXT, state TEXT, zip TEXT, phone TEXT)");

        // Insert the data into the database
        PreparedStatement insert = connection.prepareStatement("INSERT INTO persons (lastName, firstName, address, city, state, zip, phone) VALUES (?, ?, ?, ?, ?, ?, ?)");
        for (Person p : addressBook.getPersons()) {
            for (int i = 0; i < Person.fields.length; i++) {
                insert.setString(i + 1, p.getField(i));
            }
            insert.executeUpdate();
        }
        //closes connection
        connection.close();
    }

    /**
     * Deletes an address book file
     * @param addressBook
     * @param file
     * @throws SQLException
     */
    public void deleteFile(AddressBook addressBook, File file) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        Statement stmt = connection.createStatement();

        stmt.close();
        connection.close();
        boolean result = new File(file.getAbsolutePath()).delete();
    }

}
