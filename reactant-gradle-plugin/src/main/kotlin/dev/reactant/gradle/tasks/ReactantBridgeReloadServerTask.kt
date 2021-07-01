package dev.reactant.gradle.tasks

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.runBlocking
import org.gradle.api.tasks.TaskAction

abstract class ReactantBridgeReloadServerTask : ReactantBridgeTask() {
    @TaskAction
    fun reloadServer() {
        logger.debug("Sending reload server request to ${bridgeHost.get()}")
        runBlocking {
            HttpClient().post<Unit>("${bridgeHost.get()}/reload") {
            }
        }
    }
}
