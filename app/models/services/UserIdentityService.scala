package models.services

import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

/**
 * Created by salho on 06.08.15.
 */
trait UserIdentityService extends IdentityService[User]{
  def save(user: User): Future[User]
}
