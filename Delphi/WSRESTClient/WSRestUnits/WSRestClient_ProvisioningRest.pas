Unit WSRestClient_ProvisioningRest;


{
	Class TWSRest_ProvisioningRest
	
	19/07/2018 xxxxxx, SCotillard
		http://confluence.dvpt.xxxxxx.fr:8090/display/CNR/ProvisioningRest
	
	
	Usage :
	try 
		// ********************
		// Creation via :
		// ********************
		// myProvisioningTicket  := TWSRestClient_ProvisioningRest.Create(ProvisioningDemandType_RENTAL);
		// ********************
		//  ou alors via :
		// ********************
		// myProvisioningTicket  := TWSRestClient_ProvisioningRest.Create();
		// ********************
		myProvisioningTicket.raiseOnError = true;
		myProvisioningTicket.raiseOnTechError = true;
		
		myProvisioningTicket.clientId = [StringType];
		myProvisioningTicket.contratId = [JSONType / StringType] ???
		
		validationTicket := myProvisioningTicket.Declencher();
		
		if not validationTicket then
		
			writeln(myProvisioningTicket.ResponseHTTPCode);
			writeln(myProvisioningTicket.ErrorCode);
			writeln(myProvisioningTicket.ErrorMessage);
			
			writeln(myProvisioningTicket.ResponseMessage);
			writeln(myProvisioningTicket.ResponseCode);
			
			writeln(myProvisioningTicket.ResponseRawMessage);
			writeln(myProvisioningTicket.ResponseRawCode);
		
		else 
			writeln(myProvisioningTicket.ResponseMessage);
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

uses Classes, StrUtils, SysUtils, Variants, WSRESTClient; // in 'TWSRESTClient.pas'; // contient les URLs pour les webservices

// **********************
// Reglages 
// **********************
Const kWSProcessName_ProvisioningRest = 'Threads ...; ProvisioningRest';
Const kWSMethodName_ProvisioningRest = 'provisioning';
const kWSEnvRequired_ProvisioningRest = wsTest;
const kWSEntryPoint_ProvisioningRest ='provisioning-ws';
const kWSEntryPointVersion_ProvisioningRest = '2.0.x';
const kWSEndPoint_ProvisioningRest = 'services/provisioningRestWS/';


type kWSInfo_ProvisioningDemandType = record

    type kWSInfo_index = (
                                        kWSInfo_ProvisioningDemandType_NONE =0,
                                        kWSInfo_ProvisioningDemandType_FULL ,
                                        kWSInfo_ProvisioningDemandType_SIMLPLE );
    
            
    const kWSInfo_index_name : array [0..2] of string = ( '', 'FULL', 'SIMPLE' );
   
    const NONE : string = '' ;
    const FULL : string = 'FULL' ;
    const SIMPLE : string = 'SIMPLE' ;
    
    
    const DIRECTION_CREATE : string = 'IN' ;
    const DIRECTION_DELETE : string = 'OUT' ;
    
    

end;
// *************************************
// ****  Le Block Suivant permet de creer les Proprerty ++ HTTPURIParams / HTTPBodyParams
// *************************************
type kWSInfo_AccessibleObjectProperties_ProvisioningRest =  record
     
        const Names : array  [0..6] of string =	(
                
                '/{macAddress}' // parametre dans l url ;; Auto Replace Value  = Key
                ,
                'clientId',
                'equipmentKey',
                'direction',
                'provisioningType',
                'idAbonnementVOIP',
                'astra_code'
        );
            
        // le kIdx[Valeur] doit avoir la meme position dans kWSAccessibleProperties et kWSAccessiblePropertiesIndex
        type kWSInfo_AccessibleObjectProperties_Indexes_ProvisioningRest = (
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxMacPhyAddress = 0,
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId,
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey,
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection,
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType, 
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP,
                    kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidastra_code
        );
           
    
 end;

 
// *************************************
// ****  Le Block Suivant permet de creer les Methodes Accessible et VALIDE ;; Conjointement avec le Block kWSInfoMethodAvail _[classname];; par ce WebService
// *************************************
type kWSInfo_MethodNameAvail_ProvisioningRest = record		
    
    const CreateProvisioningDemand = 'CreateProvisioningDemand';
    const CreateDeprovisioningDemand = 'CreateDeProvisioningDemand';
    
    const CreateProvisioningDemandClient = 'CreateProvisioningDemandClient';
    
    const LaunchDeprovisioningDemands = 'LaunchDeprovisioningDemands';
    const LaunchProvisioningDemands = 'LaunchProvisioningDemands';
    
    const LaunchProvisioning_astra = 'launchProvisioning_astra';
    const LaunchDeprovisioning_astra = 'launchDeprovisioning_astra';
    
    const SwapProvisioning = 'SwapProvisioning';
end;

// *************************************
// ****  Le Block Suivant permet de Generer ; tout les reglages et Validation de Champs;; redirige les Params dans HTTPURIParams / HTTPBodyParams
// *************************************      
 var kWSInfoMethodAvail_ProvisioningRest : array[0..8] of TWSInfoHTTPMethodTypeInfo = ( 
            ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.CreateDeprovisioningDemand;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                    WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ) ,
              ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.CreateProvisioningDemand;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                    WSInfoHTTPMethodBODYUSEFieldIdx : [ 
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ),
              ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.CreateProvisioningDemandClient;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPointParams : string('/{clientId}');
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                   
                   WSInfoHTTPMethodURIUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId) ];
                   
                    WSInfoHTTPMethodBODYUSEFieldIdx : [ 
                                                                           
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType)
                                                                           ,integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ),
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.LaunchDeprovisioningDemands;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                    WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ),
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.LaunchProvisioningDemands;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition;
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                    WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ),
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.SwapProvisioning;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                   WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           )
             ,
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.SwapProvisioning;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPointParams : string('/preprovisioning/{macAddress}/launch');
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                   WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP)
                                                                   ] ;
           ) ,
             // :: TODO :: ASTRA ++ Autres Types
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.LaunchProvisioning_astra;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : string(kWSEndPoint_ProvisioningRest);
                   WSInfoHTTPMethodURIEndPointParams : string('/depreprovisioning/{macAddress}/launch');
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                   WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidastra_code)
                                                                   ] ;
           ) ,
             ( 
                   WSInfoHTTPMethodName: kWSInfo_MethodNameAvail_ProvisioningRest.LaunchDeprovisioning_astra;
                   WSInfoHTTPMethodNameVersion : kWSEntryPointVersion_ProvisioningRest;
                   WSInfoHTTPMethodSendType : TWSHTTPMethodType.kWSHTTPMethodType_POST;
                   WSInfoHTTPMethodURIEntryPoint : kWSEntryPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIEndPoint : kWSEndPoint_ProvisioningRest;
                   WSInfoHTTPMethodURIParamsFormat : '';
                   WSInfoHTTPMethodProcessName : kWSProcessName_ProvisioningRest;
                   // WSInfoHTTPMethodURIValidateFieldDefinition : @aDefinition
                   WSInfoHTTPMethodBODYFieldAsString : true;
                   WSInfoHTTPMethodBODYFieldAsJSONString : true;
                   WSInfoHTTPMethodBODYFieldWithUserInfo : true;
                   WSInfoHTTPMethodBODYUSEFieldIdx : [ integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP),
                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidastra_code)
                                                                   ] ;
           ) 
   );




