import { Client as TwilioClient } from "@twilio/conversations";
export class Client {
    client;
    constructor(token) {
        this.client = new TwilioClient(token);
    }
    onTest(callback) {
        // TODO: Implement onTest
    }
    onClientSynchronization(callback) {
        // TODO: Implement onClientSynchronization
    }
    onConnectionStateChanged(callback) {
        this.client.on("connectionStateChanged", (state) => {
            callback({ state });
        });
    }
    onTokenExpired(callback) {
        this.client.on("tokenExpired", () => {
            callback("Token expired!");
        });
    }
    onTokenAboutToExpire(callback) {
        this.client.on("tokenAboutToExpire", () => {
            callback("Token about to expire!");
        });
    }
    onTypingStarted(callback) {
        this.client.on("typingStarted", (participant) => {
            callback({
                sid: participant.sid,
                identity: participant.identity ?? "",
                attributes: JSON.stringify(participant.attributes),
                conversation_sid: participant.conversation.sid,
            });
        });
    }
    onTypingEnded(callback) {
        this.client.on("typingEnded", (participant) => {
            callback({
                sid: participant.sid,
                identity: participant.identity ?? "",
                attributes: JSON.stringify(participant.attributes),
                conversation_sid: participant.conversation.sid,
            });
        });
    }
    onMessageAdded(callback) {
        this.client.on("messageAdded", (message) => {
            callback({
                body: message.body ?? "",
            });
        });
    }
    async getConversationBySid(sid) {
        return this.client.getConversationBySid(sid);
    }
    shutdown() {
        this.client.shutdown();
    }
}
//# sourceMappingURL=ExpoTwilioConversations.web.js.map