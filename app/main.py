from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from app.database import Base, engine
from app.models.user import User
from app.models.wishlist import WishlistItem
from app.models.price_history import PriceHistory
from app.models.notification import Notification
from app.routes import auth, wishlist
from app.services.scheduler import start_scheduler
import traceback

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="WishList API",
    description="Save products, track prices, get notified on deals!",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={"error": str(exc), "detail": traceback.format_exc()}
    )

app.include_router(auth.router, prefix="/auth", tags=["Authentication"])
app.include_router(wishlist.router, prefix="/wishlist", tags=["Wishlist"])

@app.on_event("startup")
def startup_event():
    start_scheduler()

@app.get("/")
def root():
    return {"message": "Welcome to WishList API! 🎉"}