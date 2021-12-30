package com.example.usclassifieds;

import android.content.Intent;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserItemsTest {

    @Rule
    public final ActivityTestRule<ViewUserItemsActivity> mActivityRule =
            new ActivityTestRule<>(ViewUserItemsActivity.class, true, false);

    @Test
    public void testUserItemsLoad()
    {
        mActivityRule.launchActivity(null);
        String my_items = "My Items";

//        onView(withId(R.id.toolbar2)).check(matches((withText(my_items))));
    }

    @Test
    public void testUserItemsCustom()
    {
        Intent intent = new Intent();
        intent.putExtra("userId", 1);
        mActivityRule.launchActivity(intent);


    }
//        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
//        onView(withText(R.string.app_name)).check(matches(withParent(withId(R.id.toolbar2))));
        
}
