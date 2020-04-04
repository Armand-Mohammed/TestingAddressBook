package GUI;
import static java.awt.event.KeyEvent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.security.NoExitSecurityManagerInstaller;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

class AddressBookGUITest {
    @Rule
    public static TemporaryFolder folder = new TemporaryFolder();
    private static File testFile = null;
    private static FrameFixture window = null;

    @BeforeAll
    public static void init() {
        //Prevent program exiting
        NoExitSecurityManagerInstaller.installNoExitSecurityManager();

        // Required for full AssertJ GUI testing
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public void initEach() throws IOException, ClassNotFoundException {
        // Initialize window
        AddressBookGUI frame = GuiActionRunner.execute(() -> new AddressBookGUI());
        window = new FrameFixture(frame);
        window.show();

        // Create SQL test file
        folder.create();
        testFile = folder.newFile("myTestFile");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + testFile.getAbsoluteFile());
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE persons (firstName TEXT, lastName TEXT, address TEXT, city TEXT, state TEXT, zip 12345, phone 954123456)");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES (\"Amanda\", \"Mohammed\", \"12345 astreet\", \"acity\", \"FL\", \"12345\", \"349456781\")");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES (\"Tony\", \"Stark\", \"123 Stark Street\", \"Manhattan\", \"NY\", \"12345\", \"1234567890\")");
        } catch (SQLException exception) {
            System.out.println("Unable to create test file:\n" + exception);
        }
    }

    @AfterEach
    public void cleanEach() {
        // Close assertJ window gui
        window.cleanUp();
    }

    // Clean up the program after tests have been run
    @AfterAll
    public static void clean() {
        //Re-enable program to close after testing completes
        NoExitSecurityManagerInstaller.installNoExitSecurityManager().uninstall();
    }

    // Begin Tests

    @Test
    public void canCreateNewPerson() {
        // Click and get dialog window
        window.button("add").click();
        DialogFixture dialog = window.dialog();

        // Type 'Armand','Mohammed','1660 SW 48th ave','Plantation','FL','33317', and '9545130066'
        // into the respective boxes
        dialog.textBox("firstName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_A).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_R, VK_M, VK_A, VK_N, VK_D);
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_M).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_O, VK_H, VK_A, VK_M, VK_M, VK_E, VK_D);
        dialog.textBox("address").pressAndReleaseKeys(VK_1, VK_6, VK_6, VK_0, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S, VK_W).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_4, VK_8, VK_T, VK_H).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_A).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_V, VK_E);
        dialog.textBox("city").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_P).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_L, VK_A, VK_N, VK_T, VK_A, VK_T, VK_I, VK_O, VK_N);
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_1, VK_7);
        dialog.textBox("phone").pressAndReleaseKeys(VK_9, VK_5, VK_4, VK_5, VK_1, VK_3, VK_0, VK_0, VK_6, VK_6);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test person is added
        window.table().requireRowCount(1);
    }

    @Test
    public void canEditPerson() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'John Doe' test person entry and click 'Edit'
        window.table().cell("John").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Test person gets fully loaded
        dialog.textBox("firstName").requireText("Amanda");
        dialog.textBox("lastName").requireText("Mohammed");
        dialog.textBox("address").requireText("12345 astreet");
        dialog.textBox("city").requireText("acity");
        dialog.textBox("state").requireText("FL");
        dialog.textBox("zip").requireText("12345");
        dialog.textBox("phone").requireText("349456781");

        // Change John's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test that the table contains the updated data
        window.table().requireContents(
                new String[][] { { "Amanda", "Mohammed", "12345 astreet", "acity", "FL", "12345", "349456781" },
                        { "Tony", "Stark", "123 Stark Street", "Manhattan", "NY", "12345", "1234567890" } });
    }

    @Test
    public void canDeletePerson() {
        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Get the file chooser and select the test file
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check table now has the two persons in file
        window.table().requireRowCount(2);

        // Click on the 'John Doe' entry
        window.table().cell("Amanda").click();

        // Click 'delete'
        window.button("delete").click();

        // Test that only one row remains
        window.table().requireRowCount(1);
    }

    @Test
    public void canCreateNewPersonCancelled() {
        // Click and get dialog window
        window.button("add").click();
        DialogFixture dialog = window.dialog();

        // Type 'John'
        dialog.textBox("firstName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_J).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_O, VK_H, VK_N);

        // Click 'Cancel'
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        // Test person is not added
        window.table().requireRowCount(0);
    }

    @Test
    public void canEditPersonCancelled() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'John Doe' test person entry and click 'Edit'
        window.table().cell("JAmanda").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change John's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        // Test that the table is the same as the test file (Unchanged)
        window.table().requireContents(
                new String[][] { { "Amanda", "Mohammed", "12345 astreet", "acity", "FL", "12345", "349456781" },
                        { "Tony", "Stark", "123 Stark Street", "Manhattan", "NY", "12345", "1234567890" } });
    }

    @Test
    public void canEditPersonNoRowSelected() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        //Click edit button for no window
        window.button("edit").click();

        //Test that the edit button on the main menu
        // is still focused indicating nothing was opened
        window.button("edit").requireFocused();
    }

    @Test
    public void canDeletePersonNoRowSelected() {
        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check table now has the two persons in file
        window.table().requireRowCount(2);

        // Click 'delete'
        window.button("delete").click();

        // Test that only both rows remain
        window.table().requireRowCount(2);
    }

    @Test
    public void canStartNewBook() {
        // Check that new item is clickable
        window.menuItem("new").requireEnabled();

        // Click 'new' item
        window.menuItem("file").click();
        window.menuItem("new").click();

        // Check that save item is now disabled
        window.menuItem("save").requireDisabled();

        // Check saveAs still matches state of save
        saveAndSaveAsMatchEnabledState();
    }

    @Test
    public void canOpenExistingBookBlankFile() throws IOException {
        // Check that open item is clickable
        window.menuItem("open").requireEnabled();

        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Create a blank file
        File testFile = folder.newFile("myOtherTestFile");
        testFile.createNewFile();

        // Get the file chooser and select the file saved
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check error message is displayed
        window.optionPane().requireErrorMessage();

        // Delete file
        testFile.delete();
    }

    @Test
    public void canOpenExistingBook() {
        // Check that open item is clickable
        window.menuItem("open").requireEnabled();

        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Get the file chooser and select the file saved
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check table now has the two persons in file
        window.table().requireRowCount(2);

        // Check that save item is now disabled
        window.menuItem("save").requireDisabled();

        // Check saveAs still matches state of save
        saveAndSaveAsMatchEnabledState();
    }

    @Test
    public void canOpenExistingBookCancelled() {
        // Check that open item is clickable
        window.menuItem("open").requireEnabled();

        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Get the file chooser and select the file saved
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().cancel();

        // Check table now has the two persons in file
        window.table().requireRowCount(0);
    }

    @Test
    public void canSaveNewBookOverAnother() {
        //Add a person to a new book
        window.button("add").click();
        DialogFixture dialog = window.dialog();

        // Type 'John','Doe','1234 SomeStreet','SomeCity','FL','12345', and '1234567890'
        // into the respective boxes
        dialog.textBox("firstName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_J).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_O, VK_H, VK_N);
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_O, VK_E);
        dialog.textBox("address").pressAndReleaseKeys(VK_1, VK_2, VK_3, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_M, VK_E).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);
        dialog.textBox("city").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_O, VK_M, VK_E).pressKey(VK_SHIFT).pressAndReleaseKeys(VK_C).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_I, VK_T, VK_Y);
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);
        dialog.textBox("zip").pressAndReleaseKeys(VK_1, VK_2, VK_3, VK_4, VK_5);
        dialog.textBox("phone").pressAndReleaseKeys(VK_1, VK_2, VK_3, VK_4, VK_5, VK_6, VK_7, VK_8, VK_9, VK_0);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        //Make sure save and saveAs are enabled
        window.menuItem("save").requireEnabled();
        window.menuItem("saveAs").requireEnabled();

        //Click 'save'
        window.menuItem("file").click();
        window.menuItem("save").click();

        //Save over test file
        window.fileChooser().selectFile(testFile);
        window.fileChooser().approve();

        //Check that question message is shown for overwriting a book
        window.optionPane().requireQuestionMessage();
    }

    @Test
    public void canSaveEditedBook() throws IOException {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'John Doe' test person entry and click 'Edit'
        window.table().cell("John").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Edit the person
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Check save button is active
        window.menuItem("save").requireEnabled();
        window.menuItem("saveAs").requireEnabled();

        // Click 'save' and save to file
        window.menuItem("file").click();
        window.menuItem("saveAs").click();
        window.fileChooser().setCurrentDirectory(folder.getRoot()).fileNameTextBox().pressAndReleaseKeys(VK_T, VK_E,
                VK_S, VK_T, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
        window.fileChooser().approve();

        // Test file exists
        File file = new File(folder.getRoot() + "/test file");
        assertTrue(file.exists());
    }

    @Test
    public void canSaveEditedBookCancelled() throws IOException {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Amanda Mohammed test person entry and click 'Edit'
        window.table().cell("Amanda").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Edit the person
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'save' and cancel
        window.menuItem("file").click();
        window.menuItem("saveAs").click();
        window.fileChooser().setCurrentDirectory(folder.getRoot()).fileNameTextBox().pressAndReleaseKeys(VK_T, VK_E,
                VK_S, VK_T, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
        window.fileChooser().cancel();

        // Test file does not exists
        File file = new File(folder.getRoot() + "/test file");
        assertFalse(file.exists());
    }

    @Test
    public void canPrintBook() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click print
        window.menuItem("file").click();
        window.menuItem("print").click();

        // Make sure that the print dialog is visible
        window.dialog().requireVisible();
    }

    @Test
    public void confirmDialogShowsOnNew() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'John Doe' test person entry and click 'Edit'
        window.table().cell("John").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change John's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'New'
        window.menuItem("file").click();
        window.menuItem("new").click();

        // Test that a question message is shown
        window.optionPane().requireQuestionMessage();
    }

    @Test
    public void confirmDialogShowsOnOpen() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Amanda' test person entry and click 'Edit'
        window.table().cell("Amanda").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Amanda's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'Open' and load test file again
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Test that a question message is shown
        window.optionPane().requireQuestionMessage();
    }

    @Test
    public void confirmDialogShowsOnQuitConfirm() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Amanda Mohammed' test person entry and click 'Edit'
        window.table().cell("Amanda").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change John's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'quit'
        window.menuItem("file").click();
        window.menuItem("quit").click();

        // Test that a question message is shown
        window.optionPane().requireQuestionMessage();

        //Test closing works
        window.optionPane().buttonWithText("Yes").click();
    }

    @Test
    public void confirmDialogShowsOnWindowCloseCancel() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Amanda Mohammed' test person entry and click 'Edit'
        window.table().cell("Amanda").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Amanda's zip to '54321'
        dialog.textBox("zip").click().deleteText().pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Close window
        window.close();

        // Test that a question message is shown
        window.optionPane().requireQuestionMessage();

        //Test cancelling works
        window.optionPane().buttonWithText("No").click();
    }

    @Test
    public void canSearchPeople() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(testFile.getAbsoluteFile());
        window.fileChooser().approve();

        //Type 'jan'
        window.textBox().pressAndReleaseKeys(VK_T,VK_O,VK_N, VK_Y);

        //Check only 'Jane' shows
        window.table().requireRowCount(1);

        //Type 'AM'
        window.textBox().deleteText().pressAndReleaseKeys(VK_A,VK_M);

        //Check only 'Amanda' entry shows
        window.table().requireRowCount(1);
    }

    @Test
    public void saveIsDisabledByDefault() {
        // Check if saving is disabled
        window.menuItem("save").requireDisabled();

        // Check save and saveAs states match
        saveAndSaveAsMatchEnabledState();
    }

    @Test
    public void programLaunchesCorrectly() throws ClassNotFoundException {
        //Get robot
        Robot robot = window.robot();

        //Clear the started program
        window.cleanUp();

        //Start the application
        AddressBookGUI.main(null);

        //Find the generated window and ensure it is showing. If one is not found
        //this test fails.
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            protected boolean isMatching(JFrame frame) {
                return "Address Book".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot);
    }

    @Test
    public void saveAndSaveAsMatchEnabledState() {
        // Check if save and saveAs match enabled state
        assertEquals(window.menuItem("save").isEnabled(), window.menuItem("saveAs").isEnabled());
    }
}