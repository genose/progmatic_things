Unit WSRestClient_ConsumeADP;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}
{$M+}

{
	Class TWSRestClient_ConsumeADP
	
	30/07/2018 SCotillard
		
	
	
	Usage :
	try 
		// ********************
		// Creation via :
		// ********************
		// myConsumeADP  := TWSRestClient_ConsumeADP.Create();
		// ********************
		//  ou alors via :
		// ********************
		// myConsumeADP  := TWSRestClient_ConsumeADP.Create();
		// ********************
		myConsumeADP.raiseOnError = true;
		myConsumeADP.raiseOnTechError = true;
		
		
		validationTicket := myConsumeADP.Rechercher();
		
		if not validationTicket then
		
			writeln(myConsumeADP.ResponseHTTPCode);
			writeln(myConsumeADP.ErrorCode);
			writeln(myConsumeADP.ErrorMessage);
			
			writeln(myConsumeADP.ResponseMessage);
			writeln(myConsumeADP.ResponseCode);
			
			writeln(myConsumeADP.ResponseRawMessage);
			writeln(myConsumeADP.ResponseRawCode);
		
		else 
			writeln(myConsumeADP.ResponseMessage);
		end;
		on E: EWSValidatationErrorExeception do
		begin
			// **************************
			writeln(‘La demande à echoué ‘+ E.Message ); // erreur dans le traitement de la reponse serveur ; erreur JSON
			
		end;

		on E: EWSTechErrorExeception do
		begin
			// **************************
			writeln(‘Une erreur technique a fait echoué la demande ‘+ E.Message ); // timeout ; serveur 500 ;
			
		end;

	end;
	



}


interface

uses Classes, StrUtils, SysUtils, Variants, WSRESTClient,  Windows,
  WSObject,
  GNSWSTypes; // in 'TWSRESTClient.pas'; // contient les URLs pour les webservices

 
    // **********************
    // Reglages 
    // **********************
    
    // '%s://%s:%s/kWSName-kWSVersion/kWSEntryPoint/kWSMethodName?' 
    const kWSName_ConsumeADP ='adp-api';
    
    Const kWSProcessName_ConsumeADP = 'Threads ...; ConsumeADP';
    Const kWSMethodName_ConsumeADP = 'graphql';
    const kWSEnvRequired_ConsumeADP = wsPreProd;
    const kWSEntryPoint_ConsumeADP = '';
    const kWSEntryPointVersion_ConsumeADP = '';
    const kWSEndPoint_ConsumeADP = 'adp-api/'; 
    

    
  
   
    



// *************************************
// ****  Le Block Suivant permet de creer les Proprerty ++ HTTPURIParams / HTTPBodyParams
// *************************************
type kWSInfo_AccessibleObjectProperties_ConsumeADP =  record
 
    const Names : array  [0..1] of string =	(
            'query',
            'apikey'
    );
    // le kIdx[Valeur] doit avoir la meme position dans kWSAccessibleProperties et kWSAccessiblePropertiesIndex
    type kWSInfo_AccessibleObjectProperties_Indexes_ConsumeADP = (
                        kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxquery,
                        kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxapikey
    );
 
    const  APIKEYS : array  [0..0] of string =	(
            'dc5747d8-bc39-442c-9a32-f37e806bfce8'
    );
    const APIKEY_Defaultidx  : integer = 0;
end;


// *************************************
// ****  Le Block Suivant permet de creer les Methodes Accessible et VALIDE ;; Conjointement avec le Block kWSInfoMethodAvail _[classname];; par ce WebService
// *************************************
type  kWSInfo_MethodNameAvail_ConsumeADP = record		
     const graphQLBase  = 'graphql';
end;

