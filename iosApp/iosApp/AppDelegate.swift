import Foundation
import Bugsnag
import BugsnagPerformance
import NSExceptionKtBugsnag
import UIKit
import WidgetKit
import common

class AppDelegate: NSObject, UIApplicationDelegate {
    var watchJob: Ktor_ioCloseable? = nil

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        let config = BugsnagDelegate.shared.createBugsnagConfig()
        
        NSExceptionKt.addReporter(.bugsnag(config))
        Bugsnag.start(with: config)
        BugsnagPerformance.start()

        watchJob = FlowUtilsKt.asCommonFlow(SettingsModel.shared.widgetRefresh).watch { _ in
            WidgetCenter.shared.reloadTimelines(ofKind: "HINT_Widget")
        }
        return true
    }

    func applicationWillTerminate(_ application: UIApplication) {
        watchJob?.close()
    }
}
