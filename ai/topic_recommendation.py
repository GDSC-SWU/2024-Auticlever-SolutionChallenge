import logging
import sqlite3

from fastapi import APIRouter, HTTPException
import numpy as np
import random
from starlette.responses import JSONResponse


router = APIRouter()

# Connect to SQLite database
conn = sqlite3.connect('topic_keywords.db')
c = conn.cursor()

# Keyword recommendation
@router.get("")
async def get_topic_recommendation():
    try:
        # Fetch keywords from SQLite database
        c.execute("SELECT topic_keyword FROM topic_keywords")
        topic_keywords = c.fetchall()

        # Select 10 keywords randomly
        recommended_keywords = random.sample(topic_keywords, 10)

        # Convert each keyword into a single string and return as a list
        recommended_keywords = [keyword[0] for keyword in recommended_keywords]

        return {"recommended_keywords": recommended_keywords}

    except Exception as e:
        logging.exception("Exception occurred while processing request:")
        return JSONResponse(status_code=500, content={"error": "Server error."})

# Close SQLite connection
@router.on_event("shutdown")
def shutdown_event():
    conn.close()

