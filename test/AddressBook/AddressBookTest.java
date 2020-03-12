package AddressBook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressBookTest {

    AddressBook addressBook = new AddressBook();
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

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
        Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
        String expectedResult = "";

        // Act
        addressBook.add(instance);
        addressBook.remove(0);

        assertEquals(addressBook.getRowCount(), 0);
    }

    @Test
    void getPersons() {
        AddressBook getPersonTest = new AddressBook();

        Person[] personResult = getPersonTest.getPersons();
    }

    @Test
    void set() {
        // Act
        addressBook.add(instance);
        addressBook.set(0, instance);
    }

    @Test
    void get() {
        // Act
        addressBook.add(instance);
        addressBook.get(0);
    }

    @Test
    void clear() {
        // Act
        addressBook.clear();
        addressBook.add(instance);
        addressBook.clear();
    }

    @Test
    void getRowCount() {
        // Act
        addressBook.getRowCount();
    }

    @Test
    void getColumnCount() {
        // Act
        addressBook.getColumnCount();
    }

    @Test
    void getValueAt() {
        // Act
        addressBook.add(instance);
        addressBook.getValueAt(0, 0);
    }

    @Test
    void getColumnName() {
        // Act
        addressBook.getColumnName(0);
    }
}