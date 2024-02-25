import os

from fastapi import FastAPI
from conversation import router as conversation_router
from consultation import router as consultation_router
from main_memo import router as main_memo_router
from topic_recommendation import router as topic_recommendation_router

app = FastAPI()

# Include conversation related endpoints
app.include_router(conversation_router, prefix="/conversation", tags=["conversation"])

# Include consultation related endpoints
app.include_router(consultation_router, prefix="/consultation", tags=["consultation"])

# Include main memo related endpoints
app.include_router(main_memo_router, prefix="/main-memo", tags=["main memo"])

# Include topic recommendation related endpoints
app.include_router(topic_recommendation_router, prefix="/topic-recommendation", tags=["topic recommendation"])
