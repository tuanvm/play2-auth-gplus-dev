package com.madebymira.gplus

import play.api.{ Logger, Application, Plugin, Routes }
import play.api.libs._
import play.api.libs.ws.WS
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.json.JsValue

trait GPlusConnect {
    self: GPlusConfig =>

    /**
     * Gets the g plus authorize url.
     *
     * @return the g plus authorize url
     */
    def getGPlusAuthorizeUrl: String = {
        return "https://accounts.google.com/o/oauth2/auth?client_id=" + gpId + "&redirect_uri=" + gpCallbackURL + "&response_type=code&scope=" + gpScope
    }

    /**
     * Strip.
     *
     * @param quoted the quoted
     * @return the java.lang. string
     */
    def strip(quoted: String): String = {
        quoted.filter(char => char != '\"')
    }

    /**
     * Gets the g plus access token.
     *
     * @param code the code
     * @return the g plus access token
     */
    def getGPlusAccessToken(code: String): String = {
        val postBody = "code=" + code + "&client_id=" + gpId + "&client_secret=" + gpSecret + "&redirect_uri=" + gpCallbackURL + "&grant_type=authorization_code"
        val duration = Duration(10, SECONDS)
        val future: Future[play.api.libs.ws.Response] = WS.url("https://accounts.google.com/o/oauth2/token").withHeaders("Content-Type" -> "application/x-www-form-urlencoded").post(postBody)
        val response = Await.result(future, duration)
        
        val accessJson = response.json
        val accessToken = strip((accessJson \ "access_token").toString)
        return accessToken
    }

    /**
     * Gets the g plus user.
     *
     * @param accessToken the access token
     * @return the g plus user
     */
    def getGPlusUser(accessToken: String): JsValue = {
        val duration = Duration(10, SECONDS)
        val future: Future[play.api.libs.ws.Response] = WS.url("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken).get
        val response = Await.result(future, duration)
        return response.json
    }
}