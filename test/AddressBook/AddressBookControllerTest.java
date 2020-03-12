package AddressBook;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;


class AddressBookControllerTest {

    //Arrange
    AddressBook addressBook  = new AddressBook();
    AddressBookController addressBookController = new AddressBookController(addressBook);
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
    Person instanceNumTwo = new Person("Ben", "Fulker", "1660 sw Fuck Street", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void add() {
        //Act
        addressBookController.add(instance);

        //Assert
        assertEquals(instance, addressBookController.get(0));
    }

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

    @Test
    void get() {
        //Act
        addressBookController.add(instance);
        addressBookController.set(0,instance);
        addressBookController.get(0);

        //Assert
        assertEquals(instance, addressBookController.get(0));
    }

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

    @Rule
    public ExpectedException exception = ExpectedException.none(); // has to be public


    //NOT COMPLETE 
    @Test
    void open() {
        File file = new File("Address Book");
        exception.expect(FileNotFoundException.class);
        exception.expectMessage("File was not found");
        boolean canRead = file.canRead();
        assertTrue(canRead);
        //addressBookController.open(file);
       // InputStream is = this.getClass().getResourceAsStream("Address Book");
       // assertNull(is);
    }

    @Test
    void save() {
    }

    @Test
    void getModel() {
    }
}