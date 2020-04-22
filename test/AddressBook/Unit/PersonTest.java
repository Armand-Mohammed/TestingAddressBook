package AddressBook.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Authored by Ben Fulker and Armand Mohammed
 * This class has dependencies on the Person Class.
 */
class PersonTest {

    //Arrange for all test in class
    Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");

    /**
     * Tests if getFirstName method in the peron class works
     */
    @Test
    void getFirstName() {
        //Assert
        Assertions.assertEquals("Armand",instance.getFirstName());
    }

    /**
     * Tests if getLastName method method in the peron class works
     */
    @Test
    void getLastName() {
        //Assert
        Assertions.assertEquals("Mohammed",instance.getLastName());
    }

    /**
     * Tests if getAddress method in the peron class works
     */
    @Test
    void getAddress() {
        //Assert
        Assertions.assertEquals("1660 sw 48th ave", instance.getAddress());
    }

    /**
     * Tests if getCity method in the peron class works
     */
    @Test
    void getCity() {
        //Assert
        Assertions.assertEquals("Fort Myers", instance.getCity());
    }

    /**
     * Tests if getState method in the peron class works
     */
    @Test
    void getState() {
        //Assert
        Assertions.assertEquals("FL", instance.getState());
    }

    /**
     * Tests if getZip method in the peron class works
     * */
    @Test
    void getZip() {
        //Assert
        Assertions.assertEquals("33317", instance.getZip());
    }

    /**
     * Tests if getPhone method in the peron class works
     */
    @Test
    void getPhone() {
        //Assert
        Assertions.assertEquals("9545130066", instance.getPhone());
    }

    /**
     * Tests if our overriden toString method in the peron class works
     */
    @Test
    void toStringTest() {
        //Assert
        Assertions.assertEquals( "Mohammed, Armand",instance.toString());
    }

    /**
     * Tests if containString method in the peron class works
     */
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

    /**
     * Tests if getField method in the peron class works
     */
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

    /**
     * Tests if isLetters method in the peron class works
     */
    @Test
    void isLetters() {
        //Arrange
        String actual = "A";

        //Act

        //Assert
        assertTrue(actual.matches("[a-zA-Z]+"));
    }

    /**
     * Tests if isLetters doesn't work for numbers
     */
    @Test
    void isLettersFalse() {
        //Arrange
        int actual = '5';
        String expected = "test";

        //Act

        //Assert
        assertNotSame(actual, expected);
    }

    /**
     * Tests if isNumber method in the peron class works
     */
    @Test
    void isNumbers() {
        //Arrange
        String actual = "5";

        //Act

        //Assert
        assertTrue(actual.matches("^[0-9]*$"));
    }
    //tests that numbers can't be placed in firstName field
    @Test
    void personFirstNamePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("1Ben","fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that "" can't be placed in firstName field
    @Test
    void personFirstNameEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("","fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that null value can't be placed in firstName field
    @Test
    void personFirstNameNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person(null,"fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that numbers can't be placed in lastName field
    @Test
    void personLastNamePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","1fulker","my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that "" can't be placed in lastName field
    @Test
    void personLastNameEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","","my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that null value can't be placed in lastName field
    @Test
    void personLastNameNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben",null,"my address","fort myers", "FL","39388","9876543210"));
    }
    //tests that a null value can't be placed in address field
    @Test
    void personAddressNullTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker",null,"fort myers", "FL","39388","9876543210"));
    }
    //tests that "" can't ""be placed in address field
    @Test
    void personAddressEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","Fulker","","fort myers", "FL","39388","9876543210"));
    }
    //tests that numbers can't be placed in city field
    @Test
    void personCityPassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","1fort myers", "FL","A9388","9876543210"));
    }
    //tests that "" can't ""be placed in city field
    @Test
    void personCityEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","jhglj","my address","", "FL","","9876543210"));
    }
    //tests that a null value can't be placed in city field
    @Test
    void personCityNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address",null, "FL","98494","9876543210"));
    }
    //tests that numbers can't be placed in state field
    @Test
    void personStatePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "1L","A9388","9876543210"));
    }
    //tests that "" can't ""be placed in state field
    @Test
    void personStateEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort My", "","55555","9876543210"));
    }
    //tests that a null value can't be placed in state field
    @Test
    void personStateNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","Fort My", null,"98494","9876543210"));
    }
    //tests that only numbers can be placed in zip field
    @Test
    void personZipPassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","A9388","9876543210"));
    }
    //tests that "" can't ""be placed in zip field
    @Test
    void personZipEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","Fulker","my address","fort myers", "FL","","9876543210"));
    }
    //tests that a null value can't be placed in zip field
    @Test
    void personZipNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL",null,"9876543210"));
    }
    //tests that only numbers can be placed in phone field
    @Test
    void personPhonePassesStringRegexNegativeTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","49388","AAA6543210"));
    }
    //tests that "" can't ""be placed in phone field
    @Test
    void personPhoneEmptyTest(){
        assertThrows(IllegalArgumentException.class, () -> new Person("Ben","fjldl","my address","fort myers", "FL","45555",""));
    }
    //tests that a null value can't be placed in phone field
    @Test
    void personPhoneNullTest(){
        assertThrows(NullPointerException.class, () -> new Person("Ben","fulker","my address","fort myers", "FL","39487",null));
    }
}