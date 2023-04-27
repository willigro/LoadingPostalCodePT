import Depends.Fragment.implementFragmentKtx
import Depends.Kotlin.implementKotlinForModule
import Depends.Robbie.implementRobbie
import Depends.ViewModel.implementViewModel

android {
    buildTypes.forEach {
        it.buildConfigField("String", "BASE_NAME", "\"wtest.db\"")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // =========== Kotlin ==============
    implementKotlinForModule()

    // =========== AppCompat ==============
    implementation(Depends.AppCompat.getAppcompat())

    // =========== ViewModel ==============
    implementViewModel()

    // =========== Robbie ==============
    implementRobbie()

    // =========== Material ==============
    implementation(Depends.Material.getMaterial())

    // =========== ScreenNavigator fragment dependencies ==============
    implementFragmentKtx()
}