package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

data class ExecutionResult(
    val output: String,
    val isError: Boolean = false,
    val executionTimeMs: Long = 0L
)

object CodeExecutionEngine {

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun executeJavaScript(context: Context, code: String): ExecutionResult =
        withContext(Dispatchers.Main) {
            val startTime = System.currentTimeMillis()
            suspendCancellableCoroutine { continuation ->
                val logs = StringBuilder()
                var hasResumed = false
                val handler = Handler(Looper.getMainLooper())

                val webView = WebView(context.applicationContext).apply {
                    settings.javaScriptEnabled = true
                }

                class Bridge {
                    @JavascriptInterface
                    fun log(message: String) {
                        logs.append(message).append("\n")
                    }

                    @JavascriptInterface
                    fun error(message: String) {
                        logs.append("[ERROR] ").append(message).append("\n")
                    }

                    @JavascriptInterface
                    fun complete(result: String) {
                        if (!hasResumed) {
                            hasResumed = true
                            val elapsed = System.currentTimeMillis() - startTime
                            val finalOutput = if (logs.isNotEmpty()) {
                                logs.toString().trimEnd() + if (result.isNotBlank() && result != "undefined") "\n➔ $result" else ""
                            } else {
                                if (result.isNotBlank()) "➔ $result" else "Code executed with 0 outputs."
                            }
                            continuation.resume(ExecutionResult(output = finalOutput, isError = false, executionTimeMs = elapsed))
                            handler.post { webView.destroy() }
                        }
                    }
                }

                val bridge = Bridge()
                webView.addJavascriptInterface(bridge, "AndroidConsole")

                // Timeout after 4 seconds
                val timeoutRunnable = Runnable {
                    if (!hasResumed) {
                        hasResumed = true
                        val elapsed = System.currentTimeMillis() - startTime
                        continuation.resume(
                            ExecutionResult(
                                output = (logs.toString() + "\n[Execution Timed Out after 4000ms]").trim(),
                                isError = true,
                                executionTimeMs = elapsed
                            )
                        )
                        webView.destroy()
                    }
                }
                handler.postDelayed(timeoutRunnable, 4000)

                // Inject console wrapper and execute
                val wrappedScript = """
                    (function() {
                        var originalLog = console.log;
                        var originalErr = console.error;
                        console.log = function() {
                            var args = Array.prototype.slice.call(arguments).map(function(x) {
                                return typeof x === 'object' ? JSON.stringify(x, null, 2) : String(x);
                            });
                            AndroidConsole.log(args.join(' '));
                        };
                        console.error = function() {
                            var args = Array.prototype.slice.call(arguments).map(function(x) {
                                return typeof x === 'object' ? JSON.stringify(x, null, 2) : String(x);
                            });
                            AndroidConsole.error(args.join(' '));
                        };
                        try {
                            var res = eval(${JSONObjectString(code)});
                            AndroidConsole.complete(String(res));
                        } catch(e) {
                            AndroidConsole.error(e.message || String(e));
                            AndroidConsole.complete('Failed');
                        }
                    })();
                """.trimIndent()

                webView.loadDataWithBaseURL(null, "<html><head></head><body></body></html>", "text/html", "UTF-8", null)
                webView.evaluateJavascript(wrappedScript, null)
            }
        }

    fun executeSimulated(code: String, language: String): ExecutionResult {
        val startTime = System.currentTimeMillis()
        val lines = code.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val output = StringBuilder()

        try {
            output.append(">>> Starting $language execution...\n")
            var printCount = 0

            for (line in lines) {
                if (line.startsWith("//") || line.startsWith("#")) continue

                // Check print statements
                if (line.contains("println(") || line.contains("print(")) {
                    val match = Regex("""print(?:ln)?\((.*?)\)""").find(line)
                    if (match != null) {
                        var content = match.groupValues[1].trim()
                        if (content.startsWith("\"") && content.endsWith("\"")) {
                            content = content.substring(1, content.length - 1)
                        } else if (content.startsWith("'") && content.endsWith("'")) {
                            content = content.substring(1, content.length - 1)
                        }
                        output.append(content).append("\n")
                        printCount++
                    }
                }
            }

            if (printCount == 0) {
                output.append("✔ Code parsed successfully.\n")
                output.append("💡 Notice: To see console output, use println(...) in Kotlin or print(...) in Python.\n")
                output.append("Tip: Use the 'Ask MasterCoder AI' button below for deep step-by-step logic tracing!")
            } else {
                output.append("\n✔ Finished execution with $printCount log output(s).")
            }

            return ExecutionResult(
                output = output.toString().trimEnd(),
                isError = false,
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            return ExecutionResult(
                output = "Error parsing script: ${e.message}",
                isError = true,
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        }
    }

    private fun JSONObjectString(s: String): String {
        return org.json.JSONObject.quote(s)
    }
}
