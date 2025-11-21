package com.example.fetchdata.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fetchdata.R
import com.example.fetchdata.data.local.User
import com.example.fetchdata.ui.viewmodel.AuthViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SignInFragmentTest {

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testSignInFragmentDisplaysCorrectly() {
        // Launch the fragment
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Verify UI elements are displayed
        onView(withId(R.id.etEmailSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.etPasswordSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.tvGoToSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmailInputAcceptsText() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Type email
        val testEmail = "test@example.com"
        onView(withId(R.id.etEmailSignIn))
            .perform(typeText(testEmail), closeSoftKeyboard())

        // Verify email is entered
        onView(withId(R.id.etEmailS ignIn))
            .check(matches(withText(testEmail)))
    }

    @Test
    fun testPasswordInputAcceptsText() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Type password
        val testPassword = "password123"
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText(testPassword), closeSoftKeyboard())

        // Verify password is entered
        onView(withId(R.id.etPasswordSignIn))
            .check(matches(withText(testPassword)))
    }

    @Test
    fun testSignInButtonIsClickable() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Verify button is enabled and clickable
        onView(withId(R.id.btnSignIn))
            .check(matches(isEnabled()))
            .check(matches(isClickable()))
    }

    @Test
    fun testGoToSignUpTextIsClickable() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Verify sign up link is clickable
        onView(withId(R.id.tvGoToSignUp))
            .check(matches(isClickable()))
    }

    @Test
    fun testSignInWithValidCredentials() {
        val scenario = launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.signInFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Enter valid credentials
        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click sign in button
        onView(withId(R.id.btnSignIn)).perform(click())

    }

    @Test
    fun testSignInWithEmptyFields() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Click sign in without entering credentials
        onView(withId(R.id.btnSignIn)).perform(click())

    }

    @Test
    fun testProgressBarVisibilityDuringLoading() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        // Initially progress bar should not be visible
        onView(withId(R.id.progressBarSignIn))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testNavigationToSignUpScreen() {
        val scenario = launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.signInFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Click on "Go to Sign Up"
        onView(withId(R.id.tvGoToSignUp)).perform(click())

        // Verify navigation occurred
        assert(navController.currentDestination?.id == R.id.signUpFragment)
    }
}

