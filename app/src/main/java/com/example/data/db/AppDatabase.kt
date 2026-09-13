package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SnippetEntity::class, QuizProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun snippetDao(): SnippetDao
    abstract fun quizProgressDao(): QuizProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mastercoder_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialSnippets(database.snippetDao())
                        database.quizProgressDao().saveProgress(
                            QuizProgressEntity(
                                id = 1,
                                xp = 150,
                                completedChallengesCount = 1,
                                currentStreak = 3,
                                lastCompletedDate = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }

        suspend fun populateInitialSnippets(snippetDao: SnippetDao) {
            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Jetpack Compose StateFlow & Edge-to-Edge",
                    language = "Kotlin",
                    tags = "Android, Compose, UI",
                    description = "Standard modern Android Compose activity with ViewModel StateFlow and proper WindowInsets handling.",
                    isFavorite = true,
                    code = """
@Composable
fun TaskListScreen(viewModel: TaskViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { TopAppBar(title = { Text("My Tasks") }) }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(uiState.tasks, key = { it.id }) { task ->
                TaskCard(task = task)
            }
        }
    }
}
""".trimIndent()
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Room Database DAO & Flow Pattern",
                    language = "Kotlin",
                    tags = "Android, Room, Database",
                    description = "Reactive Room Database DAO pattern returning Flow for automated UI updates.",
                    isFavorite = true,
                    code = """
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}
""".trimIndent()
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Python FastAPI Async Route with Pydantic",
                    language = "Python",
                    tags = "Python, Backend, API",
                    description = "High-performance asynchronous REST API route with typed request validation and JSON response.",
                    isFavorite = false,
                    code = """
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel, Field

app = FastAPI(title="MasterCoder API")

class CodeRequest(BaseModel):
    prompt: str = Field(..., min_length=3)
    language: str = "python"

@app.post("/api/analyze", status_code=status.HTTP_200_OK)
async def analyze_code(req: CodeRequest):
    return {
        "status": "success",
        "lang": req.language,
        "recommendation": "Use list comprehensions and generators for optimal memory."
    }
""".trimIndent()
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Modern JavaScript Async Concurrent Fetch",
                    language = "JavaScript",
                    tags = "JavaScript, Web, Async",
                    description = "Concurrent API fetching with Promise.allSettled and error boundary handling.",
                    isFavorite = false,
                    code = """
async function fetchDeveloperData(userIds) {
    const fetchPromises = userIds.map(async (id) => {
        const response = await fetch("https://api.github.com/users/" + id);
        if (!response.ok) throw new Error("HTTP " + response.status);
        return await response.json();
    });

    const results = await Promise.allSettled(fetchPromises);
    return results.map(r => r.status === 'fulfilled' ? r.value : null);
}
""".trimIndent()
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "SQL Composite Index & Performance Query",
                    language = "SQL",
                    tags = "SQL, Database, Index",
                    description = "Create optimized composite index to prevent full table scans on large transaction tables.",
                    isFavorite = false,
                    code = """
-- Create composite index matching query WHERE & ORDER BY order
CREATE INDEX idx_user_orders_status_date 
ON orders (user_id, status, created_at DESC);

-- Query that fully utilizes the index
EXPLAIN QUERY PLAN
SELECT order_id, total_amount, created_at
FROM orders
WHERE user_id = 42 AND status = 'COMPLETED'
ORDER BY created_at DESC
LIMIT 20;
""".trimIndent()
                )
            )
        }
    }
}
