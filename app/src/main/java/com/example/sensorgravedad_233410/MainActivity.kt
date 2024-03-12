package com.example.sensorgravedad_233410

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var administradorSensores: SensorManager
    private lateinit var sensorGravedad: Sensor
    private lateinit var textoGravedad: TextView
    private lateinit var imagenView: ImageView
    private lateinit var botonContar: Button
    private lateinit var botonMostrarConteo: Button
    private lateinit var botonDetenerConteo: Button
    private lateinit var textoConteo: TextView
    private var contando = false
    private var conteo = 0
    private var ultimoIndiceImagen = 0
    private lateinit var preferenciasCompartidas: SharedPreferences
    val recursosImagenes = listOf(
        R.drawable.idefault,
        R.drawable.arriba,
        R.drawable.abajo,
        R.drawable.izquierda,
        R.drawable.derecha
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoGravedad = findViewById(R.id.textoGravedad)
        imagenView = findViewById(R.id.imagenView)
        botonContar = findViewById(R.id.botonContar)
        botonMostrarConteo = findViewById(R.id.botonMostrarConteo)
        botonDetenerConteo = findViewById(R.id.botonDetenerConteo)
        textoConteo = findViewById(R.id.textoConteo)

        // Inicializar SharedPreferences
        preferenciasCompartidas = getSharedPreferences("DatosSensor", Context.MODE_PRIVATE)

        // Inicializar el sensor de gravedad
        administradorSensores = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorGravedad = administradorSensores.getDefaultSensor(Sensor.TYPE_GRAVITY)!!

        botonContar.setOnClickListener {
            if (!contando) {
                iniciarConteo()
            }
        }

        botonMostrarConteo.setOnClickListener {
            mostrarConteo()
        }

        botonDetenerConteo.setOnClickListener {
            detenerConteo()
        }
    }

    private fun iniciarConteo() {
        contando = true
        conteo = 0
        textoConteo.text = "Contando..."
        ultimoIndiceImagen = 0
        botonContar.isEnabled = false
        administradorSensores.registerListener(this, sensorGravedad, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun detenerConteo() {
        contando = false
        textoConteo.text = ""
        botonContar.isEnabled = true
        administradorSensores.unregisterListener(this)
        guardarConteo()
    }

    private fun guardarConteo() {
        // Guardar el número de veces que cambió la imagen en SharedPreferences
        val editor = preferenciasCompartidas.edit()
        editor.putInt("conteo", conteo)
        editor.apply()
    }

    private fun mostrarConteo() {
        // Mostrar el número de veces que cambió la imagen guardado en SharedPreferences
        val conteo = preferenciasCompartidas.getInt("conteo", 0)
        textoConteo.text = "Número de cambios: $conteo"
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (contando) {
            administradorSensores.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_GRAVITY) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            textoGravedad.text = "Datos del Sensor de Gravedad: \nX: $x\nY: $y\nZ: $z"
            val indiceImagen = when {
                y > 1 -> 1 // Movimiento hacia arriba
                y < -1 -> 2 // Movimiento hacia abajo
                x > 1 -> 3 // Movimiento hacia la izquierda
                x < -1 -> 4 // Movimiento hacia la derecha
                else -> 0 // Imagen default
            }
            if (indiceImagen != ultimoIndiceImagen) {
                conteo++
                ultimoIndiceImagen = indiceImagen
                imagenView.setImageResource(recursosImagenes[indiceImagen])
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //No se implementa.
    }
}
