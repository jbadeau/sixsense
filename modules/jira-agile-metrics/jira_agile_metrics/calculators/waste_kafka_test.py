import pytest
from pandas import Timestamp

from ..conftest import (
    FauxJIRA as JIRA,
    FauxIssue as Issue,
    FauxFieldValue as Value,
    FauxChange as Change,
)

from ..utils import extend_dict

from ..querymanager import QueryManager
from .waste import WasteCalculator

from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StringType, IntegerType, StructField
from pyspark.sql.functions import from_json, schema_of_json, lit, col


@pytest.fixture(scope="session")
def spark(request):
    return SparkSession \
        .builder.master("local[2]") \
        .appName("pytest-pyspark-local-testing") \
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.1") \
        .config("spark.jars.ivy", "D:/ivy") \
        .enableHiveSupport() \
        .getOrCreate()

def test_query(spark):
    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "host.docker.internal:9092") \
        .option("subscribe", "jira-issues") \
        .option("startingOffsets", "earliest") \
        .load()

    df = df \
        .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") \
        .writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "host.docker.internal:9092") \
        .option("topic", "jira-issues-waste")\
        .option("checkpointLocation", "D:/tmp/checkpoint")\
        .start()

    spark.streams.awaitAnyTermination()