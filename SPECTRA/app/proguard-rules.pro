# SPECTRA — release ProGuard / R8 rules

-keepattributes *Annotation*, InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# Room entities keep their fields (reflection-free with kapt, but keep schema classes safe)
-keep class com.spectra.ar.data.database.entities.** { *; }

# Kotlinx Serialization
-keepattributes RuntimeVisibleAnnotations
-keep,includedescriptorclasses class com.spectra.ar.**$$serializer { *; }
-keepclassmembers class com.spectra.ar.** {
    *** Companion;
}
-keepclasseswithmembers class com.spectra.ar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# TensorFlow Lite / ONNX Runtime native bindings
-keep class org.tensorflow.lite.** { *; }
-keep class ai.onnxruntime.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# ARCore
-keep class com.google.ar.core.** { *; }
