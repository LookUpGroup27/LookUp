package com.github.lookupgroup27.lookup.model.collection

import android.util.Log
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * CollectionRepositoryFirestore fetches user-specific posts from Firestore. It queries the "posts"
 * collection and filters by the currently authenticated user's username. It returns a list of
 * [Post] objects, enabling the UI to display the user's personal collection.
 *
 * @param db The FirebaseFirestore instance used to access firestore data.
 * @param auth The FirebaseAuth instance used for user authentication.
 */
class CollectionRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CollectionRepository {

  private val collection = db.collection("posts")
  private val tag = "CollectionRepositoryFirestore"

  /**
   * Initializes the repository by checking the authentication state of the user. If the user is
   * authenticated, the provided [onSuccess] callback is called.
   *
   * @param onSuccess A callback to execute when the user is authenticated.
   */
  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  /**
   * Retrieves a list of image URLs associated with the authenticated user's email from Firebase
   * Storage. If the user is not authenticated or if an error occurs, an empty list is returned.
   *
   * @return A list of image URLs associated with the user's email.
   */
  override fun getUserPosts(onSuccess: (List<Post>?) -> Unit, onFailure: (Exception) -> Unit) {

    val userMail = auth.currentUser?.email
    if (userMail == null) {
      onFailure(Exception("User is not authenticated."))
      return
    }

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
                  if (data["userMail"] as String == userMail) {
                    Post(
                        data["uid"] as String,
                        data["uri"] as String,
                        data["username"] as String,
                        data["userMail"] as String,
                        (data["starsCount"] as? Long)?.toInt() ?: 0,
                        (data["averageStars"] as? Double) ?: 0.0,
                        data["latitude"] as Double,
                        data["longitude"] as Double,
                        (data["usersNumber"] as? Long)?.toInt() ?: 0,
                        data["ratedBy"] as? List<String> ?: emptyList(),
                        data["description"] as? String ?: "",
                        (data["timestamp"] as? Long) ?: 0L)
                  } else {
                    null
                  }
                }
                .filterNotNull()
        onSuccess(posts)
      }
    }
  }
}
