package repositories


import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.slick.{DBPasswordInfo, PasswordInfos, Users}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future


/**
 * Created by salho on 06.08.15.
 */
class PasswordInfoSlickImpl @Inject()(userRepository: UserRepository) extends DelegableAuthInfoDAO[PasswordInfo] with HasDatabaseConfig[JdbcProfile] {

  import models.slick.DBPasswordInfo._
  import scala.concurrent.ExecutionContext.Implicits.global

  val users = TableQuery[Users]
  val pwInfos = TableQuery[PasswordInfos]
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    userRepository.findByLoginInfo(loginInfo)
      .flatMap {
      case None => Future.successful(None)
      case Some(user) => db.run(pwInfos.filter(_.userID === user.id).result.headOption)
        .flatMap {
        case None => Future.successful(None)
        case Some(pwInfo) => Future.successful(Some(db2PasswordInfo(pwInfo)))
      }
    }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    userRepository.findByLoginInfo(loginInfo).flatMap {
      case Some(user) => db.run(pwInfos.filter(_.userID === user.id).update(passwordInfo2db(user.id.get,authInfo)))
        .flatMap( _ => Future.successful(authInfo))
    }

  override def remove(loginInfo: LoginInfo): Future[Unit] =
    userRepository.findByLoginInfo(loginInfo)
    .flatMap { case Some(user) => db.run(pwInfos.filter(_.userID === user.id).delete)
      .flatMap(_ => Future.successful({}) )}

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    userRepository.findByLoginInfo(loginInfo).flatMap {
      case Some(user) => db.run(pwInfos.filter(_.userID === user.id).result.headOption).flatMap {
        case Some(pwInfo) => update(loginInfo,authInfo)
        case None => add(loginInfo,authInfo)
      }
    }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    userRepository.findByLoginInfo(loginInfo)
      .flatMap { case Some(user) => db.run(pwInfos+=passwordInfo2db(user.id.get,authInfo))
      .flatMap(_ => Future.successful(authInfo))
    }
}
