/**
 * Implementation of the [PostsRepository] interface using Firebase Firestore as the data source.
 *
 * This class provides methods to manage posts in a Firestore collection, including creating,
 * reading, updating, and deleting posts. It interacts with the Firestore database and Firebase
 * Authentication for managing user-related actions.
 *
 * @property db The [FirebaseFirestore] instance used to interact with the Firestore database.
 */
package com.github.lookupgroup27.lookup.model.post

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostsRepositoryFirestore(private val db: FirebaseFirestore) : PostsRepository {

  private val auth = FirebaseAuth.getInstance()
  private val collection = db.collection("posts")
  private val tag = "FeedRepositoryFirestore"

  /**
   * Initializes the repository by adding an authentication state listener.
   *
   * This ensures that the repository operations are only accessible when a user is logged in.
   */
  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener { auth ->
      if (auth.currentUser != null) {
        onSuccess()
      }
    }
  }

  /**
   * Generates a new unique identifier (UID) for a post.
   *
   * This UID is generated by requesting a new document ID from the Firestore collection.
   */
  override fun generateNewUid(): String {
    return collection.document().id
  }

  /**
   * Retrieves all posts from the Firestore collection.
   *
   * This method listens for real-time updates to the collection and parses the retrieved data into
   * a list of [Post] objects.
   */
  override fun getPosts(onSuccess: (List<Post>?) -> Unit, onFailure: (Exception) -> Unit) {
    collection.addSnapshotListener { snapshot, exception ->
      if (exception != null) {
        Log.e(tag, "Error getting posts...", exception)
        onFailure(exception)
        return@addSnapshotListener
      }
      if (snapshot != null) {
        val posts =
            snapshot.documents
                .map { post ->
                  Log.d(tag, "${post.id} => ${post.data}")
                  val data = post.data ?: return@map null
                  Post(
                      data["uid"] as String,
                      data["uri"] as String,
                      data["username"] as String,
                      (data["starsCount"] as? Long)?.toInt() ?: 0,
                      (data["averageStars"] as? Double) ?: 0.0,
                      data["latitude"] as Double,
                      data["longitude"] as Double,
                      (data["usersNumber"] as? Long)?.toInt() ?: 0,
                      data["ratedBy"] as? List<String> ?: emptyList(),
                      (data["timestamp"] as? Long) ?: 0L)
                }
                .filterNotNull()
        onSuccess(posts)
      }
    }
  }

  /** Adds a new post to the Firestore collection. */
  override fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    collection
        .document(post.uid)
        .set(post)
        .addOnSuccessListener {
          Log.d(tag, "Post added successfully")
          onSuccess()
        }
        .addOnFailureListener {
          Log.e(tag, "Error adding post", it)
          onFailure(it)
        }
  }

  /** Deletes a post from the Firestore collection by its UID. */
  override fun deletePost(postUid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    collection
        .document(postUid)
        .delete()
        .addOnSuccessListener {
          Log.d(tag, "Post deleted successfully")
          onSuccess()
        }
        .addOnFailureListener {
          Log.e(tag, "Error deleting post", it)
          onFailure(it)
        }
  }

  /** Updates an existing post in the Firestore collection. */
  override fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    collection
        .document(post.uid)
        .set(post)
        .addOnSuccessListener {
          Log.d(tag, "Post updated successfully")
          onSuccess()
        }
        .addOnFailureListener {
          Log.e(tag, "Error updating post", it)
          onFailure(it)
        }
  }
}
