package com.example.model

data class QuizChallenge(
    val id: String,
    val category: String, // "Android / Kotlin", "Algorithms", "Python", "Web / JS"
    val questionEn: String,
    val questionAr: String,
    val codeSnippet: String? = null,
    val optionsEn: List<String>,
    val optionsAr: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationAr: String,
    val xpReward: Int = 50
)

object AcademyRepository {
    val challenges = listOf(
        QuizChallenge(
            id = "q1",
            category = "Android / Kotlin",
            questionEn = "In Jetpack Compose, what is the key difference between 'remember' and 'derivedStateOf'?",
            questionAr = "في Jetpack Compose، ما هو الفرق الجوهري بين 'remember' و 'derivedStateOf'؟",
            codeSnippet = """
val listState = rememberLazyListState()
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}
""".trimIndent(),
            optionsEn = listOf(
                "derivedStateOf only triggers recomposition when its calculated result actually changes",
                "derivedStateOf stores data permanently across process death",
                "remember executes asynchronously on Dispatchers.IO",
                "There is no difference, both do identical caching"
            ),
            optionsAr = listOf(
                "يقوم derivedStateOf بإعادة بناء الواجهة فقط عندما تتغير القيمة المحسوبة الفعلية",
                "يحفظ derivedStateOf البيانات عبر إنهاء المعالجة ونظام التشغيل",
                "يعمل remember في الخلفية بشكل غير متزامن على IO",
                "لا يوجد أي فرق بينهما"
            ),
            correctIndex = 0,
            explanationEn = "derivedStateOf creates a state object that only notifies Compose observers when the derived calculation result changes, preventing dozens of unnecessary recompositions as scroll position changes continuously.",
            explanationAr = "derivedStateOf ينشئ كائن حالة يُشعر Compose فقط عندما تتغير القيمة النهائية الناتجة عن الحساب، مما يمنع عشرات عمليات إعادة البناء غير الضرورية عند التمرير المستمر.",
            xpReward = 50
        ),
        QuizChallenge(
            id = "q2",
            category = "Algorithms",
            questionEn = "What is the worst-case time complexity of standard QuickSort with first-element pivot?",
            questionAr = "ما هو التعقيد الزمني لأسوأ حالة (Worst Case) لخوارزمية QuickSort باختيار العنصر الأول؟",
            codeSnippet = """
fun quickSort(arr: IntArray, low: Int, high: Int) {
    if (low < high) {
        val p = partition(arr, low, high)
        quickSort(arr, low, p - 1)
        quickSort(arr, p + 1, high)
    }
}
""".trimIndent(),
            optionsEn = listOf(
                "O(N log N)",
                "O(N²)",
                "O(N)",
                "O(log N)"
            ),
            optionsAr = listOf(
                "O(N log N)",
                "O(N²)",
                "O(N)",
                "O(log N)"
            ),
            correctIndex = 1,
            explanationEn = "When the array is already sorted or reverse-sorted, partitioning on the first element produces severely unbalanced partitions of size 0 and N-1, degrading performance to O(N²).",
            explanationAr = "عندما تكون المصفوفة مرتبة مسبقاً، يؤدي اختيار العنصر الأول كمحور إلى تقسيمات غير متوازنة (0 و N-1) مما يؤدي إلى تعقيد تربيعي O(N²).",
            xpReward = 50
        ),
        QuizChallenge(
            id = "q3",
            category = "Android / Kotlin",
            questionEn = "Which Coroutine dispatcher should be used for CPU-intensive mathematical sorting or JSON parsing?",
            questionAr = "أي من مديري Coroutine Dispatchers يجب استخدامه للمهام التي تستهلك المعالج مثل الفرز أو معالجة JSON الكبيرة؟",
            codeSnippet = null,
            optionsEn = listOf(
                "Dispatchers.Main",
                "Dispatchers.IO",
                "Dispatchers.Default",
                "Dispatchers.Unconfined"
            ),
            optionsAr = listOf(
                "Dispatchers.Main",
                "Dispatchers.IO",
                "Dispatchers.Default",
                "Dispatchers.Unconfined"
            ),
            correctIndex = 2,
            explanationEn = "Dispatchers.Default is backed by a thread pool with size equal to the number of CPU cores, designed specifically for CPU-intensive tasks, unlike IO which is optimized for blocking network/disk calls.",
            explanationAr = "تم تصميم Dispatchers.Default خصيصاً للمهام كثيفة المعالجة (CPU-bound) بحجم تجمع خيوط يتناسب مع عدد أنوية المعالج.",
            xpReward = 50
        ),
        QuizChallenge(
            id = "q4",
            category = "Python",
            questionEn = "What is the primary advantage of a Python generator using 'yield' over returning a list?",
            questionAr = "ما هي الميزة الأساسية للمولدات في بايثون (Generators باستخدام yield) مقارنة بإرجاع قائمة List عادية؟",
            codeSnippet = """
def stream_large_dataset():
    for item in fetch_millions():
        yield process(item)
""".trimIndent(),
            optionsEn = listOf(
                "Generators evaluate lazily and consume O(1) auxiliary memory instead of storing all items in RAM",
                "Generators execute faster than C extensions",
                "Generators automatically bypass Python's Global Interpreter Lock (GIL)",
                "Generators make all objects immutable"
            ),
            optionsAr = listOf(
                "المولدات تعمل بالتقييم الكسول (Lazy Evaluation) وتستهلك ذاكرة O(1) بدلاً من حجز كل العناصر في الرام",
                "المولدات تنفذ أسرع من ملحقات لغة C",
                "المولدات تتخطى قفل المفسر العام (GIL) تلقائياً",
                "المولدات تجعل جميع الكائنات غير قابلة للتعديل"
            ),
            correctIndex = 0,
            explanationEn = "Generators produce items on the fly only when iterated over, keeping memory consumption near O(1) even when processing gigabytes of streamed data.",
            explanationAr = "المولدات تنتج العناصر فور طلبها فقط عند التكرار، مما يحافظ على استهلاك الذاكرة ثابتاً O(1) حتى عند معالجة غيغابايت من البيانات.",
            xpReward = 50
        ),
        QuizChallenge(
            id = "q5",
            category = "Web / JS",
            questionEn = "In JavaScript, what does 'Promise.allSettled()' do differently compared to 'Promise.all()'?",
            questionAr = "في جافاسكريبت، ما الفرق بين Promise.allSettled() و Promise.all()؟",
            codeSnippet = """
const p1 = fetch('/api/user');
const p2 = fetch('/api/settings');
const results = await Promise.allSettled([p1, p2]);
""".trimIndent(),
            optionsEn = listOf(
                "allSettled waits for all promises to finish even if some fail, returning status and results for each",
                "allSettled rejects immediately on the first error encountered",
                "allSettled runs promises synchronously one by one",
                "allSettled can only be used with WebSockets"
            ),
            optionsAr = listOf(
                "allSettled تنتظر اكتمال جميع الوعود حتى لو فشل بعضها، وترجع حالة وقيمة كل وعد",
                "allSettled تفشل وتتوقف فوراً عند أول خطأ يحدث",
                "allSettled تنفذ الوعود بالتسلسل واحداً تلو الآخر",
                "allSettled تستخدم فقط مع WebSockets"
            ),
            correctIndex = 0,
            explanationEn = "Promise.all fails-fast as soon as any promise rejects. Promise.allSettled waits for every promise to resolve or reject, ensuring no unfinished requests or uncaught cancellations.",
            explanationAr = "تتوقف Promise.all فوراً عند فشل أي طلب، بينما تنتظر Promise.allSettled اكتمال كل الطلبات وتقدم تقريراً مفصلاً بحالة نجاح أو فشل كل منها.",
            xpReward = 50
        )
    )
}
