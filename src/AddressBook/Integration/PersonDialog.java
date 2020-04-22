package AddressBook.Integration;

import AddressBook.Unit.Person;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by
 * Originally by Ingrid Buckley Jan 2020
 * Edited by Ben Fulker and Armand Mohammed
 * Edited 4/22/20 by Paul Nicowski
 */
public class PersonDialog extends JDialog {
    //Options for person dialog frame
    public enum Result {
        OK,
        CANCEL,
    }

    //Properties for Person Dialog
    private Result result;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField address;
    private JTextField city;
    private JTextField state;
    private JTextField zip;
    private JTextField phone;
    //Used to verify that zip and phone are actually numbers
    private boolean isNumbers(String name) {
        return name.matches("^[0-9]*$");
    }

    //Constructor for PersonDialog Class creates all components of the PersonDialog dialog/frame for entering/editing
    //people in the addressBook
    public PersonDialog(Frame parent) {

        super(parent);

        JLabel l;
        AtomicReference<JPanel> p = new AtomicReference<>(new JPanel(new SpringLayout()));
        //creates firstName components of PersonDialog frame
        l = new JLabel("First name:", JLabel.TRAILING);
        p.get().add(l);
        firstName = new JTextField(20);
        l.setLabelFor(firstName);
        firstName.setName("firstName");
        p.get().add(firstName);

        //creates lastName components of PersonDialog frame
        l = new JLabel("Last name:", JLabel.TRAILING);
        p.get().add(l);
        lastName = new JTextField(20);
        l.setLabelFor(lastName);
        lastName.setName("lastName");
        p.get().add(lastName);

        //creates Address components of PersonDialog frame
        l = new JLabel("Address:", JLabel.TRAILING);
        p.get().add(l);
        address = new JTextField(20);
        l.setLabelFor(address);
        address.setName("address");
        p.get().add(address);

        //creates City components of PersonDialog frame
        l = new JLabel("City:", JLabel.TRAILING);
        p.get().add(l);
        city = new JTextField(20);
        l.setLabelFor(city);
        city.setName("city");
        p.get().add(city);

        //creates State components of PersonDialog frame
        l = new JLabel("State:", JLabel.TRAILING);
        p.get().add(l);
        state = new JTextField(20);
        l.setLabelFor(state);
        state.setName("state");
        p.get().add(state);

        //creates Zip Code components of PersonDialog frame
        l = new JLabel("ZIP code:", JLabel.TRAILING);
        p.get().add(l);
        zip = new JTextField(20);
        l.setLabelFor(zip);
        zip.setName("zip");
        p.get().add(zip);

        //creates Telephone components of PersonDialog frame
        l = new JLabel("Telephone:", JLabel.TRAILING);
        p.get().add(l);
        phone = new JTextField(20);
        l.setLabelFor(phone);
        phone.setName("phone");
        p.get().add(phone);

        //creates Grid
        SpringUtilities.makeCompactGrid(p.get(), 7, 2, 6, 6, 6, 6);

        // Set up the buttons panel
        JPanel buttons = new JPanel();

        //Creates Ok button
        JButton okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(e ->
        {
                result = Result.OK;
                setVisible(false);

        });
        buttons.add(okButton);
        //End OK button component

        //Creates cancel buttton component
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(e ->
        {
            result = Result.CANCEL;
            setVisible(false);
        });
        buttons.add(cancelButton);
        //ends cancel button component

        // Set window properties
        getContentPane().add(p.get(), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.PAGE_END);
        pack();
        //Sets titles of PersonDialog Frame
        setTitle("Person Information");
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setLocation((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
    }

    //Second constructor with Person as well as Frame
    public PersonDialog(Frame parent, Person person) {
        this(parent);
        //Sets text for all textField in personDialog frame
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        address.setText(person.getAddress());
        city.setText(person.getCity());
        state.setText(person.getState());
        zip.setText(person.getZip());
        phone.setText(person.getPhone());
    }

    //Sets the default to cancel when user exits/closes personDialog frame
    public Result showDialog() {
        // Default to CANCEL if the user closes the dialog window
        result = Result.CANCEL;
        setVisible(true);
        return result;
    }

    public Result showEditDialog()
    {
        // Default to CANCEL if the user closes the dialog window
        result = Result.CANCEL;
        firstName.setEditable(false);
        lastName.setEditable(false);
        setVisible(true);
        return result;

    }

    //GetPerson method handles input checking on personDialog frame
    public Person getPerson() {

        //If firstname or lastName is null or "" or if zip or phone are not all numbers
        if (firstName == null || lastName == null || firstName.getText().isEmpty() || lastName.getText().isEmpty() || !isNumbers(zip.getText()) || !isNumbers(phone.getText())) {
            if (!isNumbers(zip.getText())) {  //Check is zip is number and display message dialog to user if not
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Your Zip was invalid accepts numbers only the value will be defaulted to '0'");
                zip.setText("0");
            }
            if (!isNumbers(phone.getText())) { //Check is phone is number and display message dialog to user if not
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Your Phone input was invalid numbers only value will be default to '0'");
                phone.setText("0");
            }
            if (firstName.getText().isBlank()) { //Check is firstName is null or "" and display message dialog to user if not
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "The first name can't be empty or null the value will be defaulted to 'x'");
                firstName.setText("x");
            }
            if (lastName.getText().isBlank()) {  //Check is lastName is null or "" and display message dialog to user if not
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "The last name can't be empty or null the value will be defaulted to 'x'");
                lastName.setText("x");
            }

        }
        //If there are not errors in person dialog then return the person
        return new Person(firstName.getText(),
                lastName.getText(),
                address.getText(),
                city.getText(),
                state.getText(),
                zip.getText(),
                phone.getText());
    }
}