// ***************************************************
// ***************************************************
// *************  TWSRestClient_[Classname] **************
// ***************************************************
// ***************************************************
   
type TWSRestClient_ProvisioningRest = class(TWSRESTClient)
    private  
        // **************************************
        // **************************************
        //  ******* Variable inherent au modele TWSRestClient_[Classname]
        // ******** Nom de la Methode Reclamee lors du Create()
        // **************************************
        var __RequestedConsumeMethod : string;
        // **************************************
        // **************************************
    
        // **************************************
        // **************************************
        //  *******  Fonction Assurant la coherence de valeur pour un champ donnee 
        // **************************************
        // **************************************        
            procedure setProvisioningDemandType(const aKeyNameIdx: integer; aValue : variant);
            function getProvisioningDemandType(const aKeyNameIdx: integer) : variant;
        // **************************************
        // **************************************
    public
        // **********************
        // **********************
        constructor Create; overload;
        constructor Create(aMethodRequested : string); overload;
        // constructor Create(aMethodRequested :  kWSInfo_MethodNameAvail_[ClassName] ); overload;
        function WSGetURL():String; overload;
        
        //function WSValidate() : boolean; overload;
        //function WSReplyPassed() : Boolean;overload;
    published
    
    
        // **************************************
        // **************************************
        // ********  Methode unifiee pour l envoi de la requete 
        // **************************************
        function Declencher( bCreateDemand : boolean = true) : boolean;
        
        // **************************************
        // **************************************
        //  *******  Propriety Mapping de kWSInfo_AccessibleObjectProperties_[ClassName] .Names  < < == > > kWSInfo_AccessibleObjectProperties_[classname]_kIdx[property.names]
        // **************************************
        // **************************************
        property clientId                      : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxclientId read WSPropsReadingByIdx write WSPropsWritingByIdx;
        property equipmentKey            : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey read WSPropsReadingByIdx write WSPropsWritingByIdx ;
        property direction                    : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxdirection read WSPropsReadingByIdx  write WSPropsWritingByIdx ;
        property provisioningType         : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxprovisioningType read getProvisioningDemandType  write setProvisioningDemandType ;
        property idAbonnementVOIP     : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidAbonnementVOIP read WSPropsReadingByIdx write WSPropsWritingByIdx;
        property astra_code     : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxidastra_code read WSPropsReadingByIdx write WSPropsWritingByIdx;
        // pour HTTP GET avec  certain provisionning
        property macPhyAdress     : variant Index kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxMacPhyAddress read WSPropsReadingByIdx write WSPropsWritingByIdx;

