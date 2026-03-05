// import { NativeModules } from "react-native";

// const { ExpoTwilioConversations } = NativeModules;

export class Client {
  private token: string | undefined;

  constructor(token: string) {
    this.token = token;
  }

  public on(event: string, callback: (...args: any[]) => void) {
    console.log("token", this.token);
    // ExpoTwilioConversations.on(event, callback);
  }

  public shutdown() {
    this.token = undefined;
    // ExpoTwilioConversations.shutdown();
  }
}
