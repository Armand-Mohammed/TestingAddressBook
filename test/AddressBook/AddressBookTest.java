package AddressBook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressBookTest {

    AddressBook addressBook = new AddressBook();
    Person instance = new Person("Armand", "Mohammed", "Street address", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void add() {
        addressBook.add(instance);
        assertEquals(instance, addressBook.get(0));
    }

    @Test
    void remove() {
        addressBook.add(instance);
        addressBook.remove(0);
    }

    @Test
    void getPersons() {
        AddressBook getPersonTest = new AddressBook();

        Person[] personResult = getPersonTest.getPersons();
    }

    @Test
    void set() {
        addressBook.add(instance);
        addressBook.set(0, instance);
    }

    @Test
    void get() {
        addressBook.add(instance);
        addressBook.get(0);
    }

    @Test
    void clear() {
        addressBook.clear();

        addressBook.add(instance);
        addressBook.clear();
    }

    @Test
    void getRowCount() {
        addressBook.getRowCount();
    }

    @Test
    void getColumnCount() {
        addressBook.getColumnCount();
    }

    @Test
    void getValueAt() {
        addressBook.add(instance);
        addressBook.getValueAt(0, 0);
    }

    @Test
    void getColumnName() {
        addressBook.getColumnName(0);
    }
}