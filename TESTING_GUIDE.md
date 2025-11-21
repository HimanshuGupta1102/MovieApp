# Testing Guide for SignInFragment

## Overview
This guide explains how to test the SignIn functionality in your Android app. Tests are organized into two main categories:

## 1. Directory Structure

```
app/src/
├── main/java/              # Production code
├── test/java/              # Unit tests (run on JVM, no emulator needed)
│   └── com/example/fetchdata/
│       └── ui/viewmodel/
│           └── AuthViewModelTest.kt
└── androidTest/java/       # Instrumented tests (run on device/emulator)
    └── com/example/fetchdata/
        └── ui/fragment/
            ├── SignInFragmentTest.kt
            └── SignInFragmentIntegrationTest.kt
```

## 2. Types of Tests

### Unit Tests (`src/test/java`)
- **Purpose**: Test business logic and ViewModel behavior
- **Speed**: Very fast (run on local JVM)
- **No emulator required**
- **Example**: `AuthViewModelTest.kt`

**What to test:**
- ViewModel validation logic (empty fields, email format)
- State management (Loading, Success, Error states)
- Repository interactions (with mocking)
- Coroutine behavior

### Instrumented Tests (`src/androidTest/java`)
- **Purpose**: Test UI interactions and Android framework components
- **Speed**: Slower (require device/emulator)
- **Tests actual UI behavior**
- **Example**: `SignInFragmentTest.kt`, `SignInFragmentIntegrationTest.kt`

**What to test:**
- UI element visibility
- User interactions (button clicks, text input)
- Navigation between screens
- Fragment lifecycle
- Integration with database

## 3. Running Tests

### Run Unit Tests
```bash
# Command line
./gradlew test

# Or in Android Studio:
# 1. Right-click on test/java folder
# 2. Select "Run Tests in 'java'"
```

### Run Instrumented Tests
```bash
# Command line (requires connected device/emulator)
./gradlew connectedAndroidTest

# Or in Android Studio:
# 1. Start an emulator or connect a device
# 2. Right-click on androidTest/java folder
# 3. Select "Run Tests in 'java'"
```

### Run Specific Test Class
```bash
# Unit test
./gradlew test --tests "com.example.fetchdata.ui.viewmodel.AuthViewModelTest"

# Instrumented test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.fetchdata.ui.fragment.SignInFragmentTest
```

## 4. Test Categories Explained

### A. SignInFragmentTest.kt (UI Tests)
Tests individual UI components and basic interactions:
- ✅ Fragment displays correctly
- ✅ Text input works
- ✅ Buttons are clickable
- ✅ Navigation works

**Example test:**
```kotlin
@Test
fun testEmailInputAcceptsText() {
    launchFragmentInContainer<SignInFragment>()
    
    onView(withId(R.id.etEmailSignIn))
        .perform(typeText("test@example.com"))
        .check(matches(withText("test@example.com")))
}
```

### B. SignInFragmentIntegrationTest.kt (Integration Tests)
Tests complete flows with actual database:
- ✅ Complete sign-in flow with real user
- ✅ Error handling with invalid credentials
- ✅ Loading states and progress bar
- ✅ Button states during operations

**Example test:**
```kotlin
@Test
fun testCompleteSignInFlow_withExistingUser() = runBlocking {
    // Create test user in database
    database.userDao().insertUser(testUser)
    
    // Launch fragment and perform sign-in
    launchFragmentInContainer<SignInFragment>()
    
    onView(withId(R.id.etEmailSignIn))
        .perform(typeText("john.doe@example.com"))
    onView(withId(R.id.btnSignIn)).perform(click())
    
    // Verify successful sign-in
}
```

### C. AuthViewModelTest.kt (Unit Tests)
Tests ViewModel business logic without Android dependencies:
- ✅ Validation rules
- ✅ State transitions
- ✅ Repository calls (mocked)
- ✅ LiveData updates

## 5. Key Testing Dependencies

Already added to your `build.gradle.kts`:

```kotlin
// Unit Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.mockito:mockito-core:5.7.0")
testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
testImplementation("androidx.arch.core:core-testing:2.2.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

// Instrumented Testing
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
androidTestImplementation("androidx.navigation:navigation-testing:2.7.5")
androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")
androidTestImplementation("androidx.test:core-ktx:1.5.0")
```

## 6. Testing Best Practices

### ✅ Do's
1. **Test one thing at a time** - Each test should verify one specific behavior
2. **Use descriptive test names** - `testSignInWithEmptyEmailShowsError()`
3. **Follow AAA pattern** - Arrange, Act, Assert
4. **Mock external dependencies** - Use Mockito for repositories, APIs
5. **Use test rules** - `InstantTaskExecutorRule` for LiveData
6. **Clean up after tests** - Clear database, reset mocks

### ❌ Don'ts
1. Don't test Android framework code (it's already tested)
2. Don't make tests dependent on each other
3. Don't hardcode delays (use IdlingResource for async operations)
4. Don't test implementation details, test behavior

## 7. Common Testing Patterns

### Testing LiveData
```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

@Test
fun testLiveDataUpdate() {
    val observer = mock<Observer<AuthState>>()
    viewModel.authState.observeForever(observer)
    
    viewModel.signIn("email", "password")
    
    verify(observer).onChanged(AuthState.Loading)
}
```

### Testing Coroutines
```kotlin
@Test
fun testCoroutineExecution() = runTest {
    // Test code with coroutines
    viewModel.signIn("email", "password")
    advanceUntilIdle() // Wait for all coroutines
}
```

### Testing Navigation
```kotlin
@Test
fun testNavigationToHome() {
    val navController = TestNavHostController(context)
    scenario.onFragment { fragment ->
        Navigation.setViewNavController(fragment.requireView(), navController)
    }
    
    // Perform action that triggers navigation
    onView(withId(R.id.btnSignIn)).perform(click())
    
    // Verify destination
    assertEquals(R.id.homeFragment, navController.currentDestination?.id)
}
```

## 8. Troubleshooting

### Tests not running?
- Ensure emulator/device is connected for instrumented tests
- Sync Gradle after adding dependencies
- Check test configuration in Android Studio

### Tests failing intermittently?
- Use `IdlingResource` for async operations instead of `Thread.sleep()`
- Ensure proper cleanup in `@After` methods
- Check for race conditions in coroutines

### Can't find test results?
- Unit test results: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented test results: `app/build/reports/androidTests/connected/index.html`

## 9. Next Steps

1. **Sync Gradle** to download new dependencies
2. **Run the tests** to see them in action
3. **Customize tests** based on your specific requirements
4. **Add more test cases** as you add features
5. **Set up CI/CD** to run tests automatically

## 10. Example: Running Your First Test

1. Open Android Studio
2. Navigate to `SignInFragmentTest.kt`
3. Click the green play button next to `testSignInFragmentDisplaysCorrectly()`
4. Wait for the test to run on your emulator
5. Check the test results panel

Happy Testing! 🎉

