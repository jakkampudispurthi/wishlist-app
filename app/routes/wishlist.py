from app.models.notification import Notification
from app.services.scraper import scrape_product
from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import Optional, List
from app.database import get_db
from app.models.wishlist import WishlistItem, CategoryEnum
from app.models.price_history import PriceHistory
from app.routes.auth import get_current_user
from datetime import datetime

router = APIRouter()

# --- Pydantic Schemas ---
class WishlistItemCreate(BaseModel):
    url: str
    product_name: Optional[str] = None
    current_price: Optional[float] = None
    category: Optional[CategoryEnum] = CategoryEnum.maybe_later
    notes: Optional[str] = None

class WishlistItemUpdate(BaseModel):
    product_name: Optional[str] = None
    current_price: Optional[float] = None
    category: Optional[CategoryEnum] = None
    notes: Optional[str] = None

# --- Helper to get current user from token ---
def get_user_from_token(authorization: str = Header(...), db: Session = Depends(get_db)):
    if not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Invalid authorization header")
    token = authorization.split(" ")[1]
    user = get_current_user(token, db)
    if not user:
        raise HTTPException(status_code=401, detail="Not authenticated")
    return user

# --- Routes ---

# Add item to wishlist
@router.post("/", status_code=201)
def add_wishlist_item(
    item: WishlistItemCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    new_item = WishlistItem(
        user_id=current_user.id,
        url=item.url,
        product_name=item.product_name,
        current_price=item.current_price,
        original_price=item.current_price,
        category=item.category,
        notes=item.notes
    )
    db.add(new_item)
    db.commit()
    db.refresh(new_item)

    # Save first price history entry
    if item.current_price:
        price_record = PriceHistory(
            wishlist_item_id=new_item.id,
            price=item.current_price
        )
        db.add(price_record)
        db.commit()

    return {"message": "Item added to wishlist!", "item_id": new_item.id}

# Get all wishlist items for current user
@router.get("/")
def get_wishlist(
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    items = db.query(WishlistItem).filter(WishlistItem.user_id == current_user.id).all()
    return items

# Get items by category
@router.get("/category/{category}")
def get_by_category(
    category: CategoryEnum,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    items = db.query(WishlistItem).filter(
        WishlistItem.user_id == current_user.id,
        WishlistItem.category == category
    ).all()
    return items

# Update an item
@router.put("/{item_id}")
def update_wishlist_item(
    item_id: int,
    item_data: WishlistItemUpdate,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    item = db.query(WishlistItem).filter(
        WishlistItem.id == item_id,
        WishlistItem.user_id == current_user.id
    ).first()

    if not item:
        raise HTTPException(status_code=404, detail="Item not found")

    if item_data.product_name: item.product_name = item_data.product_name
    if item_data.current_price: item.current_price = item_data.current_price
    if item_data.category: item.category = item_data.category
    if item_data.notes: item.notes = item_data.notes
    item.updated_at = datetime.utcnow()

    db.commit()
    db.refresh(item)
    return {"message": "Item updated!", "item": item}

# Delete an item
@router.delete("/{item_id}")
def delete_wishlist_item(
    item_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    item = db.query(WishlistItem).filter(
        WishlistItem.id == item_id,
        WishlistItem.user_id == current_user.id
    ).first()

    if not item:
        raise HTTPException(status_code=404, detail="Item not found")

    db.delete(item)
    db.commit()
    return {"message": "Item deleted from wishlist!"}

# Get price history for an item
@router.get("/{item_id}/price-history")
def get_price_history(
    item_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    item = db.query(WishlistItem).filter(
        WishlistItem.id == item_id,
        WishlistItem.user_id == current_user.id
    ).first()

    if not item:
        raise HTTPException(status_code=404, detail="Item not found")

    history = db.query(PriceHistory).filter(
        PriceHistory.wishlist_item_id == item_id
    ).all()
    return history
# Auto-scrape a URL and return product details
@router.post("/scrape")
def scrape_url(
    url_data: dict,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    url = url_data.get("url")
    if not url:
        raise HTTPException(status_code=400, detail="URL is required")

    result = scrape_product(url)

    if not result["success"]:
        raise HTTPException(status_code=400, detail="Could not scrape this URL")

    return result
# Get all notifications for current user
@router.get("/notifications")
def get_notifications(
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    notifications = db.query(Notification).filter(
        Notification.user_id == current_user.id,
        Notification.is_read == False
    ).order_by(Notification.created_at.desc()).all()
    return notifications

# Mark notification as read
@router.put("/notifications/{notification_id}/read")
def mark_as_read(
    notification_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_user_from_token)
):
    notification = db.query(Notification).filter(
        Notification.id == notification_id,
        Notification.user_id == current_user.id
    ).first()
    if not notification:
        raise HTTPException(status_code=404, detail="Notification not found")
    notification.is_read = True
    db.commit()
    return {"message": "Notification marked as read!"}