-verbose

-dontnote **

##--------------- swarauto-android
-keep class com.swarauto.game.profile.CommonConfig { *; }
-keep class com.swarauto.game.profile.Profile { *; }
-keep class com.swarauto.license.data.** { *; }
-keep class com.swarauto.license.api.** { *; }
-keep class com.swarauto.util.Point { *; }
-keep class com.swarauto.util.Rectangle { *; }
-keep class com.swarauto.ui.** { *; }
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
##--------------- Libs
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

-keep class javax.** { *; }
-keep class java.** { *; }
-dontwarn java.**

-keep class com.github.** { *; }

-keep class ch.qos.logback.** { *; }
-dontwarn ch.qos.logback.**
-dontwarn org.slf4j.**

##--------------- Retrofit
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Fix GSON
-optimizations !code/allocation/variable

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
##---------------End: proguard configuration for Gson  ----------

##---------------Green robot
-dontwarn org.greenrobot.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

##---------------JavaCV
-dontwarn org.bytedeco.**
-keepattributes *Annotation*
-keep @org.bytedeco.javacpp.annotation interface * {
    *;
}
-keep @org.bytedeco.javacpp.annotation.Platform public class *
-keepclasseswithmembernames class * {
    @org.bytedeco.* <fields>;
}
-keepclasseswithmembernames class * {
    @org.bytedeco.* <methods>;
}
-keepattributes EnclosingMethod
-keep @interface org.bytedeco.javacpp.annotation.*,javax.inject.*
-keepattributes *Annotation*, Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable, LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault, InnerClasses
-keep class org.bytedeco.javacpp.** {*;}
-dontwarn java.awt.**
-dontwarn org.bytedeco.javacv.**
-dontwarn org.bytedeco.javacpp.**