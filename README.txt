Prerequisites for installing and running the application:

Softwares needed- Kotlin

IDEs needed - Android Studio

Gradle dependencies
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    implementation 'com.google.firebase:firebase-auth-ktx:21.0.3'
    implementation 'com.google.firebase:firebase-database-ktx:20.0.4'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.google.firebase:firebase-messaging-ktx:23.0.3'

    implementation platform('com.google.firebase:firebase-bom:29.3.1')
    implementation 'com.google.firebase:firebase-analytics-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx:20.0.1'

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    implementation 'com.hbb20:ccp:2.6.0'
    implementation platform('com.google.firebase:firebase-bom:29.3.0')
    implementation 'com.sendbird.sdk:sendbird-android-sdk:3.1.7'
    implementation 'com.sendbird.sdk:sendbird-calls:1.9.0'

    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

Steps to run the application:
	Create a new account in the sendbird server dashboard and generate server-id.
	Link firebase with android studio and project
	download google service from firebase and paste it in project 
	Import firebase dependencies for firebase authentication, firebase cloud storage, firebase real-time database
	

Application name: GabChat

Application features: GabChat is an android social media application, which is used to communicate with others using chat, and audio/video calls.
	also, it has screen share features and screenshot detection features.
