package me.m64diamondstar.effectmaster.update

data class Version(val major: Int, val minor: Int, val patch: Int) : Comparable<Version> {
    companion object {
        fun parse(version: String): Version {
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
        return compareValuesBy(this, other, Version::major, Version::minor, Version::patch)
    }

    override fun toString(): String = "$major.$minor.$patch"
}