// *************************************
// ****  Le Block Suivant permet de Generer ; tout les reglages et Validation de Champs;; redirige les Params dans HTTPURIParams / HTTPBodyParams
// *************************************
var kWSInfoMethodAvail_ConsumeADP : array[0..0] of TWSInfoHTTPMethodTypeInfo = ( 
                                         ( 
                                                WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ConsumeADP.graphQLBase;
                                                WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ConsumeADP;
                                                WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_GET;
                                                WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ConsumeADP;
                                                WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ConsumeADP;
                                                WSInfoHTTPMethodURIParamsFormat : '';
                                                WSInfoHTTPMethodProcessName : kWSProcessName_ConsumeADP;
                                                WSInfoHTTPMethodURIFieldWithUserInfo : false;
                                                WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                WSInfoHTTPMethodURIUSEFieldIdx : [
                                                                                                        // :: integer(kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxapikey)
                                                                                                        // :: ,
                                                                                                        integer(kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxquery)
                                                                                                ] ;
                                        )
);

// ***************************************************
// ***************************************************
// *************  TWSRestClient_[Classname] **************
// ***************************************************
// ***************************************************

type TWSRestClient_ConsumeADP = class(TWSRESTClient)
    private
    
        // **************************************
        // **************************************
        //  ******* Variable inherent au modele TWSRestClient_[Classname]
        // ******** Nom de la Methode Reclamee lors du Create()
        // **************************************
        var __RequestedConsumeMethod : string;
        // **************************************
        // **************************************
    public
        
        // **********************
        // **********************
        constructor Create(); overload;
        constructor Create(aMethodRequested : string); overload;
        // constructor Create(aMethodRequested :  kWSInfo_MethodNameAvail_[ClassName] ); overload;
        function WSGetURL():String; overload;
        //function WSValidate() : boolean; overload;
        //function WSReplyPassed() : Boolean;overload;
        // **********************
        // **********************
    published
        
        // **************************************
        // **************************************
        // ********  Methode unifiee pour l envoi de la requete 
        // **************************************
        function Declencher( bCreateDemand : boolean = true) : boolean;
        
        // **************************************
        // **************************************
        //  *******  
        // **************************************
        // **************************************
        function RechercherADP(sMethodNameAdp:string; sStringRequeteAdp:string; iPageNumberAdp: integer; sScopeValuesADP: TStringList; var aResponseValues:  TWSArrayObject) : boolean;
        function ExtractADP(sQueryIndex : String; aArrayFiltres: TStringList; var aResponseValues:  TWSArrayArrayStrings) : boolean;
        
        
        function RechercherFromCodeStreet(sStringRequeteAdp:string; var aResponseValues:  TWSArrayArrayStrings) : boolean;
        function RechercherFromCodeVille(sStringRequeteAdp:string; var aResponseValues:  TWSArrayArrayStrings) : boolean;
        
        // **************************************
        // **************************************
        property query   : variant Index kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxquery read WSPropsReadingByIdx write WSPropsWritingByIdx;
        property apikey  : variant Index kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxapikey read WSPropsReadingByIdx write WSPropsWritingByIdx;

        // **************************************
        // **************************************
end;

implementation

// **************************************
// **************************************
constructor TWSRestClient_ConsumeADP.Create();
begin
        Create(kWSMethodName_ConsumeADP);
