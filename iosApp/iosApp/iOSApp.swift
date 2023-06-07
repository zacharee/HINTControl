import Bugsnag
import SwiftUI
import common

@main
struct iOSApp: App {
    init() {
        let config = BugsnagConfiguration.loadConfig()
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
