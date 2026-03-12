import { Client as TwilioClient } from "@twilio/conversations";
import { Conversation, Message, Participant } from "./types";

export class Client {
  private client: TwilioClient;

  constructor(token: string) {
    this.client = new TwilioClient(token);
  }

  public onTest(callback: (data: any) => void) {
    // TODO: Implement onTest
  }

  public onClientSynchronization(
    callback: (event: { status: string }) => void
  ) {
    // TODO: Implement onClientSynchronization
  }

  public onConnectionStateChanged(
    callback: (event: { state: string }) => void
  ) {
    this.client.on("connectionStateChanged", (state) => {
      callback({ state });
    });
  }

  public onTokenExpired(callback: (message: string) => void) {
    this.client.on("tokenExpired", () => {
      callback("Token expired!");
    });
  }

  public onTokenAboutToExpire(callback: (message: string) => void) {
    this.client.on("tokenAboutToExpire", () => {
      callback("Token about to expire!");
    });
  }

  public onTypingStarted(callback: (participant: Participant) => void) {
    this.client.on("typingStarted", (participant) => {
      callback({
        sid: participant.sid,
        identity: participant.identity ?? "",
        attributes: JSON.stringify(participant.attributes),
        conversation_sid: participant.conversation.sid,
      });
    });
  }

  public onTypingEnded(callback: (participant: Participant) => void) {
    this.client.on("typingEnded", (participant) => {
      callback({
        sid: participant.sid,
        identity: participant.identity ?? "",
        attributes: JSON.stringify(participant.attributes),
        conversation_sid: participant.conversation.sid,
      });
    });
  }

  public onMessageAdded(callback: (message: Message) => void) {
    this.client.on("messageAdded", (message) => {
      callback({
        body: message.body ?? "",
      });
    });
  }

  public async getConversationBySid(sid: string): Promise<Conversation> {
    return this.client.getConversationBySid(sid);
  }

  public shutdown() {
    this.client.shutdown();
  }
}
