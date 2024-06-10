rootProject.name = "oop-23-TD"
plugins {
    id("com.gradle.develocity") version "3.17.4"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground = !System.getenv("CI").toBoolean()
        buildScanPublished {
            file("scan-journal.log").writeText("$buildScanId - $buildScanUri\n")
        }
    }
}
