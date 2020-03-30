package AddressBook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    //Arrange
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

    @Test
    void getFirstName() {
        //Assert
        assertEquals("Armand",instance.getFirstName());
    }

    @Test
    void getLastName() {
        //Assert
        assertEquals("Mohammed",instance.getLastName());
    }

    @Test
    void getAddress() {
        //Assert
        assertEquals("1660 sw 48th ave", instance.getAddress());
    }

    @Test
    void getCity() {
        //Assert
        assertEquals("Fort Myers", instance.getCity());
    }

    @Test
    void getState() {
        //Assert
        assertEquals("FL", instance.getState());
    }

    @Test
    void getZip() {
        //Assert
        assertEquals("33317", instance.getZip());
    }

    @Test
    void getPhone() {
        //Assert
        assertEquals("9545130066", instance.getPhone());
    }

    @Test
    void toStringTest() {
        //Assert
        assertEquals( "Mohammed, Armand",instance.toString());
    }

    @Test
    void containsString() {
        //Act
        boolean firstName = instance.containsString("arm");
        boolean lastName = instance.containsString("mo");
        boolean address = instance.containsString("1660");
        boolean city = instance.containsString("fort");
        boolean state = instance.containsString("FL");
        boolean zip = instance.containsString("333");
        boolean phone = instance.containsString("954");
        //Assert
        assertTrue(firstName);
        assertTrue(lastName);
        assertTrue(address);
        assertTrue(city);
        assertTrue(zip);
        assertTrue(state);
        assertTrue(phone);
    }

    @Test
    void getField() {
        //Arrange
       // Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
        //Act
        String lastName = instance.getField(0);
        String firstName = instance.getField(1);
        String address = instance.getField(2);
        String city = instance.getField(3);
        String state = instance.getField(4);
        String zip = instance.getField(5);
        String phone = instance.getField(6);
        Exception exception = assertThrows(Exception.class, () -> instance.getField(7));

        //Assert
        assertEquals("Armand", firstName);
        assertEquals("Mohammed", lastName);
        assertEquals("1660 sw 48th ave", address);
        assertEquals("Fort Myers", city);
        assertEquals("FL", state);
        assertEquals("33317", zip);
        assertEquals("9545130066", phone);

        assertEquals("Field number out of bounds",exception.getMessage());
    }

    @Test
    void isLetters() {
        //Arrange
        String actual = "A";

        //Act

        //Assert
        assertTrue(actual.matches("[a-zA-Z]+"));
    }

    @Test
    void isLettersFalse() {
        //Arrange
        int actual = '5';
        String expected = "test";

        //Act

        //Assert
        assertNotSame(actual, expected);
    }

    @Test
    void isNumbers() {
        //Arrange
        String actual = "5";

        //Act

        //Assert
        assertTrue(actual.matches("^[0-9]*$"));
    }
    @Test
    void personFirstNamePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("1Ben","fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personFirstNameEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("","fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personFirstNameNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person(null,"fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personLastNamePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","1fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personLastNameEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personLastNameNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben",null,"my address","fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personAddressNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker",null,"fort myers", "FL","39388","9876543210"));
    }
    @Test
    void personAddressEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","","fort myers", "FL","39388","9876543210"));
    }

    @Test
    void personCityPassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","1fort myers", "FL","A9388","9876543210"));
    }
    @Test
    void personCityEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","", "FL","","9876543210"));
    }
    @Test
    void personCityNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address",null, "FL","98494","9876543210"));
    }
    @Test
    void personStatePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "1L","A9388","9876543210"));
    }
    @Test
    void personStateEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","fort My", "","","9876543210"));
    }
    @Test
    void personStateNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","Fort My", null,"98494","9876543210"));
    }

    @Test
    void personZipPassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","A9388","9876543210"));
    }
    @Test
    void personZipEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","fort myers", "FL","","9876543210"));
    }
    @Test
    void personZipNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL",null,"9876543210"));
    }
    @Test
    void personPhonePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","49388","AAA6543210"));
    }
    @Test
    void personPhoneEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","fort myers", "FL","45555",""));
    }
    @Test
    void personPhoneNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","39487",null));
    }
}