ALTER TABLE att_records 
    ADD COLUMN check_in_latitude DECIMAL(10, 7),
    ADD COLUMN check_in_longitude DECIMAL(10, 7),
    ADD COLUMN check_in_location VARCHAR(255),
    ADD COLUMN check_out_latitude DECIMAL(10, 7),
    ADD COLUMN check_out_longitude DECIMAL(10, 7),
    ADD COLUMN check_out_location VARCHAR(255);
