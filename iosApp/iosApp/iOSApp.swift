import Bugsnag
import SwiftUI

@main
struct iOSApp: App {
    init() {
        Bugsnag.start()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
