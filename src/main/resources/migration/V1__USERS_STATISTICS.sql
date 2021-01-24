CREATE TABLE STATISTICS (
  userID BIGINT PRIMARY KEY,
  recent_run_totals VARCHAR(100) NOT NULL,
  all_run_totals  VARCHAR(100) NOT NULL,
  recent_swim_totals VARCHAR(100) NOT NULL,
  biggest_ride_distance VARCHAR(100) NOT NULL,
  ytd_swim_totals VARCHAR(100) NOT NULL,
  all_swim_totals VARCHAR(100) NOT NULL
);