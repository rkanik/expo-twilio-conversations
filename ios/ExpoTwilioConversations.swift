import ExpoModulesCore
import TwilioConversationsClient

public class ExpoTwilioConversations: Module {
  private var client: TwilioConversationsClient?
  private var clientDelegate: ClientDelegate?

  public func definition() -> ModuleDefinition {
    Name("ExpoTwilioConversations")

    Events(
      "onTest",
      "onClientSynchronization",
      "onConnectionStateChanged",
      "onTokenExpired",
      "onTokenAboutToExpire",
      "onMessageAdded",
      "onTypingStarted",
      "onTypingEnded"
    )

    AsyncFunction("create") { (token: String, promise: Promise) in
      if self.client != nil {
        promise.resolve(nil)
        return
      }

      let properties = TwilioConversationsClientProperties()
      let delegate = ClientDelegate(owner: self)
      self.clientDelegate = delegate

      TwilioConversationsClient.conversationsClient(
        withToken: token,
        properties: properties,
        delegate: delegate
      ) { result, client in
        guard let client = client, result.isSuccessful else {
          let message = result.error?.localizedDescription ?? "Failed to create TwilioConversationsClient"
          self.clientDelegate = nil
          self.onTest("create failed: \(message)")
          promise.reject("E_CREATE_FAILED", message)
          return
        }

        self.client = client
        promise.resolve(nil)
      }
    }

    AsyncFunction("typing") { (sid: String, promise: Promise) in
      guard let client = self.client else {
        promise.reject("E_NO_CLIENT", "Twilio client is not initialized")
        return
      }

      client.conversation(withSidOrUniqueName: sid) { result, conversation in
        guard let conversation = conversation, result.isSuccessful else {
          let message = result.error?.localizedDescription ?? "Failed to get conversation"
          promise.reject("E_TYPING_FAILED", message)
          return
        }

        conversation.typing()
        promise.resolve(nil)
      }
    }

    Function("shutdown") {
      self.shutdown()
    }

    OnDestroy {
      self.shutdown()
    }
  }

  private func onTest(_ message: String) {
    self.sendEvent("onTest", [
      "message": message
    ])
  }

  private func onClientSynchronization(_ status: String?) {
    self.sendEvent("onClientSynchronization", [
      "status": status as Any
    ])
  }

  private func onConnectionStateChange(_ state: String) {
    self.sendEvent("onConnectionStateChanged", [
      "state": state
    ])
  }

  private func onTokenExpiredEvent() {
    self.sendEvent("onTokenExpired", [
      "message": "Token expired!"
    ])
  }

  private func onTokenAboutToExpireEvent() {
    self.sendEvent("onTokenAboutToExpire", [
      "message": "Token about to expire!"
    ])
  }

  private func onMessageAddedEvent(_ message: TCHMessage) {
    self.sendEvent("onMessageAdded", [
      "body": message.body ?? ""
    ])
  }

  private func onTypingStartedEvent(conversation: TCHConversation, participant: TCHParticipant) {
    let attributesString = Self.encodedParticipantAttributesString(participant)

    self.sendEvent("onTypingStarted", [
      "sid": participant.sid as Any,
      "identity": participant.identity as Any,
      "attributes": attributesString as Any,
      "conversation_sid": conversation.sid as Any
    ])
  }

  private func onTypingEndedEvent(conversation: TCHConversation, participant: TCHParticipant) {
    let attributesString = Self.encodedParticipantAttributesString(participant)

    self.sendEvent("onTypingEnded", [
      "sid": participant.sid as Any,
      "identity": participant.identity as Any,
      "attributes": attributesString as Any,
      "conversation_sid": conversation.sid as Any
    ])
  }

  private func shutdown() {
    client?.shutdown()
    client = nil
    clientDelegate = nil
  }

  private static func encodedParticipantAttributesString(_ participant: TCHParticipant) -> String? {
    guard let attributes = participant.attributes() else { return nil }
    if let string = attributes.string {
      return string
    }
    if let dict = attributes.dictionary,
       let data = try? JSONSerialization.data(withJSONObject: dict, options: []),
       let json = String(data: data, encoding: .utf8) {
      return json
    }
    if let array = attributes.array,
       let data = try? JSONSerialization.data(withJSONObject: array, options: []),
       let json = String(data: data, encoding: .utf8) {
      return json
    }
    if let number = attributes.number {
      return number.stringValue
    }
    return nil
  }

  private final class ClientDelegate: NSObject, TwilioConversationsClientDelegate {
    weak var owner: ExpoTwilioConversations?

    init(owner: ExpoTwilioConversations) {
      self.owner = owner
      super.init()
    }

    func conversationsClient(
      _ client: TwilioConversationsClient,
      synchronizationStatusUpdated status: TCHClientSynchronizationStatus
    ) {
      let statusString: String
      switch status {
      case .started:
        statusString = "STARTED"
      case .conversationsListCompleted:
        statusString = "CONVERSATIONS_LIST_COMPLETED"
      case .completed:
        statusString = "COMPLETED"
      case .failed:
        statusString = "FAILED"
      @unknown default:
        statusString = "UNKNOWN"
      }

      owner?.onClientSynchronization(statusString)
    }

    func conversationsClient(
      _ client: TwilioConversationsClient,
      connectionStateUpdated state: TCHClientConnectionState
    ) {
      let stateString: String
      switch state {
      case .unknown:
        stateString = "UNKNOWN"
      case .disconnected:
        stateString = "DISCONNECTED"
      case .connected:
        stateString = "CONNECTED"
      case .connecting:
        stateString = "CONNECTING"
      case .denied:
        stateString = "DENIED"
      case .error:
        stateString = "ERROR"
      case .fatalError:
        stateString = "FATAL_ERROR"
      @unknown default:
        stateString = "UNKNOWN"
      }

      owner?.onConnectionStateChange(stateString)
    }

    func conversationsClientTokenExpired(_ client: TwilioConversationsClient) {
      owner?.onTokenExpiredEvent()
    }

    func conversationsClientTokenWillExpire(_ client: TwilioConversationsClient) {
      owner?.onTokenAboutToExpireEvent()
    }

    func conversationsClient(
      _ client: TwilioConversationsClient,
      conversation: TCHConversation,
      messageAdded message: TCHMessage
    ) {
      owner?.onMessageAddedEvent(message)
    }

    func conversationsClient(
      _ client: TwilioConversationsClient,
      typingStartedOn conversation: TCHConversation,
      participant: TCHParticipant
    ) {
      owner?.onTypingStartedEvent(conversation: conversation, participant: participant)
    }

    func conversationsClient(
      _ client: TwilioConversationsClient,
      typingEndedOn conversation: TCHConversation,
      participant: TCHParticipant
    ) {
      owner?.onTypingEndedEvent(conversation: conversation, participant: participant)
    }
  }
}

