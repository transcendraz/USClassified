package com.example.usclassifieds;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Root;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import com.example.usclassifieds.ui.login.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Collection;
import java.util.Iterator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginRegisterTest {
    private static final String KEY_SP_PACKAGE = "Login";
    private static final String LOGIN_TITLE = "USClassifieds";

    @Rule
    public final ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class, true, false);

    @Rule
    public IntentsTestRule<LoginActivity> mLoginIntentTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Before
    @After
    public void clearSharedPrefs() {
        Context context = getTargetContext();
        SharedPreferences prefs =
                context.getSharedPreferences(KEY_SP_PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    @Test
    public void testInvalidLogin() {

        String testUsername = "invalidusername";
        String testPassword = "invalidpswd";

        onView(withId(R.id.username)).perform(typeText(testUsername));
        onView(withId(R.id.username)).perform(closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(testPassword));
        onView(withId(R.id.password)).perform(closeSoftKeyboard());

        onView(withId(R.id.signin)).perform(click());

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.textView4)).check(matches(withText(LOGIN_TITLE)));
    }

    //valid login tests if activity changes
    @Test
    public void testValidLogin() {

        String testUsername = "chenhui";
        String testPassword = "pswd_chenhui";

        onView(withId(R.id.username)).perform(typeText(testUsername));
        onView(withId(R.id.username)).perform(closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(testPassword));
        onView(withId(R.id.password)).perform(closeSoftKeyboard());

        onView(withId(R.id.signin)).perform(click());

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasComponent(ExploreActivity.class.getName()));
    }


    @Test
    public void testInvalidRegister() {
        String testUsername = "chenhui";
        String testPassword = "pswd_chenhui";
        String testEmail = "chenhui@gmail.com";

        onView(withId(R.id.reg_username)).perform(typeText(testUsername));
        onView(withId(R.id.reg_username)).perform(closeSoftKeyboard());
        onView(withId(R.id.reg_password)).perform(typeText(testPassword));
        onView(withId(R.id.reg_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.reg_email)).perform(typeText(testPassword));
        onView(withId(R.id.reg_email)).perform(closeSoftKeyboard());

        onView(withId(R.id.register)).perform(click());


        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.textView4)).check(matches(withText("USClassifieds")));
    }
}
