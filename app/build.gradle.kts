import java.util.Properties

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("androidx.navigation.safeargs.kotlin")
}

android {
  namespace = "eth.social.sefer"
  compileSdk = 34

  defaultConfig {
    applicationId = "eth.social.sefer"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "0.1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())

    // Set API keys in BuildConfig
    buildConfigField(
      "String", "GOOGLE_CLIENT_ID", "\"${localProperties.getProperty("GOOGLE_CLIENT_ID")}\""
    )
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  viewBinding {
    enable = true
  }
  buildFeatures {
    buildConfig = true
    viewBinding = true
  }
}

dependencies {
  implementation(project(mapOf("path" to ":LikeButton")))

  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.11.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
  implementation("androidx.core:core-splashscreen:1.0.1")
  implementation("androidx.preference:preference-ktx:1.2.1")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")
  implementation("androidx.recyclerview:recyclerview:1.3.2")
  implementation("androidx.preference:preference-ktx:1.2.1")

  implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
  implementation("androidx.credentials:credentials:1.3.0-alpha01")
  implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha01")

  val navVersion = "2.7.6"
  val lifeCycle = "2.7.0"

  implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
  implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifeCycle")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifeCycle")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifeCycle")

  val jacksonVersion = "2.16.1"

  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
  implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
  implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
  implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.12")

  implementation("de.hdodenhof:circleimageview:3.1.0")
  implementation("com.github.bumptech.glide:glide:4.16.0")
  annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

  // firebase
  implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
  implementation("com.google.firebase:firebase-messaging")

  // media 3
  implementation("androidx.media3:media3-exoplayer:1.2.1")
//    implementation("androidx.media3:media3-exoplayer-dash:1.2.0")
  implementation("androidx.media3:media3-ui:1.2.1")

  // coroutine
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

  implementation("com.hbb20:android-country-picker:0.0.7")
  implementation("com.leinardi.android:speed-dial:3.3.0")

  // markdown
  val markdownVersion = "4.6.2"

  implementation("io.noties.markwon:core:$markdownVersion")
  implementation("io.noties.markwon:editor:$markdownVersion")
  implementation("io.noties.markwon:image:$markdownVersion")
  implementation("io.noties.markwon:image-glide:$markdownVersion")
  implementation("io.noties.markwon:linkify:$markdownVersion")
  implementation("io.noties.markwon:recycler:$markdownVersion")
  implementation("io.noties.markwon:ext-strikethrough:$markdownVersion")
  implementation("io.github.yahiaangelo.markdownedittext:markdownedittext:1.1.3")
}
