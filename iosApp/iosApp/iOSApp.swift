import Bugsnag
import SwiftUI
import WidgetKit
import common

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
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
