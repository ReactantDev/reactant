package dev.reactant.pm

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.AbstractRepositoryListener
import org.eclipse.aether.RepositoryEvent
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.artifact.JavaScopes
import java.io.File

internal class ArtifactResolver {
    fun resolve(dependencyNotation: String): File? {
        val locator = MavenRepositorySystemUtils.newServiceLocator()
        locator.addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
        locator.addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
        locator.addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        MavenRepositorySystemUtils.newSession()

        val system = locator.getService(RepositorySystem::class.java)
        val session = MavenRepositorySystemUtils.newSession()
        val spigotLibraryRepo = LocalRepository("libraries")
        session.localRepositoryManager = system.newLocalRepositoryManager(session, spigotLibraryRepo)
        val centralRepo = RemoteRepository.Builder(
            "central",
            "default",
            "https://repo.maven.apache.org/maven2/"
        ).build()
        val localRepo = RemoteRepository.Builder(
            "local",
            "default",
            "file://${System.getProperty("user.home")}/.m2/repository/"
        ).build()
        val snapshotRepo = RemoteRepository.Builder(
            "snapshot",
            "default",
            "http://nexus.hc.to/content/repositories/pub_releases/"
        ).build()

        val artifact = DefaultArtifact(dependencyNotation)
        val artifactRequest = ArtifactRequest().apply {
            this.artifact = artifact
            repositories = listOf(centralRepo, snapshotRepo, localRepo)
        }
        val collectRequest = CollectRequest()
        collectRequest.root = Dependency(artifact, JavaScopes.COMPILE)
        collectRequest.repositories = listOf(centralRepo, snapshotRepo, localRepo)

        session.setRepositoryListener(
            object : AbstractRepositoryListener() {
                override fun artifactResolving(event: RepositoryEvent?) {
                    // TODO: log
                }

                override fun artifactResolved(event: RepositoryEvent?) {
                    // TODO: log
                }
            }
        )

        return kotlin.runCatching { system.resolveArtifact(session, artifactRequest).artifact.file }.getOrNull()
    }
}