end;
// **************************************
// **************************************
constructor TWSRestClient_ConsumeADP.Create(aMethodRequested : string);
var iValue : integer;
var aMethodNameValid : string;
begin
    
        inherited Create();
        
    {$IFDEF DEBUG_VERBOSE} WSInternalLogActivityToFileScreen := true; {$ENDIF} 
    {$IFDEF DEBUG_VERBOSE_FILE } WSInternalLogActivityToFileLog := true; {$ENDIF}
    
    {$IFDEF DEBUG_RAISE_EALL }
        {$DEFINE DEBUG_RAISE_ERROR}
        {$DEFINE DEBUG_RAISE_ERROR_TECH}
    {$ENDIF}
    
    {$IFDEF DEBUG_RAISE_ERROR } 
        WSInternalRaiseOnError := true;
    {$ENDIF}
    
    {$IFDEF DEBUG_RAISE_ERROR_TECH }    
        WSInternalRaiseOnTechError := true;
    {$ENDIF}
    
    
    
    
     // descripteur des champs a envoyer au WEbService, via Body Ou Uri   
    RegisterWSMethodInfoForName(kWSInfoMethodAvail_ConsumeADP, aMethodRequested);  
    // creation des points d'entrees ...
    //WSQueryParams :=
    CreateWSRegisterObjectProperties(kWSInfo_AccessibleObjectProperties_ConsumeADP.Names);
    CreateWSRegisterConsumerProperties(kWSInfoMethodAvail_ConsumeADP,kWSInfo_AccessibleObjectProperties_ConsumeADP.Names);
    
      
    WSHTTPServerEnvType := kWSEnvRequired_ConsumeADP;
    WSInternalProcessName := WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodProcessName ) ;
    
    // *********** l utilisation de WSFindMethodInfo permet de valider transmis **************
    // modification de l'url
    WSHTTPQuerySendMethod :=  WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodSendType ) ;
     
      WSConsumeMethodName := WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) ;
    
    if(  length(WSConsumeMethodName) <= 2 ) then
    begin
        Raise TEWSValidationErrorException.create('La methode demandee n existe pas ... ('+WSConsumeMethodName+')');
        exit;
    end;
    WSConsumeEntryPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEntryPoint )  ;
    WSConsumeVersion :=  WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodNameVersion )  ;
    WSConsumeEndPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ConsumeADP, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEndPoint )  ; // par defaut ::  /services/ ::
    

  
  
  
  
    // **********************
    // ecrtire des entets HTTP
    WSHTTPQueryHeaders[kWSInfo_AccessibleObjectProperties_ConsumeADP.Names[integer(kWSInfo_AccessibleObjectProperties_ConsumeADP_kIdxapikey)]] := kWSInfo_AccessibleObjectProperties_ConsumeADP.APIKEYS[ integer(kWSInfo_AccessibleObjectProperties_ConsumeADP.APIKEY_Defaultidx)];
    // apikey := kKey__APIM_valeur;
    WSHTTPQueryHeaders[TWSHTTPContentType.kWSHTTPContentType_key_AcceptContentType] := TWSHTTPContentType.kWSHTTPContentType_APPJSON;
    
   WSHTTPQueryContentEncoding :=  TWSHTTPContentType.kWSHTTPContentType_ENCODING_ISO8859_15;
    WSHTTPQueryContentType := TWSHTTPContentType.kWSHTTPContentType_TEXTJSON;
    
    
    
    // **********************
    // Reglage du SSL
    // **********************
    WSHTTPQueryUseSSLType := kWSHTTPSSLMethod_SSLv2;
    // **********************	
    // :: WSHTTPQueryURIParams['query'] := '';
    inherited Clear();
    // ::  WSInternalLogActivityToFileLog := true;
    // :: WSInternalLogActivityToFileScreen := true;

// **********************
end;
// *****************************************
// WSGetURL est appele a l'ouverture de la requete HTTP
// en interne les portions seront assemblees
// *****************************************

function TWSRestClient_ConsumeADP.WSGetURL(): String; 
begin
        try
        // :: WSHTTPQueryProtocolType := TWSHTTPProtocolType.ProtocolType_HTTP;
        case WSHTTPServerEnvType of
                wsDev         :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.dev.xxxxxx.fr';  
                        WSHTTPQueryPort 	:= '8443';
                end;
                wsTest         :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.dev.xxxxxx.fr';  
                        WSHTTPQueryPort 	:= '8443';
                end;
                wsPreProd     :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.xxxxxx.fr';   
                        WSHTTPQueryPort 	:= '8443';
                end;
                wsProduction         :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.xxxxxx.fr';  
                        WSHTTPQueryPort 	:= '8443';
                end;
             end;
        // ***************************
        // le parent se charge du reste de l'assemblage
                Result  := inherited WSGetURL();
        // ***************************
        except
        on ErrGetUrl: Exception do
                begin
                        WSRaiseLogStack(' Exception::Inherited WSGetURL ('+ErrGetUrl.Message+')');                                
                        exit;
                end;
        end;

