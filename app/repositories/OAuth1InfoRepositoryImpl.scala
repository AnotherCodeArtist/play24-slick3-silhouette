package repositories

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth1Info

import scala.concurrent.Future

/**
 * Created by salho on 06.08.15.
 */
class OAuth1InfoRepositoryImpl extends DelegableAuthInfoDAO[OAuth1Info]{
  override def find(loginInfo: LoginInfo): Future[Option[OAuth1Info]] = ???

  override def update(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???

  override def save(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = ???

  override def add(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = ???
}
