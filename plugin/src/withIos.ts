import { type ConfigPlugin, withInfoPlist } from "expo/config-plugins";
import { ExpoTwilioConversationsPluginProps } from ".";

export const withIosConfiguration: ConfigPlugin<
  ExpoTwilioConversationsPluginProps
> = (config, props) => {
  return withInfoPlist(config, (configWithPlist) => {
    //
    return configWithPlist;
  });
};
