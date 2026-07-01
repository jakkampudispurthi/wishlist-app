from sqlalchemy import Column, Integer, Float, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

class PriceHistory(Base):
    __tablename__ = "price_history"

    id = Column(Integer, primary_key=True, index=True)
    wishlist_item_id = Column(Integer, ForeignKey("wishlist_items.id"), nullable=False)
    price = Column(Float, nullable=False)
    checked_at = Column(DateTime, default=datetime.utcnow)

    wishlist_item = relationship("WishlistItem", back_populates="price_history")