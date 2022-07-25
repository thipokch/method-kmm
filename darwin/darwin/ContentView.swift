import SwiftUI
import common

struct ContentView: View {
	let greet = Greeting().sentence()

	var body: some View {
		Text(greet)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}