package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.{User, Message}
import models.Message.messageFormat
import play.api._
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * Created by salho on 30.07.15.
 */
class MessageCtrl @Inject()(
                             val messagesApi: MessagesApi,
                             val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {


  def hello = SecuredAction.async {
    Future {
      Ok(Json toJson Message("Hello"))
    }
  }
}
