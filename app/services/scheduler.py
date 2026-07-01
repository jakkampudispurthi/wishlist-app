from apscheduler.schedulers.background import BackgroundScheduler
from sqlalchemy.orm import Session
from app.database import SessionLocal
from app.models.wishlist import WishlistItem
from app.models.price_history import PriceHistory
from app.models.notification import Notification
from app.services.scraper import scrape_product

def check_prices():
    print("⏰ Running price check job...")
    db: Session = SessionLocal()

    try:
        # Get all wishlist items that have a URL
        items = db.query(WishlistItem).all()

        for item in items:
            if not item.url:
                continue

            # Scrape current price
            result = scrape_product(item.url)

            if not result["success"] or not result["current_price"]:
                continue

            new_price = result["current_price"]
            old_price = item.current_price

            # Save to price history
            price_record = PriceHistory(
                wishlist_item_id=item.id,
                price=new_price
            )
            db.add(price_record)

            # Check if price dropped
            if old_price and new_price < old_price:
                drop = round(old_price - new_price, 2)
                percent = round((drop / old_price) * 100, 1)

                # Create notification
                notification = Notification(
                    user_id=item.user_id,
                    wishlist_item_id=item.id,
                    message=f"Price dropped by ${drop} ({percent}%) on {item.product_name or item.url}!",
                    old_price=old_price,
                    new_price=new_price
                )
                db.add(notification)
                print(f"🔔 Price drop detected for item {item.id}!")

            # Update current price
            item.current_price = new_price
            db.commit()

    except Exception as e:
        print(f"❌ Error in price check: {e}")
    finally:
        db.close()

# Create scheduler
scheduler = BackgroundScheduler()
scheduler.add_job(check_prices, 'interval', hours=6)

def start_scheduler():
    scheduler.start()
    print("✅ Price check scheduler started — runs every 6 hours!")