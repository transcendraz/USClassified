package com.example.usclassifieds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.usclassifieds.ui.login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExploreActivityTest {

    @Rule
    public final ActivityTestRule<ExploreActivity> mActivityRule =
            new ActivityTestRule<>(ExploreActivity.class, true, false);

    @Rule public final IntentsTestRule<ExploreActivity> mIntentsRule =
            new IntentsTestRule<ExploreActivity>(ExploreActivity.class, true, false) {

    };

    @Test
    // check that grid correctly populates with some values
    public void testOnCreate() {
        mActivityRule.launchActivity(null);
        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            onView(withId(R.id.grid_item_title));
        } catch (NoMatchingViewException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPriceValidation() {
        mActivityRule.launchActivity(null);
        // initially is valid
        onView(withId(R.id.apply_filter)).check(matches(isEnabled()));
        // Select spinner
        onView(withId(R.id.spinner_filter)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Price (Upper)"))).perform(click());
        onView(withId(R.id.spinner_filter)).check(matches(withSpinnerText(containsString("Price (Upper)"))));

        onView(withId(R.id.filter_val))
                .perform(typeText("abcdef"), closeSoftKeyboard());
        onView(withId(R.id.apply_filter)).check(matches(not(isEnabled())));

    }

    @Test
    public void testProximityValidation() {
        mActivityRule.launchActivity(null);
        // initially is valid
        onView(withId(R.id.apply_filter)).check(matches(isEnabled()));
        // Select spinner
        onView(withId(R.id.spinner_filter)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Proximity (Miles)"))).perform(click());
        onView(withId(R.id.spinner_filter)).check(matches(withSpinnerText(containsString("Proximity (Miles)"))));

        onView(withId(R.id.filter_val))
                .perform(typeText("abcdef"), closeSoftKeyboard());
        onView(withId(R.id.apply_filter)).check(matches(not(isEnabled())));

    }

    @Test
    public void testFriendsValidation() {
        mActivityRule.launchActivity(null);
        // initially is valid
        onView(withId(R.id.apply_filter)).check(matches(isEnabled()));
        // Select spinner
        onView(withId(R.id.spinner_filter)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Friends Only"))).perform(click());
        onView(withId(R.id.spinner_filter)).check(matches(withSpinnerText(containsString("Friends Only"))));

        onView(withId(R.id.filter_val))
                .perform(typeText("abcdef"), closeSoftKeyboard());
        onView(withId(R.id.apply_filter)).check(matches(isEnabled()));

    }

//    @Test
//    public void testToItemPost() {
//        mActivityRule.launchActivity(null);
//        try {
//            Thread.sleep(4000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onData(anything()).inAdapterView(withId(R.id.explore_grid)).atPosition(0).
//                onChildView(withId(R.id.grid_item_image)).perform(scrollTo(), click());
//        try {
//            Thread.sleep(2000L);
//        } catch(InterruptedException e) {
//            e.printStackTrace();
//        }
//        intended(hasComponent(ItemActivity.class.getName()));
//    }

    @Test
    public void testNavBarLeft() {
        mIntentsRule.launchActivity(null);
        onView(withId(R.id.buy_item)).perform(click());
        intended(hasComponent(ExploreActivity.class.getName()));
    }

    @Test
    public void testNavBarCenter() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        SharedPreferences pref = context.getSharedPreferences("login", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("userId", 1);
        edit.commit();

        mIntentsRule.launchActivity(null);
        onView(withId(R.id.user_profile_item)).perform(click());
        intended(hasComponent(ViewUserProfileActivity.class.getName()));
    }

    @Test
    public void testNavBarRight() {
        mIntentsRule.launchActivity(null);
        onView(withId(R.id.sell_item)).perform(click());
        intended(hasComponent(CreateItemPostActivity.class.getName()));
    }
}