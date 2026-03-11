import ExpoModulesCore
import TwilioConversationsClient

public class ExpoTwilioConversationsModule: Module, TwilioConversationsClientDelegate {
  private var client: TwilioConversationsClient?

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

      TwilioConversationsClient.conversationsClient(
        withToken: token,
        properties: properties,
        delegate: self
      ) { result, client in
        guard let client = client, result.isSuccessful else {
          let message = result.error?.localizedDescription ?? "Failed to create TwilioConversationsClient"
          self.onTest("create failed: \(message)")
          promise.reject("E_CREATE_FAILED", message, nil)
          return
        }

        self.client = client
        promise.resolve(nil)
      }
    }

    AsyncFunction("typing") { (sid: String, promise: Promise) in
      guard let client = self.client else {
        promise.reject("E_NO_CLIENT", "Twilio client is not initialized", nil)
        return
      }

      client.conversation(withSidOrUniqueName: sid) { result, conversation in
        guard let conversation = conversation, result.isSuccessful else {
          let message = result.error?.localizedDescription ?? "Failed to get conversation"
          promise.reject("E_TYPING_FAILED", message, nil)
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
    let attributes = participant.attributes
    let attributesString: String?
    if let string = attributes?.string {
      attributesString = string
    } else if let dict = attributes?.dictionary,
              let data = try? JSONSerialization.data(withJSONObject: dict, options: []),
              let json = String(data: data, encoding: .utf8) {
      attributesString = json
    } else if let array = attributes?.array,
              let data = try? JSONSerialization.data(withJSONObject: array, options: []),
              let json = String(data: data, encoding: .utf8) {
      attributesString = json
    } else if let number = attributes?.number {
      attributesString = number.stringValue
    } else {
      attributesString = nil
    }

    self.sendEvent("onTypingStarted", [
      "sid": participant.sid as Any,
      "identity": participant.identity as Any,
      "attributes": attributesString as Any,
      "conversation_sid": conversation.sid as Any
    ])
  }

  private func onTypingEndedEvent(conversation: TCHConversation, participant: TCHParticipant) {
    let attributes = participant.attributes
    let attributesString: String?
    if let string = attributes?.string {
      attributesString = string
    } else if let dict = attributes?.dictionary,
              let data = try? JSONSerialization.data(withJSONObject: dict, options: []),
              let json = String(data: data, encoding: .utf8) {
      attributesString = json
    } else if let array = attributes?.array,
              let data = try? JSONSerialization.data(withJSONObject: array, options: []),
              let json = String(data: data, encoding: .utf8) {
      attributesString = json
    } else if let number = attributes?.number {
      attributesString = number.stringValue
    } else {
      attributesString = nil
    }

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
  }

  // MARK: - TwilioConversationsClientDelegate

  public func conversationsClient(
    _ client: TwilioConversationsClient,
    synchronizationStatusUpdated status: TCHClientSynchronizationStatus
  ) {
    let statusString: String
    switch status {
    case .none:
      statusString = "NONE"
    case .started:
      statusString = "STARTED"
    case .failed:
      statusString = "FAILED"
    case .completed:
      statusString = "COMPLETED"
    @unknown default:
      statusString = "UNKNOWN"
    }

    onClientSynchronization(statusString)
  }

  public func conversationsClient(
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

    onConnectionStateChange(stateString)
  }

  public func conversationsClientTokenExpired(_ client: TwilioConversationsClient) {
    onTokenExpiredEvent()
  }

  public func conversationsClientTokenWillExpire(_ client: TwilioConversationsClient) {
    onTokenAboutToExpireEvent()
  }

  public func conversationsClient(
    _ client: TwilioConversationsClient,
    conversation: TCHConversation,
    messageAdded message: TCHMessage
  ) {
    onMessageAddedEvent(message)
  }

  public func conversationsClient(
    _ client: TwilioConversationsClient,
    typingStartedOn conversation: TCHConversation,
    participant: TCHParticipant
  ) {
    onTypingStartedEvent(conversation: conversation, participant: participant)
  }

  public func conversationsClient(
    _ client: TwilioConversationsClient,
    typingEndedOn conversation: TCHConversation,
    participant: TCHParticipant
  ) {
    onTypingEndedEvent(conversation: conversation, participant: participant)
  }
}

