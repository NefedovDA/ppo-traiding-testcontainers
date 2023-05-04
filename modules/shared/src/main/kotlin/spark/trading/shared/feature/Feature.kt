package spark.trading.shared.feature

import io.ktor.server.application.*
import org.kodein.di.DI

interface Feature {
    val di: DI.Module? get() = null

    fun Application.feature() {}
}
