package expo.modules.twilioconversations

import android.os.Handler
import android.os.Looper
import com.twilio.conversations.CallbackListener
import com.twilio.conversations.Conversation
import com.twilio.conversations.ConversationListener
import com.twilio.conversations.ConversationsClient
import com.twilio.conversations.Message
import com.twilio.conversations.Participant
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ExpoTwilioConversationsModule : Module() {

  private var client: ConversationsClient? = null
  private val mainHandler = Handler(Looper.getMainLooper())

  override fun definition() = ModuleDefinition {
    Name("ExpoTwilioConversations")

    Events(
            "onTest",
            "onError",
            "onClient",
            "onMessageAdded",
            "onTokenExpired",
            "onTokenAboutToExpire",
            "onConnectionStateChanged",
            "onNewMessageNotification"
    )

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
                  try {
                    client = c
                    mainHandler.post {
                      sendEvent("onClient", mapOf<String, Any?>("client" to "client created"))
                    }

                    val myIdentity = c.myIdentity
                    mainHandler.post {
                      sendEvent("onTest", mapOf<String, Any?>("myIdentity22" to myIdentity))
                    }

                    val loader = c.javaClass.classLoader
                    val listenerInterface =
                            loader.loadClass("com.twilio.conversations.ConversationsClientListener")
                    val listener =
                            Proxy.newProxyInstance(loader, arrayOf(listenerInterface)) {
                                    _: Any,
                                    method: Method,
                                    args: Array<out Any?>? ->
                              try {
                                val module = this@ExpoTwilioConversationsModule
                                when (method.name) {
                                  "onClientSynchronization" -> {
                                    if (args != null && args.isNotEmpty()) {
                                      val status = args[0]?.toString()
                                      if (status == "COMPLETED") {
                                        try {
                                          c.myConversations.forEach { conv ->
                                            module.attachConversationListener(conv)
                                          }
                                        } catch (_: Exception) {}
                                      }
                                    }
                                  }
                                  "onConversationAdded" -> {
                                    if (args != null && args.isNotEmpty()) {
                                      val conv = args[0] as? Conversation
                                      if (conv != null) {
                                        module.attachConversationListener(conv)
                                      }
                                    }
                                  }
                                  "onNewMessageNotification" ->
                                          mainHandler.post {
                                            sendEvent(
                                                    "onNewMessageNotification",
                                                    mapOf<String, Any?>()
                                            )
                                          }
                                  "onConnectionStateChange" -> {
                                    if (args != null && args.isNotEmpty()) {
                                      val state = args[0].toString()
                                      mainHandler.post {
                                        sendEvent(
                                                "onConnectionStateChanged",
                                                mapOf<String, Any?>("state" to state)
                                        )
                                      }
                                    }
                                  }
                                  "onTokenExpired" ->
                                          mainHandler.post {
                                            sendEvent("onTokenExpired", mapOf<String, Any?>())
                                          }
                                  "onTokenAboutToExpire" ->
                                          mainHandler.post {
                                            sendEvent("onTokenAboutToExpire", mapOf<String, Any?>())
                                          }
                                }
                              } catch (_: Exception) {
                                mainHandler.post {
                                  sendEvent(
                                          "onError",
                                          mapOf<String, Any?>(
                                                  "error" to "Error level 3",
                                                  "message" to "Error message"
                                          )
                                  )
                                }
                              }
                              // Return correct default for primitive return types to avoid
                              // NullPointerException
                              val returnType = method.returnType
                              when {
                                returnType == Void.TYPE -> null
                                returnType == java.lang.Integer.TYPE -> 0
                                returnType == java.lang.Long.TYPE -> 0L
                                returnType == java.lang.Boolean.TYPE -> false
                                returnType == java.lang.Byte.TYPE -> 0.toByte()
                                returnType == java.lang.Short.TYPE -> 0.toShort()
                                returnType == java.lang.Character.TYPE -> 0.toChar()
                                returnType == java.lang.Float.TYPE -> 0f
                                returnType == java.lang.Double.TYPE -> 0.0
                                else -> null
                              }
                            }
                    try {
                      c.javaClass.getMethod("addListener", listenerInterface).invoke(c, listener)
                    } catch (e: Exception) {
                      val cause = (e as? java.lang.reflect.InvocationTargetException)?.cause ?: e
                      val msg = cause.message ?: e.toString()
                      val name = cause.javaClass.simpleName
                      mainHandler.post {
                        sendEvent(
                                "onError",
                                mapOf<String, Any?>(
                                        "error" to "addListener failed",
                                        "message" to msg,
                                        "exception" to name
                                )
                        )
                      }
                    }
                  } catch (_: Exception) {
                    mainHandler.post {
                      sendEvent(
                              "onError",
                              mapOf<String, Any?>(
                                      "error" to "Error level 5",
                                      "message" to "Error message 5"
                              )
                      )
                    }
                  }
                  promise.resolve(null)
                }
              }
      )
    }

    Function("shutdown") {
      client?.shutdown()
      client = null
    }

    OnDestroy {
      client?.shutdown()
      client = null
    }
  }

  /**
   * Attach a Twilio ConversationListener that forwards onMessageAdded to JS as "onMessageAdded".
   */
  private fun attachConversationListener(conversation: Conversation) {
    conversation.addListener(
            object : ConversationListener {
              override fun onMessageAdded(message: Message) {
                mainHandler.post {
                  sendEvent(
                          "onMessageAdded",
                          mapOf<String, Any?>(
                                  "body" to message.body,
                          )
                  )
                }
              }
              override fun onMessageDeleted(message: Message) {}
              override fun onMessageUpdated(message: Message, reason: Message.UpdateReason) {}
              override fun onParticipantAdded(participant: Participant) {}
              override fun onParticipantDeleted(participant: Participant) {}
              override fun onParticipantUpdated(
                      participant: Participant,
                      reason: Participant.UpdateReason
              ) {}
              override fun onSynchronizationChanged(conversation: Conversation) {}
              override fun onTypingStarted(conversation: Conversation, participant: Participant) {}
              override fun onTypingEnded(conversation: Conversation, participant: Participant) {}
            }
    )
  }
}
