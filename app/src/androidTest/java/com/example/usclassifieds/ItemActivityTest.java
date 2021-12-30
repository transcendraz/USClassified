package com.example.usclassifieds;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;

import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
//import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.usclassifieds.ui.login.LoginActivity;

import java.io.File;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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
public class ItemActivityTest {
    @Rule
    public final ActivityTestRule<ItemActivity> mActivityRule =
            new ActivityTestRule<ItemActivity>(ItemActivity.class, true, false){
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("itemId", 1);
                    return intent;
                }
            }
            ;

@Rule public final IntentsTestRule<ItemActivity> mIntentsRule =
        new IntentsTestRule<ItemActivity>(ItemActivity.class, true, false) {
            @Override
            protected Intent getActivityIntent() {
                Intent intent = new Intent();
                intent.putExtra("itemId", 1);
                return intent;
            }
        };

    @Test
    public void testActivityLoaded() {
        mActivityRule.launchActivity(null);
        String activityTitle = "iPhone X";
        String price = "Price:\n$"+"489.99";
        String description="Description:\n"+"Used iPhone X with good cares. It turns on and works perfectly despite some scratches.";

        onView(withId(R.id.ItemName)).check(matches((withText(activityTitle))));
        onView(withId(R.id.Price)).check(matches(withText(price)));
        onView(withId(R.id.Description)).check(matches(withText(description)));

    }
////
//    @Test
//    public void testWishlistManifest(){
//        mActivityRule.launchActivity(null);
//        //onView()
//        if(onView(withId(R.id.star_button)).)
//    }
//
    @Test
    public void testExpandMap(){
        mIntentsRule.launchActivity(null);
        onView(withId(R.id.ViewMap)).perform(scrollTo(),click());
        intended(hasComponent(MapsActivity.class.getName()));
    }
//
    @Test
    public void testViewProfile(){
        mIntentsRule.launchActivity(null);
        onView(withId(R.id.ViewSeller)).perform(scrollTo(),click());
        intended(hasComponent(ViewUserProfileActivity.class.getName()));
    }

    @Test
    public void testInvalidSell(){
        mIntentsRule.launchActivity(null);
        onView(withId(R.id.BuyerName)).perform(scrollTo(),typeText("Nobody"));
        onView(withId(R.id.Buy)).perform(scrollTo(),click());
        onView(withText("Such Username does not exist.")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }
    }

    Matcher<View> hasValueEqualTo(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("test Has EditText/TextView the value:  " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView) && !(view instanceof EditText)) {
                    return false;
                }
                if (view != null) {
                    String text;
                    if (view instanceof TextView) {
                        text = ((TextView) view).getText().toString();
                    } else {
                        text = ((EditText) view).getText().toString();
                    }

                    System.out.println("xyz: "+text);

                    return (text.equalsIgnoreCase(content));
                }
                return false;
            }
        };
    }
}
