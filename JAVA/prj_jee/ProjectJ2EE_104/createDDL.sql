CREATE TABLE ACCOUNTS (IDACCOUNT INTEGER AUTO_INCREMENT NOT NULL, account_type INTEGER, CREATIONDATE DATETIME, PRIMARY KEY (IDACCOUNT))
CREATE TABLE ADRESSES (IDADRESSE INTEGER AUTO_INCREMENT NOT NULL, ADDITIONNALNAME VARCHAR(255), ADRESSETYPE INTEGER, CITYNAME INTEGER, EMAIL VARCHAR(255), NAME VARCHAR(255), POSTCODE VARCHAR(255), ACCOUNTINFO_IDACCOUNT INTEGER, IDENTITYINFO_ID INTEGER, PRIMARY KEY (IDADRESSE))
CREATE TABLE PERSONNES (ID INTEGER AUTO_INCREMENT NOT NULL, AGE INTEGER, emailidentity email, named nom, lastname prenom, PRIMARY KEY (ID))
CREATE TABLE USERS (USERID INTEGER AUTO_INCREMENT NOT NULL, USEREMAIL VARCHAR(255), USERLOGIN VARCHAR(255), USERNAME VARCHAR(255), USERPASSWORD VARCHAR(255), ACCOUNTINFO_IDACCOUNT INTEGER, USERIDENTITY_ID INTEGER, PRIMARY KEY (USERID))
CREATE TABLE USERSHISTORY (HISTORYID INTEGER AUTO_INCREMENT NOT NULL, HISTORYEVENT VARCHAR(255), HISTORYDATE DATETIME, USERHISTORYINFO_USERID INTEGER, PRIMARY KEY (HISTORYID))
CREATE TABLE ARTICLES (IDARTICLE INTEGER AUTO_INCREMENT NOT NULL, DESCRIPTION VARCHAR(255), PRIX DOUBLE, ARTICLESCOMMANDE_IDCOMMANDE INTEGER, PRIMARY KEY (IDARTICLE))
CREATE TABLE COMMANDES (IDCOMMANDE INTEGER AUTO_INCREMENT NOT NULL, commandestatus etatCommande, ACCOUNTINFO_IDACCOUNT INTEGER, ADRESSEFACTURATION_IDADRESSE INTEGER, ADRESSELIVRAISON_IDADRESSE INTEGER, ADRESSEWAREHOUSERETURN_IDADRESSE INTEGER, PRIMARY KEY (IDCOMMANDE))
CREATE TABLE WAREHOUSES_ARTICLE_POSITIONS (POSITIONID INTEGER NOT NULL, POSITIONDESCRITION VARCHAR(255), POSITIONQTY INTEGER, POSITIONQTYDATE INTEGER, POSITIONQTYLASTDATE INTEGER, POSITIONREAPPROAWAIT INTEGER, POSITIONREAPPROAWAITDATE DATETIME, ARTICLESSTOCKSPOSITIONS_IDARTICLE INTEGER, PRIMARY KEY (POSITIONID))
ALTER TABLE ADRESSES ADD CONSTRAINT FK_ADRESSES_ACCOUNTINFO_IDACCOUNT FOREIGN KEY (ACCOUNTINFO_IDACCOUNT) REFERENCES ACCOUNTS (IDACCOUNT)
ALTER TABLE ADRESSES ADD CONSTRAINT FK_ADRESSES_IDENTITYINFO_ID FOREIGN KEY (IDENTITYINFO_ID) REFERENCES PERSONNES (ID)
ALTER TABLE USERS ADD CONSTRAINT FK_USERS_ACCOUNTINFO_IDACCOUNT FOREIGN KEY (ACCOUNTINFO_IDACCOUNT) REFERENCES ACCOUNTS (IDACCOUNT)
ALTER TABLE USERS ADD CONSTRAINT FK_USERS_USERIDENTITY_ID FOREIGN KEY (USERIDENTITY_ID) REFERENCES PERSONNES (ID)
ALTER TABLE USERSHISTORY ADD CONSTRAINT FK_USERSHISTORY_USERHISTORYINFO_USERID FOREIGN KEY (USERHISTORYINFO_USERID) REFERENCES USERS (USERID)
ALTER TABLE ARTICLES ADD CONSTRAINT FK_ARTICLES_ARTICLESCOMMANDE_IDCOMMANDE FOREIGN KEY (ARTICLESCOMMANDE_IDCOMMANDE) REFERENCES COMMANDES (IDCOMMANDE)
ALTER TABLE COMMANDES ADD CONSTRAINT FK_COMMANDES_ADRESSEFACTURATION_IDADRESSE FOREIGN KEY (ADRESSEFACTURATION_IDADRESSE) REFERENCES ADRESSES (IDADRESSE)
ALTER TABLE COMMANDES ADD CONSTRAINT FK_COMMANDES_ADRESSEWAREHOUSERETURN_IDADRESSE FOREIGN KEY (ADRESSEWAREHOUSERETURN_IDADRESSE) REFERENCES ADRESSES (IDADRESSE)
ALTER TABLE COMMANDES ADD CONSTRAINT FK_COMMANDES_ADRESSELIVRAISON_IDADRESSE FOREIGN KEY (ADRESSELIVRAISON_IDADRESSE) REFERENCES ADRESSES (IDADRESSE)
ALTER TABLE COMMANDES ADD CONSTRAINT FK_COMMANDES_ACCOUNTINFO_IDACCOUNT FOREIGN KEY (ACCOUNTINFO_IDACCOUNT) REFERENCES ACCOUNTS (IDACCOUNT)
ALTER TABLE WAREHOUSES_ARTICLE_POSITIONS ADD CONSTRAINT WRHSSARTICLEPOSITIONSRTCLSSTOCKSPOSITIONSIDARTICLE FOREIGN KEY (ARTICLESSTOCKSPOSITIONS_IDARTICLE) REFERENCES ARTICLES (IDARTICLE)
