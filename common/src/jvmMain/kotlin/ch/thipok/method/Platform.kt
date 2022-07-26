package ch.thipok.method

actual class Platform actual constructor() {
    actual val name: String = System.getProperty("java.vm.name") + " " + System.getProperty("java.vendor")
}
