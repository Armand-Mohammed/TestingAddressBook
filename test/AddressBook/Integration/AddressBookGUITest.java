package AddressBook.Integration;


import AddressBook.Unit.AddressBook;
import AddressBook.Unit.AddressBookController;
import AddressBook.Unit.Person;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.exception.EdtViolationException;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.security.NoExitSecurityManagerInstaller;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.awt.event.KeyEvent.*;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: by Ben Fulker and Armand Mohammed
 * Class has dependencies on AddressBook.java, AddressBookController and Person.jav
 */
//This will only work on Windows PC and not on MAC OSX
public class AddressBookGUITest {

    /**
     * Creates rules for each test
     */
    @Rule
    public static TemporaryFolder tempFolder = new TemporaryFolder();
    private static File fakeFile = null;
    private static FrameFixture ourFrame = null;

    /**
     * does before all test
     */
    @BeforeAll
    public static void init() {
        //Prevent program exiting
        NoExitSecurityManagerInstaller.installNoExitSecurityManager();

        // Required for full AssertJ GUI testing
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     *  does before each test
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @BeforeEach
    public void initEach() throws IOException, ClassNotFoundException {
        // Initialize window
        //creates simulated GUI with the GuiActionRunner
        AddressBookGUI frame = GuiActionRunner.execute(() -> new AddressBookGUI());
        //Creates FrameFixture
        ourFrame = new FrameFixture(frame);
        //displays GUI
        ourFrame.show();

        // Create SQL test file
        tempFolder.create();

        //creates fake file
        fakeFile = tempFolder.newFile("myFakeFile");

        //Try to create an temp sql db for an AddressBook with multiple people
        try (
                Connection connection = DriverManager.getConnection(
                "jdbc:sqlite:" + fakeFile.getAbsoluteFile());
                Statement statement = connection.createStatement()
        ) {
            statement.execute(
                    "CREATE TABLE persons " +
                            "(firstName TEXT, lastName TEXT, address TEXT, city TEXT, state TEXT, zip TEXT, phone TEXT)");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES " +
                            "('Ben', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES " +
                            "('Bonnie', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES " +
                            "('Paul', 'Bucker', '1111 Up Street', 'his city', 'FL', '22222', '1234567890')");

        } catch (SQLException exception) {
            System.out.println("Test file not created:\n" + exception);
        }
    }

    // Close assertJ ourFrame gui after each test
    @AfterEach
    public void cleanEach() {
        ourFrame.cleanUp();
    }

    /**
     * uninstallls security manager after all tests complete
     */
    @AfterAll
    public static void clean() {
        //Re-enable program to close after testing completes
        NoExitSecurityManagerInstaller.installNoExitSecurityManager().uninstall();
    }

    //tests that the state of Save and SaveAs are the same
    @Test
    public void checkStateOfSaveAndSaveAs() {
        //Act
        boolean save = ourFrame.menuItem("save").isEnabled();
        boolean saveAs = ourFrame.menuItem("saveAs").isEnabled();

        //Assert enabled state is the same
        assertEquals(save, saveAs);
    }

