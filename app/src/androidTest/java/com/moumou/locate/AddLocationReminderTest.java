package com.moumou.locate;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddLocationReminderTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) &&
                       view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void addLocationReminderTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(allOf(withId(R.id.mi_button_next),
                                                            withContentDescription("Next"),
                                                            withParent(allOf(withId(R.id.mi_frame),
                                                                             withParent(withId(
                                                                                     android.R.id.content)))),
                                                            isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(allOf(withId(R.id.mi_button_next),
                                                             withContentDescription("Next"),
                                                             withParent(allOf(withId(R.id.mi_frame),
                                                                              withParent(withId(
                                                                                      android.R.id.content)))),
                                                             isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(allOf(withId(R.id.mi_button_next),
                                                             withContentDescription("Next"),
                                                             withParent(allOf(withId(R.id.mi_frame),
                                                                              withParent(withId(
                                                                                      android.R.id.content)))),
                                                             isDisplayed()));
        appCompatImageButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(allOf(withText("Grant permissions"),
                                                       withParent(allOf(withId(R.id.mi_button_cta),
                                                                        withParent(withId(R.id.mi_frame)))),
                                                       isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3586138);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton4 = onView(allOf(withId(R.id.mi_button_next),
                                                             withContentDescription("Next"),
                                                             withParent(allOf(withId(R.id.mi_frame),
                                                                              withParent(withId(
                                                                                      android.R.id.content)))),
                                                             isDisplayed()));
        appCompatImageButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3594744);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction floatingActionButton2 = onView(allOf(withId(R.id.fab1), isDisplayed()));
        floatingActionButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3587845);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(allOf(withId(R.id.input_label_edittext),
                                                         isDisplayed()));
        appCompatEditText.perform(replaceText("fds"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(allOf(withId(android.R.id.button1),
                                                        withText("OK")));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction textView = onView(allOf(withId(R.id.reminder_title),
                                                withText("fds"),
                                                childAtPosition(childAtPosition(withId(R.id.reminder_listview),
                                                                                0), 0),
                                                isDisplayed()));
        textView.check(matches(withText("fds")));
    }
}
