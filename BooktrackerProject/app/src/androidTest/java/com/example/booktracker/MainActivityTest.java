package com.example.booktracker;
import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.booktracker.ui.home.HomeFragment;
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
    /**
     * Add a city to the listview and check the city name using assertTrue
     * Clear all the cities from the listview and check again with assertFalse
     */
    @Test
    public void sign_in(){
//Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click ADD CITY Button
    }
    @Test
    public void create_acc(){
//Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Random rand = new Random();
// Obtain a number between [0 - 200].
        int n = rand.nextInt(20000);
        n += 1;
        Button msButton = (Button) solo.getView(
                R.id.create_acc);
        solo.clickOnView(msButton); //Click new user Button

        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.email), "test" + n);
        solo.enterText((EditText) solo.getView(R.id.password), "8");
        solo.enterText((EditText) solo.getView(R.id.number), "7809823423");
        Button sign_up = (Button) solo.getView(
                R.id.sign_up);
        solo.clickOnView(sign_up); //Click new user Button
        solo.assertCurrentActivity("Should be main screen activity", MainScreen.class);

    }
    @Test
    public void addBook(){
//Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click ADD CITY Button

        solo.assertCurrentActivity("Should be create account activity", MainScreen.class);
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
    public void editInfo(){
//Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email2), "test4");
        solo.enterText((EditText) solo.getView(R.id.password), "4");
        solo.clickOnButton("SIGN IN"); //Click ADD CITY Button

        solo.assertCurrentActivity("Should be create account activity", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.navigation_dashboard));

        solo.clickOnView(solo.getView(R.id.editProfile));
        Random rand = new Random();
// Obtain a number between [0 - 200].
        int n = rand.nextInt(200000);
        n += 1;
        solo.assertCurrentActivity("Should be create account activity", CreateAccount.class);
        EditText number = (EditText) solo.getView(R.id.number);
        solo.clearEditText(number);
        solo.enterText(number, String.valueOf(n));
        solo.clickOnView(solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Should be main screen activity", MainScreen.class);


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
