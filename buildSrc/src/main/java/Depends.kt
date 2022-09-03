import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

const val IMPLEMENTATION = "implementation"
const val TEST_IMPLEMENTATION = "testImplementation"
const val ANDROID_TEST_IMPLEMENTATION = "androidTestImplementation"
const val DEBUG_IMPLEMENTATION = "androidTestImplementation"
const val KAPT = "kapt"
const val KAPT_ANDROID_TEST = "kaptTest"
const val ANNOTATION_PROCESSOR = "annotationProcessor"

fun DependencyHandler.implement(url: String) {
    add(IMPLEMENTATION, url)
}

fun DependencyHandler.testImplement(url: String) {
    add(TEST_IMPLEMENTATION, url)
}

fun DependencyHandler.androidTestImplement(url: String) {
    add(ANDROID_TEST_IMPLEMENTATION, url)
}

fun DependencyHandler.debugImplement(url: String) {
    add(DEBUG_IMPLEMENTATION, url)
}

fun DependencyHandler.kapt(url: String) {
    add(KAPT, url)
}

fun DependencyHandler.kaptAndroidTest(url: String) {
    add(KAPT_ANDROID_TEST, url)
}

fun DependencyHandler.annotationProcessor(url: String) {
    add(ANNOTATION_PROCESSOR, url)
}

object Depends {

    object Module {
        fun DependencyHandler.implementAllModules(vararg less: String) {
            val result = Modules.modules - less.toSet()
            result.forEach { add(IMPLEMENTATION, project(it)) }
        }

        fun DependencyHandler.implementModules(vararg modules: String) {
            modules.forEach { add(IMPLEMENTATION, project(it)) }
        }

        fun DependencyHandler.androidTestImplementationModules(vararg modules: String) {
            modules.forEach { add(ANDROID_TEST_IMPLEMENTATION, project(it)) }
        }

        fun DependencyHandler.testImplementationModules(vararg modules: String) {
            modules.forEach { add(TEST_IMPLEMENTATION, project(it)) }
        }
    }

    object Gradle {
        fun getGradlePlugin() = "com.android.tools.build:gradle:${Versions.GRADLE_TOOL_BUILD}"
    }

    object Kotlin {
        fun getKotlin() = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}"
        fun getKotlinExtensions() =
            "org.jetbrains.kotlin:kotlin-android-extensions-runtime:${Versions.KOTLIN_VERSION}"

