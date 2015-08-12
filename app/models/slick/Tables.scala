package models.slick


import javax.xml.ws.BindingProvider

import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.{UserPreview, User}
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import scala.language.implicitConversions

/**
 * Created by salho on 03.08.15.
 */


class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def firstname = column[String]("FIRSTNAME")

  def lastname = column[String]("LASTNAME")

  def email = column[String]("EMAIL")

  def providerID = column[String]("PROVIDER_ID")

  def providerKey = column[String]("PROVIDER_KEY")

  def * = (id.?, firstname, lastname, email, providerID, providerKey) <>(User.withoutRoles, User.toTuple)

  def preview = (id, firstname, lastname, email) <>((UserPreview.apply _).tupled,UserPreview.unapply)
}

case class DBRole(id: Option[Int], userId: Int, role: String)

class Roles(tag: Tag) extends Table[DBRole](tag, "ROLES") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def userID = column[Int]("USER_ID")

  def role = column[String]("ROLE")

  def * = (id.?, userID, role) <>(DBRole.tupled, DBRole.unapply)
}

object Users {
  val users = TableQuery[Users]
}

case class DBPasswordInfo(
                           hasher: String,
                           password: String,
                           salt: Option[String],
                           userID: Int)

class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "PASSWORDINFO") {

  def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)

  def hasher = column[String]("HASHER")

  def password = column[String]("PASSWORD")

  def salt = column[Option[String]]("SALT")

  def userID = column[Int]("USER_ID")

  def * = (hasher, password, salt, userID) <>((DBPasswordInfo.apply _).tupled, DBPasswordInfo.unapply)


}

object DBPasswordInfo {
  def passwordInfo2db(userID: Int, pwInfo: PasswordInfo) = DBPasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt,userID)
  implicit def db2PasswordInfo(pwInfo: DBPasswordInfo): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
  implicit def dbTableElement2PasswordInfo(pwInfo: PasswordInfos#TableElementType): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
}

