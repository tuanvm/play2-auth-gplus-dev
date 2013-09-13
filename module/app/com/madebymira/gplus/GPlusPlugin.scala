package com.madebymira.gplus

import play.api.{ Logger, Application, Plugin, Routes }
import play.api.libs._
import play.api.libs.ws.WS
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.json.JsValue

/**
 * The Class FacebookPlugin.
 */
class GPlusPlugin(application: Application) extends Plugin {
    val GPLUS_ID: String = "gplus.id";
    val GPLUS_SECRET: String = "gplus.secret";
    val GPLUS_CALLBACK_URL: String = "gplus.callbackURL"
    val GPLUS_SCOPE: String = "gplus.scope"

    lazy val id: String = application.configuration.getString(GPLUS_ID).getOrElse(null);
    lazy val secret: String = application.configuration.getString(GPLUS_SECRET).getOrElse(null);
    lazy val callbackURL: String = application.configuration.getString(GPLUS_CALLBACK_URL).getOrElse(null);
    lazy val scope: String = application.configuration.getString(GPLUS_SCOPE).getOrElse(null);

    /* (non-Javadoc)
     * @see play.api.Plugin#onStart()
     */
    override def onStart() {
        val configuration = application.configuration;
        // you can now access the application.conf settings, including any custom ones you have added

        Logger.info("GPlusPlugin has started");
    }

    /**
     * Gets the login url.
     *
     * @param scope the scope
     * @return the login url
     */
    def getLoginUrl: String = {
        return "https://accounts.google.com/o/oauth2/auth?client_id=" + id + "&redirect_uri=" + callbackURL + "&response_type=code&scope=" + scope
    }

    def strip(quoted: String): String = {
        quoted.filter(char => char != '\"')
    }

    def getAccessToken(code: String): String = {
        val postBody = "code=" + code + "&client_id=" + id + "&client_secret=" + secret + "&redirect_uri=" + callbackURL + "&grant_type=authorization_code"
        val duration = Duration(10, SECONDS)
        val future: Future[play.api.libs.ws.Response] = WS.url("https://accounts.google.com/o/oauth2/token").withHeaders("Content-Type" -> "application/x-www-form-urlencoded").post(postBody)
        val response = Await.result(future, duration)

        //val body = WS.url("https://accounts.google.com/o/oauth2/token").withHeaders("Content-Type" -> "application/x-www-form-urlencoded").post(postBody)
        val accessJson = response.json
        val accessToken = strip((accessJson \ "access_token").toString)
        return accessToken
    }

    def getGPlusUser(accessToken: String): JsValue = {
        val duration = Duration(10, SECONDS)
        val future: Future[play.api.libs.ws.Response] = WS.url("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken).get
        val response = Await.result(future, duration)
        return response.json
    }
}