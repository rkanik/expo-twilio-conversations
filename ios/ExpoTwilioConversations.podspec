require "json"

package = JSON.parse(File.read(File.join(__dir__, "..", "package.json")))

Pod::Spec.new do |s|
  s.name         = "ExpoTwilioConversations"
  s.version      = package["version"]
  s.summary      = "Expo module that integrates the Twilio Conversations SDK."
  s.description  = s.summary
  s.homepage     = package["homepage"] || "https://github.com/your-org/expo-twilio-conversations"
  s.license      = package["license"] || "MIT"
  s.author       = package["author"] || { "Expo Community" => "support@expo.dev" }

  s.platforms    = { :ios => "13.0" }

  s.source       = { :git => "https://github.com/your-org/expo-twilio-conversations.git", :tag => "v#{s.version}" }
  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "ExpoModulesCore"
  s.dependency "TwilioConversationsClient", "~> 3.1"
end

