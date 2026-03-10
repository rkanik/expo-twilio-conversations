import { requireNativeModule } from "expo-modules-core";

const NativeModule = requireNativeModule("ExpoTwilioConversations");

export const create = (token: string) => NativeModule.create(token);
export const shutdown = () => {
  NativeModule.removeAllListeners("onClient");
  NativeModule.removeAllListeners("onTokenExpired");
  NativeModule.removeAllListeners("onTokenAboutToExpire");
  NativeModule.removeAllListeners("connectionStateChanged");
  NativeModule.shutdown();
};

export const onClient = (callback: (data: any) => void) => {
  return NativeModule.addListener("onClient", callback);
};

export const onTest = (callback: (data: any) => void) => {
  return NativeModule.addListener("onTest", callback);
};

export const onTokenExpired = (callback: (data: any) => void) => {
  return NativeModule.addListener("onTokenExpired", callback);
};

export const onTokenAboutToExpire = (callback: (data: any) => void) => {
  return NativeModule.addListener("onTokenAboutToExpire", callback);
};

export const onConnectionStateChanged = (callback: (data: any) => void) => {
  return NativeModule.addListener("onConnectionStateChanged", callback);
};

export const onNewMessageNotification = (callback: (data: any) => void) => {
  return NativeModule.addListener("onNewMessageNotification", callback);
};

export const onMessageAdded = (callback: (data: any) => void) => {
  return NativeModule.addListener("onMessageAdded", callback);
};

export const onError = (callback: (data: any) => void) => {
  return NativeModule.addListener("onError", callback);
};
