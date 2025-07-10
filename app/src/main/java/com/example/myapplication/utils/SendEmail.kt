package com.example.payflowapp.utils
import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

object EmailSender {

    fun sendEmail(
        context: Context,
        passcode: String,
        time: String,
        message: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val url = "https://api.emailjs.com/api/v1.0/email/send"

        val json = JSONObject().apply {

            put("service_id", "service_sjdndv6")
            put("template_id", "__ejs-test-mail-service__")
            put("user_id", "RnMctbiqdgV5FFPea")
            put("template_params", JSONObject().apply {
                put("to_email", "kanicos1107@gmail.com")
                put("passcode", passcode)
                put("time", time)
            })
        }

        val client = OkHttpClient()
        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onFailure("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure("Error: ${response.code}")
                }
            }
        })
    }
}
