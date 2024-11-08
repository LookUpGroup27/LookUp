package com.github.lookupgroup27.lookup.model.feed

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

@RunWith(MockitoJUnitRunner::class)
class FeedViewModelTest {

  private lateinit var feedRepository: FeedRepository
  private lateinit var feedViewModel: FeedViewModel
  private lateinit var firestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference

  private val testPost = Post("1", "testUri", "testUsername", 10)

  @Before
  fun setUp() {
    feedRepository = mock(FeedRepository::class.java)
    feedViewModel = FeedViewModel(feedRepository)
    firestore = mock(FirebaseFirestore::class.java)
    collectionReference = mock(CollectionReference::class.java)
  }

  @Test
  fun `test Post data class creation`() {
    assertThat(testPost.uid, `is`("1"))
    assertThat(testPost.uri, `is`("testUri"))
    assertThat(testPost.username, `is`("testUsername"))
    assertThat(testPost.likes, `is`(10))
  }

  @Test
  fun `test getNewUid calls repository`() {
    `when`(feedRepository.generateNewUid()).thenReturn("uid")
    assertThat(feedViewModel.generateNewUid(), `is`("uid"))
    verify(feedRepository).generateNewUid()
  }

  @Test
  fun `test addPost calls repository`() {
    feedViewModel.addPost(testPost)
    verify(feedRepository)
        .addPost(
            org.mockito.kotlin.eq(testPost), org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test getPosts calls repository`() {
    feedViewModel.getPosts()
    verify(feedRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test selectPost updates selectedTodo`() {
    feedViewModel.selectPost(testPost)
    assertThat(feedViewModel.post.value, `is`(testPost))
  }

  @Test
  fun `test getPosts updates state on success`() = runBlocking {
    val postList = listOf(testPost)
    `when`(feedRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenAnswer {
      it.getArgument<(List<Post>) -> Unit>(0)(postList)
    }

    feedViewModel.getPosts()

    assertThat(feedViewModel.allPosts.first(), `is`(postList))
  }

  @Test
  fun `test getPosts calls onFailure on repository error`() = runBlocking {
    val exception = Exception("Fetch error")
    `when`(feedRepository.getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenAnswer {
      it.getArgument<(Exception) -> Unit>(1)(exception)
    }

    feedViewModel.getPosts(onFailure = { error -> assertThat(error.message, `is`("Fetch error")) })

    verify(feedRepository).getPosts(org.mockito.kotlin.any(), org.mockito.kotlin.any())
  }

  @Test
  fun `test generateNewUid fetches new ID from repository`() {
    `when`(feedRepository.generateNewUid()).thenReturn("123")
    val newUid = feedViewModel.generateNewUid()

    assertThat(newUid, `is`("123"))
    verify(feedRepository).generateNewUid()
  }
}
