package dev.reactant.pm

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ArtifactResolverTest {
    @Test
    fun resolve() {
        val actual = ArtifactResolver().resolve("dev.reactant:reactant:0.2.3")
        assertNotNull(actual)
    }
}
