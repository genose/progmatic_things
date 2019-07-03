use SDBM;
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ARTICLE' and xtype='U')

 

/*------------------------------------------------------------
*        Script SQLSERVER 
------------------------------------------------------------*/


/*------------------------------------------------------------
-- Table: tickets
------------------------------------------------------------*/
CREATE TABLE tickets(
	id_ticket    INT IDENTITY (1,1) NOT NULL ,
	annee        DATETIME  NOT NULL ,
	DATE_VENTE   DATETIME NOT NULL  ,
	CONSTRAINT tickets_PK PRIMARY KEY (id_ticket)
);


/*------------------------------------------------------------
-- Table: typebiere
------------------------------------------------------------*/
CREATE TABLE typebiere(
	id_type    INT IDENTITY (1,1) NOT NULL ,
	nom_type   VARCHAR (50) NOT NULL  ,
	CONSTRAINT typebiere_PK PRIMARY KEY (id_type)
);


/*------------------------------------------------------------
-- Table: fabriquant
------------------------------------------------------------*/
CREATE TABLE fabriquant(
	id_fabriquant    INT IDENTITY (1,1) NOT NULL ,
	nom_fabriquant   VARCHAR (50) NOT NULL  ,
	CONSTRAINT fabriquant_PK PRIMARY KEY (id_fabriquant)
);


/*------------------------------------------------------------
-- Table: couleur
------------------------------------------------------------*/
CREATE TABLE couleur(
	id_couleur    INT IDENTITY (1,1) NOT NULL CONSTRAINT couleur_PK PRIMARY KEY (id_couleur),
	nom_couleur   VARCHAR (50) NOT NULL  DEFAULT ('NOUVELLE COULEUR'),
	
);


/*------------------------------------------------------------
-- Table: continent
------------------------------------------------------------*/
CREATE TABLE continent(
	id_continent    INT IDENTITY (1,1) NOT NULL ,
	nom_continent   VARCHAR (50) NOT NULL  DEFAULT ('NOUVEAU CONTINENT'),
	CONSTRAINT continent_PK PRIMARY KEY (id_continent)
);


/*------------------------------------------------------------
-- Table: pays
------------------------------------------------------------*/
CREATE TABLE pays(
	id_pays        INT IDENTITY(1,1) NOT NULL CONSTRAINT pays_PK PRIMARY KEY (id_pays) ,
	nom_pays       VARCHAR (50) NOT NULL DEFAULT ('NOUVEAU PAYS') ,
	id_continent   INT  NOT NULL  CONSTRAINT pays_continent_FK FOREIGN KEY (id_continent) REFERENCES continent(id_continent) 
);


/*------------------------------------------------------------
-- Table: marque
------------------------------------------------------------*/
CREATE TABLE marque(
	id_marque       INT IDENTITY(1,1) NOT NULL CONSTRAINT marque_PK PRIMARY KEY (id_marque),
	nom_marque      VARCHAR (50) NOT NULL DEFAULT('NOUVELLE MARQUE'),
	id_pays         INT  NOT NULL CONSTRAINT marque_pays_FK FOREIGN KEY (id_pays) REFERENCES pays(id_pays),
	id_fabriquant   INT  NOT NULL  CONSTRAINT marque_fabriquant0_FK FOREIGN KEY (id_fabriquant) REFERENCES fabriquant(id_fabriquant)
	 
);


/*------------------------------------------------------------
-- Table: article
------------------------------------------------------------*/
CREATE TABLE article(
	ID_ARTICLE INT IDENTITY(1,1) not null CONSTRAINT PK_ID_ARTICLE PRIMARY KEY CLUSTERED ( ID_ARTICLE ASC ),
	NOM_ARTICLE varchar(50) not null DEFAULT('NOUVEL ARTICLE'), 

	prix_achat     FLOAT  NOT NULL check (CONVERT(DECIMAL(18,2), prix_achat) > 0.00),
	volume         INT  NOT NULL DEFAULT (0) check (volume >= 0.00),
	titrage        FLOAT  NOT NULL DEFAULT (0.0) check (CONVERT(DECIMAL(18,2), titrage) > 0.00) ,
	id_type        INT  NOT NULL CONSTRAINT article_typebiere_FK FOREIGN KEY (id_type) REFERENCES typebiere(id_type),
	id_couleur     INT  NOT NULL CONSTRAINT article_couleur0_FK FOREIGN KEY (id_couleur) REFERENCES couleur(id_couleur),
	id_marque      INT  NOT NULL CONSTRAINT article_marque1_FK FOREIGN KEY (id_marque) REFERENCES marque(id_marque),
	id_pays        INT  NOT NULL CONSTRAINT article_pays2_FK FOREIGN KEY (id_pays) REFERENCES pays(id_pays),
	id_continent   INT  NOT NULL  CONSTRAINT article_continent3_FK FOREIGN KEY (id_continent) REFERENCES continent(id_continent) 
);


/*------------------------------------------------------------
-- Table: vendre
------------------------------------------------------------*/
CREATE TABLE vendre(
	id_article   INT  NOT NULL CONSTRAINT vendre_tickets0_FK FOREIGN KEY (id_ticket) REFERENCES tickets(id_ticket),
	id_ticket    INT  NOT NULL CONSTRAINT vendre_article_FK FOREIGN KEY (id_article) REFERENCES article(id_article),
	quantite     INT  NOT NULL default (0) ,
	CONSTRAINT vendre_PK PRIMARY KEY (id_article,id_ticket)
 
);




