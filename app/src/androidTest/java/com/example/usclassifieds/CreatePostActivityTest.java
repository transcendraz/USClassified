package com.example.usclassifieds;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;

import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.filters.LargeTest;
//import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import java.io.File;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.EnumSet.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreatePostActivityTest {
    private static final String CREATE_POST_ACTIVITY_TITLE = "Create a Post";

    @Rule
    public final ActivityTestRule<CreateItemPostActivity> mActivityRule =
            new ActivityTestRule<>(CreateItemPostActivity.class, true, false);

    @Test
    public void testActivityLoaded() {
        //start our activity
        mActivityRule.launchActivity(null);
        //check is our text entry displayed and enter some text to it
        String activityTitle = "Create a Post";
        String uploadImageButtonTitle = "UPLOAD IMAGE";
        String postTitleHint = "Enter a Title";
        String postPriceHint = "Price";
        String postTagsHint = "Add Some Tags (separated by commas)";
        String postDescriptionHint = "Write a Description";
        String createPostButtonTitle = "CREATE POST";

        onView(withId(R.id.create_post_title)).check(matches((withText(activityTitle))));
        onView(withId(R.id.choose_image_btn)).check(matches(withText(uploadImageButtonTitle)));
        onView(withId(R.id.post_title)).check(matches(withHint(postTitleHint)));
        onView(withId(R.id.post_item_price)).check(matches(withHint(postPriceHint)));
        onView(withId(R.id.post_tags)).check(matches(withHint(postTagsHint)));
        onView(withId(R.id.post_description)).check(matches(withHint(postDescriptionHint)));
        onView(withId(R.id.create_post_btn)).check(matches(withText(createPostButtonTitle)));

    }

    @Test
    public void testItemPostInput() {
        //start our activity
        mActivityRule.launchActivity(null);

        String testTitle = "test title";
        String testPrice = "123";
        String testTags = "car,phone,book";
        String testDescription = "test description";

        onView(withId(R.id.post_title)).perform(typeText(testTitle));
        onView(withId(R.id.post_title)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_item_price)).perform(typeText(testPrice));
        onView(withId(R.id.post_item_price)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_tags)).perform(typeText(testTags));
        onView(withId(R.id.post_tags)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_description)).perform(typeText(testDescription));
        onView(withId(R.id.post_description)).perform(closeSoftKeyboard());

        onView(withId(R.id.post_title)).check(matches(withText(testTitle)));
        onView(withId(R.id.post_item_price)).check(matches(withText("$" + testPrice + ".00")));
        onView(withId(R.id.post_tags)).check(matches(withText(testTags)));
        onView(withId(R.id.post_description)).check(matches(withText(testDescription)));
    }

    @Test
    public void testInvalidItemPost() {
        //start our activity
        mActivityRule.launchActivity(null);
        //check is our text entry displayed and enter some text to it
        String testTitle = "test title";
        String testPrice = "123";
        String testTags = "car,phone,book";
        String testDescription = "test description";
        String expectedErrorMsg = "You must upload an image.";

        onView(withId(R.id.post_title)).perform(typeText(testTitle));
        onView(withId(R.id.post_title)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_item_price)).perform(typeText(testPrice));
        onView(withId(R.id.post_item_price)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_tags)).perform(typeText(testTags));
        onView(withId(R.id.post_tags)).perform(closeSoftKeyboard());
        onView(withId(R.id.post_description)).perform(typeText(testDescription));
        onView(withId(R.id.post_description)).perform(closeSoftKeyboard());

        onView(withId(R.id.create_post_btn)).perform(click());

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.create_post_title)).check(matches(withText(CREATE_POST_ACTIVITY_TITLE)));
    }
}
