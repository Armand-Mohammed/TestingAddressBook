package AddressBook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {
    AddressBook addressBook = new AddressBook();
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void getFirstName() {
        // Arrange
        String expectedname = "Armand";

        // Act
        instance.getFirstName();

        // Assert
        assertEquals(expectedname, instance.getFirstName());

    }

    @Test
    void getLastName() {
        // Arrange
        String expectedLastName = "Mohammed";

        // Act
        instance.getLastName();

        // Assert
        assertEquals(expectedLastName, instance.getLastName());
    }

    @Test
    void getAddress() {
        // Arrange
        String expectedAddress = "1660 sw 48th ave";

        // Act
        instance.getAddress();

        // Assert
        assertEquals(expectedAddress, instance.getAddress());
    }

    @Test
    void getCity() {
        // Arrange
        String expectedCity = "Fort Myers";

        // Act
        instance.getCity();

        // Assert
        assertEquals(expectedCity, instance.getCity());
    }

    @Test
    void getState() {
        // Arrange
        String expectedState = "FL";

        // Act
        instance.getState();

        // Assert
        assertEquals(expectedState, instance.getState());
    }

    @Test
    void getZip() {
        // Arrange
        String expectedZip = "33317";

        // Act
        instance.getZip();

        // Assert
        assertEquals(expectedZip, instance.getZip());
    }

    @Test
    void getPhone() {
        // Arrange
        String phoneNumber = "9545130066";

        // Act
        instance.getPhone();

        // Assert
        assertEquals(phoneNumber, instance.getPhone());
    }

    @Test
    void testToString() {

    }

    @Test
    void containsString() {

    }

    @Test
    void getField() {

    }
}