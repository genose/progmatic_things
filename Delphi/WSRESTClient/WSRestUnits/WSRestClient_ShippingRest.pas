Unit WSRestClient_ShippingRest ;
{
	Class TWSRest_ShippingRest
	
	19/07/2018 xxxxxx, SCotillard
		http://confluence.dvpt.xxxxxx.fr:8090/display/CNR/ShippingRest
	
	
	Usage :
	try 
		// ********************
		// Creation via :
		// ********************
		// myShippingTicket  := TWSRestClient_ShippingRest.Create(WS_ShippingDemandType_RENTAL);
		// ********************
		//  ou alors via :
		// ********************
		// myShippingTicket  := TWSRestClient_ShippingRest.Create();
		// ********************
		myShippingTicket.raiseOnError = true;
		myShippingTicket.raiseOnTechError = true;
		
		myShippingTicket.clientId = [StringType];
		myShippingTicket.contratId = [JSONType / StringType] ???
		
		myShippingTicket.equipments = [JSONType / StringType] ???
		
		myShippingTicket.shippingAddress = [JSONType / StringType] ???
		myShippingTicket.comment = [StringType]
		
		validationTicket := myShippingTicket.DeclencherExpedition();
		
		if not validationTicket then
		
			writeln(myShippingTicket.ResponseHTTPCode);
			writeln(myShippingTicket.ErrorCode);
			writeln(myShippingTicket.ErrorMessage);
			
			writeln(myShippingTicket.ResponseMessage);
			writeln(myShippingTicket.ResponseCode);
			
			writeln(myShippingTicket.ResponseRawMessage);
			writeln(myShippingTicket.ResponseRawCode);
		
		else 
			writeln(myShippingTicket.ResponseMessage);
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
Const kWSProcessName_ShippingRest = 'Threads ...; ShippingRest';
Const kWSMethodName_ShippingRest = 'ShippingRest';
const kWSEnvRequired_ShippingRest = wsTest;
const kWSEntryPoint_ShippingRest ='shipping-ws';
const kWSEntryPointVersion_ShippingRest = '1.0.x';
const kWSEndPoint_ShippingRest = 'services/ShippingWS/';

// Valeur pour le DemandType
type kWSInfo_ShippingDemandType = record
                
    type kWSInfo_index = (
                                            kWSInfo_ShippingDemandType_NONE =0,
                                            kWSInfo_ShippingDemandType_PURCHASE,
                                            kWSInfo_ShippingDemandType_RENTAL,
                                            kWSInfo_ShippingDemandType_AVAILABILITY );
    
        
    const kWSInfo_index_name : array [0..3] of string = ( '', 'PURCHASE' , 'RENTAL', 'AVAILABILITY' );
   
    const NONE : string = '' ;
    const PURCHASE : string = 'PURCHASE' ;
    const RENTAL : string  = 'RENTAL'; 
    const AVAILABILITY: string  =   'AVAILABILITY' ;
   
end;
        


// *************************************
// ****  Le Block Suivant permet de creer les Proprerty ++ HTTPURIParams / HTTPBodyParams
// *************************************
 type kWSInfo_AccessibleObjectProperties_ShippingRest =  record
         const Names : array  [0..7] of string =	(
                         'clientId',
                         'contratId',
                         'demandKey',
                         'shippingDemandType',
                         'shippingAddress',
                         'equipements',
                         'comment',
                         'handleKey'
         );
         // le kIdx[Valeur] doit avoir la meme position dans kWSAccessibleProperties et kWSAccessiblePropertiesIndex   
        type kWSInfo_AccessibleObjectProperties_Indexes_ShippingRest = (
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId                     = 0,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId                       ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey                   ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType      ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress              ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments                    ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment                        ,
                        kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey                      
        );
        
         
 end;

// ************************************* 
// **********************  Modele POC :: TODO :: Validation Fields ... ****************
// *************************************
// ****    const kWSInfo_ValidateRulesProperties_ShippingRest :  array [1..7] of string = (
// ****                    'clientId:string:length:{>0,5:10}',
// ****                    'contratId:string:length:{>0,5:10}',
// ****                    'demandKey',
// ****                    'shippingDemandType',
// ****                    'shippingAddress',
// ****                    'equipments',
// ****                    'comment'
// ****    );
 
 
// *************************************
// ****  Le Block Suivant permet de creer les Methodes Accessible et VALIDE ;; Conjointement avec le Block kWSInfoMethodAvail _[classname];; par ce WebService
// *************************************
 type  kWSInfo_MethodNameAvail_ShippingRest = record		
         const createShippingDemand  = 'createShippingDemand';
         const createShippingDemandWithHandleKey = 'createShippingDemandWithHandleKey';
         const getDemand = 'getDemand';
         const treatDeliveredShipping = 'treatDeliveredShipping';
 end;
 
 
 
// *************************************
// ****  Le Block Suivant permet de Generer ; tout les reglages et Validation de Champs;; redirige les Params dans HTTPURIParams / HTTPBodyParams
// *************************************
 var kWSInfoMethodAvail_ShippingRest : array[0..4] of TWSInfoHTTPMethodTypeInfo = ( 
                                                  ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ShippingRest.createShippingDemand;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ShippingRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_ShippingRest;
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : true;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey)
                                                                                                         ] ;
                                                 ) ,
                                                    ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ShippingRest.createShippingDemandWithHandleKey;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ShippingRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_ShippingRest;
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : true;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType),
                                                                                                                 // :: integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey)
                                                                                                         ] ;
                                                 ),
                                                   ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ShippingRest.getDemand;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ShippingRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_ShippingRest;
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : true;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey)
                                                                                                         ] ;
                                                 ),
                                                   ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ShippingRest.treatDeliveredShipping;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ShippingRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_ShippingRest;
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : true;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey)
                                                                                                         ] ;
                                                 ),
                                                   ( 
                                                         WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ShippingRest.createShippingDemandWithHandleKey;
                                                         WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ShippingRest;
                                                         WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                                                         WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ShippingRest;
                                                         WSInfoHTTPMethodURIParamsFormat : '';
                                                         WSInfoHTTPMethodProcessName : kWSProcessName_ShippingRest;
                                                         WSInfoHTTPMethodURIFieldWithUserInfo : true;
                                                         WSInfoHTTPMethodBODYFieldWithUserInfo : false;
                                                         // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                                                         WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment),
                                                                                                                 integer(kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey)
                                                                                                         ] ;
                                                 ) 
                                         );
        


