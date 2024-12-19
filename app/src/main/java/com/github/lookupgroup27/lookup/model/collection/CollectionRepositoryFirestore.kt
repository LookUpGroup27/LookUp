package com.github.lookupgroup27.lookup.model.collection

import android.util.Log
import com.github.lookupgroup27.lookup.model.post.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CollectionRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CollectionRepository {

  private val collection = db.collection("posts")
  private val tag = "CollectionRepositoryFirestore"

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

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
