import UIKit
import SwiftUI
import common

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct VisualEffectView: UIViewRepresentable {
    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {
        
    }
    
    func makeUIView(context: Context) -> UIVisualEffectView {
        let view = UIVisualEffectView()
        
        view.effect = UIBlurEffect(style: .dark)
        
        return view
    }
}

struct ContentView: View {
    var body: some View {
        ZStack {
           ComposeView()
                   .ignoresSafeArea(.container)
        }
    }
}
