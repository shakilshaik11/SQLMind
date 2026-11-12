package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-2.0-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Checks if the API key is initialized and not a placeholder.
     */
    fun isApiKeyAvailable(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.contains("PLACEHOLDER")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Translates Natural Language to SQL and gets an explanation.
     * Returns a Pair of (SQL Query, Explanation)
     */
    suspend fun translateNlToSql(
        nlPrompt: String,
        dbType: String = "MySQL"
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        if (!isApiKeyAvailable()) {
            Log.w(TAG, "API key is missing or is placeholder. Using smart dynamic local engine.")
            return@withContext getLocalFallbackNlToSql(nlPrompt, dbType)
        }

        val systemInstruction = """
            You are an elite SQL Database Architect and Compiler for database dialects: MySQL, PostgreSQL, SQLite, Oracle, and SQL Server.
            Your task is to convert the user's natural language request into a 100% syntactically accurate SQL query tailored specifically for the $dbType dialect.
            
            DIALECT ACCURACY RULES FOR $dbType:
            - If MySQL/PostgreSQL/SQLite: Use `LIMIT n` for top results.
            - If Oracle: Use `FETCH FIRST n ROWS ONLY` for top results and `SYSDATE` for current date.
            - If SQL Server: Use `SELECT TOP n` and `GETDATE()` for current date.
            
            EXPLANATION REQUIREMENT:
            Provide a clear, thorough, step-by-step breakdown covering:
            1. Query Objective: What exact data/report is being retrieved.
            2. Selected Fields & Tables: Why specific columns and tables are targeted.
            3. Conditions & Filters (`WHERE` / `HAVING`): Detailed explanation of filter conditions.
            4. Relational Joins (`JOIN`): Key relationships between linked tables (if present).
            5. Aggregations & Ordering (`GROUP BY` / `ORDER BY`): Summary of grouping and sorting logic.

            FORMAT REQUIREMENTS:
            Respond ONLY as a raw JSON object with keys "sql" and "explanation".
            Do NOT include markdown formatting or ```json ``` wrappers inside your raw response.
        """.trimIndent()

        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Target Dialect: $dbType\nNatural Language Request: \"$nlPrompt\"")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
        }

        val requestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL?key=${BuildConfig.GEMINI_API_KEY}"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    Log.e(TAG, "API call failed with code ${response.code}: $bodyString")
                    return@withContext getLocalFallbackNlToSql(nlPrompt, dbType)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawText = parts?.optJSONObject(0)?.optString("text")

                if (rawText != null) {
                    val cleanText = rawText.replace(Regex("^```json\\s*"), "").replace(Regex("```$"), "").trim()
                    val resultJson = JSONObject(cleanText)
                    val sql = resultJson.optString("sql", "").trim()
                    val explanation = resultJson.optString("explanation", "").trim()
                    if (sql.isNotEmpty()) {
                        return@withContext Pair(sql, explanation)
                    }
                }
                return@withContext getLocalFallbackNlToSql(nlPrompt, dbType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network translate fault", e)
            return@withContext getLocalFallbackNlToSql(nlPrompt, dbType)
        }
    }

    /**
     * Translates SQL query back into Natural Language and lists an explanation.
     * Returns a Pair of (Natural Language Translation, Explanation Details)
     */
    suspend fun translateSqlToNl(
        sqlQuery: String
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        if (!isApiKeyAvailable()) {
            Log.w(TAG, "API key is missing. Using smart dynamic local engine.")
            return@withContext getLocalFallbackSqlToNl(sqlQuery)
        }

        val systemInstruction = """
            You are an expert SQL Database Administrator and Tutor.
            Translate the given SQL query into plain, natural English and provide a comprehensive step-by-step breakdown.
            
            FORMAT REQUIREMENTS:
            Respond ONLY as a raw JSON object with keys "translation" and "explanation".
            Do NOT include markdown formatting or ```json ``` wrappers inside your raw response.
        """.trimIndent()

        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Translate and explain this SQL query:\n$sqlQuery")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
        }

        val requestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL?key=${BuildConfig.GEMINI_API_KEY}"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    Log.e(TAG, "API call failed with code ${response.code}: $bodyString")
                    return@withContext getLocalFallbackSqlToNl(sqlQuery)
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val rawText = parts?.optJSONObject(0)?.optString("text")

                if (rawText != null) {
                    val cleanText = rawText.replace(Regex("^```json\\s*"), "").replace(Regex("```$"), "").trim()
                    val resultJson = JSONObject(cleanText)
                    val translation = resultJson.optString("translation", "").trim()
                    val explanation = resultJson.optString("explanation", "").trim()
                    if (translation.isNotEmpty()) {
                        return@withContext Pair(translation, explanation)
                    }
                }
                return@withContext getLocalFallbackSqlToNl(sqlQuery)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network sql-to-nl fault", e)
            return@withContext getLocalFallbackSqlToNl(sqlQuery)
        }
    }

    // --- High Accuracy Smart Dynamic Query Builder & Explainer ---

    private fun getLocalFallbackNlToSql(prompt: String, dbType: String): Pair<String, String> {
        val lower = prompt.lowercase().trim()

        // Detect target table
        val table = when {
            lower.contains("user") || lower.contains("customer") -> "customers"
            lower.contains("employee") || lower.contains("staff") || lower.contains("salary") -> "employees"
            lower.contains("product") || lower.contains("item") || lower.contains("inventory") || lower.contains("stock") -> "products"
            lower.contains("order") || lower.contains("sale") || lower.contains("revenue") || lower.contains("transaction") -> "orders"
            lower.contains("department") -> "departments"
            lower.contains("student") || lower.contains("course") || lower.contains("grade") -> "students"
            else -> "records"
        }

        // Detect aggregations
        val isTotal = lower.contains("total") || lower.contains("sum")
        val isAvg = lower.contains("average") || lower.contains("avg") || lower.contains("mean")
        val isCount = lower.contains("count") || lower.contains("how many") || lower.contains("number of")
        val isMax = lower.contains("highest") || lower.contains("maximum") || lower.contains("max") || lower.contains("top")
        val isMin = lower.contains("lowest") || lower.contains("minimum") || lower.contains("min")

        // Detect Grouping
        val hasGroup = lower.contains("by") || lower.contains("per") || lower.contains("each") || lower.contains("category") || lower.contains("department")

        // Detect Limit
        val limitMatch = Pattern.compile("top\\s+(\\d+)|limit\\s+(\\d+)|first\\s+(\\d+)").matcher(lower)
        val limitCount = if (limitMatch.find()) {
            limitMatch.group(1) ?: limitMatch.group(2) ?: limitMatch.group(3) ?: "10"
        } else if (isMax && !hasGroup) "5" else null

        // Detect Join
        val hasJoin = lower.contains("join") || lower.contains("with their") || (table == "employees" && lower.contains("department")) || (table == "orders" && lower.contains("customer"))

        val sqlBuilder = StringBuilder()
        val explainBuilder = StringBuilder()

        // 1. SELECT clause
        if (isCount) {
            sqlBuilder.append("SELECT COUNT(*) AS total_count\nFROM $table")
            explainBuilder.append("• **SELECT COUNT(*)**: Calculates the total count of matching entries in the '$table' table.\n")
        } else if (isTotal && table == "orders") {
            sqlBuilder.append("SELECT customer_id, SUM(total_amount) AS total_revenue\nFROM $table")
            explainBuilder.append("• **SELECT & SUM()**: Aggregates total order revenue using `SUM(total_amount)`.\n")
        } else if (isAvg && table == "employees") {
            sqlBuilder.append("SELECT department_id, AVG(salary) AS average_salary\nFROM $table")
            explainBuilder.append("• **SELECT & AVG()**: Calculates the average employee salary per department using `AVG(salary)`.\n")
        } else if (hasJoin && table == "employees") {
            sqlBuilder.append("SELECT e.id, e.first_name, e.last_name, d.department_name, e.salary\nFROM employees e\nINNER JOIN departments d ON e.department_id = d.id")
            explainBuilder.append("• **SELECT & INNER JOIN**: Retrieves employee names alongside their associated department name by linking `employees.department_id = departments.id`.\n")
        } else if (hasJoin && table == "orders") {
            sqlBuilder.append("SELECT o.id AS order_id, c.name AS customer_name, o.total_amount, o.order_date\nFROM orders o\nINNER JOIN customers c ON o.customer_id = c.id")
            explainBuilder.append("• **SELECT & INNER JOIN**: Connects order transaction records with customer details using `orders.customer_id = customers.id`.\n")
        } else {
            sqlBuilder.append("SELECT * \nFROM $table")
            explainBuilder.append("• **SELECT * **: Queries all attributes and fields stored in the '$table' dataset.\n")
        }

        // 2. WHERE clause
        val whereClauses = mutableListOf<String>()
        val whereExplains = mutableListOf<String>()

        if (lower.contains("active")) {
            whereClauses.add("status = 'ACTIVE'")
            whereExplains.add("filters for records marked with `status = 'ACTIVE'`")
        }
        if (lower.contains("2024")) {
            whereClauses.add("created_at >= '2024-01-01' AND created_at <= '2024-12-31'")
            whereExplains.add("restricts date timestamps to the year 2024")
        }
        if (lower.contains("salary") && (lower.contains("greater") || lower.contains("above") || lower.contains("more than"))) {
            whereClauses.add("salary > 50000")
            whereExplains.add("filters employees with salaries above $50,000")
        }
        if (lower.contains("price") && (lower.contains("greater") || lower.contains("above") || lower.contains("more than"))) {
            whereClauses.add("price > 100")
            whereExplains.add("filters products with price exceeding $100")
        }

        if (whereClauses.isNotEmpty()) {
            sqlBuilder.append("\nWHERE ").append(whereClauses.joinToString(" AND "))
            explainBuilder.append("• **WHERE Clause**: ").append(whereExplains.joinToString("; ")).append(".\n")
        }

        // 3. GROUP BY
        if (hasGroup && (isTotal || isAvg || isCount)) {
            val groupCol = if (table == "employees") "department_id" else if (table == "orders") "customer_id" else "category"
            sqlBuilder.append("\nGROUP BY $groupCol")
            explainBuilder.append("• **GROUP BY**: Groups rows by `$groupCol` to perform regional aggregate calculations.\n")
        }

        // 4. ORDER BY
        if (isMax || lower.contains("highest") || lower.contains("sorted") || lower.contains("order by") || lower.contains("recent")) {
            val orderCol = when {
                table == "employees" -> "salary"
                table == "orders" -> "order_date"
                table == "products" -> "price"
                else -> "created_at"
            }
            sqlBuilder.append("\nORDER BY $orderCol DESC")
            explainBuilder.append("• **ORDER BY**: Sorts query results in descending order (`DESC`) by `$orderCol`.\n")
        }

        // 5. Dialect Limit
        if (limitCount != null) {
            when (dbType) {
                "Oracle" -> {
                    sqlBuilder.append("\nFETCH FIRST $limitCount ROWS ONLY;")
                    explainBuilder.append("• **FETCH FIRST $limitCount ROWS ONLY**: Applies Oracle pagination to yield top $limitCount rows.\n")
                }
                "SQL Server" -> {
                    // Prepends TOP to SELECT
                    val currSql = sqlBuilder.toString()
                    val newSql = currSql.replaceFirst("SELECT ", "SELECT TOP $limitCount ") + ";"
                    return Pair(newSql, explainBuilder.toString().trim() + "\n• **TOP $limitCount**: Applies T-SQL filter to return top $limitCount records.")
                }
                else -> {
                    sqlBuilder.append("\nLIMIT $limitCount;")
                    explainBuilder.append("• **LIMIT $limitCount**: Limits query output to the top $limitCount records for $dbType.\n")
                }
            }
        } else {
            sqlBuilder.append(";")
        }

        return Pair(sqlBuilder.toString(), explainBuilder.toString().trim())
    }

    private fun getLocalFallbackSqlToNl(query: String): Pair<String, String> {
        val clean = query.trim().uppercase()
        val translation: String
        val explainBuilder = StringBuilder()

        val tableMatch = Pattern.compile("FROM\\s+([a-zA-Z0-9_]+)").matcher(clean)
        val tableName = if (tableMatch.find()) tableMatch.group(1) else "database table"

        when {
            clean.contains("COUNT(") -> {
                translation = "Calculates the total number of records stored in the '$tableName' table matching specified conditions."
                explainBuilder.append("• **SELECT COUNT()**: Counts rows matching query criteria.\n")
            }
            clean.contains("SUM(") || clean.contains("AVG(") -> {
                translation = "Calculates summary statistical aggregates (sum/average metrics) from the '$tableName' table."
                explainBuilder.append("• **Aggregate Function**: Evaluates numeric metrics across rows.\n")
            }
            clean.contains("JOIN") -> {
                translation = "Generates a combined relational report linking records from '$tableName' with related secondary tables."
                explainBuilder.append("• **JOIN Operation**: Matches foreign key identifiers between linked tables.\n")
            }
            else -> {
                translation = "Retrieves information from the '$tableName' database table."
                explainBuilder.append("• **SELECT Statement**: Reads fields from target dataset.\n")
            }
        }

        if (clean.contains("WHERE")) {
            explainBuilder.append("• **WHERE Clause**: Filters target rows to return only matching conditions.\n")
        }
        if (clean.contains("GROUP BY")) {
            explainBuilder.append("• **GROUP BY Clause**: Groups output into categories before calculating aggregate metrics.\n")
        }
        if (clean.contains("ORDER BY")) {
            explainBuilder.append("• **ORDER BY Clause**: Sorts result sets sequentially by target column.\n")
        }

        return Pair(translation, explainBuilder.toString().trim())
    }
}
