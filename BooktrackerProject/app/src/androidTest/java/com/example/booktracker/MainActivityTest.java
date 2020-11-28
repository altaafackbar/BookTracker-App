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
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
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
    /**
     * Gets the Activity
     * @throws Exception
     */
    //@Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }
    @Test
    public void sign_in(){
        //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click SIGN IN Button
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void create_acc(){
         //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
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

        //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
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

        //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
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

        //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
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

        Button edit_button = (Button) solo.getView(R.id.editProfile);
        solo.clickOnView(edit_button);
        //Should be in the Edit profile page (Same page as create account)
        solo.assertCurrentActivity("Edit profile screen", CreateAccount.class);

        //Click Finish without making changes should bring you back to MainScreen
        solo.clickOnView((Button) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Try emptying phone number, should fail and stay in same page
        solo.clickOnView(edit_button);
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
        //Test to ensure filters don't crash

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));

        //Test filters aren't bugged.
        solo.clickOnView((Button) solo.getView(R.id.filter_all_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView((Button) solo.getView(R.id.filter_available_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView((Button) solo.getView(R.id.filter_accepted_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
        solo.clickOnView((Button) solo.getView(R.id.filter_borrowed_btn));
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);
    }

    @Test
    public void request_book_test(){
        //Ensures Requesting books are working

        //Signing in
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click sign in Button
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Selecting an available book
        solo.clickInRecyclerView(1);
        solo.assertCurrentActivity("Should be in home screen", MainScreen.class);

        //Click request button
        solo.clickOnView((Button) solo.getView(R.id.requestBook_button));
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
    public void add_book(){
        //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
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
        Button add_book = (Button) solo.getView(R.id.add_button);
        solo.clickOnView(add_book);
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

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
