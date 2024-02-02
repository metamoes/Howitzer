-- Creating Inital Setup and import of data
DROP DATABASE IF EXISTS Howitzer;
CREATE DATABASE Howitzer;
USE Howitzer;

CREATE TABLE Portlist (
   `Protocol Information` VARCHAR(3) NOT NULL, -- TODO port then description then information
    `Port` INT PRIMARY KEY,
   `Description` TEXT
);

CREATE TABLE CVE (
   `CVE Number` VARCHAR(16) PRIMARY KEY,
   `Status` VARCHAR(10) NOT NULL,
   `Description` TEXT,
   `References` TEXT
);

CREATE TABLE Exploits
(
    `ID`              CHAR(14) PRIMARY KEY,
    `File`            VARCHAR(255),
    `Description`     TEXT,
    `date_published`  DATE,
    `Author`          TEXT,
    `Type`            VARCHAR(50),
    `Platform`        VARCHAR(50),
    `Port`            Int,
    `Date_Added`      DATE,
    `Date_Updated`    DATE,
    `Verified`        INT,
    `Code_References` TEXT,
    `tags`           TEXT,
    `aliases`         TEXT,
    `screenshot_url`  TEXT,
    `application_url` TEXT,
    `Source_url`      TEXT
);

LOAD DATA INFILE 'tcp.csv'
INTO TABLE Portlist
FIELDS TERMINATED BY ','  -- Assuming commas are the delimiters
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

LOAD DATA INFILE 'allitems.csv'
INTO TABLE CVE
FIELDS TERMINATED BY ','  -- Assuming commas are the delimiters
LINES TERMINATED BY '\n'
IGNORE 10 LINES;  -- Skip the first 10 rows

LOAD DATA INFILE 'files_exploits.csv'
INTO TABLE Exploits
FIELDS TERMINATED BY ','  -- Assuming commas are the delimiters
LINES TERMINATED BY '\n'
IGNORE 1 LINES;

SELECT * FROM CVE; -- describe all from CVE table
SELECT * FROM Portlist; -- describe all from Portlist table
SELECT * FROM Exploits; -- describe all from Exploits table

-- TODO create port to penetration test information table to store all attempts and successes
-- TODO proccess the exploit db table into a usable format with relational table structure with port and cve number remove OSCVDB records
 -- Creating Searchable Views for CVE and Portlist tables

CREATE VIEW searchable_cve_Description AS
SELECT `CVE Number`, `Status`, `Description`, `References`
FROM CVE
WHERE CVE.Description LIKE '%windows%'; -- WHERE Description LIKE '%search_term%'; where search_term is the term you want to search for

CREATE VIEW searchable_cve_References AS
SELECT `CVE Number`, `Status`, `Description`, `References`
FROM CVE
WHERE CVE.References LIKE '%windows%'; -- WHERE Description LIKE '%search_term%'; where search_term is the term you want to search for

CREATE VIEW searchable_cve_DescAndRef AS
SELECT `CVE Number`, `Status`, `Description`, `References`
FROM CVE
WHERE CVE.Description OR CVE.References LIKE '%windows 11%'; -- WHERE Description LIKE '%search_term%'; where search_term is the term you want to search for

CREATE VIEW searchable_Portlist_DescAndPort AS
SELECT `Protocol Information`, `Port`, `Description`
FROM Portlist
WHERE Portlist.Description OR Portlist.Port LIKE '%125%'; -- WHERE Description LIKE '%search_term%'; where search_term is the term you want to search for

CREATE VIEW searchable_Portlist_Description AS
SELECT `Protocol Information`, `Port`, `Description`
FROM Portlist
WHERE Portlist.Description LIKE '%Game%'; -- WHERE Description LIKE '%search_term%'; where search_term is the term you want to search for

CREATE VIEW searchable_Portlist_Port AS
SELECT `Port`,`Protocol Information`, `Description`
FROM Portlist
WHERE Portlist.Port LIKE '125%'; -- WHERE Description LIKE 'search_term%'; where search_term is the term you want to search for with auto complete

-- Export all available information from the views

select * from searchable_cve_Description; -- select from searchable_cve view
select * from searchable_cve_References; -- select from searchable_cve view
select * from searchable_cve_DescAndRef; -- select from searchable_cve view
select * from searchable_Portlist_DescAndPort; -- select from searchable_cve view
select * from searchable_Portlist_Description; -- select from searchable_cve view
select * from searchable_Portlist_Port; -- select from searchable_cve view

select * from portlist;
select * from cve;