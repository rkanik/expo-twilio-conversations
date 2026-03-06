package expo.modules.twilioconversations

import android.os.Handler
import android.os.Looper
import com.twilio.conversations.CallbackListener
import com.twilio.conversations.ConversationsClient
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
            "onError",
            "onClient",
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
                    val loader = c.javaClass.classLoader
                    val listenerInterface =
                            loader.loadClass("com.twilio.conversations.ConversationsClientListener")
                    val listener =
                            Proxy.newProxyInstance(loader, arrayOf(listenerInterface)) {
                                    _: Any,
                                    method: Method,
                                    args: Array<out Any?>? ->
                              try {
                                when (method.name) {
                                  "onNewMessageNotification" ->
                                          mainHandler.post {
                                            sendEvent(
                                                    "onNewMessageNotification",
                                                    mapOf<String, Any?>()
                                            )
                                          }

                                  // if (args != null && args.size >= 3) {
                                  //   val conversationSid = args[0] as? String
                                  //   val messageSid = args[1] as? String
                                  //   val messageIndex = (args[2] as? Long) ?: 0L
                                  //   mainHandler.post {
                                  //     sendEvent(
                                  //             "onNewMessageNotification",
                                  //             mapOf<String, Any?>(
                                  //                     "conversationSid" to conversationSid,
                                  //                     "messageSid" to messageSid,
                                  //                     "index" to messageIndex
                                  //             )
                                  //     )
                                  //   }
                                  // }
                                  "onConnectionStateChange" ->
                                          if (args != null && args.isNotEmpty()) {
                                            val state = args[0].toString()
                                            mainHandler.post {
                                              sendEvent(
                                                      "onConnectionStateChanged",
                                                      mapOf<String, Any?>("state" to state)
                                              )
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
}
