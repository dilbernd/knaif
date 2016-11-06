import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import org.gradle.api.plugins.*
import org.gradle.script.lang.kotlin.*

buildscript {
    extra["kotlinVersion"] = "1.1-M02"
    extra["kotlin-eap-repo"] = "https://dl.bintray.com/kotlin/kotlin-eap-1.1"
    extra["gradle-repo"] = "https://repo.gradle.org/gradle/repo"

    repositories {
        listOf("kotlin-eap-repo", "gradle-repo").forEach {
            maven {
                setUrl(extra[it])
            }
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
        classpath("com.github.jengelman.gradle.plugins:shadow:1.2.3")
    }
}

apply {
    plugin("kotlin")
    plugin<ApplicationPlugin>()
    plugin<ShadowPlugin>()
}

configure<ApplicationPluginConvention> {
    mainClassName = "net.berndhaug.knaif.KnaifKt"
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    listOf("kotlin-eap-repo").forEach {
        maven {
            setUrl(extra[it])
        }
    }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlinVersion"]}")
    testCompile("junit:junit:4.12")
}

