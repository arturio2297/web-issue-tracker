# Web issue tracker
The application is a forum for the publication of problems that have arisen in a certain area (which one does not matter)
The application has the following functions:
1. Registration (And validation for all fields);
2. Authorization;
3. Role system (Only two roles. Administrator and ordinary user with different rights);
4. Adding new issues (And validation for all fields);
5. Adding comments to issues (And validation for field);
6. Displaying paged lists with sorting by created date ascending;
7. Search system displaying paginated lists (It is possible to select a search according to certain criteria);
8. Ability to add some html tags for editing messages (Which ones are specified in the class "ClearService". 
Undeclared tags will be removed).

All entities (users, issues and comments) are stored in the database.
The main parent table is the "user_model" table. Model class extends spring security "UserDetails" class. The issue_model entity table (ManyToOne) is a child table in relation to the user_model table. A child of the issue_model table is the comment_model entity table (ManyToOne).

Administrator capabilities:
Delete any posts and change their status, as well as delete any comments.

User capabilities:
Create and edit your own records with the "Created" status.

Features of anonymous (unauthorized) users:
View all posts and comments without any possibility of modification.


A total of 5 pages are implemented in the application:
1. Registration page;
2. The page for authorization;
3. A page with a list of all records;
4. The page for adding a new record;
5. A page for a detailed view of the recording and adding a comment.

