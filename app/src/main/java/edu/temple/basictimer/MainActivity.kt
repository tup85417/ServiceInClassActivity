
package edu.temple.basictimer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val startButton: Button by lazy {
        findViewById(R.id.startButton)
    }

    private val stopButton: Button by lazy {
        findViewById(R.id.stopButton)
    }

    private val timerTextView: TextView by lazy {
        findViewById(R.id.textView)
    }

    private var timerBinder: TimerService.TimerBinder? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            timerBinder = service as TimerService.TimerBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            TimerInfo.currentTime.collect { time ->
                timerTextView.text = time.toString()
            }
        }

        startButton.setOnClickListener {
            timerBinder?.run {
                if (!isRunning) {
                    start(100)
                    startButton.text = "Pause"
                } else {
                    pause()
                    startButton.text =
                        if (isPaused) "Resume" else "Pause"
                }
            }
        }

        stopButton.setOnClickListener {
            timerBinder?.stop()
            startButton.text = "Start"
            timerTextView.text = "0"
        }

        isBound = bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.menu_play -> {
                startButton.performClick()
                true
            }

            R.id.menu_stop -> {
                stopButton.performClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        timerBinder = null
        super.onDestroy()
    }
}
