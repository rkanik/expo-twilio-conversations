import { requireNativeModule } from "expo-modules-core";
import { Conversation, Message, Participant } from "./types";

const NativeModule = requireNativeModule("ExpoTwilioConversations");

export class Client {
  private listeners: any[] = [];

  constructor(token: string) {
    NativeModule.create(token);
  }

  public onTest(callback: (data: any) => void) {
    const listener = NativeModule.addListener("onTest", callback);
    this.listeners.push(listener);
    return listener;
  }

  public onClientSynchronization(
    callback: (event: { status: string }) => void
  ) {
    const listener = NativeModule.addListener(
      "onClientSynchronization",
      callback
    );
    this.listeners.push(listener);
    return listener;
  }

  public onConnectionStateChanged(
    callback: (event: { state: string }) => void
  ) {
    const listener = NativeModule.addListener(
      "onConnectionStateChanged",
      callback
    );
    this.listeners.push(listener);
    return listener;
  }

  public onTokenExpired(callback: (message: string) => void) {
    const listener = NativeModule.addListener("onTokenExpired", callback);
    this.listeners.push(listener);
    return listener;
  }

  public onTokenAboutToExpire(callback: (message: string) => void) {
    const listener = NativeModule.addListener("onTokenAboutToExpire", callback);
    this.listeners.push(listener);
    return listener;
  }

  public onTypingStarted(callback: (participant: Participant) => void) {
    const listener = NativeModule.addListener("onTypingStarted", callback);
    this.listeners.push(listener);
    return listener;
  }

  public onTypingEnded(callback: (participant: Participant) => void) {
    const listener = NativeModule.addListener("onTypingEnded", callback);
    this.listeners.push(listener);
    return listener;
  }

  public onMessageAdded(callback: (message: Message) => void) {
    const listener = NativeModule.addListener("onMessageAdded", callback);
    this.listeners.push(listener);
    return listener;
  }

  public async getConversationBySid(sid: string): Promise<Conversation> {
    return {
      typing() {
        NativeModule.typing(sid);
      },
    };
  }

  public shutdown() {
    this.listeners.forEach((listener) => listener.remove());
    this.listeners = [];
    NativeModule.shutdown();
  }
}
