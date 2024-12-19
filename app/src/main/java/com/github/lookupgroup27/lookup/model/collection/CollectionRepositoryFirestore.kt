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
        try {
          val posts =
              snapshot.documents.mapNotNull { post ->
                Log.d(tag, "${post.id} => ${post.data}")
                val data = post.data ?: return@mapNotNull null

                // Safely cast userMail and compare
                val postUserMail = data["userMail"] as? String ?: return@mapNotNull null
                if (postUserMail != userMail) return@mapNotNull null

                // Safely retrieve all fields with null checks and default values
                Post(
                    uid = data["uid"] as? String ?: return@mapNotNull null,
                    uri = data["uri"] as? String ?: return@mapNotNull null,
                    username = data["username"] as? String ?: return@mapNotNull null,
                    userMail = postUserMail,
                    starsCount = (data["starsCount"] as? Long)?.toInt() ?: 0,
                    averageStars = (data["averageStars"] as? Double) ?: 0.0,
                    latitude = (data["latitude"] as? Double) ?: 0.0,
                    longitude = (data["longitude"] as? Double) ?: 0.0,
                    usersNumber = (data["usersNumber"] as? Long)?.toInt() ?: 0,
                    ratedBy = (data["ratedBy"] as? List<String>) ?: emptyList(),
                    description = data["description"] as? String ?: "",
                    timestamp = (data["timestamp"] as? Long) ?: 0L)
              }
          onSuccess(posts)
        } catch (e: Exception) {
          Log.e(tag, "Error parsing posts", e)
          onFailure(e)
        }
      }
    }
  }
}
