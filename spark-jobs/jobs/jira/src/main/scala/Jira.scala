import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro.functions._
import org.apache.spark.sql.functions.col

import java.nio.file.{Files, Paths}

object Jira {

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession.builder()
//      .master("local")
      .appName(sys.env("KAFKA_TOPIC"))
      .getOrCreate()

    spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", sys.env("MINIO_ENDPOINT"))
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", sys.env("MINIO_ACCESS_KEY"))
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", sys.env("MINIO_SECRET_KEY"))
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.path.style.access", "True")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")

    //TODO: fetch from schema registry
    val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("./src/main/resources/jira-topic-project-types.avsc")))

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", sys.env("BOOTSTRAP_SERVER"))
      .option("subscribe", sys.env("KAFKA_TOPIC"))
      .option("startingOffsets", "earliest")
      .option("kafka.group.id", sys.env("KAFKA_GROUP_ID"))
      .load()
        
    df.printSchema()

    val output = df
      .select(
        from_avro(col("value"), jsonFormatSchema).alias("ConnectDefault"))

    output
      .select(
        to_avro(col("ConnectDefault.data")).alias("value"))
      .writeStream
      .format("avro")
      .outputMode("append")
      .option("checkpointLocation", sys.env("CHECKPOINT_LOCATION"))
      .option("path", sys.env("MINIO_BUCKET") + sys.env("KAFKA_TOPIC") + "/")
//      For local development
//      .option("path", "/tmp/" + sys.env("KAFKA_TOPIC") + "/")
      .start()
      .awaitTermination()
  }
}
