ALTER TABLE personne_Adresse DROP FOREIGN KEY FK_personne_Adresse_ID
ALTER TABLE personne_Adresse DROP FOREIGN KEY FK_personne_Adresse_adresses_IDADRESSE
DROP TABLE Adresse
DROP TABLE personne
DROP TABLE personne_Adresse
DELETE FROM SEQUENCE WHERE SEQ_NAME = 'SEQ_GEN'
