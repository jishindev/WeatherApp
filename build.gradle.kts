buildscript {


    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-alpha07")
        classpath(kotlin("gradle-plugin", version = "1.3.21"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}
tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}
