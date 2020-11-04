package m.tech.democoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    val TAG = "AppDebug"

    var jobA = Job()
    var jobB = Job()
    var job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(IO + jobB).launch {
            Log.d(TAG, "jobA starting...")
            delay(5000)
            Log.d(TAG, "onCreate: jobA done")
        }

        CoroutineScope(IO + jobA).launch {
            Log.d(TAG, "jobB starting...")
            delay(2000)
            Log.d(TAG, "onCreate: jobB done")
        }

        job.cancel()


//        baiToanFlow()
//        baiToanTiki()
        baiToanSeekbar()
    }

    private fun baiToanSeekbar() {
        var jobProcessImage: Job? = null

        val seekbar = findViewById<AppCompatSeekBar>(R.id.seek_bar)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (jobProcessImage == null || !jobProcessImage!!.isActive) {
                    jobProcessImage = Job()
                    CoroutineScope(Main + jobProcessImage!!).launch {
                        processImage(p1)
                        jobProcessImage!!.cancel()
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private suspend fun processImage(progress: Int) {
        delay(1000)
        Log.d(TAG, "processImage success: $progress")
    }

    fun clickButton(view: View) {
        CoroutineScope(IO).launch {
            baiToanFlow().collect {
                Log.d(TAG, "clickButton: $it")
            }
        }
    }

    private fun baiToanFlow(): Flow<Int> {
        return flow<Int> {
            for (i in 0..10) {
                delay(500)
                emit(i)
            }
        }
    }

    suspend fun getId(): Int {
        delay(1000)
        return 1
    }

    suspend fun getListById(id: Int) {
        delay(2000)
        Log.d(TAG, "getListById: done")
    }

    private fun baiToanTiki() {
        val jobA = Job()

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d(TAG, "handleException: ${throwable.message}")
        }

        CoroutineScope(IO + jobA).launch(handler) {
            val promise = async {
                launch {
                    try {
                        fakeApi1()
                    } catch (e: Exception) {
                        Log.e(TAG, "fakeApi1 error")
                    }
                }

                launch {
                    fakeApi2()
                }
            }

            promise.await()

            fakeApi3()
        }

        jobA.invokeOnCompletion {
            Log.d(TAG, "onCreate: JobA done $it")
        }
    }

    suspend fun fakeApi3() {
        Log.d(TAG, "fakeApi3: loading...")
        delay(500)
        Log.d(TAG, "fakeApi3: done")
    }

    suspend fun fakeApi2() {
        Log.d(TAG, "fakeApi2: loading...")
        delay(2000)
        Log.d(TAG, "fakeApi2: done")
    }

    suspend fun fakeApi1() {
        Log.d(TAG, "fakeApi1: loading...")
        delay(500)
        val x = 1 / 0
        Log.d(TAG, "fakeApi1: done")
    }


}