// ***************************************************
// ***************************************************
// *************  TWSRestClient_[Classname] **************
// ***************************************************
// ***************************************************
   
type TWSRestClient_ShippingRest = class(TWSRESTClient)
        // **********************
        // **********************
    private
        var __RequestedConsumeMethod: string;
        // methode pour validation du type de deamande
        function getShippingDemandType(const aKeyNameIdx: integer) : variant;
        procedure setShippingDemandType(const aKeyNameIdx: integer; aValue : variant);
        // **********************
        // **********************
    public
        // **********************
        // **********************
        constructor Create; overload;
        constructor Create(aMethodRequested : string); overload;
        // constructor Create(aMethodRequested :  kWSInfo_MethodNameAvail_[ClassName] ); overload;
        //function WSValidate() : boolean; overload;
        //function WSReplyPassed() : Boolean;overload;
        function WSGetURL():string; overload;
        // **********************
        // **********************
    published
        
        // **************************************
        // **************************************
        // ********  Methode unifiee pour l envoi de la requete 
        // **************************************
        function Declencher( bCreateDemand : boolean = true) : boolean;
        // **********************
        // **********************
        function DeclencherExpedition() : boolean;
        
        property clientId                       : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxclientId read WSPropsReadingByIdx write WSPropsWritingByIdx;
        property contratId                    : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcontratId read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        
        property demandKey                : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxdemandKey read WSPropsReadingByIdx  write WSPropsWritingByIdx ;
        property shippingDemandType   : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingDemandType read getShippingDemandType  write setShippingDemandType;
        
        property shippingAddress           : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxshippingAddress read WSPropsReadingByIdx write WSPropsWritingByIdx;
        
        // property equipements[const akey : string]   : variant read readaArrayVariant write writeaArrayVariant;
        property equipements               : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxequipments read WSPropsReadingByIdx write WSPropsWritingByIdx;
        
        property comment                    : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxcomment read WSPropsReadingByIdx write WSPropsWritingByIdx;
        
        property handleKey                   : variant Index kWSInfo_AccessibleObjectProperties_ShippingRest_kIdxhandlekey read WSPropsReadingByIdx write WSPropsWritingByIdx;
                
             
end;

implementation
// **************************************
// **************************************
constructor TWSRestClient_ShippingRest.Create();
begin
        Create(kWSInfo_MethodNameAvail_ShippingRest.createShippingDemand);
