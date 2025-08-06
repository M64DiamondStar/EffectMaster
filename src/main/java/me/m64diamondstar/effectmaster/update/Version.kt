package me.m64diamondstar.effectmaster.update

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null,
    val buildMetadata: String? = null
) : Comparable<Version> {

    companion object {
        fun parse(version: String): Version {
            val regex = Regex("""(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z.-]+))?(?:\+([0-9A-Za-z.-]+))?""")
            val match = regex.find(version)

            if (match != null) {
                val (major, minor, patch, preRelease, buildMetadata) = match.destructured
                return Version(
                    major = major.toInt(),
                    minor = minor.toInt(),
                    patch = patch.toInt(),
                    preRelease = preRelease.ifBlank { null },
                    buildMetadata = buildMetadata.ifBlank { null }
                )
            }

            // Fallback if regex doesn't match
            val cleaned = version
                .replace(Regex("[^0-9.]"), "")
                .split('.')
                .mapNotNull { it.toIntOrNull() }

            return Version(
                major = cleaned.getOrElse(0) { 0 },
                minor = cleaned.getOrElse(1) { 0 },
                patch = cleaned.getOrElse(2) { 0 }
            )
        }
    }

    override fun compareTo(other: Version): Int {
        val base = compareValuesBy(this, other, Version::major, Version::minor, Version::patch)
        if (base != 0) return base

        return when {
            this.preRelease == null && other.preRelease == null -> 0
            this.preRelease == null -> 1 // Stable > pre-release
            other.preRelease == null -> -1
            else -> this.preRelease.compareTo(other.preRelease)
        }
    }

    override fun toString(): String {
        val base = "$major.$minor.$patch"
        val pre = if (preRelease != null) "-$preRelease" else ""
        val build = if (buildMetadata != null) "+$buildMetadata" else ""
        return base + pre + build
    }
}

