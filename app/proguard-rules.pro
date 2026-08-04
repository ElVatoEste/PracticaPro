# Room, Compose, Retrofit, OkHttp y Media3 traen sus propias reglas dentro del
# AAR. Repetirlas aquí como `-keep class ... { *; }` solo apaga la optimización.

# kotlinx.serialization busca el serializador por el `Companion` de cada clase
# anotada. Reglas oficiales de la biblioteca.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Proveedores de TLS opcionales que OkHttp resuelve en tiempo de ejecución.
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
