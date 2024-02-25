import os
import pymysql
from google.cloud import storage

# MySQL connection settings
def connect_to_mysql():
    connection = pymysql.connect(
        host=os.getenv('MYSQL_HOST'),
        user=os.getenv('MYSQL_USER'),
        password=os.getenv('MYSQL_PASSWORD'),
        database=os.getenv('MYSQL_DATABASE'),
        cursorclass=pymysql.cursors.DictCursor
    )
    return connection

# Google Cloud Storage settings
def connect_to_gcs():
    credentials_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = credentials_path
    storage_client = storage.Client()
    bucket_name = "conversation-content"
    return storage_client, bucket_name