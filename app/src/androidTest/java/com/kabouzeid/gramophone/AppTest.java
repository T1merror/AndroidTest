package com.kabouzeid.gramophone;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
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

import java.util.NoSuchElementException;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * 1. Старт и паузу проигрывания трека
 * 2. Переключение треков в проигрывателе
 * 3. Добавление/удаление трека из Избранного
 * 4. Добавление и удаление трека в плейлисте
 * 5. Удаление и переименование плейлиста
 */

@RunWith(AndroidJUnit4.class)
public class AppTest extends TestCase {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Добавление/удаление трека из Избранного
     */
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

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

    @Test
    public void addAndDeleteTrackFavourite() throws NoSuchElementException, InterruptedException {
        onView(withId(R.id.mini_player_image))
                .perform(click());
        onView(withId(R.id.action_toggle_favorite))
                .perform(click())
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.action_bar_root))
                .perform(swipeDown());
        Thread.sleep(500);
        onView(withId(R.id.tabs))
                .perform(selectTabAtPosition(4));
        onView(allOf(withId(R.id.title), withText("Избранное")))
                .perform(click());
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.menu)));
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
        /**
         * Adding a track to a playlist
         */
        onView(withIndex(withId(R.id.menu), 1))
                .perform(click());
        onView(withText("Добавить в плейлист..."))
                .perform(click());
        onView(withText("test"))
                .perform(click());
        Assert.assertNotEquals(onView(withText("INDUSTRY BABY")), onView(withText("Cant Sleep")));
        /**
         * Deleting a track from a playlist
         */
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