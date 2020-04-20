package AddressBook;

import AddressBook.AddressBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressBookTest {

    AddressBook addressBook = new AddressBook();
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
    Person instanceNumTwo = new Person("Ben", "Fulker", "1660 sw Fuck Street", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void add() {
        // Act
        addressBook.add(instance);

        // Assert
        assertEquals(instance, addressBook.get(0));
    }

    @Test
    void remove() {
        // Arrange
        Person expectedPerson = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

        // Act
        addressBook.add(instance);
        addressBook.remove(0);

        // Assert
        assertEquals(addressBook.getRowCount(),0);
    }

    @Test
    void getPersons() {
        //Arrange
        int count = 0;
        Person[] pArray = {instanceNumTwo, instance};
        AddressBook getPersonTest = new AddressBook();
        //Act
        getPersonTest.add(instanceNumTwo);
        getPersonTest.add(instance);
        Person[] personResult = getPersonTest.getPersons();
        //Assert
        assertEquals(personResult.length,2);
        for (Person p : personResult) {
            assertEquals(p,pArray[count++]);
            System.out.println(p);
        }
    }

    @Test
    void set() {
        // Act
        addressBook.add(instance);
        addressBook.add(instanceNumTwo);
        assertEquals(addressBook.get(0),instance);
        assertEquals(addressBook.get(1),instanceNumTwo);

        //Act ReArrange
        addressBook.set(0, instanceNumTwo);
        addressBook.set(1, instance);

        //Assert
        assertEquals(addressBook.get(1),instance);
        assertEquals(addressBook.get(0),instanceNumTwo);

    }

    @Test
    void get() {
        // Act
        addressBook.add(instance);
        addressBook.get(0);
        //Assert
        assertEquals(addressBook.get(0), instance);
        System.out.println(addressBook.get(0));
        System.out.println(instance);
    }

    @Test
    void clear() {
        addressBook.clear(); //Act
        addressBook.add(instance);
        addressBook.clear();
        assertEquals(addressBook.getRowCount(), 0);  //Assert

    }

    @Test
    void getRowCount() {
        // Act
        addressBook.add(instanceNumTwo);
        addressBook.add(instance);
        addressBook.add(new Person("Test", "last","34 help me", "nipple", "wv", "93398","9383890033"));
        //Assert
        assertEquals(addressBook.getRowCount(),3);
    }

    @Test
    void getColumnCount() {
        assertEquals(addressBook.getColumnCount(), 7);
        System.out.println((addressBook.getColumnCount()));
    }

    @Test
    void getValueAt() {
        addressBook.add(instance); //Act
        assertEquals(addressBook.getValueAt(0, 0), "Mohammed"); //Assert remember col 0 is "last name of person"
    }

    @Test
    void getColumnName() {
        // Act
        addressBook.getColumnName(0);
        assertEquals(addressBook.getColumnName(0), "Last Name"); //Assert getColumnName(0) or field header
    }
}