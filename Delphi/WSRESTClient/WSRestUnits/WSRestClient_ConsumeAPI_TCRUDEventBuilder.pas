Unit WSRestClient_ConsumeAPI_TCRUDEventBuilder ;
{
	Class TWSRest_CRUDEventBuilderRest
	
	05/09/2018 SCotillard
		http://confluence.dvpt.xxxxxx.fr:8090/display/CNR/CRUDEventBuilderRest
	
	
	Usage :
	try 
		// ********************
		// Creation via :
		// ********************
		// myCRUDEventBuilderTicket  := TWSRestClient_ConsumeAPI_TCRUDEventBuilder.Create(WS_CRUDEventBuilderDemandType_RENTAL);
		// ********************
		//  ou alors via :
		// ********************
		// myCRUDEventBuilderTicket  := TWSRestClient_ConsumeAPI_TCRUDEventBuilder.Create();
		// ********************
		myCRUDEventBuilderTicket.raiseOnError = true;
		myCRUDEventBuilderTicket.raiseOnTechError = true;
		
		myCRUDEventBuilderTicket.clientId = [StringType];
		myCRUDEventBuilderTicket.contratId = [JSONType / StringType] ???
		
		myCRUDEventBuilderTicket.equipments = [JSONType / StringType] ???
		
		myCRUDEventBuilderTicket.shippingAddress = [JSONType / StringType] ???
		myCRUDEventBuilderTicket.comment = [StringType]
		
		validationTicket := myCRUDEventBuilderTicket.DeclencherExpedition();
		
		if not validationTicket then
		
			writeln(myCRUDEventBuilderTicket.ResponseHTTPCode);
			writeln(myCRUDEventBuilderTicket.ErrorCode);
			writeln(myCRUDEventBuilderTicket.ErrorMessage);
			
			writeln(myCRUDEventBuilderTicket.ResponseMessage);
			writeln(myCRUDEventBuilderTicket.ResponseCode);
			
			writeln(myCRUDEventBuilderTicket.ResponseRawMessage);
			writeln(myCRUDEventBuilderTicket.ResponseRawCode);
		
		else 
			writeln(myCRUDEventBuilderTicket.ResponseMessage);
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
	
	---- CREATION / POST ----

		clientId : [REQUIS]

		contratId:  [REQUIS] == 1 Contrat 
		
		demandKey: [Optional] shipping demand key

		equipments:  [REQUIS] == NN equipements pour le contrat concerné

		shippingAddress: [REQUIS] == ID Adresse préenregistrée, pouvant etre differente de celle du contrat ou client / Adresse de retour entrepot

		userInfos : [REQUIS]

		shippingDemandType: [REQUIS] == 1 VALUE(PURCHASE,RENTAL,AVAILABILITY)

		comment: [Optional]

		==== >> RESULTAT, numero de la demande == demandKey : ship000006


}


interface

uses Classes, StrUtils, SysUtils, Variants, WSRESTClient,
  WSObject,
  GNSWSTypes; // in 'TWSRESTClient.pas'; // contient les URLs pour les webservices
        
// **********************
// Reglages 
// **********************
Const kWSProcessName_CRUDEventBuilderRest = 'Threads ...; CRUDEventBuilderRest';
Const kWSMethodName_CRUDEventBuilderRest = 'CRUDEventBuilderRest';
const kWSEnvRequired_CRUDEventBuilderRest = wsTest;
const kWSEntryPoint_CRUDEventBuilderRest ='eventbuilder';
const kWSEntryPointVersion_CRUDEventBuilderRest = '';
const kWSEndPoint_CRUDEventBuilderRest = '/';

// Valeur pour le DemandType
type kWSInfo_CRUDEventBuilderDemandType = record
                
    type kWSInfo_index = (
                                            kWSInfo_CRUDEventBuilderDemandType_NONE =0 );
    
        
    const kWSInfo_index_name : array [0..0] of string = ( '' );
   
    const NONE : string = '' ;
    
