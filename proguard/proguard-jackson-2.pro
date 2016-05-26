#Jackson 2.x

-keepattributes *Annotation*, EnclosingMethod
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry