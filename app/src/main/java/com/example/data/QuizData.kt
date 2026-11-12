package com.example.data

data class QuizQuestion(
    val id: Int,
    val category: String, // "BEGINNER", "INTERMEDIATE", "ADVANCED"
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val codeSnippet: String? = null
)

object QuizData {
    val questions = listOf(
        QuizQuestion(
            id = 1,
            category = "BEGINNER",
            questionText = "Which clause is used in SQL to filter rows based on specific conditions?",
            options = listOf("ORDER BY", "WHERE", "GROUP BY", "SELECT"),
            correctIndex = 1,
            explanation = "The WHERE clause is used to filter records so only rows meeting the condition(s) are returned. SELECT specifies columns, GROUP BY gathers records into subcategories, and ORDER BY handles sorting."
        ),
        QuizQuestion(
            id = 2,
            category = "BEGINNER",
            questionText = "What does the DISTINCT keyword do when added after SELECT?",
            options = listOf(
                "It sorts the results alphabetically.",
                "It retrieves columns from multiple tables.",
                "It filters out duplicate rows, returning only unique values.",
                "It highlights null entries in the database."
            ),
            correctIndex = 2,
            explanation = "The DISTINCT keyword is utilized to filter results to retrieve only unique (non-duplicate) data entries in the selected column(s)."
        ),
        QuizQuestion(
            id = 3,
            category = "BEGINNER",
            questionText = "Which wildcard character in SQL LIKE pattern matching represents zero, one, or multiple characters?",
            options = listOf("% (Percent)", "_ (Underscore)", "* (Asterisk)", "? (Question Mark)"),
            correctIndex = 0,
            explanation = "In standard relational SQL, '%' matches any sequence of zero or more characters, while '_' matches exactly a single character."
        ),
        QuizQuestion(
            id = 4,
            category = "INTERMEDIATE",
            questionText = "Which type of JOIN returns ALL records from the left table and matched rows from the right table, filling with NULL values on the right if there is no match?",
            options = listOf("INNER JOIN", "FULL OUTER JOIN", "CROSS JOIN", "LEFT OUTER JOIN"),
            correctIndex = 3,
            explanation = "A LEFT OUTER JOIN (or simply LEFT JOIN) guarantees all rows from the left-hand table are outputted. If no matching row is found in the right table, its columns are displayed as NULL."
        ),
        QuizQuestion(
            id = 5,
            category = "INTERMEDIATE",
            questionText = "What is the primary difference between WHERE and HAVING clauses?",
            options = listOf(
                "WHERE is used for tables, while HAVING is used for virtual views.",
                "WHERE filters rows before grouping with GROUP BY, whereas HAVING filters aggregated statistics after grouping.",
                "HAVING is an older standard, while WHERE is the modern standard.",
                "Standard databases support WHERE but do not allow HAVING."
            ),
            correctIndex = 1,
            explanation = "The WHERE clause filters individual rows before grouping takes place. The HAVING clause applies filters on groups/aggregate values (like SUM, AVG) after grouping takes place."
        ),
        QuizQuestion(
            id = 6,
            category = "INTERMEDIATE",
            questionText = "Analyze this query. What does it return?",
            codeSnippet = "SELECT COUNT(*), department_id \nFROM employees \nGROUP BY department_id \nHAVING COUNT(*) > 10;",
            options = listOf(
                "The total count of employees across all departments.",
                "A listing of departments that have exactly 10 employees.",
                "A listing of department IDs along with their headcount, but only for departments with more than 10 employees.",
                "All employee profiles working in departments labeled with IDs larger than 10."
            ),
            correctIndex = 2,
            explanation = "This query groups employees by department, counts how many employees are in each department, and uses HAVING to only yield rows where the headcount (COUNT(*)) is strictly greater than 10."
        ),
        QuizQuestion(
            id = 7,
            category = "ADVANCED",
            questionText = "Which of the following describes a 'Correlated Subquery'?",
            options = listOf(
                "A query that calculates aggregate sums on two tables concurrently.",
                "An inner query that depends on values from the outer query for its evaluation and runs once for each candidate row in the outer query.",
                "A query that is written in standard CTE format using the WITH clause.",
                "A subquery that evaluates once at the beginning of execution, returning a constant scalar result."
            ),
            correctIndex = 1,
            explanation = "A correlated subquery is nested, referencing one or more columns from the outer query. It is evaluated once for each row processed by the outer query, making it potentially slower than independent subqueries."
        ),
        QuizQuestion(
            id = 8,
            category = "ADVANCED",
            questionText = "Given a CTE defined with the WITH clause, which statement is true?",
            options = listOf(
                "CTEs can only be used in INSERT queries.",
                "A CTE creates a permanent table in the database catalog.",
                "A CTE defines a temporary result set accessible within the scope of the immediate following SELECT, INSERT, UPDATE, or DELETE.",
                "CTEs are compiled as index views on server startup."
            ),
            correctIndex = 2,
            explanation = "Common Table Expressions (CTEs) define a transient result set that serves as a virtual view, only active during the single execution of the succeeding query."
        ),
        QuizQuestion(
            id = 9,
            category = "ADVANCED",
            questionText = "Which clause is essential to construct recursive queries to parse hierarchies like tree structures?",
            options = listOf("GROUP BY ROLLUP", "WITH RECURSIVE", "CONNECT BY PRIORITY", "SELECT ACCUMULATIVE"),
            correctIndex = 1,
            explanation = "WITH RECURSIVE is the SQL-standard clause used to construct recursive queries. This allows queries to reference their own output repeatedly, which is highly useful for parsing hierarchal tables: organization charts, network nodes, and item parentages."
        )
    )
}