end;
        
 // index  des proprietes       
 type kWSInfo_AccessibleObjectProperties_Indexes_CRUDEventBuilderRest = (
                
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxclientId, 
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlastname ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxfirstname ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress2 ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxzipCode ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcity ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcountry ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxphoneNumber ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcompanyName ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlogin ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxgsm ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxemail ,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcustomerDatas,
                 
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxquery,
                 kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxapikey
 );
 
 // **** nom des proprietes
 type kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest =  record
         const Names : array  [0..15] of string =	(
            
                        'idClient',
                        'lastname',
                        'firstname',
                        'address',
                        'address2',
                        'zipCode',
                        'city',
                        'country',
                        'phoneNumber',
                        'companyName',
                        'login',
                        'gsm',
                        'email', 
                        'customerDatas',
                        
                        // :: Valeur special ...
                         'query',
                         'apikey'
                        
                        
         );
         
    const  APIKEYS : array  [0..1] of string =	(
            'eventbuilder-consumer-test-20180726',
            'dc5747d8-bc39-442c-9a32-f37e806bfce8'
     );
    
    const APIKEY_Defaultidx  : integer = 0; // utilisation de la premiere cle .... 
 end;
 
    
// ****    const kWSInfo_ValidateRulesProperties_CRUDEventBuilderRest :  array [1..7] of string = (
// ****                    'clientId:string:length:{>0,5:10}',
// ****                    'contratId:string:length:{>0,5:10}',
// ****                    'demandKey',
// ****                    'shippingDemandType',
// ****                    'shippingAddress',
// ****                    'equipments',
// ****                    'comment'
// ****    );
 // ::  https://apim.kong.dev.xxxxxx.fr:8443/eventbuilder/isp-customer
 type  kWSInfo_MethodNameAvail_CRUDEventBuilderRest = record		
         const createCRUDEventBuilderDemand  = 'createCRUDEventBuilderDemand';
         const createCRUDEventBuilder_ISPCONSUMER  ='isp-customer';
       
 end;
 
 var kWSInfoMethodAvail_CRUDEventBuilderRest : array[0..0] of TWSInfoHTTPMethodTypeInfo = ( 
                                                  ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_CRUDEventBuilderRest.createCRUDEventBuilder_ISPCONSUMER;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_CRUDEventBuilderRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_CRUDEventBuilderRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_CRUDEventBuilderRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_CRUDEventBuilderRest;
                                                         WSInfoHTTPMethodBODYFieldAsString : true;
                                                         WSInfoHTTPMethodBODYFieldAsJSONString : true;
                                                         
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : false;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         // WSInfoHTTPMethodURIUSEFieldIdx : [  integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxquery )];
                                                         
                                                         WSInfoHTTPMethodBODYUSEFieldIdx : [  integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxclientId ) 
                                                                                            ,integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlastname ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxfirstname ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress2 ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxzipCode ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcity ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcountry ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxphoneNumber ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcompanyName ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlogin ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxgsm ) ,
                                                                                                                integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxemail ) 
                                                                                                                // :: ,integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcustomerDatas )  
                                                                                                         ] ;
                                                 ) 
                                         );
        
// Declaration de la classe
   
type TWSRestClient_ConsumeAPI_TCRUDEventBuilder = class(TWSRESTClient)
        // **********************
        // **********************
    private
    
        var __RequestedConsumeMethod : string;
        // methode pour validation du type de deamande
        function getCRUDEventBuilderDemandType(const aKeyNameIdx: integer) : variant;
        procedure setCRUDEventBuilderDemandType(const aKeyNameIdx: integer; aValue : variant);
        // **********************
        // **********************
    public
        // **********************
        // **********************
        constructor Create; overload;
        constructor Create(aMethodRequested : string); overload;
        
        //function WSValidate() : boolean; overload;
        //function WSReplyPassed() : Boolean;overload;
        function WSGetURL():string; overload;
        // **********************
        // **********************
    published
        // **********************
        // **********************
        function Declencher() : boolean;
        
        property idClient                       : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxclientId read WSPropsReadingByIdx write WSPropsWritingByIdx;

        property lastname                   : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlastname read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property firstname                   : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxfirstname read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property address                     : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property address2                   : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxaddress2 read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property zipCode                    : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxzipCode read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property city                          : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcity read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property country                    : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcountry read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property phoneNumber           : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxphoneNumber read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property companyName           : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcompanyName read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property login                         : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxlogin read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property gsm                         : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxgsm read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property emai                         : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxemail read WSPropsReadingByIdx write WSPropsWritingByIdx ;              
        property customerDatas          : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxcustomerDatas read getCRUDEventBuilderDemandType  write setCRUDEventBuilderDemandType;
        
        
        // **************************************
        // **************************************
        property query   : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxquery read WSPropsReadingByIdx write WSPropsWritingByIdx;
        property apikey  : variant Index kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxapikey read WSPropsReadingByIdx write WSPropsWritingByIdx;

        // **************************************
        // ************************************** 
             
