import { type ConfigPlugin } from "expo/config-plugins";
import { withAndroidConfiguration } from "./withAndroid";
import { withIosConfiguration } from "./withIos";

export interface ExpoTwilioConversationsPluginProps {
  //
}

const withExpoTwilioConversations: ConfigPlugin<
  ExpoTwilioConversationsPluginProps
> = (config, props = {}) => {
  // Apply Android configurations
  config = withAndroidConfiguration(config, props);

  // Apply iOS configurations
  config = withIosConfiguration(config, props);

  return config;
};

export default withExpoTwilioConversations;
