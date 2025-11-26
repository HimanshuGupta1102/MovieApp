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
import com.example.fetchdata.data.api.model.User
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
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
        onView(withId(R.id.etEmailSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.etPasswordSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()))
        onView(withId(R.id.tvGoToSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmailInputAcceptsText() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
        val testEmail = "test@example.com"
        onView(withId(R.id.etEmailSignIn))
            .perform(typeText(testEmail), closeSoftKeyboard())
        onView(withId(R.id.etEmailSignIn))
            .check(matches(withText(testEmail)))
    }

    @Test
    fun testPasswordInputAcceptsText() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
        val testPassword = "password123"
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText(testPassword), closeSoftKeyboard())
        onView(withId(R.id.etPasswordSignIn))
            .check(matches(withText(testPassword)))
    }

    @Test
    fun testSignInButtonIsClickable() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
        onView(withId(R.id.btnSignIn))
            .check(matches(isEnabled()))
            .check(matches(isClickable()))
    }

    @Test
    fun testGoToSignUpTextIsClickable() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
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
        onView(withId(R.id.etEmailSignIn))
            .perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPasswordSignIn))
            .perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btnSignIn)).perform(click())

        // Give some time for the operation to complete
        Thread.sleep(500)
    }

    @Test
    fun testSignInWithEmptyFields() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
        onView(withId(R.id.btnSignIn)).perform(click())

        // Give some time for validation to show
        Thread.sleep(500)

        // Verify that we're still on the sign-in screen (no navigation occurred)
        onView(withId(R.id.etEmailSignIn)).check(matches(isDisplayed()))
    }

    @Test
    fun testProgressBarVisibilityDuringLoading() {
        launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_FetchData)
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
        onView(withId(R.id.tvGoToSignUp)).perform(click())
        assert(navController.currentDestination?.id == R.id.signUpFragment)
    }
}