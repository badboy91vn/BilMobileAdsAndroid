# BilMobileAdsAndroid

#### Step 1: Add the JitPack repository to your build file
- Add it in your root build.gradle at the end of repositories.
```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
#### Step 2: In Project Explorer go to Gradle Scripts > build.gradle (Module: app) and add the following lines to the android { ... } section:
```gradle
    android {
        ...
        compileOptions 
        {
         sourceCompatibility JavaVersion.VERSION_1_8
         targetCompatibility JavaVersion.VERSION_1_8
        }
    }
```
#### Step 3: Add the dependency with latest version
```gradle
    dependencies {
        implementation 'com.github.badboy91vn:BilMobileAdsAndroid:2.0.6'
    }
```


