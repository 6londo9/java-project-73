### Hexlet tests and linter status:
[![Actions Status](https://github.com/6londo9/java-project-73/workflows/hexlet-check/badge.svg)](https://github.com/6londo9/java-project-73/actions)
![Java CI](https://github.com/6londo9/java-project-73/actions/workflows/Java-CI.yml/badge.svg)
<a href="https://codeclimate.com/github/6londo9/java-project-73/maintainability"><img src="https://api.codeclimate.com/v1/badges/ae1e25de18e8a3f1025a/maintainability" /></a>
<a href="https://codeclimate.com/github/6londo9/java-project-73/test_coverage"><img src="https://api.codeclimate.com/v1/badges/ae1e25de18e8a3f1025a/test_coverage" /></a>

---
This is my fifth study project that contains mine backend logic with default frontend part.

It is a web app, where users can write tasks with labels and statuses for each other like Github, Jira and etc.

The fully working deployed app you can check out [here](https://java-project-73-production-83e0.up.railway.app/).

---
To make app work, at first, you need to change 'jwt.secret' and 'rollbar.token' variables in 'application.yml' to yours, then start it by
```
make start
```
It will be available on the path: http://localhost:5000

Swagger documentation is available on the path: http://localhost:5000/swagger-ui.html