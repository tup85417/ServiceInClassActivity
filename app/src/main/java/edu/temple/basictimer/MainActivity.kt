package edu.temple.basictimer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.telecom.ConnectionService
import android.widget.Button
import android.widget.TextView
import java.util.Timer

class MainActivity : AppCompatActivity() {

    val startButton : Button by lazy {
        findViewById(R.id.startButton)
    }

    val stopButton : Button by lazy {
        findViewById(R.id.stopButton)
    }

    val timerTextView : TextView by lazy {
        findViewById(R.id.textView)
    }

    var timerBinder: TimerService.TimerBinder? = null

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            timerBinder = p1 as TimerService.TimerBinder
        }

        override fun onServiceDisconnected(p0: ConnectionService) {
            timerBinder = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {

            timerBinder?. run{

                if (!isRunning) {
                    start(100)
                    startButton.text = "Pause"
                }

                else {
                    pause()
                    startButton.text = if(isPaused) "Resume" else "Pause"

                }

            }

        }

    }

        bindService(

            Intent(this, TimerService:: class.java),
            serviceConnection,
            BIND_AUTO_CREATE

        )
        
        stopButton.setOnClickListener {

            timerBinder?.stop()

        }
    }
}