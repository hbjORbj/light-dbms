# Lightweight Database Management System #

This program parses and interprets SQL commands and successfully executes them. Its own parser was written using the framework JSQLParser. The implementations supported by this program include scan, selection, projection,  join, aliases, ORDER BY and DISTINCT.



## Join Logic

Threre are three possible types of expressions that can be encountered in the WHERE clause in this coursework: select condition within one table, join condition between two tables and simple comparison between constants.

Before getting into building the query tree, the program breaks down the long conjunctive expression in the WHERE clause into a list of individual expressions. Then, for each table referred or mentioned in any of these conditions, its corresponding select conditions and join conditions are stored using HashMap, where the table name is the key and the value is either a list of select conditions or a list of join conditions (two HashMaps are used for each type). In case of join conditions, they are mapped to the table that appears later in the FROM clause so that a left-deep join tree can be successfully built. In case of simple constant comparison, it is simply mapped to the right-most table in the FROM clause.

More details can found in the comments in the constructor and build() operation of QueryTree class.