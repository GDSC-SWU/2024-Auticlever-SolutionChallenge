import logging
import os

from fastapi import APIRouter, File, UploadFile, HTTPException, Form
from google.cloud import speech, storage, language_v1
from starlette.responses import JSONResponse, FileResponse

from db_connection import connect_to_mysql, connect_to_gcs
from text_summarization import summarize_dialogue
from keyword_extraction import extract_topic_keywords

# Set up Google Cloud authentication
credentials_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
if credentials_path:
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = credentials_path
else:
    print("Error: GOOGLE_APPLICATION_CREDENTIALS environment variable is not set.")

# Set up Google Cloud Storage
storage_client = storage.Client()
bucket_name = "conversation-content"

# Set up Google Cloud Speech
speech_client = speech.SpeechClient()

# Set up Google Cloud Natural Language
language_client = language_v1.LanguageServiceClient()

router = APIRouter()
connection = connect_to_mysql()
storage_client, bucket_name = connect_to_gcs()

# Set up Google Cloud Speech
speech_client = speech.SpeechClient()

# Convert AAC file to MP3 and return the file.
async def convert_aac_to_mp3(aac_file: UploadFile):
    # Convert AAC file to AudioSegment in PyDub
    aac_audio = AudioSegment.from_file(aac_file.file)

    # Create temporary directory
    tmp_dir = tempfile.mkdtemp()

    # Convert to MP3 files
    mp3_filename = aac_file.filename.replace('.aac', '.mp3')
    mp3_file_path = os.path.join(tmp_dir, mp3_filename)
    aac_audio.export(mp3_file_path, format="mp3")

    return mp3_file_path

# Conversation registration
@router.post("/conversation")
async def create_conversation(file: UploadFile = File(...)):
    try:
        # Convert AAC files to MP3 files
        mp3_file_path = await convert_aac_to_mp3(file)

        # Upload MP3 files to Google Cloud Storage
        blob = storage_client.bucket(bucket_name).blob(os.path.basename(mp3_file_path))  # Extract only file names
        blob.upload_from_filename(mp3_file_path)  # Upload from file path
        gcs_uri = f"gs://{bucket_name}/{os.path.basename(mp3_file_path)}"

        # Extract conversation scripts using the Speech to Text API
        client = speech.SpeechClient()

        audio = speech.RecognitionAudio(uri=gcs_uri)

        config = speech.RecognitionConfig(
            encoding=speech.RecognitionConfig.AudioEncoding.MP3,
            sample_rate_hertz=16000,
            language_code="en-US",
        )

        # Speech to Text API call
        operation = client.long_running_recognize(config=config, audio=audio)
        response = operation.result(timeout=600)

        # Extract conversation script
        transcript = ""
        for result in response.results:
            transcript += result.alternatives[0].transcript + " "

        # Dialog script log output
        print("Transcript:", transcript)

        # Extract keywords from conversation scripts
        keywords = extract_topic_keywords(transcript)
        print("keywords:", keywords)

        # Conversation script summary
        summary = summarize_dialogue(transcript)

        # Insert conversation information into MySQL
        with connection.cursor() as cursor:
            # Insert conversation information into MySQL
            sql = "INSERT INTO conversation (filePath, content, keyword, summary, date) VALUES (%s, %s, %s, %s, NOW())"
            cursor.execute(sql, (gcs_uri, transcript, ', '.join(keywords), summary))
            connection.commit()

            # Get the conversationId of the inserted conversation
            conversation_id = cursor.lastrowid

        return {"message": "Your conversation has been saved successfully.", "conversationId": conversation_id, "keywords": keywords, "summary": summary}

    except Exception as e:
        logging.exception("Exception occurred while processing request:")
        raise HTTPException(status_code=500, detail="Server Error.")

# Create or update cvMemo
@router.post("/{conversationId}/cvMemo")
async def create_or_update_cv_memo(conversationId: int, content: str = Form(...)):
    try:
        with connection.cursor() as cursor:
            # Check if there is already a memo for that conversation
            sql_select = "SELECT * FROM cvMemo WHERE conversationId = %s"
            cursor.execute(sql_select, (conversationId,))
            existing_memo = cursor.fetchone()

            if existing_memo:
                # Update if you already have a memo
                memoId = existing_memo['memoId']
                sql_update = "UPDATE cvMemo SET content = %s WHERE memoId = %s"
                cursor.execute(sql_update, (content, memoId))
                connection.commit()
                return {"message": "The memo has been successfully updated."}
            else:
                # Create if there is no memo
                sql_insert = "INSERT INTO cvMemo (conversationId, content) VALUES (%s, %s)"
                cursor.execute(sql_insert, (conversationId, content))
                connection.commit()
                return {"message": "The memo has been successfully created."}
    except Exception as e:
        # Error handling
        return {"error": str(e)}

