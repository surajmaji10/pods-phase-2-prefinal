#!/bin/sh
echo "Waiting for H2 database to start..."

# Wait for the database to become available
sleep 5

# Connect to H2 and run the import command
java -cp /opt/h2/bin/h2-*.jar org.h2.tools.Shell -url "jdbc:h2:/opt/h2-data/mydb" -user sa -password "" -sql "
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price DECIMAL(10,2),
    stock_quantity INT
);
INSERT INTO products (id, name, description, price, stock_quantity)
SELECT * FROM CSVREAD('/opt/h2-data/data.csv', 'id,name,description,price,stock_quantity', 'UTF-8');
"
echo "CSV data imported successfully!"

