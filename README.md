# MISW4502-ProyectoFinal2-Android-Mobile

**CCP** – Aplicación Android para la gestión de clientes, visitas y pedidos en tiempo real.

---

## 📋 Descripción

Esta aplicación permite a los vendedores:

* Registrar visitas a clientes.
* Explorar categorías y productos.
* Añadir productos al carrito y confirmar pedidos.
* Visualizar histórico de pedidos y resúmenes.
* Sincronizar datos con el backend periódicamente o mediante WebSockets.

---

## 📂 Estructura del Proyecto

```
/
├── .github/                # Workflows de CI (GitHub Actions)
├── app/                    # Módulo Android principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/g18/ccp
│   │   │   │   ├── core/           # Constantes y utilidades
│   │   │   │   ├── data/           # Modelos, DAOs, Room, Retrofit
│   │   │   │   ├── repository/     # Implementación de Repositorios
│   │   │   │   ├── presentation/   # ViewModels y Lógica de UI
│   │   │   │   └── ui/             # Composables y temas
│   │   └── res/                    # layouts XML, strings, temas
│   └── build.gradle.kts
├── build.gradle.kts        # Configuración de Gradle raíz
├── settings.gradle.kts
└── gradle.properties
```

---

## ⚙️ Tecnologías y Librerías

* **Kotlin**
* **Jetpack Compose** para UI declarativa
* **AndroidX** (ViewModel, Lifecycle, Navigation Compose)
* **Coroutines & Flow** para asincronía
* **Room** para persistencia local
* **Retrofit + Gson** para llamadas REST
* **Koin** para inyección de dependencias
* **Mockk + JUnit4 + Coroutines Test** para unit tests
* **GitHub Actions** para CI

---

## 🚀 Requisitos Previos

* Android Studio Arctic Fox o superior
* JDK 11+
* Dispositivo o emulador Android con API 21+

---

## 🔧 Instalación y Primer Arranque

1. Clona el repositorio:

   ```bash
   git clone https://github.com/CCP-G18/MISW4502-ProyectoFinal2-Android-Mobile.git
   cd MISW4502-ProyectoFinal2-Android-Mobile
   ```
2. Abre el proyecto en Android Studio.
3. Sincroniza Gradle (Tools → Kotlin → Sync Project).
4. Ejecuta la app en un emulador o dispositivo (Run ▶️).

---

## 🏗️ Arquitectura

Patrón **MVVM**:

* **View** (Composables) observan `StateFlow` del ViewModel.
* **ViewModel** gestiona estado y llamadas a repositorios.
* **Repositories** abstraen datos (local con Room, remoto con Retrofit).
* **Data Mapping** mediante funciones de extensión para convertir Entities ↔︎ DTOs ↔︎ Domain Models.

---

## 📦 Módulos Clave

* **CustomerVisit**: Registro de visitas y selector de fecha.
* **Category & Products**: Navegación, búsqueda y filtrado de productos.
* **Cart**: Carrito de compras con control de cantidad y confirmación de pedido.
* **Orders**: Histórico de pedidos y pantalla de resumen.

---

## 🔍 Pruebas Unitarias

Ejecuta todos los tests con:

```bash
./gradlew testDebugUnitTest
```

Reglas usadas:

* `MainDispatcherRule` para coroutines.
* `InstantTaskExecutorRule` para LiveData/Compose.

---

## 📈 Integración Continua

Configurado en `.github/workflows/android.yml`:

1. Checkout del código.
2. Setup de JDK y Android SDK.
3. Compilar (`./gradlew assembleDebug`).
4. Ejecutar tests (
   `./gradlew testDebugUnitTest testDebugUnitTestCoverage jacocoTestReport checkCoverage `).

---

## 🤝 Contribuciones

1. Haz fork del repositorio.
2. Crea una rama (`feature/tu-cambio`).
3. Realiza cambios y commitea.
4. Abre un Pull Request describiendo tu propuesta.

---

## 📝 Licencia

Este proyecto está bajo licencia **MIT**. Consulta el archivo `LICENSE` para más detalles.
