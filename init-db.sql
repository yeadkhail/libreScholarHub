-- Create databases for each service
CREATE DATABASE user_service_db;
CREATE DATABASE research_paper_db;
CREATE DATABASE search_service_db;
CREATE DATABASE notification_service_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE user_service_db TO libreresearchhub;
GRANT ALL PRIVILEGES ON DATABASE research_paper_db TO libreresearchhub;
GRANT ALL PRIVILEGES ON DATABASE search_service_db TO libreresearchhub;
GRANT ALL PRIVILEGES ON DATABASE notification_service_db TO libreresearchhub;