En lugar de usar Kapt para injectar Hilt se usa ksp. El Hilt funciona igual. Para hacer posible la compilación se han añadido estas propiedades en gradle.properties:

android.nonTransitiveRClass=true
android.builtInKotlin=false
android.newDsl=false
android.disallowKotlinSourceSets=false
