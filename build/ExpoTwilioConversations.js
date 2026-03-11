import { requireNativeModule } from "expo-modules-core";
const NativeModule = requireNativeModule("ExpoTwilioConversations");
export class Client {
    listeners = [];
    constructor(token) {
        NativeModule.create(token);
    }
    onTest(callback) {
        const listener = NativeModule.addListener("onTest", callback);
        this.listeners.push(listener);
        return listener;
    }
    onClientSynchronization(callback) {
        const listener = NativeModule.addListener("onClientSynchronization", callback);
        this.listeners.push(listener);
        return listener;
    }
    onConnectionStateChanged(callback) {
        const listener = NativeModule.addListener("onConnectionStateChanged", callback);
        this.listeners.push(listener);
        return listener;
    }
    onTokenExpired(callback) {
        const listener = NativeModule.addListener("onTokenExpired", callback);
        this.listeners.push(listener);
        return listener;
    }
    onTokenAboutToExpire(callback) {
        const listener = NativeModule.addListener("onTokenAboutToExpire", callback);
        this.listeners.push(listener);
        return listener;
    }
    onTypingStarted(callback) {
        const listener = NativeModule.addListener("onTypingStarted", callback);
        this.listeners.push(listener);
        return listener;
    }
    onTypingEnded(callback) {
        const listener = NativeModule.addListener("onTypingEnded", callback);
        this.listeners.push(listener);
        return listener;
    }
    onMessageAdded(callback) {
        const listener = NativeModule.addListener("onMessageAdded", callback);
        this.listeners.push(listener);
        return listener;
    }
    async getConversationBySid(sid) {
        return {
            typing() {
                NativeModule.typing(sid);
            },
        };
    }
    shutdown() {
        this.listeners.forEach((listener) => listener.remove());
        this.listeners = [];
        NativeModule.shutdown();
    }
}
//# sourceMappingURL=ExpoTwilioConversations.js.map