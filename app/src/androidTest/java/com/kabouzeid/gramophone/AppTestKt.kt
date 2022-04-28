package com.kabouzeid.gramophone

import android.view.View

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4

import com.google.android.material.tabs.TabLayout
import com.kabouzeid.gramophone.ui.activities.MainActivity

import junit.framework.TestCase

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTestKt {

    @get:Rule
    var activityTestRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java)

    fun selectTabAtPosition(position: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    isDisplayed(),
                    isAssignableFrom(TabLayout::class.java)
                )
            }

            override fun getDescription(): String {
                return "with tab at index$position"
            }

            override fun perform(uiController: UiController, view: View) {
                if (view is TabLayout) {
                    val tab = view.getTabAt(position)
                    tab?.select()
                }
            }
        }
    }

    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

    /**
     * Добавление и удаление трека в плейлисте
     */
    @Test
    fun addAndDeleteTrackPlaylist() {
        onView(withIndex(withId(R.id.menu), 1))
            .perform(click())
        onView(withText("Добавить в плейлист..."))
            .perform(click())
        onView(withText("test"))
            .perform(click())
        Assert.assertNotEquals(
            onView(withText("INDUSTRY BABY")),
            onView(withText("Cant Sleep"))
        )

        onView(withId(R.id.tabs))
            .perform(selectTabAtPosition(4))
        onView(withText("test"))
            .perform(click())
        onView(withIndex(withId(R.id.menu), 1))
            .perform(click())
        onView(withText("Удалить из плейлиста"))
            .perform(click())
        onView(withId(R.id.md_buttonDefaultPositive))
            .perform(click())
        Assert.assertNotNull(onView(withText("Пустой плейлист")))
    }

    /**
     * Удаление и переименование плейлиста
     */
    @Test
    fun deleteAndRenamePlaylist() {
        onView(withIndex(withId(R.id.menu), 3))
            .perform(click())
        onView(withText("Переименовать"))
            .perform(click())
        onView(withText("qwerty"))
            .perform(ViewActions.replaceText("qwertyu"))
        Assert.assertNotEquals(
            onView(withText("qwerty")),
            onView(withText("qwertyu"))
        )
        onView(withText("ПЕРЕИМЕНОВАТЬ"))
            .perform(click())
        Assert.assertNotEquals(
            onView(withText("qwerty")),
            onView(withText("qwertyu"))
        )
        onView(withIndex(withId(R.id.menu), 3))
            .perform(click())
        onView(withText("Удалить"))
            .perform(click())
        onView(withId(R.id.md_buttonDefaultPositive))
            .perform(click())
        TestCase.assertNotNull(onView(withText("qwerty")))
        TestCase.assertNotNull(onView(withText("qwertyu")))
    }

    /**
     * Старт и паузу проигрывания трека
     */
    @Test
    fun clickPlayStopMusic() {
        val miniPlayerPlayPause = withId(R.id.mini_player_play_pause_button)
        onView(miniPlayerPlayPause)
            .perform(click())
            .check(matches(isDisplayed()))
        onView(miniPlayerPlayPause)
            .perform(click())
            .check(matches(isDisplayed()))
    }

    /**
     * Переключение треков в проигрывателе
     */
    @Test
    fun buttonPlayerNextPerv() {
        onView(withId(R.id.mini_player_image))
            .perform(click())
        onView(withId(R.id.player_next_button))
            .perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.player_prev_button))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    /**
     * Добавление/удаление трека из Избранного
     */
    @Test
    fun addAndDeleteTrackFavourite() {
        onView(withId(R.id.mini_player_image))
            .perform(click())
        onView(withId(R.id.action_toggle_favorite))
            .perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_bar_root))
            .perform(ViewActions.swipeDown())
        onView(withId(R.id.tabs))
            .perform(selectTabAtPosition(4))
        onView(withText("Избранное"))
            .perform(click())

        onView(withId(R.id.mini_player_image))
            .perform(click())
        onView(withId(R.id.action_toggle_favorite))
            .perform(click())
            .check(matches((isDisplayed())))
        onView(withId(R.id.action_bar_root))
            .perform(ViewActions.swipeDown())
    }
}