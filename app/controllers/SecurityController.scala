package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
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

/**
 * Created by salho on 07.08.15.
 */
class SecurityController @Inject()(val messagesApi: MessagesApi,
                                   val env: Environment[User, JWTAuthenticator],
                                   userService: UserIdentityService,
                                   authInfoRepository: AuthInfoRepository,
                                   passwordHasher: PasswordHasher,
                                   configuration: Configuration,
                                   credentialsProvider: CredentialsProvider,

                                   clock: Clock)
  extends Silhouette[User, JWTAuthenticator] {

  def signUp = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUpInfo].map { data =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
        case None =>
          val authInfo = passwordHasher.hash(data.password)
          val user = User(None,data.firstname,data.lastname,data.email,loginInfo.providerID,loginInfo.providerKey)
          for {
            user <- userService.save(user)
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(loginInfo)
            token <- env.authenticatorService.init(authenticator)
          } yield {
            env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("token" -> token))
          }
      }

    }.recoverTotal {
      case error => Future.successful(BadRequest(Json.obj("message" -> Messages("invalid.data"))))
    }
  }

  def signIn = Action.async(parse.json) { implicit request =>
    request.body.validate[SignInInfo].map { data =>
      credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(loginInfo).map {
            case authenticator if data.rememberMe.getOrElse(false) =>
              val c = configuration.underlying
              authenticator.copy(
                expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
              )
            case authenticator => authenticator
          }.flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            env.authenticatorService.init(authenticator).map { token =>
              Ok(Json.obj("token" -> token))
            }
          }
          case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }

    } recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.credentials"))))
    }
  }

  /*
   * Returns the current identity/user
   */
  def user = SecuredAction.async {
    implicit request =>
      Future { Ok(Json.toJson(request.identity)) }
  }

  /**
   * Manages the sign out action.
   */
  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, Ok)
  }

}
