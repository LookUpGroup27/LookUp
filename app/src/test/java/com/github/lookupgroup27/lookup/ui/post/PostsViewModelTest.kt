package com.github.lookupgroup27.lookup.ui.post

import com.github.lookupgroup27.lookup.model.post.Post
import com.github.lookupgroup27.lookup.model.post.PostsRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

@RunWith(MockitoJUnitRunner::class)
class PostsViewModelTest {

  private lateinit var postsRepository: PostsRepository
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var firestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference

  private val testPost =
      Post(
          "1",
          "testUri",
          "testUsername",
          10,
          2.5,
          0.0,
          0.0,
          2,
          listOf("test@gmail.com", "joedoe@gmail.com"))

  @Before
  fun setUp() {
    postsRepository = mock(PostsRepository::class.java)
    postsViewModel = PostsViewModel(postsRepository)
    firestore = mock(FirebaseFirestore::class.java)
    collectionReference = mock(CollectionReference::class.java)
  }

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

  @Test
  fun `test generateNewUid calls repository`() {
    `when`(postsRepository.generateNewUid()).thenReturn("uid")
    assertThat(postsViewModel.generateNewUid(), `is`("uid"))
    verify(postsRepository).generateNewUid()
  }

  @Test
  fun `test addPost calls repository`() {
    postsViewModel.addPost(testPost)
    verify(postsRepository)
        .addPost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test getPosts calls repository`() {
    postsViewModel.getPosts()
    verify(postsRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test selectPost updates selectedTodo`() {
    postsViewModel.selectPost(testPost)
    assertThat(postsViewModel.post.value, `is`(testPost))
  }

  @Test
  fun `test getPosts updates state on success`() = runBlocking {
    val postList = listOf(testPost)
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(List<Post>) -> Unit>(0)(postList) }

    postsViewModel.getPosts()

    assertThat(postsViewModel.allPosts.first(), `is`(postList))
  }

  @Test
  fun `test getPosts calls onFailure on repository error`() = runBlocking {
    val exception = Exception("Fetch error")
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(Exception) -> Unit>(1)(exception) }

    postsViewModel.getPosts(onFailure = { error -> assertThat(error.message, `is`("Fetch error")) })

    verify(postsRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test generateNewUid returns new UID from repository`() {
    `when`(postsRepository.generateNewUid()).thenReturn("new-uid")
    val newUid = postsViewModel.generateNewUid()
    assertThat(newUid, `is`("new-uid"))
    verify(postsRepository).generateNewUid()
  }

  @Test
  fun `test getPosts with empty list updates state`() = runBlocking {
    val emptyPostList = emptyList<Post>()
    `when`(postsRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { it.getArgument<(List<Post>) -> Unit>(0)(emptyPostList) }

    postsViewModel.getPosts()

    assertThat(postsViewModel.allPosts.first(), `is`(emptyPostList))
  }

  @Test
  fun `test updatePost calls repository`() {
    postsViewModel.updatePost(testPost)
    verify(postsRepository)
        .updatePost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test updatePost success updates state`() = runBlocking {
    // Simulate success callback
    `when`(
            postsRepository.updatePost(
                org.mockito.kotlin.eq(testPost),
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()))
        .thenAnswer {
          it.getArgument<() -> Unit>(1).invoke() // Invoke onSuccess
        }

    var successCalled = false
    postsViewModel.updatePost(testPost, onSuccess = { successCalled = true })

    assertThat(successCalled, `is`(true))
    verify(postsRepository)
        .updatePost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test updatePost calls onFailure on repository error`() = runBlocking {
    // Simulate failure callback
    val exception = Exception("Update error")
    `when`(
            postsRepository.updatePost(
                org.mockito.kotlin.eq(testPost),
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()))
        .thenAnswer {
          it.getArgument<(Exception) -> Unit>(2).invoke(exception) // Invoke onFailure
        }

    var errorMessage: String? = null
    postsViewModel.updatePost(testPost, onFailure = { error -> errorMessage = error.message })

    assertThat(errorMessage, `is`("Update error"))
    verify(postsRepository)
        .updatePost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test deletePost calls repository deletePost for matching post URI`() = runBlocking {
    val postList =
        listOf(
            testPost,
            Post(
                "2",
                "otherUri",
                "anotherUsername",
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

    // Invoke deletePost with the matching URI
    postsViewModel.deletePost(
        post = "testUri",
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Verify repository's deletePost was called with the correct UID
    verify(postsRepository)
        .deletePost(org.mockito.kotlin.eq("1"), org.mockito.kotlin.any(), org.mockito.kotlin.any())

    // Assert callbacks
    assertThat(successCalled, `is`(true))
    assertThat(failureCalled, `is`(false))
  }
}
