plugins {
    id("com.android.application")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.PokeMeng.OldManGO"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.PokeMeng.OldManGO"
        minSdk = 26
        targetSdk = 34
        versionCode = 23
        versionName = "23"
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.navigation:navigation-fragment:2.8.1")
    implementation("androidx.navigation:navigation-ui:2.8.1")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.calendarview)       //自定義日歷元件：https://blog.csdn.net/coffee_shop/article/details/130709029
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics) //Firebase分析
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore) //Firebase資料庫
    implementation(libs.firebase.messaging) //回傳到Firebase跳出資料
    implementation(libs.gson)               //自定義SharedPreferences元件：https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/tree/master
    implementation(libs.hanlp)              //將Ca的簡體中文改成繁體
    implementation(libs.lunar)              //Ca的農民節日還有公曆節日
    implementation(libs.material)
    implementation(libs.play.services.auth) //Google登陸
    implementation(libs.play.services.location)
    implementation(platform(libs.firebase.bom)) //Firebase BOM
    testImplementation(libs.junit)


    ///implementation("com.google.android:flexbox:3.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("androidx.recyclerview:recyclerview:1.3.0") // 请使用最新版本
    implementation ("androidx.core:core:1.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")

}