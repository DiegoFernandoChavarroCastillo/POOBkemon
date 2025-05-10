# POOBkemon

**Proyecto final del curso de Programación Orientada a Objetos (POOB)**  
Este proyecto es una simulación inspirada en el juego Pokémon Esmeralda. El jugador puede interactuar con criaturas, realizar batallas y gestionar su equipo a través de una interfaz gráfica.

---

## 🎮 ¿Qué contiene el proyecto?

- **Lógica del juego**: ubicada en el paquete `domain`, donde están las clases que modelan el comportamiento del juego.
- **Interfaz gráfica**: ubicada en el paquete `presentation`, contiene la clase `BattleGUI.java` con el método `main`, desde donde se lanza la aplicación.
- **Pruebas unitarias**: ubicadas en la carpeta `Test`, aseguran el correcto funcionamiento de las clases del dominio.

---

## 📁 Estructura del proyecto
'''
POOBkemon/
├── src/
│ ├── domain/ # Lógica y entidades del juego
│ └── presentation/ # Interfaz gráfica (BattleGUI.java con main)
├── Test/ # Pruebas unitarias
├── .idea/ # Archivos de configuración de IntelliJ
├── out/ # Archivos compilados
├── .gitignore
├── pruebaPook.iml
└── README.md
'''

---

## ▶️ Cómo compilar y ejecutar el proyecto desde consola

### ✅ Requisitos previos

- Tener instalado **Java JDK 17** o superior.
- Tener configurado el compilador `javac` y el intérprete `java` en el `PATH` del sistema.
- Estar ubicado en la carpeta raíz del proyecto (donde está el archivo `.iml` y las carpetas `src/` y `Test/`).

### 💻 Compilar el proyecto

Abre una terminal en la raíz del proyecto y ejecuta:

```bash
javac -d out -cp src src/presentation/BattleGUI.java src/domain/*.java

### Luego ejecuta la aplicación con el comando:

java -cp out presentation.BattleGUI


## 👥 Autores

- **Diego Chavarro**
- **Diego Rodríguez**

Escuela Colombiana de Ingeniería Julio Garavito  
Curso: Programación Orientada a Objetos (POOB)
