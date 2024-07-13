import Foundation
import Bugsnag
import NSExceptionKtBugsnag
import UIKit
import common

class AppDelegate: NSObject, UIApplicationDelegate {
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
        return true
    }
}
