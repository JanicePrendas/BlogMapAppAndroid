package com.janice.blogmapapp

import kotlinx.serialization.Serializable

@Serializable
class Article constructor(var title: String, var description: String, var author: String, var image: String,
                          var article_date: String, var link: String, var uuid: String)
