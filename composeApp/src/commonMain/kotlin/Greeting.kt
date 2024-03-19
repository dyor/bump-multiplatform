class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello skadoosh, ${platform.name}!"
    }
}