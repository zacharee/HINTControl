import Foundation
import AppIntents
import common

@available(iOS 16, *)
struct RebootIntent: AppIntent {
    static var title: LocalizedStringResource = "rebootGateway"
    
    func perform() async throws -> some IntentResult {
        let (error1, error2) = await AppIntentUtilsKt.performRebootAction()
        
        if let error = error1 ?? error2 {
            throw error
        }
        
        return .result()
    }
}

@available(iOS 16, *)
struct IntentProvider: AppShortcutsProvider {
    @AppShortcutsBuilder
    static var appShortcuts: [AppShortcut] {
        AppShortcut(
            intent: RebootIntent(),
            phrases: ["rebootGatewayWith \(.applicationName)"],
            shortTitle: "rebootGateway",
            systemImageName: "restart"
        )
    }
}
