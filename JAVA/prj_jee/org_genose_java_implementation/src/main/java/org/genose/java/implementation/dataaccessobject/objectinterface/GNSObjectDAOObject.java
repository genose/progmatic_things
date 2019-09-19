package org.genose.java.implementation.dataaccessobject.objectinterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import org.genose.java.implementation.dataaccessobject.GNSObjectDAOStrings;

public interface GNSObjectDAOObject extends GNSObjectDAOStrings {

    String sDEFAULTSELECTCOMBOXLIBELLE = "Choisir ...";

    String fieldEntityName = "tablename";
    String fieldID = "id";
    String fieldLibelle = "libelle";

    String accessorFieldID = "getid";
    String accessorFieldLibelle = "getLibelle";

    Integer getId();

    void setId(Integer idKey);

    String getLibelle();

    Boolean setLibelle(String sLibelle);

    IntegerProperty getPropertyId();

    void setPropertyId(IntegerProperty idKey);

    StringProperty getPropertyLibelle();

    Boolean setPropertyLibelle(StringProperty sLibelle);


}
