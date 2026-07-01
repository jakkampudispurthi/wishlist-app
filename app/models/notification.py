from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey, Float
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

class Notification(Base):
    __tablename__ = "notifications"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    wishlist_item_id = Column(Integer, ForeignKey("wishlist_items.id"), nullable=False)
    message = Column(String, nullable=False)
    old_price = Column(Float)
    new_price = Column(Float)
    is_read = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    user = relationship("User", back_populates="notifications")
    wishlist_item = relationship("WishlistItem", back_populates="notifications")