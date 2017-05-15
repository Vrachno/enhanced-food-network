package gr.academic.city.sdmd.foodnetwork;

import android.database.Cursor;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.domain.MealType;
import gr.academic.city.sdmd.foodnetwork.ui.activity.CreateMealActivity;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MainActivity;

import android.support.test.espresso.Espresso;
import android.view.View;

import static android.R.attr.name;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Vrachno on 15/5/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMealTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addNewMeal() throws InterruptedException {
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(Cursor.class)), CursorMatchers.withRowString(FoodNetworkContract.MealType.COLUMN_NAME,"Breakfast")))
                .inAdapterView(ViewMatchers.withId(android.R.id.list))
                .perform();


        //this was going to be an insertion test :(


    }
}