end;

implementation

// **************************************
// **************************************
constructor TWSRestClient_ProvisioningRest.Create();
begin
        Create(kWSInfo_MethodNameAvail_ProvisioningRest.CreateProvisioningDemand);
end;
// **************************************
// **************************************
constructor TWSRestClient_ProvisioningRest.Create(aMethodRequested : string);
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
    
    
    RegisterWSMethodInfoForName(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested);
     
        // creation des points d'entrees ...
    //WSQueryParams :=
    CreateWSRegisterObjectProperties(kWSInfo_AccessibleObjectProperties_ProvisioningRest.Names);
    CreateWSRegisterConsumerProperties(kWSInfoMethodAvail_ProvisioningRest,kWSInfo_AccessibleObjectProperties_ProvisioningRest.Names);
    
    WSHTTPServerEnvType := kWSEnvRequired_ProvisioningRest;
    WSInternalProcessName := WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodProcessName ) ;
    
    // *********** l utilisation de WSFindMethodInfo permet de valider transmis **************
    // modification de l'url
    WSHTTPQuerySendMethod :=  WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodSendType ) ;
     
     // Provisioning n utilise pas de nom de methode differente
    // :: WSConsumeMethodName := WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) ;
    
    WSConsumeMethodName := kWSMethodName_ProvisioningRest;
    // :: Toutefois pour les besoins  nous la stockons .... 
   
      __RequestedConsumeMethod := WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) ;
    if(  length(__RequestedConsumeMethod) <= 2 ) then
    begin
        Raise TEWSValidationErrorException.create('La methode demandee n existe pas ... ('+__RequestedConsumeMethod+')');
        exit;
    end;
    WSConsumeEntryPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEntryPoint )  ;
    WSConsumeVersion :=  WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodNameVersion )  ;
    WSConsumeEndPoint :=  WSFindMethodInfo(kWSInfoMethodAvail_ProvisioningRest, aMethodRequested, TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEndPoint )  ; // par defaut ::  /services/ ::
    

    
    // shippingDemandType  := kWSInfo_ProvisioningDemandType.kWSInfo_ProvisioningDemandType_NONE;
    // ******** ou alors *********
    iValue := integer(kWSInfo_ProvisioningDemandType_NONE);
    provisioningType  :=  kWSInfo_ProvisioningDemandType.kWSInfo_index_name[iValue];
    direction := kWSInfo_ProvisioningDemandType.DIRECTION_CREATE;
    // efface les valeurs interne
    inherited Clear(); // :: forward WSInternalClear();;
    
    // ::  WSInternalLogActivityToFileLog := true;
    // ::  WSInternalLogActivityToFileScreen := true;
