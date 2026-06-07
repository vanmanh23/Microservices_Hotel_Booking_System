INSERT INTO hotels (name, description, address, city, country, rating, amenities)
VALUES
    ('Grand Saigon Hotel', 'Luxury hotel in downtown Ho Chi Minh City', '123 Nguyen Hue', 'Ho Chi Minh City', 'Vietnam', 4.5, 'WiFi,Pool,Gym,Spa'),
    ('Hanoi Heritage Inn', 'Boutique hotel near Old Quarter', '45 Hang Bac', 'Hanoi', 'Vietnam', 4.2, 'WiFi,Breakfast,Airport Shuttle'),
    ('Da Nang Beach Resort', 'Beachfront resort with ocean views', '88 My Khe Beach', 'Da Nang', 'Vietnam', 4.7, 'WiFi,Pool,Beach Access,Restaurant');

INSERT INTO rooms (hotel_id, room_number, room_type, price_per_night, capacity, amenities)
VALUES
    (1, '101', 'STANDARD', 80.00, 2, 'WiFi,TV,AC'),
    (1, '201', 'DELUXE', 120.00, 2, 'WiFi,TV,AC,Mini Bar'),
    (1, '301', 'SUITE', 200.00, 4, 'WiFi,TV,AC,Mini Bar,Jacuzzi'),
    (2, '101', 'STANDARD', 60.00, 2, 'WiFi,TV,AC'),
    (2, '102', 'DELUXE', 90.00, 2, 'WiFi,TV,AC,City View'),
    (3, '101', 'STANDARD', 100.00, 2, 'WiFi,TV,AC,Sea View'),
    (3, '201', 'SUITE', 250.00, 4, 'WiFi,TV,AC,Sea View,Private Balcony');
