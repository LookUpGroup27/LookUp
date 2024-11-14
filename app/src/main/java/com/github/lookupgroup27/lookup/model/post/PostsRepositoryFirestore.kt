package com.github.lookupgroup27.lookup.model.post

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostsRepositoryFirestore(private val db: FirebaseFirestore) : PostsRepository {

  private val auth = FirebaseAuth.getInstance()
  private val collection = db.collection("posts")
  private val tag = "FeedRepositoryFirestore"

  override fun init(onSuccess: () -> Unit) {
    auth.addAuthStateListener { auth ->
      if (auth.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun generateNewUid(): String {
    return collection.document().id
  }

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
                      data["ratedBy"] as? List<String> ?: emptyList())
                }
                .filterNotNull()
        onSuccess(posts)
      }
    }
  }

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

  override fun deletePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    TODO("Not yet implemented")
  }

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
