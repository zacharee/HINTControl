import Foundation
import Bugsnag
import NSExceptionKtBugsnag
import UIKit
import common
import WidgetKit

class AppDelegate: NSObject, UIApplicationDelegate {
    var watchJob: OkioCloseable? = nil

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

        watchJob = FlowUtilsKt.asCommonFlow(SettingsModel.shared.widgetRefresh).watch { _ in
            WidgetCenter.shared.reloadTimelines(ofKind: "HINT_Widget")
        }
        return true
    }

    func applicationWillTerminate(_ application: UIApplication) {
        do {
            try watchJob?.close_()
        } catch {}
    }
}