end;
// **********************************************
// **********************************************
function TWSRestClient_ConsumeADP.Declencher( bCreateDemand : boolean = true) : boolean;
var ResVariant : variant;
begin
    
       
    if ( ( Length(WSHTTPQueryURIParams[kWSInfo_AccessibleObjectProperties_ConsumeADP.Names[0]]) >10) )  then
    begin
    
       // Tout est géré en interne pour la communication / décodage JSON
       
        try
            // le json sera validee en interne
            Result := Open(WSGetURL());
            
            ResVariant := WSResponseRawText;
            
            Result := (length(ResVariant)>0) and (WSErrorCode = '200');
          
        except
        on Err: Exception do
            begin
                    // **************************
                    // **** MessageDlg('La demande à echoué :: Exception :: '+chr(13)+' Message :'+ Err.Message ,mtError,[mbOk],0);
                    // **************************
                    WSErrorMessage := Err.Message ;
                    if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                    begin
                        WSRaiseLogStack('');
                    end;
                    
            end;
        end;
    end
    else
    begin 
        WSErrorMessage :='Certains Parametres Obligatoire ne sont pas renseignes ... ';
        raise TEWSValidationErrorException.Create(WSErrorMessage);
    end;
   
end;
// **********************************************
// Envoi de requete
// Recuperation des valeurs via  accesseur WSResponseJSONObject[] ;;  UtilisationType Object TListString<String, TWSObject>
// **********************************************
function TWSRestClient_ConsumeADP.RechercherADP(sMethodNameAdp:string; sStringRequeteAdp:string; iPageNumberAdp: integer; sScopeValuesADP: TStringList; var aResponseValues:  TWSArrayObject) : boolean;
var sScopeList : string;
var iScopeIndex : integer;
var ResVariant : variant;
begin
    result := false;
    
    for iScopeIndex := 0 to sScopeValuesADP.count do begin
        sScopeList := sScopeList + ' ' + sScopeValuesADP.Strings[iScopeIndex];
    end;
     // ::  == WSHTTPQueryURIParams['query']
     query := '{'+sMethodNameAdp+'( '+sStringRequeteAdp+', pageSize: '+inttostr(iPageNumberAdp)+', pageNumber: pageSize: '+inttostr(iPageNumberAdp)+' ) { scope { '+sScopeList+' } }} ';
   
    if (Length(sStringRequeteAdp) >2)  then
    begin
        try
            begin
               if( Declencher() ) then
                begin
                    aResponseValues := WSResponseJSONObject; // :: premier index  == ['data'];
                end else
                begin
                    ;;
                end;
            end;
        except
        on Err: Exception do
            begin
                WSWriteEventLog('TWSRestClient_ConsumeADP::RechercherFromCodeStreet :: Exception :: '+Err.Message, kWSEventLogType_EWSTechInternalRuntimeErrorException);
                
                WSErrorMessage := Err.Message ;
                if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                begin
                    WSRaiseLogStack('');
                end;
                
            end;
        end;
    end
    else
    begin
        WSErrorMessage :='Certains Parametres Obligatoire ne sont pas renseignes ... ';
        if (WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
        begin
            raise TEWSValidationErrorException.Create(WSErrorMessage);
        end;
    
    end;

end;

function TWSRestClient_ConsumeADP.extractADP(sQueryIndex : String; aArrayFiltres: TStringList; var aResponseValues:  TWSArrayArrayStrings) : boolean;
var lWSResultat : TWSObject;
var aSubObject : TWSObject;
var iNbResultat : Integer;
var iIndexResultat : Integer;
var iIndexResultat_sub : integer;
var iFilterIndex : integer;
var iNbFilter : integer;
var sObjectValue : string;
var sCurrentFilterName: string;
var iFilterStart : integer;
var iFilterEnd : integer;
var sFilterParams : string;
// const  aArrayFiltres : array[0..4]  of string = ('rivoli','afnorLabel','hexacleStreet','postCode','cityAfnorLabel'); 
begin
    try
        Result := false;
        // Le JSON Parse est Maintenant Accessible dans un TStringList<string, TObject>
        // meme sans resultat de ADP, l utilisation suivante est VALIDE
        lWSResultat :=  ((WSResponseJSONObject['data'])[sQueryIndex]);
        iNbResultat :=  (lWSResultat).count;
        
        // :: WSWriteEventLog('Result Count ;; '+inttostr(lWSResultat.count)+'');
        
        Result := false;
        
        if (iNbResultat = 0) then
        begin
            
            Result := True;
            setlength(aResponseValues,0,0);
            exit;
        end
        else
        begin
            
            setlength(aResponseValues, iNbResultat, (aArrayFiltres.count));
            
            iNbFilter := (aArrayFiltres.count)-1;
            for iIndexResultat := 0 to iNbResultat-1  do
            begin
               
                 aSubObject :=  TWSArrayObject(lWSResultat.Elements[iIndexResultat])['scope'];
                
            
                iFilterStart := 0;   
                iFilterEnd := 0;
                for iFilterIndex := 0 to iNbFilter  do
                begin
                    
                     sCurrentFilterName  :=  aArrayFiltres[iFilterIndex];
               
                    if(AnsiPos(':', sCurrentFilterName) >0 ) then
                    begin
                        sFilterParams := RightStr(sCurrentFilterName,  length(sCurrentFilterName) - AnsiPos(':', sCurrentFilterName) );
                        
                        sCurrentFilterName := LeftStr(sCurrentFilterName, AnsiPos(':', sCurrentFilterName)-1 ); 
                        if  (length(sFilterParams) > 0)  and (AnsiPos(':', sFilterParams) > 0 ) then
                        begin
                                iFilterStart := strtoint( LeftStr(sFilterParams, AnsiPos(':', sFilterParams) ) );
                                                         
                                iFilterEnd := strtoint(  RightStr(sFilterParams, length(sFilterParams) - AnsiPos(':', sFilterParams) ) );
                        end
                        else if (length(sFilterParams) > 0) then
                        begin
                             iFilterEnd := strtoint( sFilterParams );
                        end;
                    end;
                    
                        if(iFilterStart > 0) then
                        begin
                            sObjectValue := RightStr(sObjectValue, length(sObjectValue) - iFilterStart);
                            
                        end;
                        
                        if(iFilterEnd > 0) then
                        begin
                            sObjectValue := LeftStr(sObjectValue, iFilterEnd);
                        end;
                    
                    
                    sObjectValue := TWSObject(aSubObject[sCurrentFilterName]).toString();
                    
                    aResponseValues[iIndexResultat,iFilterIndex] := string(sObjectValue);
                end;
            end;
            
            Result := true;
        end;
    except
    on Err: Exception do
        begin
            WSWriteEventLog('TWSRestClient_ConsumeADP::extractADPStreet :: Exception :: '+Err.Message, kWSEventLogType_EWSTechInternalRuntimeErrorException);
    
                WSErrorMessage := Err.Message ;
                if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                begin
                    WSRaiseLogStack('');
                end;
                
        end;
    end;	
    // :: Return array ;; etat traitement

end;

// **********************************************
// **********************************************
function TWSRestClient_ConsumeADP.RechercherFromCodeStreet(sStringRequeteAdp:string; var aResponseValues:  TWSArrayArrayStrings) : boolean;
 var aArrayFiltre : TStringList;
 begin
    
    // :: WSHTTPQueryURIParams['query']
     query :='{findStreetByCitycode(citycode: "'+sStringRequeteAdp+'", pageSize: 1000, pageNumber: 0){ scope{  afnorLabel cityAfnorLabel rivoli postCode  hexacleStreet  }}}';
    
    
    Result := false;
    
    if (Length(sStringRequeteAdp) >2)  then
        begin
            try
                begin
                // le json sera validee en interne
                    if( Declencher() ) then
                    begin
                        aArrayFiltre := TStringList.create();
                        aArrayFiltre.add('rivoli');
                        aArrayFiltre.add('afnorLabel');
                        aArrayFiltre.add('hexacleStreet');
                        aArrayFiltre.add('postCode');
                        aArrayFiltre.add('cityAfnorLabel');

                        Result := extractADP('findStreetByCitycode', aArrayFiltre, aResponseValues);
                       
                        
                    end else
                    begin
                        ;;
                    end;
                end;
                except
            on Err: Exception do
                begin
                    WSWriteEventLog('TWSRestClient_ConsumeADP::RechercherFromCodeStreet :: Exception :: '+Err.Message, kWSEventLogType_EWSTechInternalRuntimeErrorException);
        
                    WSErrorMessage := Err.Message ;
                    if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                    begin
                        WSRaiseLogStack('');
                    end;
                    
                end;
            end;
        end
        else
    begin
        WSErrorMessage :='Certains Parametres Obligatoire ne sont pas renseignes ... ';
        if (WSInternalRaiseOnError or WSInternalRaiseOnTechError )  then
        begin
            raise TEWSValidationErrorException.Create(WSErrorMessage);
        end;
        
    end;

end;
// **********************************************
// **********************************************
function TWSRestClient_ConsumeADP.RechercherFromCodeVille(sStringRequeteAdp:string; var aResponseValues:  TWSArrayArrayStrings) : boolean;

var aArrayFiltre : TStringList;
begin
    
    // :: WSHTTPQueryURIParams['query']
    query :='{findCityByPostcode(postcode: "'+sStringRequeteAdp+'", pageSize: 1000, pageNumber: 0){ scope{   cityId cityCode postCode addressIdentifier locationId  afnorLabel aliasAfnorLabel  }}}';
    // query := '{  findCityByPostcode(postcode: "75003", pageSize: 1000, pageNumber: 0) {    scope {      cityCode      afnorLabel      aliasAfnorLabel    }  }}';
    // apikey := 'dc5747d8-bc39-442c-9a32-f37e806bfce8';
    Result := false;
    
    if (Length(sStringRequeteAdp) >2)  then
        begin
            try
                begin
                 // le json sera validee en interne
                    if( Declencher() ) then
                    begin
                        aArrayFiltre := TStringList.create();
                        aArrayFiltre.add( 'cityCode');
                        aArrayFiltre.add('postCode:2');
                        aArrayFiltre.add('postCode');
                        aArrayFiltre.add('afnorLabel');
                        aArrayFiltre.add('aliasAfnorLabel');
                        Result := extractADP('findCityByPostcode', aArrayFiltre, aResponseValues);
                       
                    end else
                    begin
                        ;;
                    end;
                end;
                except
            on Err: Exception do
                begin
                    WSWriteEventLog('TWSRestClient_ConsumeADP::RechercherFromCodeVille :: Exception :: '+Err.Message, kWSEventLogType_EWSTechInternalRuntimeErrorException);
    
                    WSErrorMessage := Err.Message ;
                    if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                    begin
                        WSRaiseLogStack('');
                    end;
                    
                end;
            end;	
        end
        else
    begin
    
        WSErrorMessage :='Certains Parametres Obligatoire ne sont pas renseignes ... ';
        if (WSInternalRaiseOnError or WSInternalRaiseOnTechError )  then
        begin
            raise TEWSValidationErrorException.Create(WSErrorMessage);
        end;
        
    end;

end;

end.
