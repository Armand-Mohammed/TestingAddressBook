package AddressBook.Integration;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.security.NoExitSecurityManagerInstaller;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static java.awt.event.KeyEvent.*;

class PersonDialogTest {

    @Test
    void addPerson() {


        // Locks Program stops program exiting
        NoExitSecurityManagerInstaller.installNoExitSecurityManager();

        // Required for AssertJ GUI testing
        FailOnThreadViolationRepaintManager.install();

        // Initialize ourFrame DialogFixture
        PersonDialog frame = GuiActionRunner.execute(() -> new PersonDialog(new JFrame()));
        DialogFixture ourFrame = new DialogFixture(frame);
        ourFrame.show();


        //Robot type firstName Ben in text box
        ourFrame.textBox("firstName")
                .pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_E, VK_N);

        //Robot type lastName Bucker in text box
        ourFrame.textBox("lastName").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_B).releaseKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_U, VK_C,VK_K, VK_E, VK_R);

        //Robot type address 4444 Down Street in text box
        ourFrame.textBox("address").pressAndReleaseKeys(VK_4, VK_4, VK_4, VK_4, VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_D).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_O, VK_W, VK_N)
                .pressAndReleaseKeys(VK_SPACE).pressKey(VK_SHIFT)
                .pressAndReleaseKeys(VK_S).releaseKey(VK_SHIFT).pressAndReleaseKeys(VK_T, VK_R, VK_E, VK_E, VK_T);

        //Robot type city "my city" in text box
        ourFrame.textBox("city").pressAndReleaseKeys(VK_M, VK_Y,VK_SPACE, VK_C, VK_I, VK_T, VK_Y);

        //Robot type state "FL" text box
        ourFrame.textBox("state").pressKey(VK_SHIFT).pressAndReleaseKeys(VK_F, VK_L).releaseKey(VK_SHIFT);

        //Robot type zip "33333" in text box
        ourFrame.textBox("zip").pressAndReleaseKeys(VK_3, VK_3, VK_3, VK_3, VK_3);

        //Robot type phone "0987654321" in text box
        ourFrame.textBox("phone").pressAndReleaseKeys(VK_0, VK_9, VK_8, VK_7, VK_6, VK_5, VK_4, VK_3, VK_2, VK_1);

        //Robot Clicks 'OK'
        ourFrame.button(JButtonMatcher.withText("OK")).click();

        // Closes ourFrame GUI
        ourFrame.cleanUp();

        // Uninstall NoExistSecurityManager to enable program to close after test runs
        NoExitSecurityManagerInstaller.installNoExitSecurityManager().uninstall();
    }

}
