import SwiftUI
import common
import resource

struct ContentView: View {
    let greet = Greeting().sentence()
    
    var body: some View {
        Text("scientific methods")
            .font(MethodTypography.TitleLarge.shared.swiftFont)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
