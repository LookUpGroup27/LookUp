/**
 * Interface representing a repository for editing images.
 *
 * This interface defines the contract for operations that can be performed on images, such as
 * deleting an image. Implementations of this interface should handle the details of interacting
 * with the underlying data source (e.g., Firebase Storage).
 */
package com.github.lookupgroup27.lookup.model.image

interface EditImageRepository {

  /**
   * Deletes an image from the data source.
   *
   * @param imageUrl The URL of the image to be deleted.
   * @return A [Result] object indicating success or failure of the operation.
   *     - On success, the result contains `Unit`.
   *     - On failure, the result contains the exception that occurred.
   */
  suspend fun deleteImage(imageUrl: String): Result<Unit>
}
