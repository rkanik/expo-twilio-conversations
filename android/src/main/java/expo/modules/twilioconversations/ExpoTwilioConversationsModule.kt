package expo.modules.twilioconversations

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoTwilioConversationsModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoTwilioConversations")

    Function("create") { token: String ->
      "client"
    }

    Function("shutdown") {}

    Events("messageAdded")

    OnDestroy {}
  }
}
