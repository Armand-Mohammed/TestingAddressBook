package AddressBook;


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
import static org.junit.jupiter.api.Assertions.*;

//NOTE: Due to a bug(s) in AssertJ/JDK, this entire class's
//tests fail on MacOS systems. This is partly because of the way
//the system simulates clicks on the GUI. It requires special
//security permissions to access the mouse and keyboard.
//SEE: https://github.com/joel-costigliola/assertj-swing/issues/25
public class AddressBookGUITest {

    @Rule
    public static TemporaryFolder folder = new TemporaryFolder();
    private static File fakeFile = null;
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
        fakeFile = folder.newFile("myFakeFile");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + fakeFile.getAbsoluteFile());
             Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE persons " +
                            "(firstName TEXT, lastName TEXT, address TEXT, city TEXT, state TEXT, zip TEXT, phone TEXT)");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES " +
                            "('Ben', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')");
            statement.execute(
                    "INSERT INTO persons (firstName, lastName, address, city, state, zip, phone) VALUES " +
                            "('Bonnie', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')");

        } catch (SQLException exception) {
            System.out.println("Unable to create test file:\n" + exception);
        }
    }

    @AfterEach
    public void cleanEach() {
        // Close assertJ window gui
        window.cleanUp();
    }

    @AfterAll
    public static void clean() {
        //Re-enable program to close after testing completes
        NoExitSecurityManagerInstaller.installNoExitSecurityManager().uninstall();
    }

    @Test
    public void canCreateNewPerson() {
        // Click and get dialog window
        window.button("add").click();
        DialogFixture dialog = window.dialog();

        // Type 'Ben','Bucker','4444 Down Street','my city','FL','33333', and '0987654321'
        // into the respective boxes
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_E, VK_N);
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Test person gets fully loaded "('Ben', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987654321')
        dialog.textBox("firstName").requireText("Ben");
        dialog.textBox("lastName").requireText("Bucker");
        dialog.textBox("address").requireText("4444 Down Street");
        dialog.textBox("city").requireText("my city");
        dialog.textBox("state").requireText("FL");
        dialog.textBox("zip").requireText("33333");
        dialog.textBox("phone").requireText("0987654321");

        // Switch Ben Bukers zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        // Test that the table contains the updated data
        //"('Ben', 'Bucker', '4444 Down Street', 'my city', 'FL', '33333', '0987645321')
        window.table().requireContents(
                new String[][] { { "Bucker", "Ben", "4444 Down Street", "my city", "FL", "33333", "5432106789" },
                        { "Bucker", "Bonnie", "4444 Down Street", "my city", "FL", "33333", "0987645321" } });
    }

    @Test
    public void canDeletePerson() {
        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Get the file chooser and select the test file
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check table now has the two persons in file
        window.table().requireRowCount(2);

        // Click on the 'Ben Bucker' entry
        window.table().cell("Ben").click();

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

        // Type 'Ben'
        dialog.textBox("firstName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_E,VK_N);

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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Switch zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


        // Click 'OK'
        dialog.button(JButtonMatcher.withText("Cancel")).click();

        // Test that the table is the same as the test file (Unchanged)
        window.table().requireContents(
                new String[][] { { "Bucker", "Ben", "4444 Down Street", "my city", "FL", "33333", "0987654321" },
                        { "Bucker", "Bonnie", "4444 Down Street", "my city", "FL", "33333", "0987654321" } });
    }

    @Test
    public void canEditPersonNoRowSelected() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
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
        File fakeFile = folder.newFile("myOtherfakeFile");
        fakeFile.createNewFile();

        // Get the file chooser and select the file saved
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Check error message is displayed
        window.optionPane().requireErrorMessage();

        // Delete file
        fakeFile.delete();
    }

    @Test
    public void canOpenExistingBook() {
        // Check that open item is clickable
        window.menuItem("open").requireEnabled();

        // Click 'open' item
        window.menuItem("file").click();
        window.menuItem("open").click();

        // Get the file chooser and select the file saved
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().cancel();

        // Check table now has the two persons in file
        window.table().requireRowCount(0);
    }

    @Test
    public void canSaveNewBookOverAnother() {
        //Add a person to a new book
        window.button("add").click();
        DialogFixture dialog = window.dialog();

        // Type 'Ben','Bucker','4444 Down Street','my city','FL','33333', and '0987654321'
        // into the respective boxes
        dialog.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_E, VK_N);
        dialog.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);
        dialog.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);
        dialog.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);
        dialog.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);
        dialog.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);
        dialog.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        // Click 'OK'
        dialog.button(JButtonMatcher.withText("OK")).click();

        //Make sure save and saveAs are enabled
        window.menuItem("save").requireEnabled();
        window.menuItem("saveAs").requireEnabled();

        //Click 'save'
        window.menuItem("file").click();
        window.menuItem("save").click();

        //Save over test file
        window.fileChooser().selectFile(fakeFile);
        window.fileChooser().approve();

        //Check that question message is shown for overwriting a book
        window.optionPane().requireQuestionMessage();
    }

    @Test
    public void canSaveEditedBook() throws IOException {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Edit the person
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);

        dialog.button(JButtonMatcher.withText("OK")).click();

        // Check save button is active
        window.menuItem("save").requireEnabled();
        window.menuItem("saveAs").requireEnabled();

        // Click 'save' and save to file
        window.menuItem("file").click();
        window.menuItem("saveAs").click();
        window.fileChooser().setCurrentDirectory(folder.getRoot()).fileNameTextBox()
                .pressAndReleaseKeys(VK_F, VK_A, VK_K, VK_E, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Edit the person
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8);

        dialog.button(JButtonMatcher.withText("OK")).click();

        // Click 'save' and cancel
        window.menuItem("file").click();
        window.menuItem("saveAs").click();
        window.fileChooser().setCurrentDirectory(folder.getRoot()).fileNameTextBox()
                .pressAndReleaseKeys(VK_F, VK_A, VK_K, VK_E, VK_SPACE, VK_F, VK_I, VK_L, VK_E);
        window.fileChooser().cancel();

        // Test file Buckers not exists
        File file = new File(folder.getRoot() + "/fake file");
        assertFalse(file.exists());
    }

    @Test
    public void canPrintBook() {
        // Load sample address Book
        window.menuItem("file").click();
        window.menuItem("open").click();
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Ben's zip to '66666'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Ben's zip to '54321'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Ben's zip to '54321'
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);


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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        // Click 'Ben Bucker' test person entry and click 'Edit'
        window.table().cell("Ben").click();
        window.button("edit").click();

        // Get the person dialog
        DialogFixture dialog = window.dialog();

        // Change Ben's phone number
        dialog.textBox("phone").click().deleteText()
                .pressAndReleaseKeys(VK_5, VK_4, VK_3, VK_2, VK_1, VK_0, VK_6, VK_7, VK_8, VK_9);

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
        window.fileChooser().selectFile(fakeFile.getAbsoluteFile());
        window.fileChooser().approve();

        //Type 'jan'
        window.textBox().pressAndReleaseKeys(VK_B,VK_O,VK_N);

        //Check only 'Bonnie' shows
        window.table().requireRowCount(1);

        //Type 'jo'
        window.textBox().deleteText().pressAndReleaseKeys(VK_B,VK_E);

        //Check only 'Ben' entry shows
        window.table().requireRowCount(1);

        //Type '12'
        window.textBox().deleteText().pressAndReleaseKeys(VK_4,VK_4);

        //Check both entries show
        window.table().requireRowCount(2);
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
