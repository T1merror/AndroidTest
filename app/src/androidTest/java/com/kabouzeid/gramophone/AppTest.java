package com.kabouzeid.gramophone;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.android.material.tabs.TabLayout;
import com.kabouzeid.gramophone.ui.activities.MainActivity;

import junit.framework.TestCase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * 1. Добавление и удаление трека в плейлисте
 * 2. Удаление и переименование плейлиста
 */

@RunWith(AndroidJUnit4.class)
public class AppTest extends TestCase {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @NonNull
    private static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + String.valueOf(position);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof TabLayout) {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    /**
     * 1. Добавление и удаление трека в плейлисте
     */
    @Test
    public void addAndDeleteTrackPlaylist() {
        onView(withIndex(withId(R.id.menu), 1))
                .perform(click());
        onView(withText("Добавить в плейлист..."))
                .perform(click());
        onView(withText("test"))
                .perform(click());
        Assert.assertNotEquals(onView(withText("INDUSTRY BABY")), onView(withText("Cant Sleep")));

        onView(withId(R.id.tabs))
                .perform(selectTabAtPosition(4));
        onView(withText("test"))
                .perform(click());
        onView(withIndex(withId(R.id.menu), 1))
                .perform(click());
        onView(withText("Удалить из плейлиста"))
                .perform(click());
        onView(withId(R.id.md_buttonDefaultPositive))
                .perform(click());
        Assert.assertNotNull(onView(withText("Пустой плейлист")));
    }

    /**
     * 2. Удаление и переименование плейлиста
     */
    @Test
    public void deleteAndRenamePlaylist() {
        onView(withIndex(withId(R.id.menu), 4))
                .perform(click());
        onView(withText("Переименовать"))
                .perform(click());
        onView(withText("qwerty"))
                .perform(replaceText("qwertyu"));
        Assert.assertNotEquals(onView(withText("qwerty")), onView(withText("qwertyu")));
        onView(withText("ПЕРЕИМЕНОВАТЬ"))
                .perform(click());
        Assert.assertNotEquals(onView(withText("qwerty")), onView(withText("qwertyu")));

        onView(withIndex(withId(R.id.menu), 4))
                .perform(click());
        onView(withText("Удалить"))
                .perform(click());
        onView(withId(R.id.md_buttonDefaultPositive))
                .perform(click());
        assertNotNull(onView(withText("qwerty")));
        assertNotNull(onView(withText("qwertyu")));
    }
}