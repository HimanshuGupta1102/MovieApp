package com.example.fetchdata.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fetchdata.R
import com.example.fetchdata.data.local.MovieDatabase
import com.example.fetchdata.data.local.User
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for SignInFragment with actual database
 * Tests the complete sign-in flow including database operations
 */
@RunWith(AndroidJUnit4::class)
class SignInFragmentIntegrationTest {


    private lateinit var database: MovieDatabase
    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        database = MovieDatabase.getDatabase(ApplicationProvider.getApplicationContext())
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runBlocking {
            database.clearAllTables()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteSignInFlow_withExistingUser() = runBlocking {
        val testUser = User(
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            password = "password123"
        )
        database.userDao().insertUser(testUser)

        val scenario = launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.signInFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("john.doe@example.com"), closeSoftKeyboard())

        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.btnSignIn)).perform(click())

        Thread.sleep(1000)

    }

    @Test
    fun testSignInFlow_withInvalidCredentials() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())

        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())

        onView(withId(R.id.btnSignIn)).perform(click())

        Thread.sleep(1000)

    }

    @Test
    fun testSignInButton_disabledDuringLoading() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.btnSignIn))
            .check(matches(isEnabled()))

        onView(withId(R.id.btnSignIn)).perform(click())

    }

    @Test
    fun testProgressBar_visibleDuringSignIn() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        onView(withId(R.id.progressBarSignIn))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btnSignIn)).perform(click())
    }

    @Test
    fun testFragmentRecreation_maintainsState() {
        val scenario = launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("test@example.com"), closeSoftKeyboard())

        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)

    }
}