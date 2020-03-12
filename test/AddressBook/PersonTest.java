package AddressBook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    //Arrange
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void getFirstName() {
        //Act
        String firstName = instance.getFirstName();
        //Assert
        assertEquals("Armand",firstName);
    }

    @Test
    void getLastName() {
        //Act
        String lastName = instance.getLastName();
        //Assert
        assertEquals("Mohammed",lastName);
    }

    @Test
    void getAddress() {
        //Act
        String address = instance.getAddress();
        //Assert
        assertEquals("1660 sw 48th ave", address);
    }

    @Test
    void getCity() {
        //Act
        String city = instance.getCity();
        //Assert
        assertEquals("Fort Myers", city);
    }

    @Test
    void getState() {
        //Act
        String state = instance.getState();
        //Assert
        assertEquals("FL", state);
    }

    @Test
    void getZip() {
        //Act
        String zip = instance.getZip();
        //Assert
        assertEquals("33317", zip);
    }

    @Test
    void getPhone() {
        //Act
        String phone = instance.getPhone();
        //Assert
        assertEquals("9545130066", phone);
    }

    @Test
    void testToString() {
        //Act
        String fullName = (instance.getLastName()+", "+instance.getFirstName());
        //Assert
        assertEquals( "Mohammed"+", "+"Armand",fullName);
    }

    @Test
    void containsString() {
    }

    @Test
    void getField() {
    }
}