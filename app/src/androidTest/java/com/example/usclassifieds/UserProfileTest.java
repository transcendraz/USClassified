package com.example.usclassifieds;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.content.Intent;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfileTest {

    @Rule
    public final ActivityTestRule<ViewUserProfileActivity> mActivityRule =
            new ActivityTestRule<>(ViewUserProfileActivity.class, true, false);

    @Test
    public void testProfileAppearance()
    {
        mActivityRule.launchActivity(null);
        String user_name = "User Name:";
        String buysell = "Buy/Sell Count:";
        String phone = "Phone:";
        String email = "Email:";

        onView(withId(R.id.name_label)).check(matches((withText(user_name))));
        onView(withId(R.id.blank_label)).check(matches((withText(buysell))));
        onView(withId(R.id.phone_label)).check(matches((withText(phone))));
        onView(withId(R.id.email_label)).check(matches(withText(email)));
    }

    @Test
    public void testProfileLoadForOtherUser()
    {

        //intent.getExtras().containsKey("userId")
        Intent intent = new Intent();
        intent.putExtra("userId", 1);
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.user_profile_name)).check(matches(withText("chenhui")));
        onView(withId(R.id.user_buy_and_sell)).check(matches(withText("0/0")));
        onView(withId(R.id.user_email)).check(matches(withText("chenhui@usc.edu")));
    }

    @Test
    public void testProfileLoadForLoggedInUser()
    {

    }
}
