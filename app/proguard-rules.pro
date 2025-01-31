# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep OkHttp3

-keep class com.vhennus.auth.domain.** { *; }
-keep interface com.vhennus.general.data.APIService { *; }
# Retrofit rules
# Retrofit rules
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# OkHttp rules
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class okio.** { *; }
-dontwarn okio.**

# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Kotlin rules
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# Preserve your API models and interfaces
-keep class your.package.name.model.** { *; }
-keep interface your.package.name.api.** { *; }
# Preserve your API models
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

# -keep,allowobfuscation,allowshrinking interface retrofit2.Call
# -keep,allowobfuscation,allowshrinking class retrofit2.Response
# -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation


 # Dagger Hilt rules
# -keep class dagger.hilt.** { *; }
# -keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
# -keep class * extends dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
# -keep class * extends dagger.hilt.android.internal.managers.FragmentComponentManager { *; }
# -keep class * extends dagger.hilt.android.internal.managers.ServiceComponentManager { *; }
# -keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
# -keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }

# Keep the GenericResp class and its type parameters
-keep class com.vhennus.general.domain.GenericResp { *; }

# Keep Gson annotations to prevent stripping serialized names
-keepattributes *Annotation*

# Keep all Retrofit service interfaces
#-keep interface com.yourpackage.api.** { *; }

# Keep Retrofit and Gson converters
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }

# Ensure Gson properly maps serialized fields
-keep class com.google.gson.annotations.SerializedName { *; }

# Keep class names and methods (disable obfuscation)
#-keepattributes InnerClasses, EnclosingMethod
#-keep class * { *; }



# Keep class names of certain packages
#-keep class com.yourpackage.** { *; }

# Keep annotations (useful for reflection-based libraries)
#-keepattributes *Annotation*

# Avoid shrinking (optional, only use if debugging)
#-dontshrink

# Avoid optimization (optional, if causing issues)
#-dontoptimize

-keepattributes Signature
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.bumptech.glide.Glide
-dontwarn com.bumptech.glide.RequestBuilder
-dontwarn com.bumptech.glide.RequestManager
-dontwarn com.bumptech.glide.load.DataSource
-dontwarn com.bumptech.glide.load.engine.GlideException
-dontwarn com.bumptech.glide.request.BaseRequestOptions
-dontwarn com.bumptech.glide.request.RequestListener
-dontwarn com.bumptech.glide.request.target.Target
-dontwarn com.bumptech.glide.request.target.ViewTarget
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn com.squareup.picasso.Callback
-dontwarn com.squareup.picasso.Picasso$Builder
-dontwarn com.squareup.picasso.Picasso
-dontwarn com.squareup.picasso.RequestCreator
-dontwarn org.joda.time.Instant