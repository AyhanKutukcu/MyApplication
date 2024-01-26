package com.example.myapplication

import android.os.AsyncTask
import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.net.InetSocketAddress
import java.net.Socket
import androidx.compose.foundation.lazy.items
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Query()
                }
            }
        }
    }
}
internal var cevap = "yok"
@Composable
@Preview
fun Query() {
    var il by remember { mutableStateOf("") }
    var ilce by remember { mutableStateOf("") }
    var semtBucakMahalle by remember { mutableStateOf("") }
    var mahalle by remember { mutableStateOf("") }
    var serverResponse by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,

        ) {
        TextField(
            value = il,
            onValueChange = { newIl -> il = newIl },
            label = { Text("İl") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = ilce,
            onValueChange = { newIlce -> ilce = newIlce },
            label = { Text("Ilçe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = semtBucakMahalle,
            onValueChange = { newSemtBucakMahalle -> semtBucakMahalle = newSemtBucakMahalle},
            label = { Text(text = "Semt")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = mahalle,
            onValueChange = { newMahalle -> mahalle = newMahalle },
            label = { Text("Mahalle") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(
            onClick = {
                val query = "${il.uppercase()},${ilce.uppercase()},${semtBucakMahalle.uppercase()},${mahalle.uppercase()}"
                val serverIp = "192.168.19.35"
                val serverPort = 12345
                val serverAddress = InetSocketAddress(serverIp, serverPort)

                ServerTask(object : ServerTask.OnServerTaskListener {
                    override fun onTaskComplete(result: String) {
                        serverResponse = result

                    }
                }).execute(serverAddress, query)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ARA")
        }

        Text(text = "$serverResponse   ",
            Modifier.background(Color.White))
    }
}

class ServerTask(private val listener: OnServerTaskListener) : AsyncTask<Any, Void, String>() {
    interface OnServerTaskListener {
        fun onTaskComplete(result: String)
    }

    override fun doInBackground(vararg params: Any): String {
        val serverAddress = params[0] as InetSocketAddress
        val query = params[1] as String

        var response = ""

        try {
            val clientSocket = Socket()
            clientSocket.connect(serverAddress)

            val outputStream = clientSocket.getOutputStream()
            val inputStream = clientSocket.getInputStream()

            outputStream.write(query.toByteArray())

            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            if (bytesRead != -1) {
                response = buffer.decodeToString(0, bytesRead)
            }

            clientSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    override fun onPostExecute(result: String) {
        listener.onTaskComplete(result)
    }
}
