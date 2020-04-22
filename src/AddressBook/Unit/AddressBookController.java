package AddressBook.Unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Created by
 * Orignainlly by Ingrid Buckley Jan 2020
 * Edited by Ben Fulker and Armand Mohammed
 * This class is used to control interaction between the GUI, person, addressbook, and person Dialog classes.
 * This class has a dependency on addressBook.java class
 */
public class AddressBookController {
    AddressBook addressBook;

    /**
     * Contruction for AddressBookContorller
     * @param addressBook insntance of the addressbook
     */
    public AddressBookController(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    /**
     * adds a person to the addressbook
     * @param p
     */
    public void add(Person p) {
        addressBook.add(p);
    }

    /**
     * Sets a person allows for someone to chane a person to a different index
     * @param index
     * @param person
     */
    public void set(int index, Person person) {
        addressBook.set(index, person);
    }

    /**
     * Removes a person from the address based on index location
     * @param index
     */
    public void remove(int index) {
        addressBook.remove(index);
    }

    /**
     * Gets a person based on index location
     * @param index
     * @return
     */
    public Person get(int index) {
        return addressBook.get(index);
    }

    /**
     * clears the address book should be index of zero after clear
     */
    public void clear() {
        addressBook.clear();
    }

    /**
     * Opens address book file for GUI class allows interaction between gui and filesystem classes
     * @param file
     * @throws FileNotFoundException
     * @throws SQLException
     */
    public void open(File file) throws FileNotFoundException, SQLException {
        new FileSystem().readFile(addressBook, file);
        addressBook.fireTableDataChanged();
    }

    /**
     * savae address book instance as a sqlite file
     * @param file
     * @throws SQLException
     */
    public void save(File file) throws SQLException {
        new FileSystem().saveFile(addressBook, file);
    }

    /**
     * get the Address book model and returns it
     * @return
     */
    public AddressBook getModel() {
        return addressBook;
    }
}