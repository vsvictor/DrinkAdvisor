CREATE TABLE IF NOT EXISTS 'bars'(
	'_id' 'integer' primary key autoincrement,
	'id' integer default 0,
	'rating' integer default 0,
	'dist' double default 0.0,
	'city_id' integer default 0,
	'title' varchar(150) NULL,
	'city_name' varchar(100) NULL,
	'picture' BLOB  NULL,
	'picture_name' varchar(150) NULL,
	'common_info' varchar(2000) NULL,
	'name' varchar(2000) NULL,
	'latitude' varchar(50) NULL,
	'longitude' varchar(50) NULL,
	'timezone_id' integer default 0,
	'phone' varchar(100) NULL,
	'address' varchar(250) NULL
	
);

CREATE TABLE IF NOT EXISTS 'bars_pictures'(
	'_id' 'integer' primary key autoincrement,
	'id_bar' integer default 0,
	'pic_name' varchar(50) NULL,
	'pic' BLOB  NULL
);
CREATE TABLE IF NOT EXISTS 'bars_working'(
	'_id' 'integer' primary key autoincrement,
	'id_bar' integer default 0,
	'working_day' varchar(50) NULL,
	'working_time' varchar(50) NULL
);