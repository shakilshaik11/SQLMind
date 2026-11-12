package com.example.data

data class Lesson(
    val id: String,        // e.g. "A", "B", ...
    val title: String,
    val summary: String,
    val theory: String,
    val exampleQuery: String,
    val exampleResultExplanation: String,
    val category: String = "SQL Core"
)

object LearningData {
    val lessons = listOf(
        Lesson(
            id = "A",
            title = "Introduction to Databases & SQL",
            summary = "Understand relational databases, tables, and the fundamental purpose of SQL.",
            theory = """
                A relational database organizes data into tables (or relations) with rows and columns. 
                SQL (Structured Query Language) is the standard language used to communicate with databases.
                
                • Table: A collection of related data entries consisting of columns (fields) and rows (records).
                • Primary Key: A unique identifier for every row in a table.
                
                The main types of SQL commands are:
                1. DDL (Data Definition Language): Defining schemas (e.g., CREATE, ALTER, DROP).
                2. DML (Data Manipulation Language): Modifying data (e.g., INSERT, UPDATE, DELETE).
                3. DQL (Data Query Language): Fetching data (e.g., SELECT).
            """.trimIndent(),
            exampleQuery = "SELECT * FROM employees;",
            exampleResultExplanation = "Retrieves all columns and all records from the 'employees' table."
        ),
        Lesson(
            id = "B",
            title = "The SELECT Statement",
            summary = "Learn how to query specific columns and rename columns using ALIASES.",
            theory = """
                The SELECT statement is the foundation of SQL queries. Rather than retrieving all data using '*', you should query only the columns you need for performance.
                
                • Querying columns: SELECT column1, column2 FROM table_name;
                • Aliasing (AS): Renames a column or table temporarily in the results to make them more readable.
                • DISTINCT: Removes duplicates from the query results.
            """.trimIndent(),
            exampleQuery = "SELECT DISTINCT department_id AS dept, first_name AS name FROM employees;",
            exampleResultExplanation = "Retrieves unique department IDs and names, renaming columns in the output to 'dept' and 'name' for cleaner reading."
        ),
        Lesson(
            id = "C",
            title = "The WHERE Clause",
            summary = "Filter records dynamically using conditional operators, logical AND/OR, and patterns.",
            theory = """
                The WHERE clause filters results so only rows meeting specific criteria are returned.
                
                Key Operators:
                • Comparisons: = , != (or <>), < , > , <= , >=
                • IN: Matches any value in a specified list (e.g., IN (1, 2, 3)).
                • BETWEEN: Filters within an inclusive range.
                • LIKE: Search for wildcards (% matches zero or more characters, _ matches a single character).
                • Logical: AND (both must be true), OR (either must be true), NOT (inverts condition).
            """.trimIndent(),
            exampleQuery = "SELECT * FROM employees WHERE salary BETWEEN 50000 AND 80000 AND email LIKE '%@gmail.com';",
            exampleResultExplanation = "Retrieves all employees who earn between 50,000 and 80,000 inclusive, and whose contact emails end with '@gmail.com'."
        ),
        Lesson(
            id = "D",
            title = "ORDER BY & LIMIT",
            summary = "Sort your query outputs ascending or descending, and restrict the result counts.",
            theory = """
                Sorting and slicing are essential when dealing with large volumes of data.
                
                • ORDER BY: Sorts the result set. Ascending (ASC) is default, specify DESC for descending. You can sort by multiple columns.
                • LIMIT: Restricts the total number of rows returned. (In some SQL variants like Oracle, FETCH FIRST is used; in SQL Server, TOP is used. LIMIT is standard in MySQL, PostgreSQL, and SQLite).
                • OFFSET: Skips a specified number of rows before beginning to return results. Used with LIMIT for pagination.
            """.trimIndent(),
            exampleQuery = "SELECT first_name, salary FROM employees ORDER BY salary DESC, first_name ASC LIMIT 5 OFFSET 10;",
            exampleResultExplanation = "Retrieves employees' names and salaries, sorted from highest-paid to lowest-paid. It skips the top 10 highest-paid employees and returns the next 5 (ranks 11 to 15)."
        ),
        Lesson(
            id = "E",
            title = "Aggregate Functions",
            summary = "Perform standard computational operations like COUNT, SUM, AVG, MIN, and MAX.",
            theory = """
                Aggregate functions perform a calculation on a set of values and return a single value.
                
                Core Aggregates:
                • COUNT(): Returns the number of rows.
                • SUM(): Calculates the total numeric sum of a column.
                • AVG(): Calculates the average value of a numeric column.
                • MIN() / MAX(): Returns the lowest or highest value in a column.
                
                Note: Aggregates ignore NULL values except for COUNT(*), which counts every matching row including null fields.
            """.trimIndent(),
            exampleQuery = "SELECT COUNT(*) AS total_staff, AVG(salary) AS average_salary FROM employees WHERE department_id = 4;",
            exampleResultExplanation = "Calculates the total number of active workers in department 4, and retrieves their average monthly salary, labelling output columns clearly."
        ),
        Lesson(
            id = "F",
            title = "GROUP BY & HAVING",
            summary = "Group output records by categorical attributes and filter collective groups.",
            theory = """
                When you need to get aggregating statistics partitioned by categories (such as average salary per department), you use GROUP BY.
                
                • GROUP BY: Collects rows with the same values in specified columns into summary rows.
                • HAVING: Filters groups. Since the WHERE clause operates on individual rows before grouping, it cannot filter aggregate values. HAVING is evaluated *after* grouping, allowing filters based on aggregate results (e.g., HAVING COUNT(*) > 5).
            """.trimIndent(),
            exampleQuery = "SELECT job_title, COUNT(*) AS employee_count, AVG(salary) AS avg_sal FROM employees GROUP BY job_title HAVING COUNT(*) > 2 ORDER BY avg_sal DESC;",
            exampleResultExplanation = "Groups employees by their titles, calculates the count and average salary for each title. Filters out titles with 2 or fewer employees, and sorts the remaining grouped titles by average salary descending."
        ),
        Lesson(
            id = "G",
            title = "SQL JOINs (Relating Tables)",
            summary = "Combine columns from two or more tables based on matching key values.",
            theory = """
                Relational databases distribute data across multiple tables to avoid duplication. JOINs pull this separate data back together.
                
                Types of JOINs:
                • INNER JOIN: Returns records that have matching values in both tables.
                • LEFT (OUTER) JOIN: Returns all records from the left table, and matching records from the right table. If no match, right side returns NULL.
                • RIGHT (OUTER) JOIN: Returns all records from the right table, and matching records from the left table.
                • FULL (OUTER) JOIN: Returns all records when there is a match in either left or right table.
                • CROSS JOIN: Returns the Cartesian product of the two tables (every row from left table paired with every row from right table).
            """.trimIndent(),
            exampleQuery = "SELECT e.first_name, d.department_name FROM employees e INNER JOIN departments d ON e.department_id = d.id;",
            exampleResultExplanation = "Combines the 'employees' table (aliased as 'e') and the 'departments' table (aliased as 'd'), matching on the department ID. Returns each employee's first name alongside their respective department name."
        ),
        Lesson(
            id = "H",
            title = "Complex Subqueries & Nested Queries",
            summary = "Master subqueries nested inside SELECT, WHERE, or FROM clauses for advanced retrieval.",
            theory = """
                A Subquery is a query nested inside another SQL statement. It is also called an inner query or nested query.
                
                Classification:
                • Scalar Subquery: Returns a single row and single column (e.g., to compare a value against the average).
                • Multi-row Subquery: Returns one column with multiple rows. Evaluated using IN, ANY, ALL, or EXISTS.
                • Correlated Subquery: Outward-referencing subquery. The inner query relies on columns from the outer query, evaluating row-by-row.
                • Inline Views: A subquery in the FROM clause that acts as a temporary virtual table.
            """.trimIndent(),
            exampleQuery = "SELECT first_name, salary FROM employees WHERE salary > (SELECT AVG(salary) FROM employees) AND department_id IN (SELECT id FROM departments WHERE location = 'New York');",
            exampleResultExplanation = "First, calculates the global average salary (inner subquery 1) and gets NY department IDs (inner subquery 2). Then, the outer query fetches employees whose salaries exceed that average AND who work in New York."
        ),
        Lesson(
            id = "I",
            title = "Common Table Expressions (CTEs)",
            summary = "Write highly legible, maintainable nested queries using the WITH statement.",
            theory = """
                Common Table Expressions (CTEs) define a temporary result set that can be referenced within a subsequent SELECT, INSERT, UPDATE, or DELETE query.
                
                Benefits of CTEs over nested Subqueries:
                1. Readability: Queries read linearly from top-to-bottom instead of inside-out.
                2. Reusability: A single CTE can be joined multiple times in the outer queries.
                3. Recursive Queries: Supports hierarchical/parent-child walks (e.g., org charts or bill-of-materials structures) using CTEs prefix WITH RECURSIVE.
            """.trimIndent(),
            exampleQuery = "WITH DeptSalaries AS (SELECT department_id, AVG(salary) AS avg_sal FROM employees GROUP BY department_id) SELECT e.first_name, e.salary, d.avg_sal FROM employees e INNER JOIN DeptSalaries d ON e.department_id = d.department_id WHERE e.salary > d.avg_sal;",
            exampleResultExplanation = "Defines a CTE containing average salaries by department. The main query then joins this temporary set with the employees table, listing employees who earn more than their own department's average salary."
        )
    )
}
