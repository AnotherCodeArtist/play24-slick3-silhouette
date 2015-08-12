package controllers

import javax.inject.Inject

import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.Request
import repositories.UserRepository
import com.mohiva.play.silhouette.api.{Authorization, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models.UserPreview._

/*import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers._
import play.api.libs.functional.syntax._
import models.{SignInInfo, SignUpInfo, User}
import models.services.UserIdentityService
import play.api.Configuration
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Action
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
*/
/**
 * Created by salho on 12.08.15.
 */
class UserController @Inject()(val messagesApi: MessagesApi,
                               val env: Environment[User, JWTAuthenticator],
                               userRepository: UserRepository) extends Silhouette[User, JWTAuthenticator] {

  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  def list = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.all.map(users => Ok(Json toJson users)))

  def listWithPages(page: Int, pageSize: Int) = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.all(page,pageSize).map(users => Ok(Json toJson users)))

  def delete(id: Int) = SecuredAction(IsAdmin()
  ).async { implicit request =>
    userRepository.delete(id).map(_ => Ok(Json obj "message" -> s"User with id $id was deleted"))
  }

  def count = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.count.map(count => Ok(Json toJson count)))

}
