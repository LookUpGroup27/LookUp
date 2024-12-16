package com.github.lookupgroup27.lookup.ui.post

import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * Unit tests for the `PostsViewModel` class.
 *
 * This test suite ensures that `PostsViewModel` correctly interacts with the `PostsRepository` and
 * updates its state (`allPosts`, `post`) appropriately. The tests validate the following:
 * - Proper invocation of repository methods such as `addPost`, `getPosts`, `deletePost`, etc.
 * - Accurate state management in the ViewModel.
 * - Handling of success and failure callbacks from the repository.
 * - Correct mapping and updating of posts within the ViewModel.
 */
@RunWith(MockitoJUnitRunner::class)
class PostsViewModelTest {

  /** Mocked `PostsRepository` instance to simulate the data layer. */
  private lateinit var postsRepository: PostsRepository

  /** The ViewModel under test. */
  private lateinit var postsViewModel: PostsViewModel

  /** Mocked `FirebaseFirestore` and `CollectionReference` to simulate Firestore interactions. */
  private lateinit var firestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference

  /** A test post used in multiple test cases. */
  private val testPost =
      Post(
          "1",
          "testUri",
          "testUsername",
          "testMail",
          10,
          2.5,
          0.0,
          0.0,
          2,
          listOf("test@gmail.com", "joedoe@gmail.com"))

  /**
   * Sets up the test environment before each test.
   * - Mocks the repository and Firestore dependencies.
   * - Initializes the `PostsViewModel` with the mocked repository.
   */
  @Before
  fun setUp() {
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    firestore = mock(FirebaseFirestore::class.java)
    collectionReference = mock(CollectionReference::class.java)
  }

  /**
   * Tests the integrity of the `Post` data class.
   *
   * Ensures that the `Post` object is correctly constructed with the expected field values.
   */
  @Test
  fun `test Post data class creation`() {
    assertThat(testPost.uid, `is`("1"))
    assertThat(testPost.uri, `is`("testUri"))
    assertThat(testPost.username, `is`("testUsername"))
    assertThat(testPost.starsCount, `is`(10))
    assertThat(testPost.averageStars, `is`(2.5))
    assertThat(testPost.latitude, `is`(0.0))
    assertThat(testPost.longitude, `is`(0.0))
    assertThat(testPost.usersNumber, `is`(2))
    assertThat(testPost.ratedBy, `is`(listOf("test@gmail.com", "joedoe@gmail.com")))
  }

  /** Ensures that `generateNewUid` calls the repository's corresponding method. */
  @Test
  fun `test generateNewUid calls repository`() {
    `when`(postsRepository.generateNewUid()).thenReturn("uid")
    assertThat(postsViewModel.generateNewUid(), `is`("uid"))
    verify(postsRepository).generateNewUid()
  }

