package models

import com.mohiva.play.silhouette.api.{LoginInfo, Identity}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by salho on 05.08.15.
 */


case class User(id: Option[Int],
                firstname: String,
                lastname: String,
                email: String,
                providerID: String,
                providerKey: String,
                roles: Set[String] = Set("USER")) extends Identity {
  def loginInfo = new LoginInfo(providerID, providerKey)

  def toTuple() = (id, firstname, lastname, email, providerID, providerKey)

  def addRole(role: String) = copy(roles = roles + role)
}

case class SignUpInfo(firstname: String,
                      lastname: String,
                      email: String,
                      password: String)

object SignUpInfo {
  implicit val signUpInfoWrites = Json.writes[SignUpInfo]
  implicit val signUpInfoReads : Reads[SignUpInfo] = (
    (JsPath \ "firstname").read[String](minLength[String](2)) and
      (JsPath \ "lastname").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](email) and
      (JsPath \ "password").read[String]
    )(SignUpInfo.apply _)
}

case class SignInInfo(email: String, password: String, rememberMe: Option[Boolean] = None)

object SignInInfo {
  implicit val singInFormat = Json.format[SignInInfo]
}

case class UserPreview(id: Int,
                       firstname: String,
                       lastname: String,
                       email: String)


object User {
  implicit val userFormat = Json.format[User]

  def withoutRoles(t: (Option[Int], String, String, String, String, String)) =
    User(t._1, t._2, t._3, t._4, t._5, t._6, Set())

  def toTuple(u: User) = Some((u.id, u.firstname, u.lastname, u.email, u.providerID, u.providerKey))
}
