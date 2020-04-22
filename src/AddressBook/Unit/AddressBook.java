package AddressBook.Unit;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 * Orignainlly by Ingrid Buckley Jan 2020
 * Edited by Ben Fulker and Armand Mohammed
 * This class has a dependency on the Person Class
 */
public class AddressBook extends AbstractTableModel {
    //persons list creates a list of people stored as an arrayList
    private List<Person> persons = new ArrayList<>();

    //create a person array size of persons list and returns it
    public Person[] getPersons() {
        return persons.toArray(new Person[persons.size()]);
    }

    /**
     * Adds a person to the address book has a dependency on Person class
     * @param p
     */
    public void add(Person p) {
        int newIndex = persons.size();
        persons.add(p);
        fireTableRowsInserted(newIndex, newIndex);
    }

    /**
     * Sets the person at the given index to the Person specified.
     *
     * @param index  Index to update.
     * @param person Person to replace the item with.
     */
    public void set(int index, Person person) {
        persons.set(index, person);
        fireTableRowsUpdated(index, index);
    }

    /**
     * Removes a person from the addressBook
     * @param index
     */
    public void remove(int index) {
        persons.remove(index);
        fireTableRowsDeleted(index, index);
    }

    /**
     * gets person from addressBook based on index location in persons arrayList
     * @param index
     * @return person at index location
     */
    public Person get(int index) {
        return persons.get(index);
    }

    /**
     * Clears this address book.
     */
    public void clear() {
        if (persons.size() == 0) {
            return;
        }
        fireTableRowsDeleted(0, persons.size() - 1);
        persons.clear();
    }

    /**
     * Gets the row count by persons arrayList size
     * @return size of list
     */
    @Override
    public int getRowCount() {
        return persons.size();
    }

    /**
     * gets column counts
     * @return returns column count
     */
    public int getColumnCount() {
        return Person.fields.length;
    }

    /**
     * Gets field value based on row and column index
     * @param row
     * @param column
     * @return returns value of textField
     */
    @Override
    public Object getValueAt(int row, int column) {
        /*try {
            return persons.get(row).getField(column);
        }catch(NullPointerException e){
            System.out.println("null pointer: " + "\n" + e);
        }*/
        return persons.get(row).getField(column);
    }

    /**
     * Gets name of column based on Person field names with the column as index
     * @param column
     * @return name of field
     */
    @Override
    public String getColumnName(int column) {
        return Person.fields[column];
    }
}
