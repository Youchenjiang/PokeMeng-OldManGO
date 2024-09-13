plugins {
    id("com.android.application")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.myapplication0412"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication0412"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
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
    implementation(libs.gson)  //自定義SharedPreferences元件：https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/tree/master
    implementation(libs.activity)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    implementation(libs.calendarview)  //自定義日歷元件：https://blog.csdn.net/coffee_shop/article/details/130709029
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore) //Firebase資料庫
    implementation(libs.firebase.analytics) //Firebase分析
    androidTestImplementation(libs.ext.junit)
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    androidTestImplementation(libs.espresso.core)
    implementation(libs.play.services.location)
}