import SwiftUI
import common
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        DarwinKermitKt.doInitKermit()
        KoinKt.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
