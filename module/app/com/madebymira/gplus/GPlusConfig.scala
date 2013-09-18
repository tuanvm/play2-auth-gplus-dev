package com.madebymira.gplus

import play.api.mvc._

trait GPlusConfig {
    self: Controller =>

    val gpId: String
    val gpSecret: String
    val gpCallbackURL: String
    val gpScope: String
}