package ch.thipok.method

class Placeholder {
    fun sentence(): String {
        return "Hello, ${Platform().name}!"
    }
}
