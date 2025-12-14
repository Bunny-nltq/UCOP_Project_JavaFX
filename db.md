
+Table: cart_items
Columns:
id bigint AI PK 
cart_id bigint 
item_id bigint 
quantity bigint 
unit_price decimal(19,4) 
created_at datetime 
updated_at datetime


Table: carts
Columns:
id bigint AI PK 
account_id bigint 
created_at datetime 
updated_at datetime

Table: customers
Columns:
id bigint AI PK 
account_id bigint 
full_name varchar(100) 
email varchar(100) 
phone varchar(20) 
address varchar(255) 
city varchar(50) 
postal_code varchar(20) 
created_at datetime 
updated_at datetime

Table: order_items
Columns:
id bigint AI PK 
order_id bigint 
item_id bigint 
quantity bigint 
unit_price decimal(19,4) 
item_discount decimal(19,4) 
created_at datetime 
updated_at datetime

Table: order_items
Columns:
id bigint AI PK 
order_id bigint 
item_id bigint 
quantity bigint 
unit_price decimal(19,4) 
item_discount decimal(19,4) 
created_at datetime 
updated_at datetime

Table: payments
Columns:
id bigint AI PK 
order_id bigint 
payment_method enum('COD','BANK_TRANSFER','GATEWAY','WALLET') 
amount decimal(19,4) 
status enum('PENDING','PROCESSING','SUCCESS','FAILED','CANCELED') 
transaction_id varchar(100) 
reference_number varchar(100) 
notes varchar(500) 
created_at datetime 
updated_at datetime 
created_by varchar(100) 
updated_by varchar(100) 
paid_at datetime

Table: products
Columns:
id bigint AI PK 
name varchar(200) 
description text 
category varchar(100) 
price decimal(19,4) 
image_url varchar(500) 
stock_quantity bigint 
is_active tinyint(1) 
created_at datetime 
updated_at datetime

Table: promotion_usages
Columns:
id bigint AI PK 
promotion_id bigint 
order_id bigint 
account_id bigint 
discount_amount decimal(19,4) 
used_at datetime

Table: promotion_usages
Columns:
id bigint AI PK 
promotion_id bigint 
order_id bigint 
account_id bigint 
discount_amount decimal(19,4) 
used_at datetime

Table: refunds
Columns:
id bigint AI PK 
payment_id bigint 
amount decimal(19,4) 
refund_type enum('FULL','PARTIAL') 
status enum('PENDING','PROCESSING','SUCCESS','FAILED','CANCELED') 
reason varchar(500) 
refund_transaction_id varchar(100) 
created_at datetime 
updated_at datetime 
created_by varchar(100) 
updated_by varchar(100) 
refunded_at datetime

Table: roles
Columns:
id bigint 
name varchar(50) 
description varchar(255)

Table: shipments
Columns:
id bigint AI PK 
order_id bigint 
tracking_number varchar(100) 
carrier varchar(100) 
status enum('PENDING','PICKED','PACKED','SHIPPED','IN_TRANSIT','OUT_FOR_DELIVERY','DELIVERED','FAILED','RETURNED') 
expected_delivery_date datetime 
actual_delivery_date datetime 
warehouse_id bigint 
notes varchar(500) 
created_at datetime 
updated_at datetime 
created_by varchar(100) 
updated_by varchar(100)

able: stock_items
Columns:
id bigint AI PK 
warehouse_id bigint 
item_id bigint 
on_hand bigint 
reserved bigint 
low_stock_threshold bigint 
is_low_stock tinyint(1) 
created_at datetime 
updated_at datetime 
created_by varchar(100) 
updated_by varchar(100)

Table: users
Columns:
id bigint 
username varchar(100) 
password varchar(255) 
email varchar(100) 
status tinyint(1) 
created_at datetime 
updated_at datetime 
is_active tinyint(1) 
created_by varchar(100) 
updated_by varchar(100) 
active bit(1) 
locked bit(1)

Table: warehouses
Columns:
id bigint AI PK 
name varchar(100) 
address varchar(255) 
phone varchar(20) 
is_active tinyint(1) 
created_at datetime 
updated_at datetime 
created_by varchar(100) 
updated_by varchar(100)