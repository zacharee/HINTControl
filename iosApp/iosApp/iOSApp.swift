import Bugsnag
import NSExceptionKtBugsnag
import SwiftUI
import WidgetKit
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
        
        NSExceptionKt.addReporter(.bugsnag(config))
        Bugsnag.start(with: config)
        
        FlowUtilsKt.asCommonFlow(SettingsModel.shared.widgetRefresh).watch { _ in
            WidgetCenter.shared.reloadTimelines(ofKind: "HINT_Widget")
        }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
