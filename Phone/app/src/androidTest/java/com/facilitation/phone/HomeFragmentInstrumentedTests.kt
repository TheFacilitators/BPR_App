package com.facilitation.phone

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facilitation.phone.view.HomeFragment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class HomeFragmentInstrumentedTests {

    private lateinit var scenario: FragmentScenario<HomeFragment>
    private lateinit var activityRule: ActivityScenarioRule<MainActivity>

    @Before
    fun setUp() {
        // Launch the fragment using FragmentScenario
        activityRule = ActivityScenarioRule(MainActivity::class.java)
        scenario = FragmentScenario.launchInContainer(HomeFragment::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
    }

    @Test
    fun sayHeartyHelloToVuzix() {
        // Retrieve the fragment instance
        scenario.onFragment {
            onView(withId(R.id.HelloVuzix)).perform(click())

            val expectedToastMessage = "Hello to Vuzix sent"
            onView(withText(expectedToastMessage))
                .inRoot(withDecorView(not(`is`(it.activity?.window?.decorView))))
                .check(matches(isDisplayed()))
        }
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.facilitation.phone", appContext.packageName)
    }
}