end;
// **************************************
// **************************************
constructor TWSRestClient_ShippingRest.Create(aMethodRequested : string);
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
    RegisterWSMethodInfoForName(kWSInfoMethodAvail_ShippingRest, aMethodRequested);
    
    // creation des points d'entrees ...
    //WSQueryParams :=
    CreateWSRegisterObjectProperties(kWSInfo_AccessibleObjectProperties_ShippingRest.Names);
    CreateWSRegisterConsumerProperties(kWSInfoMethodAvail_ShippingRest,kWSInfo_AccessibleObjectProperties_ShippingRest.Names);
    
    WSHTTPServerEnvType := kWSEnvRequired_ShippingRest;
    WSInternalProcessName := WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodProcessName ) ;
    
    // *********** l utilisation de WSFindMethodInfo permet de valider transmis **************
    // modification de l'url
    WSHTTPQuerySendMethod :=  WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodSendType ) ;
     
    __RequestedConsumeMethod :=  WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) ;
    if(  length(__RequestedConsumeMethod) <= 2 ) then
    begin
        Raise TEWSValidationErrorException.create('La methode demandee n existe pas ... ('+__RequestedConsumeMethod+')');
        exit;
    end;
    
    WSConsumeMethodName := __RequestedConsumeMethod;
    
    WSConsumeEntryPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEntryPoint )  ;
    WSConsumeVersion :=  WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodNameVersion )  ;
    WSConsumeEndPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ShippingRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEndPoint )  ; // par defaut ::  /services/ ::
    
    // shippingDemandType  := kWSInfo_ShippingDemandType.kWSInfo_ShippingDemandType_NONE;
    // ******** ou alors *********
    iValue := integer(kWSInfo_ShippingDemandType_PURCHASE);
    shippingDemandType  :=  kWSInfo_ShippingDemandType.kWSInfo_index_name[iValue];
    
    // efface les valeurs interne
    inherited Clear(); // :: forward WSInternalClear();
     // ::  WSInternalLogActivityToFileLog := true;
      // :: WSInternalLogActivityToFileScreen := true;
end;

// **************************************
// **************************************
// ******* Function overloaded
// **************************************
// **************************************
function TWSRestClient_ShippingRest.WSGetURL:String;
begin
    try
    
        // :: WSHTTPQueryProtocolType := TWSHTTPProtocolType.ProtocolType_HTTP;
        case WSHTTPServerEnvType of
            wsDev         :
            begin
                    WSHTTPQueryHost         := '10.100.109.128'; // ne pas utiliser
                    WSHTTPQueryPort         := '8080';
            end;
            wsPreProd     :
            begin
                    WSHTTPQueryHost         := '10.100.119.12';  // ne pas utiliser
                    WSHTTPQueryPort         := '8080';
            end;
        
            wsTest        :
            begin
                    WSHTTPQueryHost         := '10.100.119.11';  // RECETTE
                    WSHTTPQueryPort         := '8080';
            end;
        
            wsProduction  :
            begin
                    WSHTTPQueryHost         := 'tomcat-c1.tomcat.xxxxxx.fr';
                    WSHTTPQueryPort         := '8080';
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


// **************************************
// **************************************
// ******** Declenche l envoi de la demande 
// **************************************
// **************************************
function TWSRestClient_ShippingRest.Declencher( bCreateDemand : boolean = true) : boolean;
var ResVariant : variant;
begin
        
    Result := false;
            // :: not WSParamsKeyExists('userInfos', WSHTTPQueryURIParams )
    {$IFDEF DEBUG_VERBOSE}
     writeln('>>>> TWSRestClient_ShippingRest :: READING :: clientId ::'+WSHTTPQueryURIParams['clientId']+'::'+clientId);
     writeln('>>>> TWSRestClient_ShippingRest :: READING :: contratId ::'+WSHTTPQueryURIParams['contratId']+'::'+contratId);
     writeln('>>>> TWSRestClient_ShippingRest :: READING :: equipments ::'+WSHTTPQueryURIParams['equipements']+'::'+equipements);
     writeln('>>>> TWSRestClient_ShippingRest :: READING :: shippingAddress ::'+WSHTTPQueryURIParams['shippingAddress']+'::'+shippingAddress);
     writeln('>>>> TWSRestClient_ShippingRest :: READING :: handelkey ::'+WSHTTPQueryURIParams['handleKey']+'::'+shippingAddress);
     {$ENDIF}
     
    if ( ( Length(WSHTTPQueryURIParams['clientId']) >2) and (Length(string(equipements)) >5) )  then
    begin
    
            // Tout est géré en interne pour la communication / décodage JSON 
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

// **************************************
// **************************************
function TWSRestClient_ShippingRest.DeclencherExpedition() : boolean;
begin
    result := Declencher();
end;
// *************************
// ***** assurrer la correspondance du type de shipping
// *************************
procedure TWSRestClient_ShippingRest.setShippingDemandType(const aKeyNameIdx: integer; aValue : variant);
var iDemandTypeIndex : integer;
var bValidType : boolean;
begin
         
    bValidType  := false;
    iDemandTypeIndex := 0;
    for iDemandTypeIndex := 0 to high(kWSInfo_ShippingDemandType.kWSInfo_index_name) do begin
       if (kWSInfo_ShippingDemandType.kWSInfo_index_name[iDemandTypeIndex] = string(aValue)) then
       begin
           bValidType  := true;
           WSPropsWritingByIdx(aKeyNameIdx, aValue);
           break;
       end;
   end;
    
    if(not bValidType) then
    begin
        raise TEWSValidationErrorException.create(' Invalid Shipping Type : '+string(aValue));
        exit;
    end; 
end;
// **********************
// **********************
function TWSRestClient_ShippingRest.getShippingDemandType(const aKeyNameIdx: integer) : variant;
begin
    result := string(WSPropsReadingByIdx( aKeyNameIdx ));
end;

end.
