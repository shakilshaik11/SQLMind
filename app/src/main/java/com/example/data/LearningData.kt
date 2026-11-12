package com.example.data

enum class LessonLevel(val displayName: String, val badgeColor: Long) {
    BASIC("Basic", 0xFF10B981),        // Emerald Green
    MODERATE("Moderate", 0xFF0284C7),   // Sky Blue / Sapphire
    ADVANCED("Advanced", 0xFFA855F7)    // Neon Violet / Purple
}

data class Lesson(
    val id: String,
    val title: String,
    val summary: String,
    val theory: String,
    val exampleQuery: String,
    val exampleResultExplanation: String,
    val category: String = "SQL Core",
    val level: LessonLevel = LessonLevel.BASIC
)

object LearningData {
    val lessons = listOf(
        // ==========================================
        // SESSION 1: BASIC LEVEL (FOUNDATIONS)
        // ==========================================
        Lesson(
            id = "B1",
            title = "Relational Database Fundamentals",
            summary = "Understand relational databases, tables, records, columns, and primary keys.",
            theory = """
                A Relational Database Management System (RDBMS) organizes information into structured tables consisting of rows (records) and columns (attributes).
                
                • Table: A structured dataset (e.g. 'employees', 'orders').
                • Primary Key: A unique identifier guaranteeing every record is distinct (e.g. 'id').
                • Foreign Key: A column that references the primary key of another table, creating a relational link.
                
                SQL Commands are split into core families:
                1. DQL (Data Query Language): Retrieving information (SELECT).
                2. DML (Data Manipulation Language): Modifying data (INSERT, UPDATE, DELETE).
                3. DDL (Data Definition Language): Creating/modifying structures (CREATE, ALTER, DROP).
            """.trimIndent(),
            exampleQuery = "SELECT * FROM employees;",
            exampleResultExplanation = "Retrieves all columns (*) and every row currently stored in the 'employees' table.",
            category = "Database Basics",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B2",
            title = "Selecting Columns & Aliases (AS)",
            summary = "Target exact fields, rename columns for reports, and perform inline calculations.",
            theory = """
                In production, querying with '*' is discouraged because it wastes memory and bandwidth. Always explicitly list the required columns.
                
                • Column Selection: SELECT col1, col2 FROM table_name;
                • Column Aliasing (AS): Renames an output column temporarily for clean reporting without modifying the underlying database schema.
                • Inline Calculations: You can perform mathematical computations (+, -, *, /) directly in your SELECT statement.
            """.trimIndent(),
            exampleQuery = "SELECT first_name AS name, salary, salary * 0.10 AS annual_bonus FROM employees;",
            exampleResultExplanation = "Returns each employee's first name renamed as 'name', their base salary, and a calculated 10% bonus labeled 'annual_bonus'.",
            category = "Query Syntax",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B3",
            title = "Filtering Rows with WHERE Clause",
            summary = "Filter records dynamically using conditional and comparison operators.",
            theory = """
                The WHERE clause filters results so only rows satisfying specific boolean conditions are returned.
                
                Standard Comparison Operators:
                • Equal (=) and Not Equal (!= or <>)
                • Greater than (>) and Greater than or equal (>=)
                • Less than (<) and Less than or equal (<=)
                
                Text values and dates must be wrapped in single quotes (e.g., status = 'ACTIVE'), while numbers are written bare.
            """.trimIndent(),
            exampleQuery = "SELECT first_name, department, salary FROM employees WHERE salary >= 65000 AND status = 'ACTIVE';",
            exampleResultExplanation = "Filters and returns active employees earning at least $65,000 per year, excluding inactive staff.",
            category = "Filtering",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B4",
            title = "Logical Operators: IN, BETWEEN & NOT",
            summary = "Combine complex conditions using discrete sets and inclusive ranges.",
            theory = """
                SQL provides powerful logical operators to simplify multi-condition filtering:
                
                • IN (val1, val2, ...): Matches any value present in a specified set, replacing repetitive 'OR' chains.
                • BETWEEN min AND max: Matches values within an inclusive range (works on numbers, dates, and text).
                • NOT: Inverts any condition (e.g., NOT IN, NOT BETWEEN, NOT LIKE).
            """.trimIndent(),
            exampleQuery = "SELECT product_name, category, price FROM products WHERE category IN ('Electronics', 'Hardware') AND price BETWEEN 50 AND 300;",
            exampleResultExplanation = "Finds products belonging to either Electronics or Hardware whose retail price is between $50 and $300 inclusive.",
            category = "Filtering",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B5",
            title = "Pattern Matching with LIKE & Wildcards",
            summary = "Perform fuzzy text searches using % and _ wildcard operators.",
            theory = """
                When you do not know the exact string, the LIKE operator enables pattern matching:
                
                • % (Percent): Matches zero, one, or multiple characters.
                  - LIKE 'A%' -> Starts with 'A'
                  - LIKE '%tech%' -> Contains 'tech' anywhere
                  - LIKE '%.com' -> Ends with '.com'
                • _ (Underscore): Matches exactly ONE single character.
                  - LIKE 'C_t' -> Matches 'Cat', 'Cot', 'Cut'
            """.trimIndent(),
            exampleQuery = "SELECT customer_name, email, city FROM customers WHERE email LIKE '%@techcorp.io' AND city LIKE 'San%';",
            exampleResultExplanation = "Locates customers with corporate '@techcorp.io' email domains residing in cities starting with 'San' (e.g. San Francisco, San Jose).",
            category = "Text Search",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B6",
            title = "Sorting Results with ORDER BY",
            summary = "Sort result sets in ascending or descending order across multiple columns.",
            theory = """
                Relational databases store records in arbitrary order. To guarantee consistent ordering, always declare an ORDER BY clause.
                
                • ASC: Ascending order (lowest to highest, A to Z). Default when omitted.
                • DESC: Descending order (highest to lowest, Z to A).
                • Multi-column Sorting: Evaluates left-to-right. Secondary columns break ties in primary column values.
            """.trimIndent(),
            exampleQuery = "SELECT first_name, department, salary, hire_date FROM employees ORDER BY department ASC, salary DESC;",
            exampleResultExplanation = "Groups employees alphabetically by department, and within each department, sorts staff from highest-paid to lowest-paid.",
            category = "Sorting",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B7",
            title = "Pagination with LIMIT & OFFSET",
            summary = "Control result batch sizes and skip rows for UI page pagination.",
            theory = """
                Querying millions of rows at once can freeze applications. Use LIMIT and OFFSET to paginate records efficiently.
                
                • LIMIT n: Specifies the maximum number of rows to return.
                • OFFSET m: Specifies the number of rows to skip before returning results.
                
                Pagination Formula:
                LIMIT = pageSize
                OFFSET = (pageNumber - 1) * pageSize
            """.trimIndent(),
            exampleQuery = "SELECT id, order_number, total_amount, order_date FROM orders ORDER BY order_date DESC LIMIT 10 OFFSET 20;",
            exampleResultExplanation = "Fetches Page 3 of order history (10 records per page), skipping the first 20 most recent orders and returning orders 21 through 30.",
            category = "Pagination",
            level = LessonLevel.BASIC
        ),
        Lesson(
            id = "B8",
            title = "Handling NULLs with IS NULL & COALESCE",
            summary = "Manage unknown, missing, or blank database values accurately.",
            theory = """
                In SQL, NULL means 'absence of data' or 'unknown'. NULL cannot be evaluated using '=' or '!=' because NULL is never equal to anything—not even another NULL!
                
                • IS NULL / IS NOT NULL: The only valid operators for testing nullability.
                • COALESCE(val1, val2, ...): Returns the first non-NULL value in its argument list. Essential for preventing calculation errors when math involves null fields.
            """.trimIndent(),
            exampleQuery = "SELECT employee_name, salary, COALESCE(commission_pct, 0.0) AS commission, salary + (salary * COALESCE(commission_pct, 0.0)) AS total_pay FROM employees WHERE termination_date IS NULL;",
            exampleResultExplanation = "Safely computes total pay for active employees, substituting 0.0 for commission whenever commission_pct is missing (NULL).",
            category = "Null Handling",
            level = LessonLevel.BASIC
        ),

        // ==========================================
        // SESSION 2: MODERATE LEVEL (INTERMEDIATE)
        // ==========================================
        Lesson(
            id = "M1",
            title = "Core Aggregate Functions",
            summary = "Compute high-level dataset metrics using COUNT, SUM, AVG, MIN, and MAX.",
            theory = """
                Aggregate functions calculate across multiple rows and collapse them into a single summary scalar value.
                
                Core Functions:
                • COUNT(*): Total count of matching rows including nulls.
                • COUNT(column): Counts only rows where the specified column is NOT NULL.
                • COUNT(DISTINCT column): Counts unique non-null entries.
                • SUM(column): Total numeric addition.
                • AVG(column): Arithmetic mean of numeric values.
                • MIN() / MAX(): Lowest and highest values (numbers, dates, or strings).
            """.trimIndent(),
            exampleQuery = "SELECT COUNT(*) AS total_staff, COUNT(DISTINCT department_id) AS active_depts, ROUND(AVG(salary), 2) AS avg_salary, MIN(salary) AS min_salary, MAX(salary) AS max_salary FROM employees WHERE status = 'ACTIVE';",
            exampleResultExplanation = "Outputs an executive dashboard summary of active headcount, number of distinct departments, and min/max/average compensation metrics.",
            category = "Aggregations",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M2",
            title = "GROUP BY Categorical Aggregations",
            summary = "Segment and summarize datasets by specific attributes and categories.",
            theory = """
                When you need metrics per category (e.g. sales per region, average salary per job title), combine aggregates with GROUP BY.
                
                Golden Rule of GROUP BY:
                Every column listed in your SELECT statement MUST either be an aggregate function (COUNT, SUM, AVG) OR explicitly included in the GROUP BY clause.
            """.trimIndent(),
            exampleQuery = "SELECT department_id, COUNT(*) AS headcount, ROUND(AVG(salary), 2) AS average_salary FROM employees GROUP BY department_id ORDER BY headcount DESC;",
            exampleResultExplanation = "Groups all staff by department, calculating the exact headcount and average compensation for each department, sorted by largest department first.",
            category = "Aggregations",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M3",
            title = "HAVING vs WHERE Clauses",
            summary = "Filter grouped aggregate results after calculation.",
            theory = """
                Understanding the execution order of WHERE and HAVING is a crucial milestone for every SQL developer:
                
                • WHERE filters raw rows BEFORE aggregation occurs. It cannot evaluate aggregates like SUM() or COUNT().
                • HAVING filters grouped buckets AFTER aggregation is completed.
                
                Rule of Thumb:
                Use WHERE for row conditions (status = 'ACTIVE').
                Use HAVING for aggregate conditions (COUNT(*) >= 5).
            """.trimIndent(),
            exampleQuery = "SELECT department_id, COUNT(*) AS team_size, ROUND(AVG(salary), 2) AS avg_sal FROM employees WHERE status = 'ACTIVE' GROUP BY department_id HAVING COUNT(*) >= 5 AND AVG(salary) > 60000;",
            exampleResultExplanation = "First filters active employees, groups them by department, and then filters to retain only departments with at least 5 staff and an average salary above $60k.",
            category = "Aggregations",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M4",
            title = "INNER JOIN (Relational Linking)",
            summary = "Extract interconnected data across related tables using primary and foreign keys.",
            theory = """
                Relational schemas normalize data into distinct tables to eliminate redundancy. An INNER JOIN combines matching records from two tables based on a join condition.
                
                • Syntax: FROM tableA INNER JOIN tableB ON tableA.key = tableB.foreign_key
                • Behavior: Only rows with matching values in BOTH tables appear in the output. If an employee has no department, they are omitted.
            """.trimIndent(),
            exampleQuery = "SELECT e.id, e.first_name, e.last_name, d.department_name, d.location FROM employees e INNER JOIN departments d ON e.department_id = d.id WHERE d.location = 'California';",
            exampleResultExplanation = "Pairs employee records with their department details using the shared department ID, filtering for teams stationed in California.",
            category = "Relational Joins",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M5",
            title = "LEFT JOIN & Missing Relationship Detection",
            summary = "Preserve all records from the primary table even when no child records exist.",
            theory = """
                A LEFT (OUTER) JOIN returns ALL rows from the left table, plus matched data from the right table.
                
                • If a match exists: Right table columns are populated normally.
                • If no match exists: Right table columns return NULL.
                
                Superpower: Detecting orphan records!
                By adding 'WHERE right_table.id IS NULL', you can instantly locate customers who have never placed an order or products that have never sold.
            """.trimIndent(),
            exampleQuery = "SELECT c.id AS customer_id, c.customer_name, o.id AS order_id, COALESCE(o.total_amount, 0.00) AS total_amount FROM customers c LEFT JOIN orders o ON c.id = o.customer_id WHERE o.id IS NULL;",
            exampleResultExplanation = "Uses a LEFT JOIN to find all registered customers who currently have zero orders in the database, perfect for re-engagement marketing.",
            category = "Relational Joins",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M6",
            title = "Data Insertion (INSERT INTO)",
            summary = "Insert single and multi-row records safely into database tables.",
            theory = """
                INSERT statements add fresh records into a table.
                
                Best Practices:
                1. Always explicitly enumerate column names in parentheses to avoid schema mismatch bugs.
                2. Bulk Inserts: Insert multiple rows in a single query by separating value tuples with commas for massive performance gains over individual queries.
            """.trimIndent(),
            exampleQuery = "INSERT INTO customers (name, email, membership_tier, created_at) VALUES ('Elena Rostova', 'elena@enterprise.com', 'PLATINUM', CURRENT_TIMESTAMP), ('Marcus Vance', 'marcus@cloudtech.org', 'GOLD', CURRENT_TIMESTAMP);",
            exampleResultExplanation = "Inserts two new customer records simultaneously with their respective emails, tier levels, and automatic creation timestamps.",
            category = "Data Modification",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M7",
            title = "Updating Records (UPDATE with WHERE)",
            summary = "Modify existing row values safely without altering unintended records.",
            theory = """
                The UPDATE statement modifies values in one or more columns of existing rows.
                
                CRITICAL WARNING:
                If you omit the WHERE clause, SQL will update EVERY SINGLE ROW in the entire table! Always write and verify your WHERE condition before running an UPDATE.
                
                You can set absolute values (status = 'PAID') or relative calculations (salary = salary * 1.05).
            """.trimIndent(),
            exampleQuery = "UPDATE employees SET salary = salary * 1.08, last_reviewed_at = CURRENT_DATE WHERE department_id = 3 AND performance_rating >= 4.5;",
            exampleResultExplanation = "Awards an 8% salary raise and updates the review timestamp for high-performing staff (rating >= 4.5) in department 3.",
            category = "Data Modification",
            level = LessonLevel.MODERATE
        ),
        Lesson(
            id = "M8",
            title = "Deleting Records (DELETE vs TRUNCATE)",
            summary = "Remove obsolete records safely and understand table clearing mechanics.",
            theory = """
                There are fundamental differences in how SQL deletes data:
                
                • DELETE FROM table WHERE condition:
                  Selectively removes matching rows. Transaction-safe and triggers database log events.
                • TRUNCATE TABLE table:
                  Wipes every row in the table at high speed by resetting table pages. Cannot use a WHERE clause and resets auto-increment keys.
                • DROP TABLE table:
                  Completely destroys the data AND removes the table structure itself from the database schema.
            """.trimIndent(),
            exampleQuery = "DELETE FROM shopping_cart_items WHERE user_id = 1042 AND added_at < DATE_SUB(NOW(), INTERVAL 30 DAY);",
            exampleResultExplanation = "Cleans up expired shopping cart items older than 30 days for user 1042, preserving newer items.",
            category = "Data Modification",
            level = LessonLevel.MODERATE
        ),

        // ==========================================
        // SESSION 3: ADVANCED LEVEL (COMPLEX SQL)
        // ==========================================
        Lesson(
            id = "A1",
            title = "FULL OUTER JOIN & CROSS JOIN",
            summary = "Perform comprehensive multi-table audits and combinatorial Cartesian products.",
            theory = """
                Advanced relational analysis frequently requires seeing unmatched records from BOTH tables simultaneously:
                
                • FULL OUTER JOIN: Returns all rows when there is a match in either left or right table. Unmatched columns fill with NULL. Great for reconciling financial ledgers or audit logs.
                • CROSS JOIN: Generates the Cartesian product (every row of Table A paired with every row of Table B). Used for generating matrices, calendar dimensions, or testing permutations.
            """.trimIndent(),
            exampleQuery = "SELECT p.project_name, e.first_name, e.last_name FROM projects p FULL OUTER JOIN employees e ON p.lead_employee_id = e.id;",
            exampleResultExplanation = "Audits both projects lacking an assigned project lead and employees who are not currently leading any active project.",
            category = "Advanced Joins",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A2",
            title = "Self JOIN (Hierarchies & Org Trees)",
            summary = "Join a table to itself to traverse managerial trees and parent-child hierarchies.",
            theory = """
                A Self JOIN occurs when a table references its own primary key through a recursive foreign key (e.g., employees.manager_id -> employees.id).
                
                To execute a Self JOIN:
                You MUST assign two distinct aliases to the same table (e.g., 'e' for employee and 'm' for manager) so SQL can differentiate which instance you are referencing.
            """.trimIndent(),
            exampleQuery = "SELECT e.first_name AS employee, e.job_title, COALESCE(m.first_name || ' ' || m.last_name, 'Top Executive / CEO') AS manager FROM employees e LEFT JOIN employees m ON e.manager_id = m.id;",
            exampleResultExplanation = "Generates a complete company organizational chart, pairing each employee with their direct manager's name, marking the CEO when manager_id is NULL.",
            category = "Advanced Joins",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A3",
            title = "Correlated & Scalar Subqueries",
            summary = "Nest dynamic sub-computations inside SELECT, WHERE, and HAVING clauses.",
            theory = """
                A Subquery is an inner SQL statement evaluated within an outer query.
                
                Types of Subqueries:
                • Scalar Subquery: Returns exactly 1 row and 1 column. Can be placed anywhere an expression is expected (e.g., comparing against average).
                • Correlated Subquery: References a column from the outer query table. It re-evaluates dynamically for every single row processed by the outer query.
            """.trimIndent(),
            exampleQuery = "SELECT e.first_name, e.salary, e.department_id, (SELECT ROUND(AVG(salary), 2) FROM employees WHERE department_id = e.department_id) AS dept_average FROM employees e WHERE e.salary > (SELECT AVG(salary) FROM employees WHERE department_id = e.department_id);",
            exampleResultExplanation = "Uses a correlated subquery to identify employees earning more than their own specific department's average salary, displaying both figures side-by-side.",
            category = "Subqueries",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A4",
            title = "Common Table Expressions (WITH / CTE)",
            summary = "Structure readable, modular query pipelines and eliminate messy subquery nesting.",
            theory = """
                A Common Table Expression (CTE) defines a named, temporary result set available for the duration of a primary query.
                
                Key Advantages over Nested Subqueries:
                1. Linear Readability: Code reads sequentially top-to-bottom instead of deeply nested inside-out.
                2. Reusability: A single CTE can be joined multiple times in the main query.
                3. Recursive CTEs: Can traverse infinite-depth graphs, category trees, and org charts.
            """.trimIndent(),
            exampleQuery = "WITH HighValueCustomers AS (SELECT customer_id, SUM(total_amount) AS total_spend FROM orders GROUP BY customer_id HAVING SUM(total_amount) > 10000) SELECT c.customer_name, c.email, h.total_spend FROM customers c INNER JOIN HighValueCustomers h ON c.id = h.customer_id ORDER BY h.total_spend DESC;",
            exampleResultExplanation = "Defines a CTE calculating high-value customer spends (> $10k), and then cleanly joins it with customer contact data for VIP reporting.",
            category = "CTE & Modularity",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A5",
            title = "Window Functions: ROW_NUMBER, RANK & DENSE_RANK",
            summary = "Perform rank calculations across partitions without collapsing output rows.",
            theory = """
                Unlike GROUP BY which aggregates rows into a single summary row, Window Functions compute metrics across a set of rows while preserving each individual row's identity.
                
                • OVER (PARTITION BY col ORDER BY sort_col): Defines the window partition and ordering.
                • ROW_NUMBER(): Assigns sequential unique integers (1, 2, 3...) regardless of ties.
                • RANK(): Assigns rank with gaps upon ties (1, 2, 2, 4).
                • DENSE_RANK(): Assigns rank without gaps upon ties (1, 2, 2, 3).
            """.trimIndent(),
            exampleQuery = "SELECT first_name, department_id, salary, DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS salary_rank_in_dept FROM employees;",
            exampleResultExplanation = "Ranks employees by salary within each department independently. The top earner of each department receives rank 1 without collapsing any rows.",
            category = "Window Functions",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A6",
            title = "Running Totals & Moving Averages",
            summary = "Calculate cumulative financial totals and rolling time-window averages.",
            theory = """
                Window frames allow evaluating progressive metrics over time (e.g., year-to-date revenue or rolling 7-day averages).
                
                Window Framing Syntax:
                • ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW: Accumulates all prior rows up to the current row (Running Total).
                • ROWS BETWEEN 6 PRECEDING AND CURRENT ROW: Computes rolling 7-row moving metric.
            """.trimIndent(),
            exampleQuery = "SELECT order_date, total_amount, SUM(total_amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_revenue, ROUND(AVG(total_amount) OVER (ORDER BY order_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW), 2) AS seven_day_moving_avg FROM daily_sales;",
            exampleResultExplanation = "Produces a financial ledger showing each day's sales alongside cumulative year-to-date revenue and a 7-day smoothed moving average.",
            category = "Window Functions",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A7",
            title = "Conditional Logic with CASE WHEN Expressions",
            summary = "Implement if-else business logic, categorization, and conditional counting.",
            theory = """
                The CASE expression is SQL's native if-then-else construct:
                
                Syntax:
                CASE 
                    WHEN condition1 THEN result1
                    WHEN condition2 THEN result2
                    ELSE default_result
                END
                
                Use Cases:
                1. Data binning (grouping numeric ranges into 'Low', 'Medium', 'High').
                2. Pivot tables / Conditional aggregations: SUM(CASE WHEN status = 'PAID' THEN amount ELSE 0 END).
            """.trimIndent(),
            exampleQuery = "SELECT product_name, stock_quantity, CASE WHEN stock_quantity = 0 THEN 'OUT OF STOCK' WHEN stock_quantity <= 10 THEN 'CRITICAL - REORDER' WHEN stock_quantity <= 50 THEN 'HEALTHY' ELSE 'OVERSTOCKED' END AS inventory_status FROM warehouse_inventory;",
            exampleResultExplanation = "Dynamically evaluates inventory levels and categorizes each item into actionable supply chain status alerts.",
            category = "Conditional Logic",
            level = LessonLevel.ADVANCED
        ),
        Lesson(
            id = "A8",
            title = "DDL Engineering: CREATE TABLE & Constraints",
            summary = "Architect resilient database schemas with primary keys, foreign keys, and checks.",
            theory = """
                Data Definition Language (DDL) creates and defines the structural schema of your tables.
                
                Essential Integrity Constraints:
                • PRIMARY KEY: Uniquely identifies each record and enforces NOT NULL.
                • FOREIGN KEY ... REFERENCES: Enforces referential integrity between tables.
                • NOT NULL: Prevents missing values.
                • CHECK (condition): Enforces domain business rules (e.g., price > 0).
                • DEFAULT: Automatically assigns values if omitted during insertion.
            """.trimIndent(),
            exampleQuery = "CREATE TABLE account_transactions (id BIGINT PRIMARY KEY AUTO_INCREMENT, account_id INT NOT NULL, amount DECIMAL(12,2) NOT NULL CHECK (amount != 0), transaction_type VARCHAR(20) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (account_id) REFERENCES bank_accounts(id) ON DELETE RESTRICT);",
            exampleResultExplanation = "Defines a bank transaction ledger table with strict schema validation: non-zero amounts, automatic timestamps, and referential protection against deleted accounts.",
            category = "Schema Engineering",
            level = LessonLevel.ADVANCED
        )
    )
}
