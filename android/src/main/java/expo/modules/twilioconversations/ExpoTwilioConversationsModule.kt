package expo.modules.twilioconversations

import com.twilio.conversations.CallbackListener
import com.twilio.conversations.ConversationsClient
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoTwilioConversationsModule : Module() {

  private var client: ConversationsClient? = null

  override fun definition() = ModuleDefinition {
    Name("ExpoTwilioConversations")

    AsyncFunction("create") { token: String, promise: Promise ->
      val ctx =
              appContext.reactContext?.applicationContext
                      ?: run {
                        promise.reject("E_NO_CONTEXT", "React context not available", null)
                        return@AsyncFunction
                      }
      val properties = ConversationsClient.Properties.newBuilder().createProperties()
      ConversationsClient.create(
              ctx,
              token,
              properties,
              object : CallbackListener<ConversationsClient> {
                override fun onSuccess(c: ConversationsClient) {
                  client = c
                  sendEvent(
                          "onConnectionStateChanged",
                          mapOf<String, Any?>("connectionState" to "connected")
                  )
                  promise.resolve(null)
                }
              }
      )
    }

    Events("onMessageAdded")
    Events("onConnectionStateChanged")

    Function("shutdown") {
      client?.shutdown()
      client = null
    }

    OnDestroy {
      client?.shutdown()
      client = null
    }
  }
}
