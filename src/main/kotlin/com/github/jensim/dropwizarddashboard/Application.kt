package com.github.jensim.dropwizarddashboard

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.github.jensim.dropwizarddashboard")
                .mainClass(Application.javaClass)
                .start()
    }
}