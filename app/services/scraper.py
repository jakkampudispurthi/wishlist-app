import httpx
from bs4 import BeautifulSoup
import re

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Accept-Language": "en-US,en;q=0.9",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Encoding": "gzip, deflate, br",
    "Connection": "keep-alive",
}

def extract_price(text: str):
    if not text:
        return None
    match = re.search(r'\$?\s*(\d{1,6}(?:,\d{3})*(?:\.\d{2})?)', text)
    if match:
        price_str = match.group(1).replace(',', '')
        try:
            return float(price_str)
        except:
            return None
    return None

def scrape_amazon(soup, url):
    product_name, price, image = None, None, None
    name_tag = soup.find(id="productTitle")
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("span", {"class": "a-price-whole"}) or
        soup.find("span", {"id": "priceblock_ourprice"}) or
        soup.find("span", {"id": "priceblock_dealprice"}) or
        soup.find("span", {"class": "a-offscreen"})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    img_tag = soup.find("img", {"id": "landingImage"})
    if img_tag:
        image = img_tag.get("src") or img_tag.get("data-src")
    return product_name, price, image

def scrape_ebay(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"class": "x-item-title__mainTitle"}) or
        soup.find("h1", {"class": "it-ttl"})
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("div", {"class": "x-price-primary"}) or
        soup.find("span", {"id": "prcIsum"}) or
        soup.find("span", {"id": "mm-saleDscPrc"})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    img_tag = soup.find("img", {"id": "icImg"})
    if img_tag:
        image = img_tag.get("src")
    return product_name, price, image

def scrape_walmart(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"itemprop": "name"}) or
        soup.find("h1", {"class": re.compile("prod-title")})
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("span", {"itemprop": "price"}) or
        soup.find("span", {"class": re.compile("price-characteristic")})
    )
    if price_tag:
        price = extract_price(price_tag.get("content") or price_tag.get_text())
    og_image = soup.find("meta", {"property": "og:image"})
    if og_image:
        image = og_image.get("content")
    return product_name, price, image

def scrape_bestbuy(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"class": re.compile("heading-5")}) or
        soup.find("div", {"class": re.compile("sku-title")})
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("div", {"class": re.compile("priceView-hero-price")}) or
        soup.find("span", {"class": re.compile("sr-only")}) or
        soup.find("div", {"class": re.compile("pricing-price")})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    og_image = soup.find("meta", {"property": "og:image"})
    if og_image:
        image = og_image.get("content")
    return product_name, price, image

def scrape_target(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"data-test": "product-title"}) or
        soup.find("h1", {"class": re.compile("Heading")})
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("span", {"data-test": "product-price"}) or
        soup.find("div", {"data-test": re.compile("price")})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    og_image = soup.find("meta", {"property": "og:image"})
    if og_image:
        image = og_image.get("content")
    return product_name, price, image

def scrape_etsy(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"data-buy-box-listing-title": True}) or
        soup.find("h1", {"class": re.compile("wt-text-body")}) or
        soup.find("h1")
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("div", {"data-selector": "price-only"}) or
        soup.find("p", {"class": re.compile("wt-text-title")}) or
        soup.find("span", {"class": re.compile("currency-value")})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    og_image = soup.find("meta", {"property": "og:image"})
    if og_image:
        image = og_image.get("content")
    return product_name, price, image

def scrape_newegg(soup, url):
    product_name, price, image = None, None, None
    name_tag = (
        soup.find("h1", {"class": "product-title"}) or
        soup.find("h1", {"class": re.compile("product")})
    )
    if name_tag:
        product_name = name_tag.get_text(strip=True)
    price_tag = (
        soup.find("li", {"class": "price-current"}) or
        soup.find("span", {"class": re.compile("price-current")})
    )
    if price_tag:
        price = extract_price(price_tag.get_text(strip=True))
    img_tag = soup.find("img", {"class": re.compile("product-view-img")})
    if img_tag:
        image = img_tag.get("src")
    return product_name, price, image

def scrape_generic(soup, url):
    product_name, price, image = None, None, None
    og_title = soup.find("meta", {"property": "og:title"})
    if og_title:
        product_name = og_title.get("content")
    og_image = soup.find("meta", {"property": "og:image"})
    if og_image:
        image = og_image.get("content")
    for pattern in ["price", "Price", "PRICE", "product-price", "sale-price"]:
        price_tag = soup.find(class_=re.compile(pattern))
        if price_tag:
            price = extract_price(price_tag.get_text(strip=True))
            if price:
                break
    if not product_name:
        title_tag = soup.find("title")
        if title_tag:
            product_name = title_tag.get_text(strip=True)
    return product_name, price, image

def scrape_product(url: str):
    try:
        with httpx.Client(headers=HEADERS, follow_redirects=True, timeout=15) as client:
            response = client.get(url)
            soup = BeautifulSoup(response.text, 'html.parser')

            # Route to the right scraper based on URL
            if "amazon.com" in url:
                product_name, price, image = scrape_amazon(soup, url)
            elif "ebay.com" in url:
                product_name, price, image = scrape_ebay(soup, url)
            elif "walmart.com" in url:
                product_name, price, image = scrape_walmart(soup, url)
            elif "bestbuy.com" in url:
                product_name, price, image = scrape_bestbuy(soup, url)
            elif "target.com" in url:
                product_name, price, image = scrape_target(soup, url)
            elif "etsy.com" in url:
                product_name, price, image = scrape_etsy(soup, url)
            elif "newegg.com" in url:
                product_name, price, image = scrape_newegg(soup, url)
            else:
                product_name, price, image = scrape_generic(soup, url)

            store_name = url.split("/")[2].replace("www.", "")

            return {
                "product_name": product_name,
                "current_price": price,
                "product_image": image,
                "store_name": store_name,
                "success": True
            }

    except Exception as e:
        return {
            "product_name": None,
            "current_price": None,
            "product_image": None,
            "store_name": None,
            "success": False,
            "error": str(e)
        }