end;

implementation
 
constructor TWSRestClient_ConsumeAPI_TCRUDEventBuilder.Create();
begin
        Create(kWSInfo_MethodNameAvail_CRUDEventBuilderRest.createCRUDEventBuilder_ISPCONSUMER);
end;
constructor TWSRestClient_ConsumeAPI_TCRUDEventBuilder.Create(aMethodRequested : string);
var iValue : integer;
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
    RegisterWSMethodInfoForName(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested);
    
    // creation des points d'entrees ...
    //WSQueryParams :=
    CreateWSRegisterObjectProperties(kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest.Names);
    CreateWSRegisterConsumerProperties(kWSInfoMethodAvail_CRUDEventBuilderRest,kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest.Names);
    
    WSHTTPServerEnvType := kWSEnvRequired_CRUDEventBuilderRest;
    WSInternalProcessName := WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodProcessName ) ;
    
    // *********** l utilisation de WSFindMethodInfo permet de valider transmis **************
    // modification de l'url
    WSHTTPQuerySendMethod :=  WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodSendType ) ;
     
    WSConsumeMethodName := '';
    __RequestedConsumeMethod := WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) ;
    if(  length(__RequestedConsumeMethod) <= 2 ) then
    begin
        Raise TEWSValidationErrorException.create('La methode demandee n existe pas ... ('+WSConsumeMethodName+')');
        exit;
    end;
    
    WSConsumeMethodName := __RequestedConsumeMethod;
    
    WSConsumeEntryPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEntryPoint )  ;
    WSConsumeVersion :=  WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodNameVersion )  ;
    WSConsumeEndPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_CRUDEventBuilderRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEndPoint )  ; // par defaut ::  /services/ ::
 
   
     // ::  WSInternalLogActivityToFileLog := true;
      // :: WSInternalLogActivityToFileScreen := true;

          // **********************
    // ecrtire des entets HTTP
       WSHTTPQueryHeaders[kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest.Names[integer(kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest_kIdxapikey)]] := kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest.APIKEYS[integer( kWSInfo_AccessibleObjectProperties_CRUDEventBuilderRest.APIKEY_Defaultidx)];

    // apikey := kKey__APIM_valeur;
    WSHTTPQueryHeaders[ TWSHTTPContentType.kWSHTTPContentType_key_AcceptContentType ] := TWSHTTPContentType.kWSHTTPContentType_APPJSON;
    
   WSHTTPQueryContentEncoding :=  TWSHTTPContentType.kWSHTTPContentType_ENCODING_ISO8859_15;
    WSHTTPQueryContentType := TWSHTTPContentType.kWSHTTPContentType_APPJSON;
    
    
    
    // **********************
    // Reglage du SSL
    // **********************
    WSHTTPQueryUseSSLType := kWSHTTPSSLMethod_SSLv2;
    
    
     // efface les valeurs interne
    inherited Clear(); // :: forward WSInternalClear();
        // ::  WSInternalLogActivityToFileLog := true;
    // :: WSInternalLogActivityToFileScreen := true;
      
end;

