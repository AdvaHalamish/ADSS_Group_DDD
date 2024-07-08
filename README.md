## README

### Developers:
- Adva Halamish 206594012
- Shani Zilberberg 207106824
- Ido Badash 206917510

### Instructions for Running the Code:
Run the following command:
java -jar adss2024_v02.jar

### Tools and Libraries Used:
- Database Engine: SQLite
- Testing Framework: JUnit
- JDBC Driver: org.xerial/sqlite-jdbc
- Build Tool: Maven

### Database Schema:
-Products: Stores data about products, such as ProductCode, names, categories, sub-categories, sizes, manufacturers, cost prices, selling prices, statuses, quantities in store, quantities in warehouse, and minimum quantities for alerts.
Items: Manages inventory items, including their codes, product codes, storage locations (Store/Warehouse), expiration dates, and statuses (Defective, Sold, Expired).
Discounts: Tracks discount information, including discount rates, start dates, end dates, and associated product codes.
