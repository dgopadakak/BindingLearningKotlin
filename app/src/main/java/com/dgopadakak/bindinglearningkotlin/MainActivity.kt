package com.dgopadakak.bindinglearningkotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dgopadakak.serverbindinglearningkotlin.ICalculator

class MainActivity : AppCompatActivity() {
    private var calculator: ICalculator? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            calculator = ICalculator.Stub.asInterface(service)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            calculator = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button_get_result).setOnClickListener {
            val a = findViewById<EditText>(R.id.editTextNum1).text.toString().toIntOrNull()?: 0
            val b = findViewById<EditText>(R.id.editTextNum2).text.toString().toIntOrNull()?: 0
            findViewById<TextView>(R.id.textViewResult).text = calculator?.add(a, b).toString()
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(createExplicitIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    private fun createExplicitIntent(): Intent {
        val intent = Intent("com.dgopadakak.serverbindinglearningkotlin.REMOTE_CONNECTION")
        val services = packageManager.queryIntentServices(intent, 0)
        if (services.isEmpty()) {
            throw IllegalStateException("Приложение-сервер не установлено")
        }
        return Intent(intent).apply {
            val resolveInfo = services[0]
            val packageName = resolveInfo.serviceInfo.packageName
            val className = resolveInfo.serviceInfo.name
            component = ComponentName(packageName, className)
        }
    }
}