        fun DependencyHandler.implementKotlinForModule() {
            implement("org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN_VERSION}")
            implement("org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_VERSION}")
            implement("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN_VERSION}")
            implement("androidx.core:core-ktx:${Versions.KOTLIN_KTX}")
        }
    }

    object Robbie {
        fun DependencyHandler.implementRobbie() {
//            implement("com.github.willigro:RobbieAndroidUtil:${Versions.ROBBIE}")

            implementLocalRobbie()
        }

        fun DependencyHandler.implementLocalRobbie() {
            implement("com.rittmann:buttons:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:typography:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:widgets:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:textfield:${Versions.ROBBIE_LOCAL}")
            // see why it broken later
//            implement("com.rittmann:sqltools:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:core:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:baselifecycle:${Versions.ROBBIE_LOCAL}")
            implement("com.rittmann:androidtools:${Versions.ROBBIE_LOCAL}")
        }
    }

    object AppCompat {
        fun getAppcompat() =
            "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    }

    object Material {
        fun getMaterial() =
            "com.google.android.material:material:${Versions.MATERIAL}"
    }

    object Views {
        fun DependencyHandler.implementLayouts() {
            add(
                IMPLEMENTATION,
                "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
            )
        }
    }

    object IdlingResource {
        fun DependencyHandler.implementIdling() {
            implement("androidx.test.espresso:espresso-idling-resource:${Versions.ESPRESSO}")
        }
    }

    object Test {
        const val ANDROID_JUNIT_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

        const val JUNIT = "junit:junit:${Versions.JUNIT}"
        const val FRAGMENT_TESTING = "androidx.fragment:fragment-testing:${Versions.FRAGMENT}"
        const val TEST_CORE = "androidx.test:core:${Versions.ANDROID_X_TEST_CORE}"
        const val TEST_CORE_KTX = "androidx.test:core-ktx:${Versions.ANDROID_X_TEST_CORE}"
        const val TEXT_EXT_KTX_JUNIT = "androidx.test.ext:junit-ktx:${Versions.JUNIT_EXT}"
        const val TEXT_EXT_JUNIT = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"
        const val COROUTINES_TEST =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"
        const val ARCH_CORE_TEST = "androidx.arch.core:core-testing:${Versions.ARCH_TESTING}"
        const val ROBOLETRIC = "org.robolectric:robolectric:${Versions.ROBOLETRIC}"
        const val HAMCREST = "org.hamcrest:hamcrest-all:${Versions.HAMCREST}"
        const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
        const val MOCKK_AGENT = "io.mockk:mockk-agent-jvm:${Versions.MOCKK}"

        fun DependencyHandler.implementTest() {
            testImplement(JUNIT)

            debugImplement(FRAGMENT_TESTING)

            implement(TEST_CORE)
            testImplement(TEST_CORE_KTX)
            testImplement(TEXT_EXT_KTX_JUNIT)
            testImplement(TEXT_EXT_JUNIT)
            testImplement(COROUTINES_TEST)
            testImplement(ARCH_CORE_TEST)
            testImplement(ROBOLETRIC)
            testImplement(HAMCREST)

            testImplement(MOCKK)
            testImplement(MOCKK_AGENT)
        }

        fun DependencyHandler.implementAllInDebugTest() {
            debugImplement(JUNIT)
            debugImplement(FRAGMENT_TESTING)
            debugImplement(TEST_CORE)
            debugImplement(TEST_CORE_KTX)
            debugImplement(TEXT_EXT_KTX_JUNIT)
            debugImplement(TEXT_EXT_JUNIT)
            debugImplement(COROUTINES_TEST)
            debugImplement(ARCH_CORE_TEST)
            debugImplement(ROBOLETRIC)
            debugImplement(HAMCREST)
            debugImplement(MOCKK)
            debugImplement(MOCKK_AGENT)
        }
    }

    object AndroidTest {

        const val ESPRESSO_IDLING =
            "androidx.test.espresso:espresso-idling-resource:${Versions.ESPRESSO}"
        const val FRAGMENT_TESTING = "androidx.fragment:fragment-testing:${Versions.FRAGMENT}"

        const val EXPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
        const val EXPRESSO_CONTRIB = "androidx.test.espresso:espresso-contrib:${Versions.ESPRESSO}"

        const val COROUTINES_TEST =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"

        const val MOCKK = "io.mockk:mockk-android:${Versions.MOCKK}"

        fun DependencyHandler.implementAndroidTest() {
            androidTestImplement("junit:junit:${Versions.JUNIT}")
            androidTestImplement("androidx.test.ext:junit:${Versions.JUNIT_EXT}")
            androidTestImplement("androidx.arch.core:core-testing:${Versions.ARCH_TESTING}")
            androidTestImplement(MOCKK)
            androidTestImplement(Test.MOCKK_AGENT)
            androidTestImplement(COROUTINES_TEST)
        }

        fun DependencyHandler.implementEspressoTest() {
            androidTestImplement(EXPRESSO_CORE)
            androidTestImplement(EXPRESSO_CONTRIB)
        }

        fun DependencyHandler.implementEspressoTestAllInDebug() {
            implement(EXPRESSO_CORE)
            implement(EXPRESSO_CONTRIB)
        }
    }

    object Room {
        fun DependencyHandler.implementRoom() {
            implement("androidx.room:room-runtime:${Versions.ROOM}")
            kapt("androidx.room:room-compiler:${Versions.ROOM}")
            implement("androidx.room:room-ktx:${Versions.ROOM}")
        }
    }

    object Dagger {
        fun DependencyHandler.implementDagger() {
            kapt("com.google.dagger:dagger-android-support:${Versions.DAGGER}")
            kapt("com.google.dagger:dagger-compiler:${Versions.DAGGER}")
            kapt("com.google.dagger:dagger-android-processor:${Versions.DAGGER}")

            annotationProcessor("com.google.dagger:dagger-android-processor:${Versions.DAGGER}")
            annotationProcessor("com.google.dagger:dagger-compiler:${Versions.DAGGER}")

            implement("com.google.dagger:dagger:${Versions.DAGGER}")
            implement("com.google.dagger:dagger-android:${Versions.DAGGER}")
            implement("com.google.dagger:dagger-android-support:${Versions.DAGGER}")
        }
    }

    object ViewModel {
        fun DependencyHandler.implementViewModel() {
            implement("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEWMODEL}")
            implement("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.VIEWMODEL}")
        }
    }

    object Navigation {
        fun DependencyHandler.implementNavigation() {
            implement("androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}")
            implement("androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}")
            implement("androidx.navigation:navigation-dynamic-features-fragment:${Versions.NAVIGATION}")
        }
    }

    object Databinding {
        fun DependencyHandler.implementDatabinding() {
            val kapt = "androidx.databinding:databinding-compiler:7.1.3"
            implement(kapt)
            kaptAndroidTest(kapt)
        }
    }

    object LegacySupport {
        fun DependencyHandler.implementLegacySupport() {
            implement("androidx.legacy:legacy-support-v4:1.0.0")
        }
    }

    object Tags {
        fun DependencyHandler.implementTags() {
            implement("co.lujun:androidtagview:1.1.7")
        }
    }

    object Retrofit {
        fun DependencyHandler.implementRetrofit() {
            implement("com.squareup.retrofit2:retrofit:${Versions.RETROFIT}")
            implement("com.squareup.retrofit2:converter-gson:${Versions.RETROFIT_GSON_CONVERTER}")
            implement("com.google.code.gson:gson:${Versions.GOOGLE_GSON}")
            implement("com.squareup.okhttp3:logging-interceptor:${Versions.SQUAREUP_OK_HTTP_3_LOGGING_INTERCEPTOR}")
        }
    }

    object Glide {
        fun DependencyHandler.implementGlide() {
            implement("com.github.bumptech.glide:glide:${Versions.GLIDE}")
            annotationProcessor("com.github.bumptech.glide:compiler:${Versions.GLIDE}")
        }
    }

    object Worker {
        fun DependencyHandler.implementWorker() {
            implement("androidx.work:work-runtime:2.7.1")
            implement("androidx.work:work-runtime-ktx:2.7.1")
        }
    }
}