  /** Ensures that `addPost` calls the repository's `addPost` method with the correct parameters. */
  @Test
  fun `test addPost calls repository`() {
    postsViewModel.addPost(testPost)
    verify(postsRepository)
        .addPost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  /** Ensures that `getPosts` invokes the repository's `getPosts` method. */
  @Test
  fun `test getPosts calls repository`() {
    postsViewModel.getPosts()
    verify(postsRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  /** Verifies that `selectPost` updates the `post` property of the ViewModel. */
  @Test
  fun `test selectPost updates selectedTodo`() {
    postsViewModel.selectPost(testPost)
    assertThat(postsViewModel.post.value, `is`(testPost))
  }

  /** Ensures that `getPosts` updates the ViewModel state (`allPosts`) when successful. */
  @Test
  fun `test getPosts updates state on success`() = runBlocking {
    val postList = listOf(testPost)
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(List<Post>) -> Unit>(0)(postList) }

    postsViewModel.getPosts()

    assertThat(postsViewModel.allPosts.first(), `is`(postList))
  }

  /** Verifies that `getPosts` invokes the failure callback if the repository operation fails. */
  @Test
  fun `test getPosts calls onFailure on repository error`() = runBlocking {
    val exception = Exception("Fetch error")
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(Exception) -> Unit>(1)(exception) }

    postsViewModel.getPosts(onFailure = { error -> assertThat(error.message, `is`("Fetch error")) })

    verify(postsRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  /**
   * Tests that `deletePost` invokes the repository's `deletePost` method for the matching post URI.
   * - Populates the ViewModel with a list of posts.
   * - Calls `deletePost` with a specific UID.
   * - Verifies that the repository's `deletePost` method was invoked with the correct UID.
   */
  @Test
  fun `test deletePost calls repository deletePost for matching post uid`() = runBlocking {
    val postList =
        listOf(
            testPost,
            Post(
                "2",
                "otherUri",
                "anotherUsername",
                "testMail",
                5,
                4.0,
                10.0,
                20.0,
                1,
                listOf("another@gmail.com")))

    // Mock repository behavior to provide post list
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(List<Post>) -> Unit>(0)(postList) }
    postsViewModel.getPosts() // Populate ViewModel with posts

    var successCalled = false
    var failureCalled = false

    // Mock the repository's deletePost method to simulate success
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess() // Simulate success callback
          null
        }
        .whenever(postsRepository)
        .deletePost(org.mockito.kotlin.eq("1"), org.mockito.kotlin.any(), org.mockito.kotlin.any())

    // Invoke deletePost with the matching UID
    postsViewModel.deletePost(
        postUid = "1", onSuccess = { successCalled = true }, onFailure = { failureCalled = true })

    // Verify repository's deletePost was called with the correct UID
    verify(postsRepository)
        .deletePost(org.mockito.kotlin.eq("1"), org.mockito.kotlin.any(), org.mockito.kotlin.any())

    // Assert callbacks
    assertThat(successCalled, `is`(true))
    assertThat(failureCalled, `is`(false))
  }

  @Test
  fun `test updateDescription calls repository with correct parameters`() {
    val postUid = "1"
    val newDescription = "Updated description"
    var successCalled = false
    var failureCalled = false

    // Mock repository behavior for success
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as () -> Unit
          onSuccess() // Simulate success callback
          null
        }
        .whenever(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    // Call the method
    postsViewModel.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Verify repository method was called with correct parameters
    verify(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    // Assert callbacks
    assert(successCalled) { "onSuccess callback was not called" }
    assert(!failureCalled) { "onFailure callback should not have been called" }
  }

  @Test
  fun `test updateDescription calls onFailure on repository error`() {
    val postUid = "1"
    val newDescription = "Updated description"
    val exception = Exception("Update failed")
    var successCalled = false
    var failureCalled = false

    // Mock repository behavior for failure
    doAnswer { invocation ->
          val onFailure = invocation.arguments[3] as (Exception) -> Unit
          onFailure(exception) // Simulate failure callback
          null
        }
        .whenever(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    // Call the method
    postsViewModel.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { successCalled = true },
        onFailure = { error ->
          failureCalled = true
          assert(error.message == "Update failed") { "Error message mismatch" }
        })

    // Verify repository method was called with correct parameters
    verify(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    // Assert callbacks
    assert(!successCalled) { "onSuccess callback should not have been called" }
    assert(failureCalled) { "onFailure callback was not called" }
  }

  @Test
  fun `test updateDescription calls onSuccess after repository method completes`() {
    val postUid = "validUid"
    val newDescription = "Valid description"
    var callbackTriggered = false

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as () -> Unit
          onSuccess() // Simulate success callback
          assert(callbackTriggered) { "onSuccess should be called after repository method" }
          null
        }
        .whenever(postsRepository)
        .updateDescription(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    postsViewModel.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { callbackTriggered = true },
        onFailure = { fail("onFailure should not be called") })

    verify(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())
  }

  @Test
  fun `test updateDescription handles exceptions thrown by repository`() {
    val postUid = "validUid"
    val newDescription = "Valid description"

    doThrow(RuntimeException("Repository exception"))
        .whenever(postsRepository)
        .updateDescription(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())

    var failureTriggered = false

    postsViewModel.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = { fail("onSuccess should not be called") },
        onFailure = { exception ->
          failureTriggered = true
          assert(exception.message == "Repository exception") { "Unexpected exception message" }
        })

    assert(failureTriggered) { "onFailure should be triggered when repository throws an exception" }
  }

  @Test
  fun `test updateDescription calls repository with exact arguments`() {
    val postUid = "validUid"
    val newDescription = "Valid description"

    postsViewModel.updateDescription(
        postUid = postUid,
        newDescription = newDescription,
        onSuccess = {},
        onFailure = { fail("onFailure should not be called") })

    verify(postsRepository)
        .updateDescription(
            org.mockito.kotlin.eq(postUid),
            org.mockito.kotlin.eq(newDescription),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any())
  }
}
