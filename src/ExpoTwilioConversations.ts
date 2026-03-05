import { requireNativeModule } from "expo-modules-core";

const Module = requireNativeModule("ExpoTwilioConversations");

export const create = Module.create;
export const shutdown = Module.shutdown;
export const addListener = Module.addListener;
