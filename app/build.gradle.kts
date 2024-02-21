plugins {
    id("com.android.application")
}


android {
    namespace = "jp.ac.cm0107.road_to_music"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.ac.cm0107.road_to_music"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}



dependencies {

    implementation ("android.arch.navigation:navigation-fragment:1.0.0")
    implementation ("android.arch.navigation:navigation-ui:1.0.0")
    implementation ("android.arch.navigation:navigation-fragment-ktx:1.0.0")
    implementation ("android.arch.navigation:navigation-ui-ktx:1.0.0")

   // implementation ("com.spotify.android:auth:2.0.1") // Maven dependency
    // All other dependencies for your app should also be here:
    implementation ("androidx.browser:browser:1.7.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation ("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation ("androidx.browser:browser:1.7.0")
    implementation ("androidx.appcompat:appcompat-resources:1.6.1")
    implementation("com.squareup.picasso:picasso:2.3.3")
    implementation ("com.spotify.android:auth:1.2.5")
    implementation(files("libs/spotify-web-api-android-0.4.1.aar"))

    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation ("com.squareup.retrofit:retrofit:1.9.0")
    implementation ("com.squareup.retrofit2:retrofit:2.0.0-beta4")
    implementation ("com.squareup.okhttp:okhttp:2.7.4")
    implementation ("com.squareup.okhttp:okhttp-urlconnection:2.7.4")



}