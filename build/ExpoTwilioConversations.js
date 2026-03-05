// import { NativeModules } from "react-native";
import { requireNativeModule } from "expo-modules-core";
const NativeModule = requireNativeModule("ExpoTwilioConversations");
export class Client {
    constructor(token) {
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
    on(event, callback) {
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
    shutdown() {
        // this.subscriptions.forEach((s) => s.remove());
        // this.subscriptions = [];
        // NativeModule?.shutdown();
        console.log("shutdown");
    }
}
//# sourceMappingURL=ExpoTwilioConversations.js.map