package com.comp313002.team3.g5refugeeaid;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import android.content.Context;
import android.os.SystemClock;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpAndroidTests {

    public static final String[] STRING_TO_BE_TYPED = {
            "TestName", "TestLastName", "United States", "test@testing.com", "647-703-9898", "UN-14556HJ", "pass123"
    };

    @Rule public ActivityScenarioRule<SignUpActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SignUpActivity.class);
    @Before
    public void initialize() {
    }


    @Test
    public void createRefugeeUser_sameActivity() {
        // Type text and then press the button.
        onData(allOf(is(instanceOf(String.class)))).atPosition(0).perform(click());
        onView(withText("I am a Refugee")).inRoot(isPlatformPopup()).perform(click());

        // filling the form
        onView(withId(R.id.txtFirstName)).perform(typeText(STRING_TO_BE_TYPED[0]));
        onView(withId(R.id.txtLastName)).perform(typeText(STRING_TO_BE_TYPED[1]));
        onView(withId(R.id.bDayPicker)).perform(PickerActions.setDate(2001, 6, 30));
        onView(withId(R.id.txtNationality)).perform(typeText(STRING_TO_BE_TYPED[2]), closeSoftKeyboard());
        onView(withId(R.id.txtEmail)).perform(typeText(STRING_TO_BE_TYPED[3]), closeSoftKeyboard());
        onView(withId(R.id.txtPhoneNumber)).perform(typeText(STRING_TO_BE_TYPED[4]), closeSoftKeyboard());
        onView(withId(R.id.txtUnNumber)).perform(typeText(STRING_TO_BE_TYPED[5]), closeSoftKeyboard());
        onView(withId(R.id.txtPassword)).perform(scrollTo(), typeText(STRING_TO_BE_TYPED[6]), closeSoftKeyboard());
        onView(withId(R.id.txtConfirmPassword)).perform(scrollTo(), typeText(STRING_TO_BE_TYPED[6]), closeSoftKeyboard());
        onView(withId(R.id.btn_submit)).perform(scrollTo(), click());
        SystemClock.sleep(3500);

        // Check if confirm button show up after creating user
        onView(withId(R.id.btn_Confirm)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
