package AddressBook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressBookTest {

    private AddressBook addressbookStub;
    private Person personStub;

    private AddressBook addressbookMock = new AddressBook();
    private Person samplePersonMock = mock(Person.class);

    private AddressBook addressbookTest = new AddressBook();
    private Person personTest = new Person("Armand", "person", "street", "city",
            "state", "33317", "9545130066");

    @BeforeEach
    public void setUp() {
        addressbookStub = mock(AddressBook.class);
        personStub = new Person("test", "person", "street", "city",
                "state", "33317", "9545130066");
    }


    @Test
    void addStub() {
        addressbookStub.add(personStub);
        Assertions.assertEquals(personStub.getFirstName(), "test");
    }

    @Test
    void removeMock() {
        addressbookMock.add(samplePersonMock);
        addressbookMock.remove(0);
        assertEquals(addressbookMock.getRowCount(), 0);
    }

    @Test
    void getPersons() {
        AddressBook getTest = new AddressBook();
        Person[] result = getTest.getPersons();


    }

    @Test
    void set() {
        addressbookTest.add(personTest);
        addressbookTest.set(0, personTest);

    }

    @Test
    void get() {
        addressbookTest.add(personTest);
        addressbookTest.get(0);

    }

    @Test
    void clear() {
        addressbookTest.add(personTest);
        addressbookTest.clear();
    }

    @Test
    void getRowCount() {
        addressbookTest.getRowCount();
    }

    @Test
    void getColumnCount(){
        addressbookTest.getColumnCount();
    }

    @Test
    void getValueAt(){
        addressbookTest.add(personTest);
        addressbookTest.getValueAt(0,0);

    }

    @Test
    void getColumnName(){
        addressbookTest.getColumnName(0);
    }

}