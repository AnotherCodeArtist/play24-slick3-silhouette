package repositories

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.UserPreview

import scala.concurrent.Future

/**
 * Created by salho on 05.08.15.
 */

trait UserRepository {
    def findByEmail(email:String): Future[Option[User]]
    def save(user: User): Future[User]
    def count: Future[Int]
    def find(id: Int): Future[Option[User]]
    def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]]
    def all: Future[Seq[UserPreview]]
}
