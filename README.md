# MISW4502-ProyectoFinal2-Android-Mobile
CCP, Android mobile app

# CCP Android

Este repositorio contiene la aplicación Android de **CCP (Customer Control Platform)**, una solución
móvil para vendedores y administradores de clientes y pedidos.

---

## 📋 Descripción

CCP Android permite:

* Registrar y gestionar visitas a clientes.
* Navegar categorías y productos, añadir ítems al carrito.
* Visualizar y confirmar pedidos en tiempo real.
* Sincronización con el backend mediante polling o socket.
* Soporte completo para flujos de trabajo de vendedores.

---

## 🏗 Arquitectura y Patrones

* **MVVM** con ViewModels y StateFlows.
* **Repository Pattern** para abstracción de datos (local y remoto).
* **Room** para persistencia local de productos y carrito.
* **Retrofit** para llamadas HTTP a servicios REST.
* **Koin** para inyección de dependencias.
* **Coroutines & Flows** para asincronía y streams reactivos.

---

## 📁 Estructura de carpetas

```
app/
├─ src/
│  ├─ main/
│  │  ├─ java/com/g18/ccp
│  │  │  ├─ data/               # Modelos, DAOs, servicios
│  │  │  ├─ presentation/       # ViewModels y pantallas Compose
│  │  │  ├─ repository/         # Implementaciones de repositorios
│  │  │  ├─ ui/                 # Components de UI reutilizables
│  │  │  └─ core/               # Constantes y utilidades
│  │  └─ res/                   # layouts, strings, temas
│  └─ test/                     # Unit tests
└─ build.gradle
```

---

## ⚙️ Configuración

1. Clonar repo:

git
clone [git@github.com:CCP-G18/MISW4502-ProyectoFinal2-Android-Mobile.git](https://github.com/CCP-G18/MISW4502-ProyectoFinal2-Android-Mobile)
cd ccp-android

````
2. Abrir en **Android Studio Bumblebee** o superior.  
3. Configurar tu archivo `local.properties` con URL de API si aplica.  
4. Sincronizar Gradle.

---

## 🚀 Ejecución

- **Debug**: Run ▶️ en Android Studio.  
- **Release**: Ejecuta en terminal:  
  ```bash
./gradlew assembleRelease
````

---

## 🔬 Tests

* Unit tests con **Junit4**, **Mockk** y **Kotlin Coroutines Test**.
* Para ejecutar:

  ```bash
  ```

./gradlew testDebugUnitTest testDebugUnitTestCoverage jacocoTestReport checkCoverage

```

---

## 🤝 Contribuciones

1. Haz un **fork** del repositorio.  
2. Crea una rama (`git checkout -b feature/nueva-funcion`).  
3. Realiza tus cambios y **commitea** (`git commit -m 'feat: descripción'`).  
4. Haz **push** a tu rama (`git push origin feature/nueva-funcion`).  
5. Abre un **Pull Request**.

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver `LICENSE` para más detalles.

```
