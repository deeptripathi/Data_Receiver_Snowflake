package Handler

import models.AndroidRequest

class HandlerFactory {

  def initializeHandler(androidRequest: AndroidRequest): Handler = {
    new DrmInfoHandler(androidRequest)
  }
}
