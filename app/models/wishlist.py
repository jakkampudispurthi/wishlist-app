from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Enum
from sqlalchemy.orm import relationship
from datetime import datetime
import enum
from app.database import Base

class CategoryEnum(str, enum.Enum):
    must_buy = "must-buy"
    maybe_later = "maybe-later"
    need_next_time = "need-next-time"

class WishlistItem(Base):
    __tablename__ = "wishlist_items"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    url = Column(String, nullable=False)
    product_name = Column(String)
    product_image = Column(String)
    current_price = Column(Float)
    original_price = Column(Float)
    currency = Column(String, default="USD")
    category = Column(Enum(CategoryEnum), default=CategoryEnum.maybe_later)
    store_name = Column(String)
    notes = Column(String)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow)

    owner = relationship("User", back_populates="wishlist_items")
    price_history = relationship("PriceHistory", back_populates="wishlist_item")
    notifications = relationship("Notification", back_populates="wishlist_item")