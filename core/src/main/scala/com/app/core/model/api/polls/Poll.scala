package com.app.core.model.api.polls

case class Poll(
  id:         String,
  subject:    String,
  options:    Map[String, Int],
  multi_vote: Boolean,
  votes:      Map[String, List[String]])