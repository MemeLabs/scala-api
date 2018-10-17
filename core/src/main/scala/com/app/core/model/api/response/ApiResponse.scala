package com.app.core.model.api.response

import com.app.core.model.api.ApiMeta

/**
 *
 */
case class ApiResponse[T, K](
  response: T,
  request:  Option[K]       = None,
  meta:     Option[ApiMeta] = None)