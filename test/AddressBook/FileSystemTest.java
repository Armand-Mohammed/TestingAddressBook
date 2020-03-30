package AddressBook;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemTest {

    //Arrange
    AddressBook addressBook  = new AddressBook();
    AddressBookController addressBookController = new AddressBookController(addressBook);
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
    Person instanceNumTwo = new Person("Ben", "Fulker", "1660 sw Fuck Street", "Fort Myers", "FL", "33317", "9545130066");


    @Test
    void readFile() {

        //Arrange

        FileSystem fileSystem = new FileSystem();
        //Act
        addressBook.add(instance);
        addressBook.add(instanceNumTwo);
        Throwable exception = assertThrows(FileNotFoundException.class, () -> {
            throw new FileNotFoundException("FileNotFound message");
        });

        Throwable exception2 = assertThrows(SQLException.class, () -> {
            throw new SQLException("SQL message");
        });
       // Files.deleteIfExists(Paths.get("Address Book");
        File file = new File("Address Book");
        assertDoesNotThrow(() -> fileSystem.saveFile(addressBook,file));


        assertDoesNotThrow(() -> fileSystem.readFile(addressBook,file));
        assertTrue(addressBook.getRowCount()==2);

        boolean canRead = file.canRead();
        //Assert
        assertEquals("SQL message", exception2.getMessage());
        assertEquals("FileNotFound message", exception.getMessage());
        assertTrue(canRead);
    }

    @Test
    void saveFile(){
        FileSystem fileSystem = new FileSystem();
        Throwable exception2 = assertThrows(SQLException.class, () -> {
            throw new SQLException("SQL message");
        });
        File file = new File("Address Book2");
        assertDoesNotThrow(() -> fileSystem.saveFile(addressBook,file));
        //fileSystem.saveFile(addressBook,file);

        boolean exists = file.exists();
        assertTrue(exists, file.toString());
    }
}
