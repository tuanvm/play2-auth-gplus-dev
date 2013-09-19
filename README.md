This is your new Play 2.1 application
=====================================

This file will be packaged with your application, when using `play dist`.

How to use this module
1, run: play publish-local => to public this module to local

2, add "play2-auth-gplus-module" % "play2-auth-gplus-module_2.10" % "1.0-SNAPSHOT" to Build.scala in your project (with 2.10 is your play version => re-check your play version if needed)

3, create your gplus app, config callbackURL to your callback URL (example: http://localhost:9000/callback/gp)

4, add these lines to application.conf (and test.conf if needed), change these value properly
gplus.id=your app id
gplus.secret=your app secret
gplus.callbackURL="http://localhost:9000/callback/gp"
gplus.scope=(for example:  """https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&state=%2Fprofile&""")

5, create social config trait (must extends Controller)
trait SocialConfig extends Controller with GPlusConfig {
    //GPlus configuration
    val gpId: String = Play.current.configuration.getString("gplus.id").getOrElse("")
    val gpSecret: String = Play.current.configuration.getString("gplus.secret").getOrElse("")
    val gpCallbackURL: String = Play.current.configuration.getString("gplus.callbackURL").getOrElse("")
    val gpScope: String = Play.current.configuration.getString("gplus.scope").getOrElse("")
}

6, let your controller which handle login/register with gplus connect extends SocialConfig, and with GPlusConnect (because SocialConfig extends Controller, so you no need to extends Controller anymore)
object SocialConnect extends SocialConfig with GPlusConnect{...}


7, click login/register with gplus button, redirect user to Redirect(getGPlusAuthorizeUrl)

8, implement a callback function to handle callbackURL, gplus will return a code or error (example: user denied), exchange code to get access token
 val accessToken = getGPlusAccessToken(code)

9. use this access token to get user info
val guser = getGPlusUser(accessToken) will return JsValue

10. do your business logic and logged user in
