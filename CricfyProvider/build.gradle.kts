plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.lagradost.cloudstream3.gradle")
}

cloudstream {
    setRepo("https://github.com/abhi-buddy007/extensions")
}

android {
    namespace = "com.cricfy"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val cloudstreamApiVersion = "pre-release"
    compileOnly("com.lagradost:cloudstream3:$cloudstreamApiVersion")
}
