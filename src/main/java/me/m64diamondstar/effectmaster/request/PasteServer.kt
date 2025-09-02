package me.m64diamondstar.effectmaster.request

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object PasteServer {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @Serializable
    data class NewPasteResponse(
        val id: String
    )

    /**
     * Creates a new paste and uploads it to the paste server: https://paste.m64.dev/.
     * @return the ID given to the paste.
     */
    suspend fun createPaste(
        content: String,
        title: String? = null,
        expiry: String? = "week",
        password: String? = null
    ): String {
        val response: NewPasteResponse = client.submitForm(
            url = "https://paste.m64.dev/paste/new",
            formParameters = Parameters.build {
                append("content", content)
                title?.let { append("title", it) }
                expiry?.let { append("expiry", it) }
                password?.let { append("password", it) }
            }
        ) {
            headers.append("Accept", "application/json")
        }.body()

        return response.id
    }

    /**
     * Retrieves the raw contents of a paste.
     * @return the contents of a paste, or null if it isn't found.
     */
    suspend fun getPasteContent(pasteId: String, password: String? = null): String? {
        val url = "https://paste.m64.dev/paste/raw/$pasteId"
        return try {
            client.get(url) {
                password?.let { parameter("password", it) }
            }.body()
        } catch (_: Exception) {
            null
        }
    }

}