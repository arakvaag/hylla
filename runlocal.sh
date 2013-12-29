export DATABASE_URL=postgres://hyllaDB:hyllaDB@localhost:5432/hyllaDB
java -jar hylla/target/dependency/jetty-runner.jar hylla/target/*.war