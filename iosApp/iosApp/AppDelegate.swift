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
        let config = BugsnagConfiguration.loadConfig()
        
        config.addOnSendError { event in
            CrossPlatformBugsnag.shared.generateExtraErrorData().forEach { data in
                event.addMetadata(data.value, key: data.key, section: data.tabName)
            }
            return true
        }
        
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