# Delete conversation
@router.delete("/{conversationId}")
async def delete_conversation(conversationId: int):
    try:
        with connection.cursor() as cursor:
            # Delete cvMemo dependent on conversation
            sql_delete_cvMemo = "DELETE FROM cvMemo WHERE conversationId = %s"
            cursor.execute(sql_delete_cvMemo, (conversationId,))
            connection.commit()

            # Delete conversation
            sql_delete_conversation = "DELETE FROM conversation WHERE cvId = %s"
            cursor.execute(sql_delete_conversation, (conversationId,))
            connection.commit()

            return {"message": f"The conversation and corresponding cvMemo have been deleted successfully."}

    except Exception as e:
        # Error handling
        raise HTTPException(status_code=500, detail=f"Error occurred while deleting conversation: {str(e)}")

# Get conversation file (mp3)
@router.get("/{conversationId}/file")
async def download_file(conversationId: int):
    try:
        # Get conversation information
        with connection.cursor() as cursor:
            sql_select_conversation = "SELECT * FROM conversation WHERE cvId = %s"
            cursor.execute(sql_select_conversation, (conversationId,))
            conversation_data = cursor.fetchone()

            # Get GCS URI
            gcs_uri = conversation_data["filePath"]

        # Download file from GCS
        bucket_name, object_name = gcs_uri.split("//")[1].split("/", 1)
        storage_client = storage.Client()
        bucket = storage_client.bucket(bucket_name)
        blob = bucket.blob(object_name)

        # Specify the path to save the file locally
        local_file_path = f"conversation_{conversationId}.mp3"

        # Download the file
        blob.download_to_filename(local_file_path)

        # Return file download response
        return FileResponse(local_file_path, media_type="audio/mpeg", filename="conversation.mp3")
    except Exception as e:
        # Error handling
        raise HTTPException(status_code=500, detail=f"Error occurred while downloading file: {str(e)}")

# Get conversation details
@router.get("/{conversationId}/data")
async def get_conversation_data(conversationId: int):
    try:
        # Get conversation information
        with connection.cursor() as cursor:
            sql_select_conversation = "SELECT * FROM conversation WHERE cvId = %s"
            cursor.execute(sql_select_conversation, (conversationId,))
            conversation_data = cursor.fetchone()

            # Get cvMemo
            sql_select_cvMemo = "SELECT * FROM cvMemo WHERE conversationId = %s"
            cursor.execute(sql_select_cvMemo, (conversationId,))
            cvMemo_data = cursor.fetchall()

        # Return JSON data
        return {"conversation_data": conversation_data, "cvMemo_data": cvMemo_data}
    except Exception as e:
        # Error handling
        raise HTTPException(status_code=500, detail=f"Error occurred while fetching conversation data: {str(e)}")

# Get conversation list
@router.get("/conversations/list")
async def get_conversations():
    try:
        conversations_data = []
        with connection.cursor() as cursor:
            sql_select_conversations = "SELECT * FROM conversation"
            cursor.execute(sql_select_conversations)
            conversations = cursor.fetchall()
            for conversation in conversations:
                conversation_id = conversation["cvId"]
                date = conversation["date"]
                keyword = conversation["keyword"]
                # Get cvMemo
                sql_select_cvMemo = "SELECT * FROM cvMemo WHERE conversationId = %s"
                cursor.execute(sql_select_cvMemo, (conversation_id,))
                cvMemos = cursor.fetchall()
                cvMemo_data = []
                for cvMemo in cvMemos:
                    memo_id = cvMemo["memoId"]
                    content = cvMemo["content"]
                    cvMemo_data.append({"memoId": memo_id, "content": content})
                conversation_data = {
                    "conversationId": conversation_id,
                    "date": date,
                    "keyword": keyword,
                    "cvMemo": cvMemo_data
                }
                conversations_data.append(conversation_data)
        return {"conversations": conversations_data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error occurred while fetching conversation list: {str(e)}")