    /**
     * Test that a new person can be created via the AddressBookGUI.java
     */
    @Test
    public void createsNewPerson() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);

        //Robot type lastName Bucker in text box
         dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        ourFrame.table().requireRowCount(1);
    }

    /**
     * Test that a new person can't be created when Zip has non numbers in it
     */
    @Test
    public void tryToCreateNewPersonWithBadZip() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);

        //Robot type lastName Bucker in text box
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_W, VK_W);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        ourFrame.table().requireRowCount(0);
    }

    /**
     * Test that a new person can't be created when Phone has non numbers in it
     */
    @Test
    public void tryToCreateNewPersonWithBadPhoneNumber() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);

        //Robot type lastName Bucker in text box
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_H);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        ourFrame.table().requireRowCount(0);

       //  dialog.button(JButtonMatcher.withText("OK")).click();
    }

    /**
     * Test that a new person can't be created when firstName is "" in it
     */
    @Test
    public void tryToCreateNewPersonWithBadFirstName() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressAndReleaseKeys(VK_SPACE).pressAndReleaseKeys(VK_BACK_SPACE);

        //Robot type lastName Bucker in text box
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        ourFrame.table().requireRowCount(0);
       // dialog.button(JButtonMatcher.withText("OK")).click();
    }

    /**
     * Test that a new person can't be created when lastName is "" in it
     */
    @Test
    public void tryToCreateNewPersonWithBadLastName() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);


        //Robot type lastName blank in text box
        dialog.textBox("lastName").pressAndReleaseKeys(VK_SPACE).pressAndReleaseKeys(VK_BACK_SPACE);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        ourFrame.table().requireRowCount(0);
       // dialog.button(JButtonMatcher.withText("OK")).click();
    }

    /**
     * Test that an esisting person can be edited
     */
    @Test
    public void editsPerson() {
        //open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        //Load firstName "Ben" into textbox
        dialog.textBox("firstName").requireText("Ben");

        //Load lastName "Bucker" into textbox
        dialog.textBox("lastName").requireText("Bucker");

        //Load address "4444 Down Street" into textbox
        dialog.textBox("address").requireText("4444 Down Street");

        //Load city "my city" into textbox
        dialog.textBox("city").requireText("my city");

        //Load state "FL" into textbox
        dialog.textBox("state").requireText("FL");

        //Load zip "33333" into textbox
        dialog.textBox("zip").requireText("33333");

        //Load phone "0987654321" into textbox
        dialog.textBox("phone").requireText("0987654321");

        // Robot Edits Ben Bukers zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test that the table contains the updated data
        //"('Ben', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')
        ourFrame.table().requireContents(
                new String[][] { { "Bucker", "Ben", "4444 Down Street", "my city", "FL", "33333", "5432106789" },
                        { "Bucker", "Bonnie", "4444 Down Street", "my city", "FL", "33333", "0987654321" },
                        { "Bucker", "Paul", "1111 Up Street", "his city", "FL", "22222", "1234567890" }
                });
    }

    /**
     * Test that an exsisting person can be deleted
     */
    @Test
    public void canDeletePerson() {
        // select file from menu
        ourFrame.menuItem("file").click();

        //select open from menu
        ourFrame.menuItem("open").click();

        // Get the file chooser and select the test file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Check table now has the two persons in file
        ourFrame.table().requireRowCount(3);

        // Click on the 'Ben Bucker' entry
        ourFrame.table().cell("Ben").click();

        // Click 'delete'
        ourFrame.button("delete").click();

        // Test that only one row remains
        ourFrame.table().requireRowCount(2);
    }

    /**
     * Test that user can click cancel buttong when trying to create a new person
     */
    @Test
    public void canCreateNewPersonCancelled() {
        // Click and get dialog window
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        // Robot types 'Ben'
        dialog.textBox("firstName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_E,VK_N);

        // Click 'Cancel'
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        // Test person is not added
        ourFrame.table().requireRowCount(0);
    }

    /**
     * Test that user can click cancel buttong when trying to edit an existing person
     */
    @Test
    public void canEditPersonCancelled() {
        // open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Switch zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        // Test that the table is the same as the test file (Unchanged)
        ourFrame.table().requireContents(
                new String[][] { { "Bucker", "Ben", "4444 Down Street", "my city", "FL", "33333", "0987654321" },
                        { "Bucker", "Bonnie", "4444 Down Street", "my city", "FL", "33333", "0987654321" },
                        { "Bucker", "Paul", "1111 Up Street", "his city", "FL", "22222", "1234567890" }
                });
    }
    //"('Paul', 'Bucker', '1111 Up Street', 'his city', 'FL', '22222', '1234567890')"

    /**
     * Test that user can click edit button when no row is selected and nothing happens
     */
    @Test
    public void canEditPersonNoRowSelected() {
        // open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        //Click edit button for no window
        ourFrame.button("edit").click();

        //Test that the edit button on the main menu
        // is still focused indicating nothing was opened
        ourFrame.button("edit").requireFocused();
    }

    /**
     * Test that user can click delete button when no row is selected and nothing happens
     */
    @Test
    public void canDeletePersonNoRowSelected() {
        // Click 'open' item
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Check table now has the two persons in file
        ourFrame.table().requireRowCount(3);

        // Click 'delete'
        ourFrame.button("delete").click();

        // Test that only both rows remain
        ourFrame.table().requireRowCount(3);
    }

    /**
     * Test that user can create a new address book
     */
    @Test
    public void canStartNewAddressBook() {
        // Check that new item is clickable
        ourFrame.menuItem("new").requireEnabled();

        // Click 'new' item
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("new").click();

        // Check that save item is now disabled
        ourFrame.menuItem("save").requireDisabled();

        // Check saveAs still matches state of save
        checkStateOfSaveAndSaveAs();
    }

    @Test
    public void canOpenExistingAddressBookBlankFile() throws IOException {
        // Check that open item is clickable
        ourFrame.menuItem("open").requireEnabled();

        // Click 'open' item
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("open").click();

        // Create a blank file
        File fakeFile = tempFolder.newFile("myOtherfakeFile");
        fakeFile. createNewFile();

        // Get the file chooser and select the file saved
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        ourFrame.fileChooser().approve();

        // Check error message is displayed
        ourFrame.optionPane().requireErrorMessage();

        // Delete file
        fakeFile.delete();
    }

    /**
     * Tests that user can open an existing addressbook
     */
    @Test
    public void canOpenExistingAddressBook() {
        // Check that open item is clickable
        ourFrame.menuItem("open").requireEnabled();

        // Click 'open' item
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("open").click();

        // Get the file chooser and select the file saved
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Check table now has the three persons in file
        ourFrame.table().requireRowCount(3);

        // Check that save item is now disabled
        ourFrame.menuItem("save").requireDisabled();

        // Check saveAs still matches state of save
        checkStateOfSaveAndSaveAs();
    }

    /**
     * test that user can click cancel when opening an existing address book
     */
    @Test
    public void canOpenExistingAddressBookCancelled() {
        // Check that open item is clickable
        ourFrame.menuItem("open").requireEnabled();

        // Click 'open' item
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("open").click();

        // Get the file chooser and select the file saved
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        ourFrame.fileChooser().cancel();

        // Check table now has the two persons in file
        ourFrame.table().requireRowCount(0);
    }

    /**
     * Test user can save current address book over an existing address book
     */
    @Test
    public void canSaveNewAddressBookOverAnother() {
        //Add a person to a new book
        ourFrame.button("add").click();
        DialogFixture dialog = ourFrame.dialog();

        //Robot type firstName Ben in text box
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);

        //Robot type lastName Bucker in text box
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        //Make sure save and saveAs are enabled
        ourFrame.menuItem("save").requireEnabled();
        ourFrame.menuItem("saveAs").requireEnabled();

        //Click 'save'
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("save").click();

        //Save over test file
        ourFrame.fileChooser().selectFile(fakeFile);
        ourFrame.fileChooser().approve();

        //Check that question message is shown for overwriting a book
        ourFrame.optionPane().requireQuestionMessage();
    }

    /**
     * Test that a user can save the changes made to an existing address book
     * @throws IOException
     */
    @Test
    public void saveAddressBookAfterEdit() throws IOException {
        // open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Edit the phone field
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);

        dialog.button(JButtonMatcher.withText("OK")).click();

        // Check save button is active
        ourFrame.menuItem("save").requireEnabled();
        ourFrame.menuItem("saveAs").requireEnabled();

        // Click 'save' and save to file
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("saveAs").click();
        ourFrame.fileChooser().setCurrentDirectory(tempFolder.getRoot()).fileNameTextBox()
                .pressAndReleaseKeys(VK_F, VK_A, VK_K, VK_E, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
        ourFrame.fileChooser().approve();

        // Test file exists
        File file = new File(tempFolder.getRoot() + "/fake file");
        assertTrue(file.exists());
    }

    /**
     * Test that user can cancel save after an edit
     * @throws IOException
     */
    @Test
    public void canSaveEditedAddressBookCancelled() throws IOException {
        // open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        ourFrame.fileChooser().approve();

        // Click 'Ben' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Edit the phone for person Ben
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8);

        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'save' and cancel
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("saveAs").click();
        ourFrame.fileChooser().setCurrentDirectory(tempFolder.getRoot()).fileNameTextBox()
                .pressAndReleaseKeys(VK_F, VK_A, VK_K, VK_E, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
        ourFrame.fileChooser().cancel();

        // Test file Buckers not exists
        File file = new File(tempFolder.getRoot() + "/fake file");
        assertFalse(file.exists());
    }

    /**
     * test user can print the address book
     */
    @Test
    public void printsTheAddressBook() {
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Click print
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("print").click();

        // Make sure that the print dialog is visible
        ourFrame.dialog().requireVisible();
    }

    /**
     * test that and print exception can be thrown
     */
    @Test
    public void printException(){
        try {
            AddressBookGUI gui = new AddressBookGUI();
            AddressBook addressBook = new AddressBook();
            AddressBookController addressBookController = new AddressBookController(addressBook);
            Person instance = new Person("Armand", "Mohammed", "1660 sw 48th ave", "Fort Myers", "FL", "33317", "9545130066");
            addressBook.add(instance);
            JTable nameList = new JTable(addressBook);

            //open menu and select file
            ourFrame.menuItem("file").click();

            //menu select open
            ourFrame.menuItem("open").click();

            //opens fake file
            ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

            //approve fakeFile
            ourFrame.fileChooser().approve();

            // Click print
            ourFrame.menuItem("file").click();
            ourFrame.menuItem("print").click();

            // Make sure that the print dialog is visible
            ourFrame.dialog().requireVisible();

            //assertThrows(java.awt.print.PrinterException.class, () -> nameList.print());
            Exception ex = assertThrows(java.awt.print.PrinterException.class, () -> nameList.print());

           /* try {
                nameList.print();
            } catch (Exception ex) {
                assertTrue(ex instanceof java.awt.print.PrinterException);
            }
            */
            fail("Expected exception not thrown");


            // Make sure that the print dialog is visible


        }catch(EdtViolationException e){
            System.out.println("Exception caught");
        }
    }

    /**
     * Test that person dialong is confirmed
     */
    @Test
    public void confirmDialogOnNew() {
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fakeFile
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Change Ben's zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Open menu select file
        ourFrame.menuItem("file").click();

        // Open menu select New
        ourFrame.menuItem("new").click();

        // Test that a question message is shown
        ourFrame.optionPane().requireQuestionMessage();
    }

    /**
     * tests person dialog show on open
     */
    @Test
    public void confirmDialogShowsOnOpen() {
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Change Ben's zip to '54321'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'Open' and load test file again
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("open").click();

        // Test that a question message is shown
        ourFrame.optionPane().requireQuestionMessage();
    }

    /**
     * test confirm dialog show on quit
     */
    @Test
    public void confirmDialogShowsOnQuitConfirm() {
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        ourFrame.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Change Ben's zip to '54321'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'quit'
        ourFrame.menuItem("file").click();
        ourFrame.menuItem("quit").click();

        // Test that a question message is shown
        ourFrame.optionPane().requireQuestionMessage();

        //Test closing works
        ourFrame.optionPane().buttonWithText("Yes").click();
    }

    /**
     * tests person dialog show when canceled or window is closed
     */
    @Test
    public void confirmDialogShowsOnWindowCloseCancel() {
        // open with sample address Book
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fake file
        ourFrame.fileChooser().approve();

        // Selects 'Ben Bucker' test person entry and click 'Edit'
        ourFrame.table().cell("Ben").click();
        ourFrame.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = ourFrame.dialog();

        // Change Ben's phone number
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Close window
        ourFrame.close();

        // Test that a question message is shown
        ourFrame.optionPane().requireQuestionMessage();

        //Test cancelling works
        ourFrame.optionPane().buttonWithText("No").click();
    }

    /**
     * Tests that user can search for a person in search box
     */
    @Test
    public void searchForPerson() {
        //open menu and select file
        ourFrame.menuItem("file").click();

        //menu select open
        ourFrame.menuItem("open").click();

        //opens fake file
        ourFrame.fileChooser().selectFile(fakeFile.getAbsoluteFile());

        //approve fake file
        ourFrame.fileChooser().approve();

        //Type 'jan'
        ourFrame.textBox().pressAndReleaseKeys(VK_B,VK_O,VK_N);

        //Check only 'Bonnie' shows
        ourFrame.table().requireRowCount(1);

        //Type 'jo'
        ourFrame.textBox().deleteText().pressAndReleaseKeys(VK_B,VK_E);

        //Check only 'Ben' entry shows
        ourFrame.table().requireRowCount(1);

        //Type '44'
        ourFrame.textBox().deleteText().pressAndReleaseKeys(VK_4,VK_4);

        //Check both entries show
        ourFrame.table().requireRowCount(2);
    }

    /**
     * Test that the save function is diabled when no change or no entries in current address book
     */
    @Test
    public void saveIsDisabledOnStartUp() {
        // Check if saving is disabled
        ourFrame.menuItem("save").requireDisabled();

        // Check save and saveAs states match
        checkStateOfSaveAndSaveAs();
    }

    /**
     * Test that the program loads
     * @throws ClassNotFoundException
     */
    @Test
    public void programLoads() throws ClassNotFoundException {
        //Get robot
        Robot robot = ourFrame.robot();

        //Clear the started ourFrame
        ourFrame.cleanUp();

        //Start the AddressBookGUI application
        AddressBookGUI.main(null);

        //Generate and find ourFrame if not found test will fail
        ourFrame = findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            protected boolean isMatching(JFrame frame) {
                return "Address Book".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot);
    }
}
