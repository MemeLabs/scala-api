package com.app.core.model.api.polls

case class CreatePoll(subject: String, options: List[String], multi_vote: Boolean)
