package com.example.booktracker;
import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booktracker.ui.EditPageFragment;
import com.example.booktracker.ui.home.HomeFragment;
import com.example.booktracker.ui.home.HomeViewModel;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is used
 * Note: Does not include tests for activities requiring use of the camera, map, or gallery.
 *       Such tests were performed manually.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    @Test
    public void sign_in(){
        //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click SIGN IN Button
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void create_acc(){
         //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Random rand = new Random();
        // Obtain a number between [0 - 200].
        int n = rand.nextInt(200000);
        n += 1;
        Button msButton = (Button) solo.getView(
                R.id.create_acc);
        solo.clickOnView(msButton); //Click new user Button

        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.email), "test" + n);
        solo.enterText((EditText) solo.getView(R.id.password), "8");
        solo.enterText((EditText) solo.getView(R.id.confirmPassword), "8");
        solo.enterText((EditText) solo.getView(R.id.number), "7809823423");
        Button sign_up = (Button) solo.getView(
                R.id.sign_up);
        solo.clickOnView(sign_up); //Click finish changes Button
        solo.assertCurrentActivity("Should be main screen activity", MainScreen.class);

    }
    @Test
    public void create_existing_acc(){
        //Test to make sure account can't be created for existing username

        //Check that current activity is MainActivity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Button msButton = (Button) solo.getView(R.id.create_acc);
        solo.clickOnView(msButton); //Click new user button

        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);
        //Entering in details of an existing account
        solo.enterText((EditText) solo.getView(R.id.email), "test");
        solo.enterText((EditText) solo.getView(R.id.password), "8");
        solo.enterText((EditText) solo.getView(R.id.confirmPassword), "8");
        solo.enterText((EditText) solo.getView(R.id.number), "7809823423");
        Button sign_up = (Button) solo.getView(
                R.id.sign_up);
        solo.clickOnView(sign_up); //Click finish changes Button
        //Sign up should fail and still be in create account page
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);
    }
    @Test
    public void create_account_exception(){
        //Test to make sure fields must be properly filled in
        Random rand = new Random();
        int n = rand.nextInt(200000);
        n += 1;
        //Check that current activity is MainActivity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Button msButton = (Button) solo.getView(R.id.create_acc);
        solo.clickOnView(msButton); //Click new user button

        //Check if we're indeed in the create account page
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish changes with no fields filled in
        Button sign_up = (Button) solo.getView(
                R.id.sign_up);
        solo.clickOnView(sign_up);
        //Should have failed and stayed in same activity
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish changes with only username filled in
        solo.enterText((EditText) solo.getView(R.id.email), "test"+n);
        solo.clickOnView(sign_up);
        //Should have failed and stayed in same activity
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish without phone number
        solo.enterText((EditText) solo.getView(R.id.password), "8");
        solo.enterText((EditText) solo.getView(R.id.confirmPassword), "8");
        solo.clickOnView(sign_up);
        //Should have failed and stayed in same activity
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish with phone number shorter than 7 letters
        solo.enterText((EditText) solo.getView(R.id.number), "1");
        solo.clickOnView(sign_up);
        //Should have failed and stayed in same activity
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish with non matching passwords
        solo.enterText((EditText) solo.getView(R.id.number), "175435264");
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.password), "7");
        solo.clickOnView(sign_up);
        //Should have failed and stayed in same activity
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);

        //Click finish with all fields properly filled
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.password), "8");
        solo.clickOnView(sign_up);
        //Should succeed and be in main screen
        solo.assertCurrentActivity("Should be main screen activity", MainScreen.class);

    }

    @Test
    public void search_user_test(){
        //Test search for existing user

        //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button

        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_home));
        //Search for username "test"
        solo.enterText((EditText) solo.getView(R.id.userSearch), "test");
        //Click search
        Button search_button = (Button) solo.getView(R.id.searchUserButton);
        solo.clickOnView(search_button);
        //Should be in search profile screen
        solo.assertCurrentActivity("Should be in search profile screen", SearchProfile.class);
    }
    @Test
    public void search_user_exception() {
        //Test searches for non-existent user and empty search

        //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_home));
        //Test empty search, should fail and remain in home screen
        Button search_button = (Button) solo.getView(R.id.searchUserButton);
        solo.clickOnView(search_button);
        //Should be in search profile screen
        solo.assertCurrentActivity("Should fail search and remain in main screen", MainScreen.class);

        //Search for username Non existent username
        solo.enterText((EditText) solo.getView(R.id.userSearch), "testNotExistingUser");
        //Click search
        solo.clickOnView(search_button);
        //Should be in search profile screen
        solo.assertCurrentActivity("Should fail search and remain in main screen", MainScreen.class);
    }

    @Test
    public void search_book_test(){
        //Test Book search with a empty field, and valid term

        //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_home));
        Button search_button = (Button) solo.getView(R.id.bookSearch);
        //Click search with empty input should fail and remain in main screen
        solo.clickOnView(search_button);
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Search with valid input
        solo.enterText((EditText) solo.getView(R.id.bookText), "the");
        solo.clickOnView(search_button);
        solo.assertCurrentActivity("Should be in search result screen", BookSearchActivity.class);
    }

    @Test
    public void edit_profile_test(){
        //Test Editing phone number in edit profile page

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));

        solo.clickOnView(solo.getView(R.id.editProfile));
        //Should be in the Edit profile page (Same page as create account)
        solo.assertCurrentActivity("Edit profile screen", CreateAccount.class);

        //Click Finish without making changes should bring you back to MainScreen
        solo.clickOnView((Button) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Try emptying phone number, should fail and stay in same page
        solo.clickOnView(solo.getView(R.id.editProfile));
        solo.clearEditText((EditText)solo.getView(R.id.number));
        solo.clickOnView((Button) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Edit profile screen", CreateAccount.class);


        //Try using phone number less than 7 digit, should fail and stay in same page
        solo.enterText((EditText)solo.getView(R.id.number),"253495");
        solo.clickOnView((Button) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Edit profile screen", CreateAccount.class);

        //Try using new vaild number, should work and bring you to MainScreen
        solo.clearEditText((EditText)solo.getView(R.id.number));
        solo.enterText((EditText)solo.getView(R.id.number),"6046666666");
        solo.clickOnView((Button) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Edit profile screen", MainScreen.class);

    }

    @Test
    public void filter_test(){
        //Test filters are working, MUST HAVE TestOwner in database with pass 4

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "TestOwner");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));


        solo.clickOnView((Button) solo.getView(R.id.filter_all_btn));
        //Makes sure that the filter is showing this book and can be clicked on
        solo.clickOnText("TestAll");
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        solo.clickOnView((Button) solo.getView(R.id.filter_available_btn));
        //Makes sure filter is showing an available book and can be clicked on
        solo.clickOnText("TestAvailable");
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        solo.clickOnView((Button) solo.getView(R.id.filter_accepted_btn));
        //Makes sure filter is showing an accepted book and can be clicked on
        solo.clickOnText("TestAccepted");
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);


        solo.clickOnView((Button) solo.getView(R.id.filter_borrowed_btn));
        solo.clickOnText("TestBorrowed");
        //Makes sure filter is showing an borrowed book and can be clicked on
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void request_book_from_profile(){
        //Tests requesting of books through user profile after making a search, Must have TestOwner and test4

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Search for username
        solo.enterText((EditText) solo.getView(R.id.userSearch), "TestOwner");
        Button search_button = (Button) solo.getView(R.id.searchUserButton);
        solo.clickOnView(search_button);
        //Selecting an available book
        solo.clickOnText("RequestTest");

        //Request it
        solo.clickOnView( solo.getView(R.id.requestBook_button));

        //Sign out
        solo.goBack();
        solo.goBack();
        Button sign_out = (Button) solo.getView(R.id.sign_out_button2);
        solo.clickOnView(sign_out);

        //Sign in as owner
        solo.clearEditText((EditText) solo.getView(R.id.email2));
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.email2), "TestOwner");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to tracker page for requested book
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnText("RequestTest");
        solo.clickOnView(solo.getView(R.id.track_button));
        //Make sure request was received and can be declined
        solo.clickOnView(solo.getView(R.id.request_decline_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void request_book_from_search(){
        //Test Requesting of books through searching

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Search for book
        solo.enterText((EditText) solo.getView(R.id.bookText), "RequestTest");
        Button search_button = (Button) solo.getView(R.id.bookSearch);
        solo.clickOnView(search_button);
        //Selecting an available book
        solo.clickOnText("RequestTest",2);

        //Request it
        solo.clickOnView((Button) solo.getView(R.id.requestBook_button));

        //Sign out
        solo.goBack();
        solo.goBack();
        Button sign_out = (Button) solo.getView(R.id.sign_out_button2);
        solo.clickOnView(sign_out);

        //Sign in as owner
        solo.clearEditText((EditText) solo.getView(R.id.email2));
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.email2), "TestOwner");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to tracker page for requested book
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnText("RequestTest");
        solo.clickOnView((Button)solo.getView(R.id.track_button));
        //Make sure request was received and can be declined
        solo.clickOnView((Button)solo.getView(R.id.request_decline_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void alert_requested_tracker_tab_test(){
        //Test that the alerts, tracker and requested tab in notifications tabs are updated properly upon accept/decline

        //Signing in as requester
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "TestAlertRequester");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Select the book
        solo.clickOnText("AlertBook");
        //Request it
        solo.clickOnView( solo.getView(R.id.requestBook_button));
        //Go to notifications
        solo.clickOnView(solo.getView(R.id.navigation_notifications));
        //Go to requested tab
        solo.clickOnText("Requested");
        //Click on text to make sure it is there
        solo.clickOnText("AlertBook");

        //Sign out
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnView(solo.getView(R.id.sign_out_button2));

        //Sign in as owner
        solo.clearEditText((EditText) solo.getView(R.id.email2));
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.email2), "TestAlertOwner");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to notifications tab
        solo.clickOnView(solo.getView(R.id.navigation_notifications));
        //Click delete, works if notification was received
        solo.clickOnView(solo.getView(R.id.notification_item_delete_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to dashboard->tracker to decline the book
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnText("AlertBook");
        solo.clickOnButton("Tracker");
        solo.clickOnButton("Decline");

        //Sign out
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnView(solo.getView(R.id.sign_out_button2));

        //Sign in as requester
        solo.clearEditText((EditText) solo.getView(R.id.email2));
        solo.clearEditText((EditText) solo.getView(R.id.password));
        solo.enterText((EditText) solo.getView(R.id.email2), "TestAlertRequester");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to notifications tab
        solo.clickOnView(solo.getView(R.id.navigation_notifications));
        //Click delete, works if notification was received
        solo.clickOnView(solo.getView(R.id.notification_item_delete_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void sign_out_test(){
        //Testing sign out

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);


        //Signing out
        Button sign_out = (Button) solo.getView(R.id.sign_out_button2);
        solo.clickOnView(sign_out);
        solo.assertCurrentActivity("Should be in logged out screen", MainActivity.class);
    }

    @Test
    public void edit_book_test(){
        //Tests the editing of a book with proper information filled in

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Add a book
        solo.clickOnView(solo.getView(R.id.navigation_add));
        solo.enterText((EditText) solo.getView(R.id.authorText), "EditTesterAuthor");
        solo.enterText((EditText) solo.getView(R.id.titleText), "EditTesterTitle");
        solo.enterText((EditText) solo.getView(R.id.isbnText), "520505050");
        solo.clickOnView(solo.getView(R.id.add_button));

        //Go to dashboard and select the book
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnText("EditTesterTitle");
        solo.clickOnView(solo.getView(R.id.edit_button));

        //Clear the fields, edit it, then click add
        solo.clearEditText((EditText) solo.getView(R.id.edit_authorText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_titleText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_isbnText));
        solo.enterText((EditText) solo.getView(R.id.edit_authorText), "NewAuthorEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_titleText), "NewTitleEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_isbnText), "02385002375");
        solo.clickOnView(solo.getView(R.id.edit_add_button));

        //Should be in dashboard, and we should be able to select the new title if successfully changed
        solo.clickOnText("NewTitleEditTest");
        //Delete the book
        solo.clickOnView(solo.getView(R.id.deleteBook_button));
    }
    @Test
    public void edit_book_test_exception(){
        //Tests the editing of a book with missing fields

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Add a book
        solo.clickOnView(solo.getView(R.id.navigation_add));
        solo.enterText((EditText) solo.getView(R.id.authorText), "EditTesterAuthor");
        solo.enterText((EditText) solo.getView(R.id.titleText), "EditTesterTitle");
        solo.enterText((EditText) solo.getView(R.id.isbnText), "520505050");
        solo.clickOnView(solo.getView(R.id.add_button));

        //Go to dashboard and select the book
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnText("EditTesterTitle");
        solo.clickOnView(solo.getView(R.id.edit_button));

        //Clear the fields, edit it, then click add - Missing Title
        solo.clearEditText((EditText) solo.getView(R.id.edit_authorText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_titleText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_isbnText));
        solo.enterText((EditText) solo.getView(R.id.edit_authorText), "NewAuthorEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_titleText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_isbnText), "02385002375");
        solo.clickOnView(solo.getView(R.id.edit_add_button));

        //Should still be in same page and can continue to edit text since add failed
        //Clear the fields, edit it, then click add -Missing Author
        solo.clearEditText((EditText) solo.getView(R.id.edit_authorText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_titleText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_isbnText));
        solo.enterText((EditText) solo.getView(R.id.edit_authorText), "");
        solo.enterText((EditText) solo.getView(R.id.edit_titleText), "NewTitleEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_isbnText), "02385002375");
        solo.clickOnView(solo.getView(R.id.edit_add_button));

        //Should still be in same page and can continue to edit text since add failed
        //Clear the fields, edit it, then click add - Proper fields
        solo.clearEditText((EditText) solo.getView(R.id.edit_authorText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_titleText));
        solo.clearEditText((EditText) solo.getView(R.id.edit_isbnText));
        solo.enterText((EditText) solo.getView(R.id.edit_authorText), "NewAuthorEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_titleText), "NewTitleEditTest");
        solo.enterText((EditText) solo.getView(R.id.edit_isbnText), "02385002375");
        solo.clickOnView(solo.getView(R.id.edit_add_button));

        //Should work
        //Should be in dashboard, and we should be able to select the new title if successfully changed
        solo.clickOnText("NewTitleEditTest");
        //Delete the book
        solo.clickOnView(solo.getView(R.id.deleteBook_button));
    }
    @Test
    public void borrowed_lent_page_test(){
        //tests that the Books Borrowed and Books Lent out page are working

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go to dashboard and click books borrowed
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnView(solo.getView(R.id.borrowing_page_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Go back to dashboard and click books lent out
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        solo.clickOnView(solo.getView(R.id.lent_out_page_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void add_book(){
        //Asserts that the current activity is the MainActivity. Otherwise, show Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button

        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_add));

        Random rand = new Random();
        // Obtain a number between [0 - 200].
        int n = rand.nextInt(20000);
        n += 1;
        solo.enterText((EditText) solo.getView(R.id.authorText), "TestAuthor");
        solo.enterText((EditText) solo.getView(R.id.titleText), "TestTitle");
        solo.enterText((EditText) solo.getView(R.id.isbnText), "0" + n);
        solo.clickOnView(solo.getView(R.id.add_button));
    }

    @Test
    public void add_book_exception(){
        //Test adding of books with missing fields

        //Sign in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_add));

        //Adding an empty book, should fail
        solo.clickOnView((Button) solo.getView(R.id.add_button));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_add));

        //Adding book with no title, should fail
        solo.enterText((EditText) solo.getView(R.id.authorText), "TestAuthor");
        solo.enterText((EditText) solo.getView(R.id.isbnText), "0");
        solo.clickOnView((Button) solo.getView(R.id.add_button));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_add));

        //Adding book with no isbn, should fail
        solo.enterText((EditText) solo.getView(R.id.authorText), "TestAuthor");
        solo.enterText((EditText) solo.getView(R.id.titleText), "TestTitle");
        solo.clickOnView((Button) solo.getView(R.id.add_button));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_add));

        //Adding book with no author, should fail
        solo.enterText((EditText) solo.getView(R.id.titleText), "TestTitle");
        solo.enterText((EditText) solo.getView(R.id.isbnText), "0");
        solo.clickOnView((Button) solo.getView(R.id.add_button));
        solo.clickOnView(solo.getView(R.id.navigation_add));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void test_delete_book(){
        //Test deleting of books, requires test4 to have at least one available book 'TestTitle'
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));
        //Click on book to be deleted
        solo.clickOnText("TestTitle");
        //Delete book
        solo.clickOnView(solo.getView(R.id.deleteBook_button));
        //Successful delete
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        //User should be in home page
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
