import java.util.Properties

// Expose a reusable local.properties accessor to module build scripts.
extra["localProperty"] = { name: String ->
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (!localPropertiesFile.exists()) {
        ""
    } else {
        localPropertiesFile.inputStream().use { properties.load(it) }
        properties.getProperty(name, "")
    }
}