// ***************************************
// ******* Function overloaded
// ***************************************
function TWSRestClient_ConsumeAPI_TCRUDEventBuilder.WSGetURL:String;
begin
    try
    
        // :: WSHTTPQueryProtocolType := TWSHTTPProtocolType.ProtocolType_HTTP;
        case WSHTTPServerEnvType of
             wsDev         :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.dev.xxxxxx.fr';  
                        WSHTTPQueryPort 	:= '8443';
                        
                        // WSHTTPQueryHost  	:=  'microservice-proxy.dev.xxxxxx.fr';
                        //WSHTTPQueryPort 	:= '';
                end;
                wsTest         :
                begin
                        WSHTTPQueryHost  	:= 'apim.kong.dev.xxxxxx.fr';  
                        WSHTTPQueryPort 	:= '8443';
                        
                        //WSHTTPQueryHost  	:=  'microservice-proxy.dev.xxxxxx.fr';
                        //WSHTTPQueryPort 	:= '';
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
// *************************
// ***** assurrer la correspondance du type de shipping
// *************************
procedure TWSRestClient_ConsumeAPI_TCRUDEventBuilder.setCRUDEventBuilderDemandType(const aKeyNameIdx: integer; aValue : variant);
var iDemandTypeIndex : integer;
var bValidType : boolean;
begin
         
    bValidType  := false;
    iDemandTypeIndex := 0;
    for iDemandTypeIndex := 0 to high(kWSInfo_CRUDEventBuilderDemandType.kWSInfo_index_name) do begin
       if (kWSInfo_CRUDEventBuilderDemandType.kWSInfo_index_name[iDemandTypeIndex] = string(aValue)) then
       begin
           bValidType  := true;
           WSPropsWritingByIdx(aKeyNameIdx, aValue);
           break;
       end;
   end;
    
    if(not bValidType) then
    begin
        raise TEWSValidationErrorException.create(' Invalid CRUDEventBuilder Type : '+string(aValue));
        exit;
    end; 
end;
// **********************
// **********************
function TWSRestClient_ConsumeAPI_TCRUDEventBuilder.getCRUDEventBuilderDemandType(const aKeyNameIdx: integer) : variant;
begin
    result := string(WSPropsReadingByIdx( aKeyNameIdx ));
end;
// **********************
// Declenche l envoi de la demande 
// **********************
function TWSRestClient_ConsumeAPI_TCRUDEventBuilder.Declencher() : boolean;
var ResVariant : variant;
begin
        
    Result := false;
            // :: not WSParamsKeyExists('userInfos', WSHTTPQueryURIParams )
    {$IFDEF DEBUG_VERBOSE}
     writeln('>>>> WSRestClient_ConsumeAPI_TCRUDEventBuilder :: READING :: clientId ::'+WSHTTPQueryURIParams['idClient']);
     
     {$ENDIF}
     
    // :: if ( ( Length(WSHTTPQueryURIParams['clientId']) >2)   )  then
    begin
    
            // Tout est géré en interne pour la communication / décodage JSON
            { Self.WSQueryData := [
            'clientId',
            _clientId.toJsonString(),
            'contratId',			
            _contratId.toJsonString(),
            'demandKey',
            _demandKey.toJsonString(),
            'shippingDemandType',
            _shippingDemandType.toJsonString(),
            'shippingAddress',
            _shippingAddress.toJsonString(), 
            'equipments',
            _equipments.toJsonString(),
            'comment',
            _comment.toJsonString()]; }
            
            
        try
            // le json sera validee en interne
            Result := Open(WSGetURL());
            
            ResVariant := WSResponseRawText;
            
            Result := (length(ResVariant)>0) and (WSErrorCode = '200');
            { **** ResVariant := WSResponseJSON['ticketid'] ;
            if (Count <> 0) and (ResVariant <> null )  then
            begin
                    WSWriteEventLog(' JSON :: '+WSResponseJSON['ticketid']);
                    Result := true;
            end
            else
            begin
                    WSWriteEventLog('ERROR :: NO JSON :: ');
                    Result := False;
            end; **** }
        except
        on Err: Exception do
            begin
                 WSErrorMessage := Err.Message ;
                    // **************************
                    // **** MessageDlg('La demande à echoué :: Exception :: '+chr(13)+' Message :'+ Err.Message ,mtError,[mbOk],0);
                    // **************************
                    if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                    begin
                        WSRaiseLogStack('');
                    end;
                    
            end;
        end;
   // end
   // else
   // begin

   //     WSErrorMessage :='Certains Parametres Obligatoire ne sont pas renseignes ... ';
   //     raise TEWSValidationErrorException.Create(WSErrorMessage);
    end;
                
end;

end.
