import Bugsnag
import SwiftUI
import common

@main
struct iOSApp: App {
    init() {
        let config = BugsnagConfiguration.loadConfig()
        
        config.addOnSendError { event in
            CrossPlatformBugsnag.shared.generateExtraErrorData().forEach { data in
                event.addMetadata(data.value, key: data.key, section: data.tabName)
            }
            return true
        }
        
        Main_iosKt.updateBugsnagConfig(config: config)
        Bugsnag.start()
        Main_iosKt.setupBugsnag()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
