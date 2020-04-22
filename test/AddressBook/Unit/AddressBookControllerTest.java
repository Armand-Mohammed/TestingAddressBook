package AddressBook.Unit;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: by Ben Fulker and Armand Mohammed
 * Class has dependencies on AddressBook.java, AddressBookController and Person.jav
 */
class AddressBookControllerTest {

    //Arrange for all tests
    AddressBook addressBook  = new AddressBook();
    AddressBookController addressBookController = new AddressBookController(addressBook);
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
    Person instanceNumTwo = new Person("Ben", "Fulker", "1660 sw Fuck Street", "Fort Myers", "FL", "33317", "9545130066");

    /**
     * Test if the add method works in the addressBookController class
     */
    @Test
    void add() {
        //Act
        addressBookController.add(instance);

        //Assert
        assertEquals(instance, addressBookController.get(0));
    }

    /**
     * Test if the set method works in the addressBookController class
     */
    @Test
    void set() {
        //Act
        addressBookController.add(instance);
        addressBookController.add(instanceNumTwo);
        assertEquals(instance, addressBookController.get(0));
        assertEquals(instanceNumTwo, addressBookController.get(1));

        addressBookController.set(1,instance);
        addressBookController.set(0,instanceNumTwo);

        //Assert
        assertEquals(instanceNumTwo,addressBookController.get(0));
        assertEquals(instance, addressBookController.get(1));

    }

    /**
     * Test if the remove method works in the addressBookController class
     */
    @Test
    void remove() {
        //Act
        addressBookController.add(instance);
        addressBookController.set(0,instance);
        addressBookController.remove(0);

        //Assert
        assertEquals(addressBook.getRowCount(), 0);
        assertNotEquals(instance, null);
    }

    /**
     * Test if the get method works in the addressBookController class
     */
    @Test
    void get() {
        //Act
        addressBookController.add(instance);
        addressBookController.set(0,instance);
        addressBookController.get(0);

        //Assert
        assertEquals(instance, addressBookController.get(0));
    }

    /**
     * Test if the clear method works in the addressBookController class
     */
    @Test
    void clear() {
        //Act
        addressBookController.add(instance);
        addressBookController.add(instanceNumTwo);
        assertEquals(instance, addressBookController.get(0));
        assertEquals(instanceNumTwo, addressBookController.get(1));

        addressBookController.clear();

        //Assert
        assertNotEquals(instanceNumTwo, null);
        assertEquals(addressBook.getRowCount(), 0);
    }

    /**
     * Test if the open method works in the addressBookController class
     */
    @Test
    void open()throws FileNotFoundException, SQLException {



        Throwable exception = assertThrows(FileNotFoundException.class, () -> {
            throw new FileNotFoundException("FileNotFound message");
        });

        Throwable exception2 = assertThrows(SQLException.class, () -> {
            throw new SQLException("SQL message");
        });
        File file = new File("Address Book");
        new FileSystem().saveFile(addressBook,file);
        addressBookController.open(new File("Address Book"));

        boolean canRead = file.canRead();
        //Assert
        assertEquals("SQL message", exception2.getMessage());
        assertEquals("FileNotFound message", exception.getMessage());
        assertTrue(canRead);
    }

    /**
     * Test if the save method works in the addressBookController class
     */
    @Test
    void save() throws SQLException {
        Throwable exception2 = assertThrows(SQLException.class, () -> {
            throw new SQLException("SQL message");
        });
        File file = new File("Address Book2");
        addressBookController.save(file);

        boolean exists = file.exists();
        assertTrue(exists, file.toString());
    }

    /**
     * Test if the getModel method works in the addressBookController class
     */
    @Test
    void getModel() {
        assertEquals(addressBookController.getModel(), addressBook);
    }
}