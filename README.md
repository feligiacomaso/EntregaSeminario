# FilmWish

Aplicación Android en Kotlin y Jetpack Compose para explorar películas de TMDB, buscar títulos, consultar tendencias, revisar detalles y guardar deseados localmente.

## Configurar TMDB

1. En TMDB, generá un **API Read Access Token** (token Bearer de lectura).
2. En `local.properties`, en la raíz del proyecto, agregá esta propiedad sin comillas:

   `TMDB_READ_ACCESS_TOKEN=tu_token_de_lectura`

3. Sincronizá Gradle y ejecutá `app` / `debug` desde Android Studio.

`local.properties` está excluido por `.gitignore`. El token se incorpora al APK durante la compilación y puede extraerse de una aplicación distribuida; este mecanismo evita versionarlo, pero no protege un token cliente dentro de un APK público.

Sin token, las consultas de red muestran un error localizado y ofrecen reintento. Room mantiene disponibles los deseados sin conexión.

## Estructura

```text
com.example.entregaseminario/
├── FilmWishApp.kt
├── MainActivity.kt
├── di/AppModule.kt
├── domain/model/Movie.kt
├── data/
│   ├── remote/
│   │   ├── TmdbService.kt
│   │   ├── LocaleInterceptor.kt
│   │   ├── TmdbAuthInterceptor.kt
│   │   └── dto/ (MoviePage, MovieDto, MovieDetailsDto, GenreDto)
│   ├── local/
│   │   ├── entity/SavedMovie.kt
│   │   ├── dao/WatchlistDao.kt
│   │   └── database/FilmWishDatabase.kt
│   └── repository/ (MovieRepository, TmdbRepository)
├── paging/TmdbPagingSource.kt
└── ui/
    ├── navigation/FilmWishNavigation.kt
    ├── theme/FilmWishTheme.kt
    ├── state/LoadState.kt
    ├── components/ (MovieCard, Poster, UiStateComponents)
    ├── home/ (HomeScreen, HomeViewModel)
    └── details/ (DetailScreen, DetailViewModel)
```

Cada modelo DTO, entidad, DAO, base Room, servicio, interceptor, repositorio, fuente de paginación, ViewModel, pantalla, tema y componente reutilizable tiene su propio archivo. `LoadState.kt` reúne el estado y sus dos funciones de soporte, que se usan juntas para convertir resultados y errores. `FilmWishNavigation.kt` mantiene `Section` junto a las rutas que representa.

## Ejecutar y validar

Desde la raíz del proyecto:

- Windows: `gradlew.bat :app:assembleDebug`
- Tests unitarios: `gradlew.bat :app:testDebugUnitTest`

El APK debug se genera en `app/build/outputs/apk/debug/app-debug.apk`.
