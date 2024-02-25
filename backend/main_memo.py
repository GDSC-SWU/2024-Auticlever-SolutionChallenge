import logging

from fastapi import APIRouter, Form, HTTPException
from starlette.responses import JSONResponse

from db_connection import connect_to_mysql

router = APIRouter()
connection = connect_to_mysql()


# Main memo
@router.post("")
async def create_or_update_main_memo(content: str = Form(...)):
    try:
        # Check if main memo already exists
        with connection.cursor() as cursor:
            sql = "SELECT COUNT(*) FROM mainMemo"
            cursor.execute(sql)
            result = cursor.fetchone()
            memo_exists = result['COUNT(*)'] > 0

        # If main memo exists, update the existing one
        if memo_exists:
            with connection.cursor() as cursor:
                sql = "UPDATE mainMemo SET content = %s"
                cursor.execute(sql, (content,))
                connection.commit()
        # If main memo doesn't exist, create a new one
        else:
            with connection.cursor() as cursor:
                sql = "INSERT INTO mainMemo (content) VALUES (%s)"
                cursor.execute(sql, (content,))
                connection.commit()

        return {"message": "Main memo has been successfully saved."}

    except Exception as e:
        logging.exception("Exception occurred while processing request:")
        return JSONResponse(status_code=500, content={"error": "Server error."})

@router.get("")
async def get_main_memo():
    try:
        # Fetch the main memo content from the database
        with connection.cursor() as cursor:
            sql = "SELECT content FROM mainMemo"
            cursor.execute(sql)
            result = cursor.fetchone()

        # If main memo exists, return its content
        if result:
            return {"content": result['content']}
        # If main memo doesn't exist, return a message indicating it
        else:
            return {"message": "Main memo does not exist."}

    except Exception as e:
        logging.exception("Exception occurred while processing request:")
        return JSONResponse(status_code=500, content={"error": "Server error."})
