package dev.reactant.gradle

import com.palantir.gradle.gitversion.VersionDetails
import org.gradle.api.Project

enum class VersionType {
    Snapshot, Release, ReleaseGap
}

data class TaggedVersionDetails(
    val versionType: VersionType,
    val version: String
)

abstract class ReactantTaggedVersionExtension(private val project: Project) {
    operator fun invoke(): TaggedVersionDetails {
        val extraProperties = project.extensions.extraProperties
        @Suppress("UNCHECKED_CAST") val getVersionDetails =
            (extraProperties["versionDetails"] as groovy.lang.Closure<VersionDetails>)
        val versionDetails = getVersionDetails.call()
        val lastTag = kotlin.runCatching { versionDetails.version }
            .getOrElse {
                throw IllegalStateException("Failed to get last git tag, git may not init or have any commit yet")
            }.removeSuffix(".dirty")

        fun isSnapshotTag(tag: String) = tag.endsWith("-SNAPSHOT")
        val lastTagIsSnapshot = isSnapshotTag(lastTag)
        val isRelease = !lastTagIsSnapshot && versionDetails.isCleanTag
        val isReleaseGap = !isRelease && !lastTagIsSnapshot
        return TaggedVersionDetails(
            versionType = when {
                isRelease -> VersionType.Release
                isReleaseGap -> VersionType.ReleaseGap
                else -> VersionType.Snapshot
            },
            version = lastTag
        )
    }
}
