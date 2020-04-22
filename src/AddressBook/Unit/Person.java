package AddressBook.Unit;

import java.util.regex.Pattern;

public class Person {

    public static final String[] fields = {"Last Name", "First Name", "Address", "City", "State", "ZIP", "Phone",};

    private String firstName, lastName, address, city, state, zip, phone;

    public boolean isLetters(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public boolean isNumbers(String name) {
        return name.matches("^[0-9]*$");
    }

    public Person(String firstName, String lastName, String address, String city, String state, String zip, String phone)throws IllegalArgumentException {


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

            if (zip.equals(null)) {
                throw new NullPointerException("Zip can't be null");
            }
            if(zip.isEmpty()) {
                throw new IllegalArgumentException("Zip cannot be empty");
            }

            if (phone.equals(null)) {
                throw new NullPointerException("phone can't be null");
            }
            if(phone.isEmpty()) {
                throw new IllegalArgumentException("Phone cannot be empty");
            }

            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.phone = phone;
    }
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
    @Override public String toString() { return lastName + ", " + firstName; }
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