end;

// ***************************************
// ******* Function overloaded
// ***************************************
function TWSRestClient_ProvisioningRest.WSGetURL():String;
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
// **********************************************
// **********************************************
 function TWSRestClient_ProvisioningRest.Declencher( bCreateDemand : boolean = true) : boolean;
 var ResVariant : variant;
 var iIndexMethod : integer;
begin
        
    Result := false;
            // :: not WSParamsKeyExists('userInfos', WSHTTPQueryURIParams )
   
    if(bCreateDemand = true) then
    begin
            WSHTTPQuerySendMethod := TWSHTTPMethodType.kWSHTTPMethodType_POST;
           { if( __RequestedConsumeMethod <> kWSInfo_MethodNameAvail_ProvisioningRest.CreateDeprovisioningDemand) then
            begin
                direction := kWSInfo_ProvisioningDemandType.DIRECTION_CREATE;
            end
            else
            begin
                direction := kWSInfo_ProvisioningDemandType.DIRECTION_DELETE;
            end;}
    end
    else
    begin
        
        WSHTTPQuerySendMethod := TWSHTTPMethodType.kWSHTTPMethodType_GET;
        // :: macPhyAdress =  '/[MACAPHYADDR]'; ;; Replace au besoin
        // :: le parametre (macPhyAdress) sera insere dans l URI via le parametrage suivant ....
        // :: WSConsumeMethodName := WSConsumeMethodName;
        
       
        // ::  iIndexMethod  := WSMethodGetIndexInfoForName(kWSInfoMethodAvail_ProvisioningRest, __RequestedConsumeMethod);
        // :: Ajoute les champ a envoyer dans la requete Recherche GET
        // :: utilisable pour ASTRA Provisioning .... 
        // :: TWSInfoHTTPMethodTypeInfo(kWSInfoMethodAvail_ProvisioningRest[iIndexMethod]).WSInfoHTTPMethodURIUSEFieldIdx :=  WSMethodAddEnumInfo( (kWSInfoMethodAvail_ProvisioningRest[iIndexMethod]).WSInfoHTTPMethodURIUSEFieldIdx, [
        // ::                                                                                                                                                                                                                           integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxMacPhyAddress)
        // ::                                                                                                                                                                                                                          ,integer(kWSInfo_AccessibleObjectProperties_ProvisioningRest_kIdxequipmentKey)
                                                                                                                                                                                                                               //  ] );
        // USERINFOS dans le GET
        // :: TWSInfoHTTPMethodTypeInfo(kWSInfoMethodAvail_ProvisioningRest[iIndexMethod]). WSInfoHTTPMethodURIFieldWithUserInfo := true;
         // en HTTP GET, le Body Est ignoree ... Quoi qu il arrive .... donc pas de USERINFOS dans le BODY ....
        // :: TWSInfoHTTPMethodTypeInfo(kWSInfoMethodAvail_ProvisioningRest[iIndexMethod]). WSInfoHTTPMethodBODYFieldWithUserInfo := false;
        // :: Redefinition de la validation de champ utilisee
        
    end;
    
    RegisterWSMethodInfoForName(kWSInfoMethodAvail_ProvisioningRest, __RequestedConsumeMethod);
        
    {$IFDEF DEBUG_VERBOSE}
     writeln('>>>> TWSRestClient_ProvisioningRest :: READING :: clientId ::'+WSHTTPQueryURIParams['clientId']+'::'+clientId);
     writeln('>>>> TWSRestClient_ProvisioningRest :: READING :: contratId ::'+WSHTTPQueryURIParams['provisioningType']+'::'+provisioningType);
     writeln('>>>> TWSRestClient_ProvisioningRest :: READING :: contratId ::'+WSHTTPQueryURIParams['direction']+'::'+direction);
     writeln('>>>> TWSRestClient_ProvisioningRest :: READING :: equipments ::'+WSHTTPQueryURIParams['equipmentKey']+'::'+equipmentKey);
     writeln('>>>> TWSRestClient_ProvisioningRest :: READING :: equipments ::'+WSHTTPQueryURIParams['idAbonnementVOIP']+'::'+idAbonnementVOIP);
     {$ENDIF}
    if ( ( Length(WSHTTPQueryURIParams['direction']) >0) )  then
    begin
        
        // ******************************************
        // Tout est géré en interne pour la communication / décodage JSON
        // ******************************************
        // ************** TRAME Provisioning *************
        // ******************************************
         //        Self.WSQueryData :=  (JSON == ) {
         // **************************************************
         // ******** Valeur geree en interne automatiqument ************
         // **************************************************
         //       
         //         "userInfos": {
         //           "userName": "userName",
         //           "ip": "ip",
         //           "httpUserAgent": "httpUserAgent"
         //         },
         // *********************************************************
         // ******** Valuur geree a l appel de Methode declencher() **************
         // *********************************************************
         //         "direction": "IN",
         // ****************************************************
         // ********  Valeur geree  par le programme appellant ************
         // ****************************************************
         //         "equipmentKey": "3100",
         //         "provisioningType": "FULL",
         //         "idAbonnementVOIP": "idAbonnementVOIP"
         //       }
        // *********************************************************
        // *********************************************************
        try
            // le json sera validee en interne
             Result := Open(WSGetURL());
            
            ResVariant := WSResponseRawText;
            
            Result := (length(ResVariant)>0) and (strtoint(WSErrorCode) > 200) and (strtoint(WSErrorCode) < 300);
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

// *************************
// ***** assurrer la correspondance du type de shipping
// *************************
procedure TWSRestClient_ProvisioningRest.setProvisioningDemandType(const aKeyNameIdx: integer; aValue : variant);
var iDemandTypeIndex : integer;
var bValidType : boolean;
begin
         
    bValidType  := false;
    iDemandTypeIndex := 0;
    for iDemandTypeIndex := 0 to high(kWSInfo_ProvisioningDemandType.kWSInfo_index_name) do begin
           if (kWSInfo_ProvisioningDemandType.kWSInfo_index_name[iDemandTypeIndex] = string(aValue)) then
           begin
                            bValidType  := true;
                            WSPropsWritingByIdx(aKeyNameIdx, aValue);
                            break;
           end;
    end;
    
    if(not bValidType) then
    begin
            raise TEWSValidationErrorException.create(' Invalid Provisionning Type : '+string(aValue));
            exit;
    end;
end;
// **********************************************
// **********************************************
function TWSRestClient_ProvisioningRest.getProvisioningDemandType(const aKeyNameIdx: integer) : variant;
begin
         result := string(WSPropsReadingByIdx( aKeyNameIdx )); 
end;
// **********************************************
// **********************************************

end.
