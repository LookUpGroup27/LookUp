/**
 * Interface representing a repository for managing posts.
 *
 * This interface defines the contract for operations that can be performed on posts.
 * Implementations of this interface are responsible for interacting with the underlying data source
 * (e.g., Firestore or other databases).
 */
package com.github.lookupgroup27.lookup.model.post

interface PostsRepository {

  /**
   * Initializes the repository.
   *
   * @param onSuccess Callback function invoked when initialization is successful.
   */
  fun init(onSuccess: () -> Unit)

  /**
   * Generates a new unique identifier (UID) for a post.
   *
   * @return A string representing the newly generated UID.
   */
  fun generateNewUid(): String

  /**
   * Retrieves all posts from the data source.
   *
   * This method fetches all posts available in the repository.
   *
   * @param onSuccess Callback function invoked with the list of posts when the operation succeeds.
   *   The list may be `null` if no posts are available.
   * @param onFailure Callback function invoked with an exception if the operation fails.
   */
  fun getPosts(onSuccess: (List<Post>?) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Adds a new post to the repository.
   *
   * @param post The [Post] object to be added.
   * @param onSuccess Callback function invoked when the operation is successful.
   * @param onFailure Callback function invoked with an exception if the operation fails.
   */
  fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes a post from the repository.
   *
   * @param post The unique identifier of the post to be deleted.
   * @param onSuccess Callback function invoked when the operation is successful.
   * @param onFailure Callback function invoked with an exception if the operation fails.
   */
  fun deletePost(post: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates an existing post in the repository.
   *
   * @param post The [Post] object containing the updated data.
   * @param onSuccess Callback function invoked when the operation is successful.
   * @param onFailure Callback function invoked with an exception if the operation fails.
   */
  fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates the description of an existing post in the repository.
   *
   * @param postUid The UID of the post to update.
   * @param onSuccess Callback function invoked when the operation is successful.
   * @param onFailure Callback function invoked with an exception if the operation fails.
   */
  fun updateDescription(
      postUid: String,
      newDescription: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
