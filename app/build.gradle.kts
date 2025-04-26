plugins {
    id ("com.android.application")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.chatapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatapplication"
        minSdk = 17
        multiDexEnabled = true
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation (libs.appcompat.v160)
    implementation (libs.material.v170)
    implementation (libs.constraintlayout.v214)
    implementation (libs.firebase.database.v2010)
    implementation (libs.firebase.auth.v2110)
    implementation (libs.firebase.storage.v2010)
    testImplementation (libs.junit)
    androidTestImplementation (libs.junit.v115)
    androidTestImplementation (libs.espresso.core.v351)
    implementation (libs.sdp.android.v110)
    implementation (libs.circleimageview)
    implementation (libs.picasso)

}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(18)
    }
}
