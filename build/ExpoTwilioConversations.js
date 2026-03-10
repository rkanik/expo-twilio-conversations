import { requireNativeModule } from "expo-modules-core";
const NativeModule = requireNativeModule("ExpoTwilioConversations");
export const create = (token) => NativeModule.create(token);
export const shutdown = () => {
    NativeModule.removeAllListeners("onClient");
    NativeModule.removeAllListeners("onTokenExpired");
    NativeModule.removeAllListeners("onTokenAboutToExpire");
    NativeModule.removeAllListeners("connectionStateChanged");
    NativeModule.shutdown();
};
export const onClient = (callback) => {
    return NativeModule.addListener("onClient", callback);
};
export const onTest = (callback) => {
    return NativeModule.addListener("onTest", callback);
};
export const onTokenExpired = (callback) => {
    return NativeModule.addListener("onTokenExpired", callback);
};
export const onTokenAboutToExpire = (callback) => {
    return NativeModule.addListener("onTokenAboutToExpire", callback);
};
export const onConnectionStateChanged = (callback) => {
    return NativeModule.addListener("onConnectionStateChanged", callback);
};
export const onNewMessageNotification = (callback) => {
    return NativeModule.addListener("onNewMessageNotification", callback);
};
export const onMessageAdded = (callback) => {
    return NativeModule.addListener("onMessageAdded", callback);
};
export const onError = (callback) => {
    return NativeModule.addListener("onError", callback);
};
//# sourceMappingURL=ExpoTwilioConversations.js.map