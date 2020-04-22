package AddressBook.Unit;

import java.util.regex.Pattern;

/**
 * Created by
 * Orignainlly by Ingrid Buckley Jan 2020
 * Edited by Ben Fulker and Armand Mohammed
 */
public class Person {
    //fields is the name of each column in addressBook GUI and addressbook itself
    public static final String[] fields = {"Last Name", "First Name", "Address", "City", "State", "ZIP", "Phone",};

    private String firstName, lastName, address, city, state, zip, phone;

    //used to value of string to match to Alpha characters only
    public boolean isLetters(String name) {
        return name.matches("[a-zA-Z]+");
    }

    //used to ensure string input to match all digits to 0 through 9
    public boolean isNumbers(String name) {
        return name.matches("^[0-9]*$");
    }

    /**
     * Person Contructor
     * @param firstName
     * @param lastName
     * @param address
     * @param city
     * @param state
     * @param zip
     * @param phone
     * @throws IllegalArgumentException
     */
    public Person(String firstName, String lastName, String address, String city, String state, String zip, String phone)throws IllegalArgumentException {

            //input handling for each parameter throws and exception if invalid value makes it to this point
            if (firstName == null || firstName.isEmpty())
                throw new IllegalArgumentException("First name cannot be empty");

            if (!isLetters(firstName))
                throw new IllegalArgumentException("Please, only use letters");

            if (lastName == null || lastName.isEmpty())
                throw new IllegalArgumentException("Last name cannot be empty");

            if (!isLetters(lastName))
                throw new IllegalArgumentException("Please, only use letters");

            if (!isNumbers(zip)) {
                //JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "zip must be numbers");

                throw new IllegalArgumentException("Please, only use numbers");
            }

            if (!isNumbers(phone))
                throw new IllegalArgumentException("Please, only use numbers");

            if (address == null || address.isEmpty())
                throw new IllegalArgumentException("Address cannot be empty");

            if (city == null) {
                throw new NullPointerException("City can't be null");
            }
            if(city.isEmpty()) {
                throw new IllegalArgumentException("City cannot be empty");
            }

            if (state == null) {
                throw new NullPointerException("State can't be null") ;
            }
            if(state.isEmpty()) {
                throw new IllegalArgumentException("State cannot be empty");
            }

            if(zip.isEmpty()) {
                throw new IllegalArgumentException("Zip cannot be empty");
            }

            if(phone.isEmpty()) {
                throw new IllegalArgumentException("Phone cannot be empty");
            }
            //end of input handling

            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.phone = phone;
    }
    //Getters for each column in each person row of an address
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getAddress() {
        return address;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZip() {
        return zip;
    }
    public String getPhone() {
        return phone;
    }

    /**
     * Overrides the toString() method to return a full name
     * @return a full name of a person
     */
    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }

    /**
     * Finds pattern based on the field value using the field name
     * @param findMe
     * @return
     */
    public boolean containsString(String findMe) {
        Pattern p = Pattern.compile(Pattern.quote(findMe), Pattern.CASE_INSENSITIVE);
        return p.matcher(firstName).find()
                || p.matcher(lastName).find()
                || p.matcher(address).find()
                || p.matcher(city).find()
                || p.matcher(state).find()
                || p.matcher(zip).find()
                || p.matcher(phone).find();
    }

    /**
     * Gets feild using a case statement where the switch is based on column number
     * @param field
     * @return retruns field based on column number
     */
    public String getField(int field) {
        switch (field) {
            case 0:
                return lastName;
            case 1:
                return firstName;
            case 2:
                return address;
            case 3:
                return city;
            case 4:
                return state;
            case 5:
                return zip;
            case 6:
                return phone;
            default:
                throw new IllegalArgumentException("Field number out of bounds");
        } }}
