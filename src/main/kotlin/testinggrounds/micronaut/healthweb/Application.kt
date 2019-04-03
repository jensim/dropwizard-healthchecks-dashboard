package testinggrounds.micronaut.healthweb

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("testinggrounds.micronaut.healthweb")
                .mainClass(Application.javaClass)
                .start()
    }
}