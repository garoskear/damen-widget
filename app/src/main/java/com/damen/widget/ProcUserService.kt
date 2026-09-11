package com.damen.widget

// Shizuku içinde root/shell kimliğiyle çalışan servis.
// `ps` çıktısını ham metin olarak döndürür, ayrıştırma uygulamada yapılır.
class ProcUserService : IProcService.Stub() {

    override fun topProcesses(): String {
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "ps -A -o COMM,RSS"))
            val out = p.inputStream.bufferedReader().readText()
            p.waitFor()
            out
        } catch (t: Throwable) {
            ""
        }
    }
}
