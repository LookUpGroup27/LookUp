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

@RunWith(MockitoJUnitRunner::class)
class PostsViewModelTest {

  private lateinit var postsRepository: PostsRepository
  private lateinit var postsViewModel: PostsViewModel
  private lateinit var firestore: FirebaseFirestore
  private lateinit var collectionReference: CollectionReference

  private val testPost = Post("1", "testUri", "testUsername", 10)

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
    assertThat(testPost.likes, `is`(10))
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
}
