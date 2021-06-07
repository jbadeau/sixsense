name := "jira"

version := "0.1"

scalaVersion := "2.12.14"

libraryDependencies += "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % "3.1.2"
libraryDependencies += "org.apache.spark" % "spark-sql_2.12" % "3.1.2"
libraryDependencies += "org.apache.spark" % "spark-avro_2.12" % "3.1.2"
//libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.3.0"
//libraryDependencies += "com.amazonaws" % "aws-java-sdk-pom" % "1.11.1033"