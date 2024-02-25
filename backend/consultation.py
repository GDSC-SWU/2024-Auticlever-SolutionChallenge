import logging
import os

from fastapi import APIRouter, File, UploadFile, HTTPException, Form
from google.cloud import speech, storage, language_v1
from starlette.responses import JSONResponse

from db_connection import connect_to_mysql, connect_to_gcs
from text_summarization import summarize_dialogue

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

@router.post("")
async def create_consultation(file: UploadFile = File(...)):
    try:
        
        # Upload MP3 file to Google Cloud Storage
        blob = storage_client.bucket(bucket_name).blob(file.filename)
        blob.upload_from_file(file.file)
        gcs_uri = f"gs://{bucket_name}/{file.filename}"

        # Extract conversation scripts using the Speech to Text API
        client = speech.SpeechClient()

        audio = speech.RecognitionAudio(uri=gcs_uri)
        config = speech.RecognitionConfig(
            encoding=speech.RecognitionConfig.AudioEncoding.MP3,
            sample_rate_hertz=16000,
            language_code="en-US",
        )
        operation = client.long_running_recognize(config=config, audio=audio)
        response = operation.result(timeout=600)

        transcript = ""
        for result in response.results:
            transcript += result.alternatives[0].transcript + " "

        # Conversation script summarization
        summary = summarize_dialogue(transcript)

        # Insert consultation information into MySQL
        with connection.cursor() as cursor:
            sql = "INSERT INTO consultation (content, summary, date) VALUES (%s, %s, NOW())"
            cursor.execute(sql, (transcript, summary))
            connection.commit()

        return {"message": "Your consultation has been successfully saved.", "summary": summary}

    except Exception as e:
        logging.exception("Exception occurred while processing request:")
        return JSONResponse(status_code=500, content={"error": "server error."})

# Create or update consultation's memo
@router.post("/{consultationId}/csMemo")
async def create_or_update_cs_memo(consultationId: int, title: str = Form(...), csMemo: str = Form(...), mainMemo: str = Form(...)):
    try:
        with connection.cursor() as cursor:
            # Check if there are already memos for that consultation
            sql_select = "SELECT * FROM csMemo WHERE consultationId = %s"
            cursor.execute(sql_select, (consultationId,))
            existing_memo = cursor.fetchone()

            if existing_memo:
                # Update if you already have a memo
                memoId = existing_memo['memoId']  
                sql_update = "UPDATE csMemo SET title = %s, content = %s WHERE memoId = %s"
                cursor.execute(sql_update, (title, csMemo, memoId))
                connection.commit()
                csMemo_message = "csMemo has been updated successfully."
            else:
                # Create a memo if it does not exist
                sql_insert = "INSERT INTO csMemo (consultationId, title, content) VALUES (%s, %s, %s)"
                cursor.execute(sql_insert, (consultationId, title, csMemo))
                connection.commit()
                csMemo_message = "csMemo has been updated successfully."

        # Create or update mainMemo
        with connection.cursor() as cursor:
            # Check if there is already a main memo
            sql_select_main = "SELECT * FROM mainMemo"
            cursor.execute(sql_select_main)
            existing_main_memo = cursor.fetchone()

            if existing_main_memo:
                # Update if you already have a memo
                main_memo_id = existing_main_memo['id']
                sql_update_main = "UPDATE mainMemo SET content = %s WHERE id = %s"
                cursor.execute(sql_update_main, (mainMemo, main_memo_id))
                connection.commit()
                mainMemo_message = "mainMemo has been updated successfully."
            else:
                # Create a note if it does not exist
                sql_insert_main = "INSERT INTO mainMemo (content) VALUES (%s)"
                cursor.execute(sql_insert_main, (mainMemo,))
                connection.commit()
                mainMemo_message = "mainMemo has been created successfully."

        return {"csMemo_message": csMemo_message, "mainMemo_message": mainMemo_message}
    except Exception as e:
        # Error handling
        return {"error": str(e)}

# Delete consultation
@router.delete("/{consultationId}")
async def delete_consultation(consultationId: int):
    try:
        with connection.cursor() as cursor:
            # Delete csMemo dependent on consultation
            sql_delete_csMemo = "DELETE FROM csMemo WHERE consultationId = %s"
            cursor.execute(sql_delete_csMemo, (consultationId,))
            connection.commit()

            # Delete consultation
            sql_delete_consultation = "DELETE FROM consultation WHERE csId = %s"
            cursor.execute(sql_delete_consultation, (consultationId,))
            connection.commit()

            return {"message": f"The consultation and corresponding csMemo have been deleted successfully."}

    except Exception as e:
        # Error handling
        raise HTTPException(status_code=500, detail=f"Error occurred while deleting consultation: {str(e)}")

# Get consultation information and csMemo
@router.get("/{consultationId}")
async def get_consultation(consultationId: int):
    try:
        # Get consultation information
        with connection.cursor() as cursor:
            sql_select_consultation = "SELECT * FROM consultation WHERE csId = %s"
            cursor.execute(sql_select_consultation, (consultationId,))
            consultation_data = cursor.fetchone()

            # Get csMemo
            sql_select_csMemo = "SELECT * FROM csMemo WHERE consultationId = %s"
            cursor.execute(sql_select_csMemo, (consultationId,))
            csMemo_data = cursor.fetchall()

        # Combine consultation information and csMemo data and return
        return {"consultation_data": consultation_data, "csMemo_data": csMemo_data}
    except Exception as e:
        # Error handling
        raise HTTPException(status_code=500, detail=f"Error occurred while fetching consultation information and csMemo: {str(e)}")

# Get consultation list
@router.get("/consultations/list")
async def get_consultations_list():
    try:
        consultations_data = []
        with connection.cursor() as cursor:
            sql_select_consultations = "SELECT * FROM consultation"
            cursor.execute(sql_select_consultations)
            consultations = cursor.fetchall()
            for consultation in consultations:
                consultation_id = consultation["csId"]
                date = consultation["date"]
                # Get csMemo
                sql_select_csMemo = "SELECT * FROM csMemo WHERE consultationId = %s"
                cursor.execute(sql_select_csMemo, (consultation_id,))
                csMemo = cursor.fetchone()
                if csMemo:
                    csMemo_content = csMemo["content"]
                    title = csMemo["title"]
                else:
                    csMemo_content = None
                    title = None
                consultation_data = {
                    "consultationId": consultation_id,
                    "title": title,
                    "date": date,
                    "csMemo": csMemo_content
                }
                consultations_data.append(consultation_data)
        return {"consultations": consultations_data}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error occurred while fetching consultation list: {str(e)}")


