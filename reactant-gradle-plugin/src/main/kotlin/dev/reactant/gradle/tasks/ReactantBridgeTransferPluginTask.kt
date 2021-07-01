package dev.reactant.gradle.tasks

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class ReactantBridgeTransferPluginTask : ReactantBridgeTask() {

    @InputFiles
    val pluginJar: ConfigurableFileCollection = project.files()

    @get:Input
    abstract val pluginName: Property<String>

    init {
        @Suppress("LeakingThis")
        pluginName.convention(project.name)
    }

    @TaskAction
    fun transferPlugin() {
        val jars = pluginJar.filter { it.extension == "jar" }
        if (jars.files.size > 1) {
            throw IllegalArgumentException("Transfer plugin task input have more than 1 jar")
        }
        jars.files.firstOrNull()?.let { uploadingJar ->
            logger.debug("Sending \"${uploadingJar.name}\" as plugin \"${pluginName.get()}\" to ${bridgeHost.get()}")
            runBlocking {
                kotlin.runCatching {
                    HttpClient().post<Unit>("${bridgeHost.get()}/plugins") {
                        body = MultiPartFormDataContent(
                            formData {
                                append(
                                    pluginName.get(),
                                    InputProvider(uploadingJar.length()) { uploadingJar.inputStream().asInput() },
                                    Headers.build {
                                        this.append(HttpHeaders.ContentType, ContentType.MultiPart.FormData.toString())
                                        this.append(HttpHeaders.ContentDisposition, "filename=${uploadingJar.name}")
                                    }
                                )
                            }
                        )
                    }
                }.onFailure {
                    logger.error("Failed when sending request to ${bridgeHost.get()}")
                }.getOrThrow()
            }
        }
    }
}
