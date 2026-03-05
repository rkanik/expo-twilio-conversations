"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.withIosConfiguration = void 0;
const config_plugins_1 = require("expo/config-plugins");
const withIosConfiguration = (config, props) => {
    return (0, config_plugins_1.withInfoPlist)(config, (configWithPlist) => {
        //
        return configWithPlist;
    });
};
exports.withIosConfiguration = withIosConfiguration;
