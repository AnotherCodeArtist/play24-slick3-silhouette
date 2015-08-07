package repositories

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth2Info

import scala.concurrent.Future

/**
 * Created by salho on 06.08.15.
 */
class OAuth2InfoRepositoryImpl extends DelegableAuthInfoDAO[OAuth2Info]{
  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = ???

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = ???

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = ???
}
