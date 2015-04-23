
#mPosSample
This is a sample implementation of the payleven mPOS-SDK.
My first question was: How to pass the valid payleven instance through all activities?
I'm doing it with dependency injection using dagger. Read more in my Blog.
You find the official payleven example here: https://github.com/payleven/mPOS-SDK-Android.

##Prerequisites: 
Registered payleven account

Bluetooth chip and pin card reader

Android-Studio + Gradle

##Installation:

###Payleven:

####Repository
```groovy
repositories {
    maven{
        // payleven maven repository
        url 'https://download.payleven.com/maven'
    }
}
``` 
####Library
``` groovy
//required for payleven library
compile 'com.google.code.gson:gson:2.3'
//Use the specific library version here
compile 'de.payleven.payment:mpos:1.0.0@jar'
//This is a helper payleven library.
compile 'de.payleven:psp-library:1.0.0@aar'
```

###Dagger:
``` groovy
compile 'com.squareup.dagger:dagger:1.2.2'
provided 'com.squareup.dagger:dagger-compiler:1.2.2'
``` 
Dagger is a dependency injector. I mainly use it to inject sessionprovider to get valid sessiondata through the activities or it produces a relogin.


###Butterknife:
``` groovy
compile 'com.jakewharton:butterknife:6.1.0'
```
Butterknife is used for view injection. In my opinion the code gets more readable.


