CREATE TABLE approved_site (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_id VARCHAR(256),
	client_id VARCHAR(256),
	creation_date DATE,
	access_date DATE,
	timeout_date DATE,
	whitelisted_site_id VARCHAR(256)
);