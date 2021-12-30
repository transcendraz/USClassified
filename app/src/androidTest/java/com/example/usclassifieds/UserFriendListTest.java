package com.example.usclassifieds;

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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserFriendListTest {
    @Rule
    public final ActivityTestRule<ViewUserFriendsActivity> mActivityRule =
            new ActivityTestRule<>(ViewUserFriendsActivity.class, true, false);

    @Test
    public void testFriendsListLoad()
    {
        mActivityRule.launchActivity(null);
        String your_friends = "Your Friends";

        onView(withId(R.id.friend_list_title)).check(matches((withText(your_friends))));
    }
}
