// import { NativeModules } from "react-native";
// const { ExpoTwilioConversations } = NativeModules;
export class Client {
    token;
    constructor(token) {
        this.token = token;
    }
    on(event, callback) {
        console.log("token", this.token);
        // ExpoTwilioConversations.on(event, callback);
    }
    shutdown() {
        this.token = undefined;
        // ExpoTwilioConversations.shutdown();
    }
}
//# sourceMappingURL=ExpoTwilioConversations.js.map