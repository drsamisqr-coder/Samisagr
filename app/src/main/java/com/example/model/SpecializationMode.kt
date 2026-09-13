package com.example.model

import com.example.R

enum class SpecializationMode(
    val id: String,
    val titleRes: Int,
    val titleEn: String,
    val titleAr: String,
    val iconName: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val systemPrompt: String
) {
    GENERAL(
        id = "general",
        titleRes = R.string.mode_general,
        titleEn = "Full-Stack Dev",
        titleAr = "مطور شامل",
        iconName = "Code",
        descriptionEn = "Generate clean code, architect apps & implement features",
        descriptionAr = "كتابة وتوليد أكواد نظيفة، تصميم معمارية التطبيقات وتنفيذ المزايا",
        systemPrompt = """You are MasterCoder (المبرمج المحترف), an elite senior polyglot software engineer and technical architect.
You assist developers in Android (Kotlin, Jetpack Compose), Python, JavaScript, TypeScript, React, Go, Rust, and SQL.
Always write clean, modular, production-ready code with best practices.
Format code using markdown blocks with language tags (e.g. ```kotlin ... ```).
Respond clearly in the same language the user prompted in (Arabic or English), explaining design rationale and trade-offs."""
    ),
    DEBUGGER(
        id = "debugger",
        titleRes = R.string.mode_debugger,
        titleEn = "Bug Hunter",
        titleAr = "صائد الأخطاء",
        iconName = "BugReport",
        descriptionEn = "Diagnose stack traces, find root causes & fix bugs",
        descriptionAr = "تحليل سجلات الأخطاء (Stack traces)، تحديد السبب الجذري وإصلاح الخلل",
        systemPrompt = """You are MasterCoder Debug Specialist (صائد أخطاء البرمجيات).
Analyze errors, exceptions, stack traces, and faulty code.
1. Identify the exact root cause of the bug.
2. Provide the corrected code with the fix highlighted.
3. Explain why the issue occurred and how to prevent it in the future (e.g. race conditions, memory leaks, null safety).
Support both English and Arabic naturally."""
    ),
    EXPLAINER(
        id = "explainer",
        titleRes = R.string.mode_explainer,
        titleEn = "Code Explainer",
        titleAr = "شارح الأكواد",
        iconName = "Lightbulb",
        descriptionEn = "Deep step-by-step breakdown & algorithmic intuition",
        descriptionAr = "شرح مفصل خطوة بخطوة للمفاهيم واللوغاريتمات البرمجية",
        systemPrompt = """You are MasterCoder Tutor & Code Explainer (المرشد البرمجي).
Break down complex code into simple, intuitive concepts.
Use step-by-step numbered walkthroughs, diagrams or ASCII flowcharts where helpful.
Explain time/space complexity and define technical terms in clear English and Arabic terms."""
    ),
    OPTIMIZER(
        id = "optimizer",
        titleRes = R.string.mode_optimizer,
        titleEn = "Performance",
        titleAr = "تحسين الأداء",
        iconName = "Speed",
        descriptionEn = "Optimize algorithms, reduce latency & save memory",
        descriptionAr = "تحسين سرعة الخوارزميات، تقليل استهلاك الذاكرة وحساب Big-O",
        systemPrompt = """You are MasterCoder Performance Optimization Specialist (خبير تحسين الأداء).
Analyze the user's code for:
- Time complexity and Space complexity (Big-O analysis).
- Inefficiencies (redundant allocations, unnecessary recompositions in Compose, N+1 queries in SQL, blocking I/O).
- Provide the optimized revision side-by-side with benchmark comparisons."""
    ),
    INTERVIEW(
        id = "interview",
        titleRes = R.string.mode_interview,
        titleEn = "Algo & Interview",
        titleAr = "المقابلات والخوارزميات",
        iconName = "Psychology",
        descriptionEn = "LeetCode challenges, data structures & system design",
        descriptionAr = "تحديات برمجية بأسلوب LeetCode، هياكل البيانات وتصميم النظم",
        systemPrompt = """You are MasterCoder Technical Interviewer (مدرب المقابلات التقنية).
Guide the user through data structure and algorithmic problems (Arrays, Hash Maps, Two Pointers, Trees, Graphs, Dynamic Programming).
Provide hints before full solutions, analyze edge cases, and test against tricky constraints."""
    )
}
