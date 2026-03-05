import { type ConfigPlugin, withAppBuildGradle } from "expo/config-plugins";
import { ExpoTwilioConversationsPluginProps } from ".";

const TWC_DEPENDENCY_REGEX =
  /implementation\s+['"]com\.twilio:conversations-android:([\w\.\-]+)['"]/;

export const withAndroidConfiguration: ConfigPlugin<
  ExpoTwilioConversationsPluginProps
> = (config, props) => {
  return withAppBuildGradle(config, (gradleConfig) => {
    let contents = gradleConfig.modResults.contents;

    // If a Twilio Conversations dependency is already present, leave it as-is.
    if (!TWC_DEPENDENCY_REGEX.test(contents)) {
      const implementationLine = `    implementation "com.twilio:conversations-android:6.1.1"`;

      // Naively inject the dependency into the `dependencies {}` block.
      contents = contents.replace(
        /dependencies\s*{\s*/,
        (match) => `${match}${implementationLine}\n`
      );
    }

    gradleConfig.modResults.contents = contents;
    return gradleConfig;
  });
};
