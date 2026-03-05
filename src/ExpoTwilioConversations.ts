// import { NativeModules } from "react-native";

import { requireNativeModule } from "expo-modules-core";

type MessagePayload = {
  sid: string;
  body: string;
  author: string;
  conversationSid: string;
  attributes: Record<string, unknown>;
  dateCreated: string;
  index?: number;
};

type NativeModuleType = {
  create: (token: string) => Promise<void>;
  shutdown: () => void;
  addListener: (
    event: string,
    callback: (payload: MessagePayload) => void
  ) => { remove: () => void };
};

const NativeModule = requireNativeModule<NativeModuleType>(
  "ExpoTwilioConversations"
);

export class Client {
  constructor(token: string) {
    if (NativeModule) {
      const client = NativeModule.create(token);
      console.log("client", client);
    }
    // if (NativeModule) {
    //   NativeModule.create(token)
    //     .then((client: any) => {
    //       console.log("client", client);
    //     })
    //     .catch(() => {
    //       // Creation errors are reported via promise reject in native layer
    //     });
    // }
  }

  public on(event: string, callback: (...args: unknown[]) => void): void {
    // if (!NativeModule) return;
    if (event === "messageAdded") {
      console.log("messageAdded");
      // const sub = NativeModule.addListener(
      //   "messageAdded",
      //   (payload: MessagePayload) => {
      //     callback(payload);
      //   }
      // );
      // this.subscriptions.push(sub);
    }
    // Add more events (e.g. tokenExpired, tokenAboutToExpire) when implemented in native
  }

  public shutdown(): void {
    // this.subscriptions.forEach((s) => s.remove());
    // this.subscriptions = [];
    // NativeModule?.shutdown();
    console.log("shutdown");
  }
}
