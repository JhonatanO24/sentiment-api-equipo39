## 🧠 Data Science & Modelo

### Tabla de Contenidos

* [🎯 Objetivo](#objetivo-del-modelo)
* [📜 Datasets](#datasets)
* [🅾️ 2 Modelos](#modelo-binario)
* [🧹 Limpieza](#limpieza-y-preprocesamiento) 
* [↘️ Vectorización (TF-IDF)](#vectorización-de-texto-tf-idf)
* [🔰 Modelo de Clasificación](#modelo-de-clasificación)
* [📏 Métricas de evaluación](#métricas-de-evaluación)
* [❌ Matriz de confusión sin umbral](#matriz-de-confusión-sin-umbral)
* [✅ Matriz de confusión con umbral](#matriz-de-confusión-con-umbral-051)
* [💾 Serialización del modelo](#serialización-del-modelo)
* [🛜 Integración con la API](#integración-con-la-api)
* [🧠 Ejemplos de predicción](#ejemplos-de-predicción)
* [🚫 Limitaciones](#limitaciones)
* [✨ Posibles mejoras futuras](#posibles-mejoras-futuras)



### 🎯Objetivo del modelo

El objetivo de este modelo es clasificar automáticamente textos de reseñas y comentarios en función de su sentimiento, permitiendo identificar si un mensaje expresa una opinión positiva o negativa. Dentro de las necesidades del cliente se encuentran dar atención a los usuarios, priorizando a los comentarios negativos.

Inicialmente se intentó construir un modelo de clasificación ternaria (Positivo / Neutro / Negativo) utilizando reseñas de productos de Amazon, donde las clases se definieron a partir del número de estrellas (Con 3 estrellas como neutro). Sin embargo, se observó que muchos usuarios asignaban tres estrellas a comentarios claramente negativos, lo que generaba ruido en las etiquetas y afectaba la calidad de las predicciones, especialmente para la clase “Neutro”.

Debido a esta inconsistencia en el etiquetado, se decidió trabajar con un enfoque binario (Positivo / Negativo), utilizando reseñas de películas del dataset de IMDB en español, donde las clases están mejor definidas. Este cambio permitió obtener un modelo más confiable y con mejor desempeño.

El modelo es consumido por una API desarrollada en FastAPI, la cual recibe un JSON con texto de usuarios (reseñas, comentarios o mensajes) y devuelve automáticamente:

- La predicción de sentimiento (Positivo o Negativo)
- La probabilidad asociada a dicha predicción
- Las palabras que dieron peso a la predicción

Esta solución está orientada a empresas de atención al cliente, marketing y operaciones que necesitan analizar grandes volúmenes de opiniones para:

- Detectar rápidamente quejas o problemas
- Priorizar respuestas a comentarios negativos
- Medir la satisfacción de los clientes a lo largo del tiempo

### 📜DataSets

Para el desarrollo del proyecto se trabajó con dos enfoques: un modelo binario y un intento de modelo ternario.

#### Modelo binario
- Datos obtenidos desde Kaggle
- Se utilizaron aproximadamente 50.000 registros
- Idioma original: inglés y español
- Se filtraron para eliminar los comentarios en inglés
- Columnas principales: text y sentiment

#### Modelo ternario
- Datos obtenidos desde Hugging Face
- Se utilizaron aproximadamente 210.000 registros
- Idioma: español
- Columnas principales: text y label

El modelo elegido para el MVP fue el binario, ya que presentó un comportamiento más estable y con mayor precisión que el ternario.

### Distribución del tamaño del texto según el sentimiento

Distribución del tamaño del texto según el sentimiento

![Distribución del tamaño del texto según el sentimiento](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/distribucion_tama%C3%B1o_texto_segun_sentimiento.png?raw=true)
![Longitud de textos por tipo de sentimiento](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/longitud_textos_por_tipo_sentimiento.png?raw=true)

### 🧹Limpieza y preprocesamiento

En ambos notebooks se realizaron los siguientes pasos:

- Revisión de valores nulos
- Conversión de todos los textos a minúsculas
- Eliminación de signos de puntuación y caracteres especiales
- Eliminación de stopwords
- Eliminación de reseñas duplicadas

En el modelo binario se utilizaron stopwords personalizadas.

**Filtrado de idioma en el modelo binario.**
Durante el análisis se observó que algunas reseñas muy largas (más de 1000 palabras) aparecían como “español” en el dataset, pero en realidad estaban en inglés, por lo que se aplicó un filtro adicional por idioma para garantizar la coherencia del conjunto de datos.

### Vectorización de texto (TF-IDF)

Durante las iteraciones del proyecto se identificó una limitación común en modelos basados únicamente en palabras: las negaciones (ej. “no fue una buena experiencia”) tendían a ser mal interpretadas cuando se eliminaban stopwords o cuando el modelo dependía solo de tokens completos.

Para mitigar este problema, se adoptó una estrategia de vectorización híbrida, combinando dos enfoques complementarios:

- **TF-IDF a nivel de palabras**: Captura el significado semántico global del texto.
- Utiliza un vectorizador con unigramas y bigramas, también otro con trigramas hasta 5 combinaciones de palabras, permitiendo representar expresiones como “muy bueno” o “nada recomendable”.
- Se aplican stopwords personalizadas para reducir ruido, manteniendo términos relevantes.
- La frecuencia se suaviza para evitar que palabras muy repetidas dominen el modelo.

### 🔰Modelo de Clasificación

Se utilizó Regresión Logística como modelo de clasificación, configurada con los siguientes parámetros:

- `random_state = 42`
- `max_iter = 1000`
- `class_weight = 'balanced'`
- `solver = 'liblinear'`
- `C = 20`

Este modelo fue elegido porque:
- Presenta un buen desempeño en tareas de clasificación de texto cuando se combina con representaciones TF-IDF.
- Es estable, eficiente y fácilmente interpretable, lo que lo hace adecuado para un entorno de producción.
- Permite obtener probabilidades de predicción, requisito clave para la API desarrollada.

El uso de `class_weight='balanced'` ayuda a compensar posibles desbalances entre clases, mientras que `max_iter=1000` asegura la convergencia del modelo.

El parámetro `C` se incrementó para reducir la regularización, permitiendo al modelo aprender patrones más complejos derivados de la combinación de vectorización entre palabras y caracteres, lo cual contribuyó a una mejor interpretación de frases con negación.

### 📏Métricas de evaluación

```
              precision    recall  f1-score    support

negativo       0.88       0.86       0.87       7155
positivo       0.86       0.88       0.87       7155

accuracy                           0.87      14310
macro avg      0.87       0.87       0.87      14310
weighted avg   0.87       0.87       0.87      14310
```

#### ❌Matriz de confusión sin umbral
![Matriz de confusión sin umbral](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/matriz_de_confucion_no-umbral.png?raw=true)

#### ✅Matriz de confusión con umbral 0.51
![Matriz de confusión con umbral](https://github.com/JhonatanO24/sentiment-api-equipo39/blob/data-science/data_science/assets/matriz_de%20confucion_con_umbral.png?raw=true)

### Matriz de Confusión

En el modelo binario, después del entrenamiento se obtienen 1.646 predicciones erróneas de 14.310 predicciones totales, lo que representa tan solo un 11,51% de error.

Los errores se ven reflejados de mejor manera en la matriz de confusión.
En la matriz se observan los errores divididos en los falsos positivos (comentarios negativos clasificados como positivos) y los falsos negativos (comentarios positivos clasificados como negativos).

### Selección del umbral

Al probar diferentes umbrales, se llegó a la conclusión de que el umbral más conveniente era 0.51 porque así disminuían los falsos positivos sin aumentar de manera tan exagerada los falsos negativos.

```
Umbral     Falsos Positivos (Riesgo) Falsos Negativos (Revisión extra)
----------------------------------------------------------------------
0.4        967                       517
0.45       875                       591
0.5        769                       670
0.51       746                       688
0.55       681                       764
0.58       633                       824
0.6        605                       879
0.61       592                       899
0.62       580                       919
0.63       559                       939
0.635      550                       952
0.64       544                       959
0.645      533                       976
0.65       527                       994
0.655      517                       1007
0.66       505                       1016
0.665      501                       1031
0.67       494                       1051
0.675      482                       1066
0.68       478                       1075
0.69       461                       1104
0.7        441                       1144
0.75       378                       1303
0.8        312                       1502
```

### Selección del modelo final

De los dos modelos desarrollados se seleccionó el modelo binario porque el modelo ternario presentó problemas para identificar correctamente la clase neutra. Por esta razón, se eligió el modelo binario como versión final para el MVP.

### 💾Serialización del modelo

Para la integración con el Back-End se serializó el pipeline completo ((vectorizador1 + vectorizador2) + modelo) usando joblib.

#### Pipeline final
- TF-IDF con los parámetros definidos
- Regresión Logística entrenada

El pipeline completo se guarda en un solo archivo para que el Back-End pueda cargarlo y hacer predicciones con el modelo ya entrenado.

### Ejecución del modelo

Existen dos notebooks:
- `Modelo_hackaton_binario.ipynb`
- `Modelo_hackaton_ternario.ipynb`

El notebook principal utilizado para el MVP es: `Modelo_hackaton_binario.ipynb`

Para entrenar el modelo y obtener el modelo serializado:
1. Abrir el notebook
2. Ejecutar todas las celdas en orden, desde el inicio hasta el final
3. Al finalizar, se genera el archivo serializado del modelo

### 🛜Integración con la API

El modelo se carga y expone a través de un endpoint con FastAPI.

- **POST /sentiment**: Recibe el texto y hace la predicción retornando el sentimiento y la probabilidad.
- **POST /sentiment-explain**: Recibe el texto, hace la predicción con el modelo, además utilizando un umbral y retorna el sentimiento, la probabilidad y las 3 palabras más significativas para la predicción. Este fue el endpoint utilizado por el backend en nuestro MVP por las palabras y el uso del umbral.

El umbral se definió en 0.51, obtenido a través de iteraciones en el notebook buscando el mejor rendimiento al priorizar las reseñas negativas.

Para las palabras se utiliza la función `explicar_prediccion()` que utiliza el vectorizador del modelo para transformar el texto de entrada y buscar esas palabras para posteriormente ordenarlas según el impacto que tiene, en orden descendente para las predicciones 'Positivo' y en orden ascendente para las predicciones 'Negativo', al final retorna las 3 palabras más significativas para la predicción.

#### Formato de entrada (JSON)
```json
{
  "text": "Comentario enviado desde el backend"
}
```

#### Formato de salida (JSON)
```json
{
    "original_text": "Comentario enviado desde el backend",
    "prevision": "Negativo/Positivo",
    "probabilidad": 0.999,
    "palabras_clave": [
        "Lista de máximo",
        "3 palabras con más",
        "peso para la predicción"
    ]
}
```

### 🧠Ejemplos de predicción

```json
{
    "original_text": "Hasta ahora me han funcionado perfectamente, compatibles con los perfiles amp y xmp, recomendadas, y gracias a su tamaño pude usar un enfriamiento mas grande.",
    "prevision": "Positivo",
    "probabilidad": 0.787,
    "palabras_clave": [
        "perfectamente",
        "gracias", 
        "grande"
    ]
}
```

```json
{
    "original_text": "Teléfono basura, no funciona como debería, se cortan las llamadas o simplemente no enlaza, además de que el audio del auricular es deficiente.",
    "prevision": "Negativo",
    "probabilidad": 0.955,
    "palabras_clave": [
        "basura",
        "simplemente",
        "deficiente"
    ]
}
```

```json
{
    "original_text": "No me gusto, el pantalon segun talla chico- mediano, parece grande-extra grande y muy largo, la sudadera esta super chica, tela muy delgada.",
    "prevision": "Negativo",
    "probabilidad": 0.82,
    "palabras_clave": [
        "no gusto",
        "delgada",
        "parece"
    ]
}
```

Ejemplos con reseñas de productos varios de "Amazon" y respuestas del modelo:

```json
{
  "original_text": "Nunca volvería a comprar en esta empresa. La tarjeta se convirtió en humo y no la cubrieron, lo que provocó daños físicos a bordo. A la que no puedes acceder a menos que te abran. Mis hijos de 11 años ahorraron todo el verano para que esto fuera basura en 2 meses",
  "prevision": "Negativo",
  "probabilidad": 0.72,
  "palabras_clave": ["basura", "menos", "bordo"]
}
```

```json
{
    "original_text": "El título dice 'Mancuernas' no especifica que solo es una.. Me siento estafado",
    "prevision": "Negativo",
    "probabilidad": 0.908,
    "palabras_clave": [
        "siento", 
        "título", 
        "solo"
    ]
}
```

```json
{
    "original_text": "Está descuadrada. No todos los tornillos entran bien y es difícil de armar",
    "prevision": "Negativo",
    "probabilidad": 0.553,
    "palabras_clave": [
        "entran",
        "difícil", 
        "bien"
    ]
}
```

### 🚫Limitaciones

- Actualmente, el modelo solo predice a **'Positivo'** o **'Negativo'**.
- El modelo funciona para el idioma español, aunque fue compensado en el backend usando traducciones.

### ✨Posibles mejoras futuras

- Mejorar los falsos negativos para reducir los tiempos necesarios para la atención y aumentar la calidad de predicciones del modelo.
- Implementar un rango más amplio de etiquetas, empezar con un modelo ternario y posiblemente expandirlo hasta abarcar **"Muy positivo"**, **"Positivo"**, **"Neutro"**, **"Negativo"** y **"Muy negativo"**.
- Entrenar modelos especializados a los idiomas disponibles en el frontend y que son traducidos en el backend.
- Mejorar cómo el modelo entienda las negaciones de las palabras positivas.

---

## 📄 Licencia

Este proyecto está licenciado bajo la **MIT License** - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 🙏 Agradecimientos

**Gracias a todos los contribuidores**

💻 Yohan Sebastian Ospina Gonzalez

💻 Julio Alejandro Serrepe Ramírez


**y a la comunidad Oracle ONE**

[![Made with ❤️](https://img.shields.io/badge/Made%20with%20❤️-red.svg)]()

**Desarrollado con ☕ y 🎵 durante el Hackathon Oracle ONE 2026**
