"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const withAndroid_1 = require("./withAndroid");
const withIos_1 = require("./withIos");
const withExpoTwilioConversations = (config, props = {}) => {
    // Apply Android configurations
    config = (0, withAndroid_1.withAndroidConfiguration)(config, props);
    // Apply iOS configurations
    config = (0, withIos_1.withIosConfiguration)(config, props);
    return config;
};
exports.default = withExpoTwilioConversations;
