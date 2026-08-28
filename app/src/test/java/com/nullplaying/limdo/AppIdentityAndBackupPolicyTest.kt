package com.nullplaying.limdo

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppIdentityAndBackupPolicyTest {
    @Test
    fun androidIdentityAndSourcePackagesUseTheNullplayingNamespace() {
        val gradle = File("build.gradle.kts").readText()
        assertTrue(gradle.contains("namespace = \"$EXPECTED_PACKAGE\""))
        assertTrue(gradle.contains("applicationId = \"$EXPECTED_PACKAGE\""))
        val oldPackage = listOf("com", "example", "limdo").joinToString(".")
        assertFalse(gradle.contains(oldPackage))

        listOf(File("src/main/java"), File("src/test/java"))
            .flatMap { root -> root.walkTopDown().filter { it.extension == "kt" }.toList() }
            .forEach { source ->
                assertEquals(
                    "${source.path} package",
                    "package $EXPECTED_PACKAGE",
                    source.useLines { lines -> lines.first() },
                )
            }
    }

    @Test
    fun cloudBackupAndDeviceTransferExcludeEveryAppDataDomain() {
        val manifest = File("src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android:allowBackup=\"false\""))
        assertTrue(manifest.contains("android:fullBackupContent=\"@xml/backup_rules\""))
        assertTrue(manifest.contains("android:dataExtractionRules=\"@xml/data_extraction_rules\""))

        val legacyRules = File("src/main/res/xml/backup_rules.xml").readText()
        val extractionRules = File("src/main/res/xml/data_extraction_rules.xml").readText()
        assertTrue(extractionRules.contains("<cloud-backup>"))
        assertTrue(extractionRules.contains("<device-transfer>"))

        excludedDomains.forEach { domain ->
            val exclusion = "<exclude domain=\"$domain\" path=\".\" />"
            assertEquals("legacy $domain exclusion", 1, legacyRules.windowed(exclusion.length).count { it == exclusion })
            assertEquals("cloud and transfer $domain exclusions", 2, extractionRules.windowed(exclusion.length).count { it == exclusion })
        }
    }

    private companion object {
        const val EXPECTED_PACKAGE = "com.nullplaying.limdo"
        val excludedDomains = listOf(
            "root",
            "file",
            "database",
            "sharedpref",
            "external",
            "device_root",
            "device_file",
            "device_database",
            "device_sharedpref",
        )
    }
}
