package expo.modules.twilioconversations

import android.os.Handler
import android.os.Looper
import com.twilio.conversations.CallbackListener
import com.twilio.conversations.Conversation
import com.twilio.conversations.ConversationListener
import com.twilio.conversations.ConversationsClient
import com.twilio.conversations.Message
import com.twilio.conversations.Participant
import com.twilio.util.ErrorInfo
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ExpoTwilioConversationsModule : Module() {

  private var client: ConversationsClient? = null
  private val mainHandler = Handler(Looper.getMainLooper())
  private val conversationListenersAttached = mutableSetOf<String>()

  override fun definition() = ModuleDefinition {
    Name("ExpoTwilioConversations")

    Events(
            "onTest",
            "onClientSynchronization",
            "onConnectionStateChanged",
            "onTokenExpired",
            "onTokenAboutToExpire",
            "onMessageAdded",
            "onTypingStarted",
            "onTypingEnded",
    )

    AsyncFunction("create") { token: String, promise: Promise ->
      if (client != null) {
        promise.resolve(null)
        return@AsyncFunction
      }
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
                                      module.onClientSynchronization(status)
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
                                  "onConnectionStateChange" -> {
                                    if (args != null && args.isNotEmpty()) {
                                      val state = args[0].toString()
                                      module.onConnectionStateChange(state)
                                    }
                                  }
                                  "onTokenExpired" -> module.onTokenExpired()
                                  "onTokenAboutToExpire" -> module.onTokenAboutToExpire()
                                }
                              } catch (_: Exception) {
                                onTest("Error level 3")
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
                      onTest("addListener failed: $msg, $name")
                    }
                  } catch (_: Exception) {
                    onTest("Error level 5")
                  }
                  promise.resolve(null)
                }
              }
      )
    }

    AsyncFunction("typing") { sid: String, promise: Promise ->
      val c = client
      if (c == null) {
        promise.reject("E_NO_CLIENT", "Conversations client not initialized", null)
        return@AsyncFunction
      }
      c.getConversation(
              sid,
              object : CallbackListener<Conversation> {
                override fun onSuccess(conversation: Conversation) {
                  conversation.typing()
                  promise.resolve(null)
                }
                override fun onError(error: ErrorInfo) {
                  promise.reject("E_TYPING_FAILED", error.message, null)
                }
              }
      )
    }

    Function("shutdown") { shutdown() }
    OnDestroy { shutdown() }
  }

  private fun onTest(message: String) {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onTest",
            mapOf<String, Any?>(
                    "message" to message,
            )
    )
  }

  private fun onClientSynchronization(status: String?) {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onClientSynchronization",
            mapOf<String, Any?>(
                    "status" to status,
            )
    )
  }

  private fun onConnectionStateChange(state: String) {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onConnectionStateChanged",
            mapOf<String, Any?>(
                    "state" to state,
            )
    )
  }

  private fun onTokenExpired() {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onTokenExpired",
            mapOf<String, Any?>(
                    "message" to "Token expired!",
            )
    )
  }

  private fun onTokenAboutToExpire() {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onTokenAboutToExpire",
            mapOf<String, Any?>(
                    "message" to "Token about to expire!",
            )
    )
  }

  private fun onMessageAdded(message: Message) {
    this@ExpoTwilioConversationsModule.sendEvent(
            "onMessageAdded",
            mapOf<String, Any?>(
                    "body" to message.body,
            )
    )
  }

  private fun onTypingStarted(conversation: Conversation, participant: Participant) {
    val attributes = participant.attributes
    this@ExpoTwilioConversationsModule.sendEvent(
            "onTypingStarted",
            mapOf(
                    "sid" to participant.sid,
                    "identity" to participant.identity,
                    "attributes" to attributes?.toString(),
                    "conversation_sid" to conversation.sid,
            )
    )
  }

  private fun onTypingEnded(conversation: Conversation, participant: Participant) {
    val attributes = participant.attributes
    this@ExpoTwilioConversationsModule.sendEvent(
            "onTypingEnded",
            mapOf<String, Any?>(
                    "sid" to participant.sid,
                    "identity" to participant.identity,
                    "attributes" to attributes?.toString(),
                    "conversation_sid" to conversation.sid,
            )
    )
  }

  private fun shutdown() {
    conversationListenersAttached.clear()
    client?.shutdown()
    client = null
  }

  private fun attachConversationListener(conversation: Conversation) {
    val sid = conversation.sid ?: return
    synchronized(conversationListenersAttached) {
      if (sid in conversationListenersAttached) return
      conversationListenersAttached.add(sid)
    }
    val module = this@ExpoTwilioConversationsModule
    conversation.addListener(
            object : ConversationListener {
              override fun onMessageAdded(message: Message) {
                module.onMessageAdded(message)
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
              override fun onTypingStarted(conversation: Conversation, participant: Participant) {
                module.onTypingStarted(conversation, participant)
              }
              override fun onTypingEnded(conversation: Conversation, participant: Participant) {
                module.onTypingEnded(conversation, participant)
              }
            }
    )
  }
}
