unit WSRESTClient;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}

{ classes de consommation des services REST }


interface
        uses
{$IFnDEF FPC}
  Windows,
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
  Classes, StrUtils, SysUtils, Variants,
        
          // TObject List Definition dans Unit Contnrs
        Contnrs,
  
        Winsock,

        IdHTTP, IdMultipartFormData, IdURI, IdSSL, IdSSLOpenSSL, IdIOHandler, IdExceptionCore,  IdException,
        WSObject,
        WSMappedCustomVariantInvokeable,  GNSTListHelper,
        uLkJSON,
        GNSWSTypes,
        Sockets
        ;

 // :: TALJsonDocument.pas in '..\..\Communs\alcinoe\TALJsonDocument.pas';
 // type AssocObject = TDictionnary<string,TObject>;

 


type
{ =============== mode d'utilisation du WS ===============}
 TWSClientMode = (
         wsDev,
         wsTest,
         wsPreProd,
         wsProduction);
         
type	 TWSClientMethodType = (  kwsSendMethod_GET, kwsSendMethod_POST ) ;
type	 TWSHTTPAuthClientMethodType = (  kWSHTTPAuthMethod_NONE, kWSHTTPAuthMethod_BASIC, kWSHTTPAuthMethod_DIGEST ) ;

type	 TWSHTTPSSLClientMethodType = (  kWSHTTPSSLMethod_NONE, kWSHTTPSSLMethod_SSLv1, kWSHTTPSSLMethod_SSLv2, kWSHTTPSSLMethod_SSLv3 ) ;

type	TWSHTTPContentType = record
               const kWSHTTPContentType_NONE 			= '';
               const kWSHTTPContentType_APPJSON 		= 'application/json';
               
               const kWSHTTPContentType_TEXTJSON 		= 'text/json';
               
               const kWSHTTPContentType_JAVASCRIPT 	= 'text/javascript';
               const kWSHTTPContentType_TEXT 			= 'text/plain';
               
               const kWSHTTPContentType_HTML 			= 'text/html';
               const kWSHTTPContentType_FORMDATA 		= 'multipart/form-data';
               const kWSHTTPContentType_APPFORMDATA = 'application/x-www-form-urlencoded';
               
               const kWSHTTPContentType_ENCODING_ISO8859_15 = '8859-15';
               const kWSHTTPContentType_ENCODING_UTF8 = 'utf-8';
               
               
               const kWSHTTPContentType_key_AcceptContentType = 'Accept';
               
end;

// **************************************
// **************************************
const kiTWSTypeIntSet_Max : integer = 32;
type TWSArrayIntegerFileds  = array of integer;
type TWSTypeIntSet = 0..32; // :: kiTWSTypeIntSet_Max
type TWSIntSet = set of  TWSTypeIntSet;
type TPWSFieldDefinition = pointer ;
// :: type TWSIntSetNulll = [];
// var TPWSFieldDefinitionNull  : TObject ;
// type TWSHTTPMethodTypeInfo_index = set of TWSTypeIntSet;

// **************************************
// **************************************
type TWSInfoHTTPMethodTypeInfo = record
    var WSInfoHTTPMethodName : string;
    var WSInfoHTTPMethodNameVersion : string;
    var WSInfoHTTPMethodSendType : string; 
    var WSInfoHTTPMethodURIEntryPoint : string;
    var WSInfoHTTPMethodURIEndPoint : string;
    //  Style ;; /{macAddress}/launch
    var WSInfoHTTPMethodURIEndPointParams : string;
    
    var WSInfoHTTPMethodURIParamsFormat : string;
    var WSInfoHTTPMethodProcessName : string;
    
    var WSInfoHTTPMethodURIFieldAsString : boolean;
    var WSInfoHTTPMethodURIFieldAsJSONString : boolean;
    
    var WSInfoHTTPMethodBODYFieldAsString : boolean;
    var WSInfoHTTPMethodBODYFieldAsJSONString : boolean;
    
    var WSInfoHTTPMethodURIFieldWithUserInfo : boolean;
    var WSInfoHTTPMethodBODYFieldWithUserInfo : boolean;
    
    
    // :: var WSInfoHTTPMethodURIValidateFieldDefinition : TWSArrayObject;
    //  :: var WSInfoHTTPMethodBODYValidateFieldDefinition : TWSArrayObject;
    
    var WSInfoHTTPMethodURIValidateFieldIdx : TWSIntSet;
    var WSInfoHTTPMethodURIUSEFieldIdx : TWSIntSet;
    
    var WSInfoHTTPMethodBODYUSEFieldIdx : TWSIntSet;
    var WSInfoHTTPMethodBODYValidateFieldIdx : TWSIntSet;

      //  property Values[vValues]
end;
// **************************************
// **************************************
type TWSInfoHTTPMethodTypeInfo_ReturnInfo =record 
       const kWSInfoHTTPMethodName                                : string  = 'HTTPMethodName' ;
       const kWSInfoHTTPMethodNameVersion                       : string  = 'HTTPMethodNameVersion' ;
       const kWSInfoHTTPMethodSendType                           : string  = 'HTTPMethodSendType' ; 
       const kWSInfoHTTPMethodURIEntryPoint                      : string  = 'HTTPMethodURIEntryPoint' ;
       const kWSInfoHTTPMethodURIEndPoint                        : string  = 'HTTPMethodURIEndPoint' ;
       const kWSInfoHTTPMethodURIParamsFormat                : string  = 'HTTPMethodURIParamsFormat' ;
       const kWSInfoHTTPMethodProcessName                      : string  = 'HTTPMethodProcessName';
       const kWSInfoHTTPMethodURIValidateFieldIdx               : string  = 'HTTPMethodURIValidateFieldIdx';
        
end;

// **************************************
// **************************************

type TWSInfoHTTPMethodTypeInfoList = array of TWSInfoHTTPMethodTypeInfo;


type TWSInfoHTTPMethodTypeInfoArray = array of TWSInfoHTTPMethodTypeInfo;

// **************************************
// **************************************

type	TWSHTTPMethodType = record
               const kWSHTTPMethodType_POST = 'POST';
               const kWSHTTPMethodType_GET = 'GET';
               const kWSHTTPMethodType_PUT = 'PUT';
end;

type	TWSHTTPProtocolType = record
               const ProtocolType_HTTP = 'http';
               const ProtocolType_HTTPS = 'https';
end; 

// **************************************
// **************************************        
const kWSFormatLogStringBase : string = '[%s];;%s;;'; // CSV Formated ';;' == date ;; message 	 

// **************************************
// **************************************
type TWSEventLogType = ( 
                kWSEventLogType_INFO,
                kWSEventLogType_USERINFO,
                
                kWSEventLogType_WARNING,
                
                kWSEventLogType_ERROR = 16,
                
                kWSEventLogType_EWSHTTPErrorException = 32,
                
                kWSEventLogType_EWSValidationErrorException = 64,
                kWSEventLogType_EWSValidationParseQueryParamsErrorException,
                kWSEventLogType_EWSValidationParseQueryDataErrorException,
                kWSEventLogType_EWSValidationParseResponseErrorException,
                
                kWSEventLogType_EGenericException = 256,
                kWSEventLogType_EWSTechErrorException,
                
                kWSEventLogType_EWSTechInternalRuntimeErrorException = 512,
                
                kWSEventLogType_EWSUnknowERRORException = 1024,
                kWSEventLogType_EWSUnknowFormattingOutput = 4096
                );	 



// **************************************
// **************************************
// alias migration

type TWSHTTPConnectionStreamObject                            = TIdHTTP;
type TWSHTTPSSLConnectionStreamObject                       = TIdSSLIOHandlerSocketOpenSSL;
type TWSHTTPBodyDataStreamObject                             =  TStringList; // :: TIdMultiPartFormDataStream;

{$IFDEF FPC}
type TCustomIpClient =   class(TStringList) 
    private
    var _hostnameClient: string;
    var _hostadrClient: string;
    var _hostDomainClient: string;
    var _hostIpClient: string;
    public
        constructor Create(AOwner: TComponent); 
        property LocalHostName : string read  _hostnameClient  write _hostnameClient;
        property LocalHostAddr : string read _hostadrClient write _hostadrClient;
        property LocalDomainName : string read _hostDomainClient write _hostDomainClient;
        property LocalHost  : string read _hostIpClient write _hostIpClient;
end;
{$ENDIF}
// **************************************
// **************************************

type TWSHTTPQueryURIObject                                      = TIdURI;
// *******************************************
// *******************************************

type TEWSValidationErrorException                                  = class(Exception);


type TEWSTechGenericErrorException                              = class(Exception);

type TEWSTechErrorException                                        = class(TEWSTechGenericErrorException);

type TEWSTechInternalRuntimeErrorException                   = class(TEWSTechErrorException);
// *******************************************
type TEWSHTTPErrorException                                       = EIdException;
type TEWSHTTPErrorProtocolException                             = EIdHTTPProtocolException;
type TEWSHTTPErrorIOException                                    = EIdTCPConnectionError;

// *******************************************
// *******************************************

type TWSAccessibleParamsArrayProperties 		= class(TStringList);
type TWSAccessibleParams                		= TWSStringArray;


{ ===============classe mère de consommation des services REST ===============
  utilisation : créer une classe hériant de TRESTClient
                implémenter getURL pour fournir l'URL du serveur fonction du mode choisi (test, prod,...)
                implémenter les méthodes d'accès au WS dans la classe fille en utilisant _WSServerConnection
                ex : voir TRESTKeyyoBackOffice
}
  TWSRESTClient = class (TObject)
private
        const _ClassName				: string = 'TWSRESTClient';
        const _ClassVersion                               : string = '1.0.0b';
        var _aClassVersion : string;
        
        const _kWSProcessName		  	: string = 'WSRestClient.ClassName'; // nom du programme utilisateur
        const _kWSMethodName 			: string  = 'WSRestClient.Create';
        
        
        
        var _raiseOnError 						: Boolean;
        _raiseOnTechError 						: Boolean;
        _wslogActivityToFile 					: boolean;
        _wslogActivityBitMask 					: integer;
        _wslogActivityToFileName 				: widestring; // :: [_wslogActivityToFileName].log.csv
        _wslogActivityToFileNameDirPath                      : widestring;
        const _kwslogActivityToFileNameFormat           : string = 'wsLogResult_%s_%s_%s_%s.log.csv'; //  ;'wsLogResult_'+_kWSMethodName+'_'+sDateTimeEventString+'.log.csv'
        var _wslogActivityToFileScreen			: boolean;
        _wslogActivityToFile_HTTP 				: boolean;
        _wslogActivityToFile_JSON 				: boolean;
        _wslogActivityToFile_HTTP_Verbosity 	: smallint;
        _wslogActivityToFile_JSON_Verbosity 	: smallint;
        // **********************
        _serverEnvType 				: TWSClientMode;
        _WSServerConnection				: TWSHTTPConnectionStreamObject;
        _WSServerConnectionSSLHandler           : TWSHTTPSSLConnectionStreamObject;
        _WSServerConnectionBodyDataStream   :TWSHTTPBodyDataStreamObject;
        
        __WSClientMethodInfoAvail : TWSInfoHTTPMethodTypeInfoList;
        __WSClientMethodInfoUsed : TWSInfoHTTPMethodTypeInfo;
        
         // TThreadTrt = class(TThread);
        // **********************
        _programName                            : string; // nom du programme utilisateur
        _WSMethodName                        : string; // nom de la methode envoyee en "entete" REST
        // **********************
        _hostQueryInfos                       : TStringList;
        _hostInfoClient                        : TCustomIpClient; // Objet complete par les infos utilisateur actuel
        _hostIpClient                           : string; // IP localhost / station de travail
        _hostnameClient                      : string; // Hostname localhost / station de travail
        _hostadrClient                         : string; // MAC Addr localhost / station de travail
        _hostDomainClient                    : string; // Windows DomainName localhost / station de travail
        // **********************
        _WS_Username                  : string;
        _WS_Password                   : string;
        _WS_QueryUseSSL             : TWSHTTPSSLClientMethodType;
        _WS_AUTHMethodType      : TWSHTTPAuthClientMethodType;
 
        
        // **********************
        _WSUrlConsumed                               : string;
        // _WSUrlConsumedService 	: string;
        _WSHTTPQuerySendMethod               : string;
        // **********************
        
        _WSParsedResponseJSON                   : TWSObjectJSONObject;
        _WSParsedResponseCodeJSON            : TWSObjectJSONObject;
        _WSParsedResponse                          : TWSStringStream;
        _WSParsedResponseCode                   : TWSStringStream;
        // **********************
        _WSResponseErrorJson                       : TWSStringStream;
        _WSResponseErrorCodeJson                : TWSStringStream;
        
        _WSResponseCodeHTTP                      : TWSStringStream;
        _WSRawResponseCodeHTTP                : TWSStringStream;
        // **********************
        _WSRawResponse                               : TWSStringStream;
        _WSRawQuery                                    : TWSStringStream;
        _WSRawQueryData                              : TGNAssocValueObject;
        _WSRawQueryJSON                             : TWSObjectJSONObject;
        
        _WSHTTPQueryURIParams                    : TWSArrayObject;
        
        _WSHTTPQueryHeaders                       : TWSArrayObject;
        _WSHTTPQueryBodyParams                  : TWSArrayObject; // Form Post Dataset

        _WSHTTPQueryContentType                : String;
        _WSHTTPQueryContentEncoding           : String;
        // **********************
        const   _WS_BASE_URIFORMAT                                           : string =  '%s://%s:%s/%s%s/%s%s%s' ;
        var     _WS_BASE_VERSION                                                : string ;// '2.0.x';
        var     _WS_BASEHTTP_protocol                                           : string ;//= 'http://';
        var     _WS_BASEHTTP_host                                               : string ;
        var     _WS_BASEHTTP_port                                                : string ; // = '8080';
        var     _WS_BASEHTTP_uri_baseentrypoint                             : string ; // = '[wsname]';
        
        var     _WS_BASEHTTP_uri_baseconsumepoint                       : string ; //= '[services/]';
        var     _WS_BASEHTTP_uri_baseconsumepointparams             : TWSArrayObject ; //= '[alt/]';
        
        var     _WSRAWUrlParameters                                             : TWSString ; // = '';
        
        var     _WSUrlParametersPropsArrayIdxKeys                       : TWSArrayObject ; // = '';
        // var 	_WSHTTPQueryURIParams	                	: TWSArrayObject ; // = '';
        
        
        // var 	_WSRAWUrlParameters	                		: TWSArrayObject ; // = '';
        // **********************
        // **********************
        var __WSLOGCALLSTACK                                            : TStringList;
        // **********************
        // **********************
        function __appendLogFile(stextEvent : widestring):boolean;
        function __appendLogStack(stextEvent : widestring):boolean;
        function __LogStackGetLast() : widestring;
        
        protected 
        function _wsInternalParamsValidate() : boolean;
        function _wsInternalReplyPassed() : Boolean;
        
        function WSPropsReadingByIdxTInt(const aKeyNameIdx: integer): Integer ;
        
        function WSPropsReadingByIdx(const aKeyNameIdx: integer): Variant ; 
        function WSPropsReading(aKeyName: variant): variant ;

        function WSPropsReadingObject(aKeyName: variant): TObject ;        
        
        
        procedure WSPropsWritingByIdxTInt(const aKeyNameIdx: integer; aValue: Integer) ;
        
        procedure WSPropsWritingByIdx(const aKeyNameIdx: integer; aValue: Variant) ;
        
        procedure WSPropsWriting(aKeyName: Variant; aValue: Variant) ;
        procedure WSPropsWritingObject(aKeyName: variant; aValue:TObject)  ;
        
        // ******************************** Special Accessor
        function _wsread_props_WSRAWUrlParameters (): widestring ;
        function _wsread_props_WSParsedResponse (): string ;
        function _wsread_props_WSParsedResponseCode (): string ;
        function _wsread_props_WSResponseErrorJson (): string ;
        function _wsread_props_WSResponseErrorCodeJson (): string ;
        function _wsread_props_WSResponseCodeHTTP (): string ;
        function _wsread_props_WSRawResponseCodeHTTP (): string ;
        
        //**************************************************
        
        function _wsread_props_WSRawResponse (): string ;
        function _wsread_props_WSRawQuery (): string ;
        function _wsread_props_WSConsumeEndPointParams (const aKeyName : string): widestring ;
        
        
        //**************************************************
        function _wsread_props_WSHTTPQueryContentType (): string ;
        function _wsread_props_WSHTTPQueryContentEncoding (): string ;
        
        function _wsread_props_WSHTTPQueryBodyParams 	(const aKeyName : string): widestring ;
        function _wsread_props_WSHTTPQueryURIParams (const aKeyName : string): widestring ;
        function _wsread_props_WSHTTPQueryHeaders 	(const aKeyName : string): widestring ;
                
        
        //**************************************************
        
        
        
        
        procedure _wswrite_props_WSRAWUrlParameters (const aValue :widestring) ;
        procedure _wswrite_props_WSParsedResponse (aValue :string) ;
        procedure _wswrite_props_WSParsedResponseCode (aValue :string) ;
        procedure _wswrite_props_WSResponseErrorJson (aValue :string) ;
        procedure _wswrite_props_WSResponseErrorCodeJson(aValue :string) ;
        procedure _wswrite_props_WSResponseCodeHTTP (aValue :string) ;
        procedure _wswrite_props_WSRawResponseCodeHTTP (aValue :string) ;
                     
        procedure _wswrite_props_WSRawResponse (aValue :string) ;
        procedure _wswrite_props_WSRawQuery (aValue :string) ;
        procedure _wswrite_props_WSConsumeEndPointParams (const aKeyName : string; const aValue :widestring) ;
        
        
        procedure _wswrite_props_WSHTTPQueryContentType	(const aContentTypeName : string);
        procedure _wswrite_props_WSHTTPQueryContentEncoding	(const aContentEncodingName : string) ;
        
        procedure _wswrite_props_WSHTTPQueryBodyParams 	(const aKeyName : string; const aValue: widestring) ;
        procedure _wswrite_props_WSHTTPQueryURIParams (const aKeyName : string; const aValue: widestring) ;
        procedure _wswrite_props_WSHTTPQueryHeaders 	(const aKeyName : string; const aValue: widestring) ;
        
        //**************************************************
        function WSParamsKeyExists(const aKeyName: string; aArrayObject : TWSArrayObject) : boolean;
        //**************************************************
        procedure WSInternalClear();
        //**************************************************
        function WSReadParsedResponse(ColumnName: variant): TObject; 
        procedure WSWriteParsedResponse(ColumnName: variant; const Value: TObject);
        //**************************************************
        function WSReadParsedResponseCount(): integer;
        function WSReadParamsCount(): integer; 
        // **********************
        // **********************
        public
        // **********************
        
                // getURL doit être implémentée dans la classe fille pour retourner
                // l'URL serveur en fonction de _serverEnvType (test, prod, etc...)
                //function getURL: String; virtual; abstract;
        // Base Url portion serveur / port 
        function WSGetURL(): String;
        function WSGetURLParts():String;	
        // URL assemblee
        function WSGetURLAssemble():String;
        
        function WSGetLocalIP(): string;
        
        function WSRaiseLogStack(aMessageString :string; aRaiseError: boolean = true): boolean;
        
        constructor Create(); 
        destructor Destroy(); override;
        // **********************
        // Reset valeur DATA / Response / Code
        // **********************

        procedure Clear();  virtual; 
        // **********************
        //procedure Send();  overload;
        procedure Send( query:string;var source:TWSStringStream;var responseText:TWSStringStream;var responseCode:integer);overload;
        function Open() : Boolean;   overload;
        function Open(withUrl : string):Boolean;  overload;
        function Open(withUrl : string; usedHTTPSendMethod : string):Boolean;  overload;
        // **********************
        
        // **********************
        // **********************
        // **********************
        // **********************
        function WSValidate(): boolean; virtual;
        function WSReplyPassed(): boolean; virtual;
        
        
        //**************************************************
        //**************************************************
        
        // **********************
        // **********************
        // **********************
        // **********************
        
        //**************************************************
        function WSWriteEventLog(stextEvent : widestring):boolean;overload;
        function WSWriteEventLog(stextEvent : widestring; eventType : TWSEventLogType): boolean;overload;
        procedure WSWriteEventLogName(aFileLogPathName : widestring);
        function WSReadEventLogName() : widestring;
        //**************************************************
        function encodeURIComponents(sUriParts : TWSStringArray): widestring ;overload;
        function EncodeURIComponents(const ASrc: string): UTF8String; overload;
        function parseURI(sUrlString : widestring): TWSStringArray;
        
        //**************************************************
         // Fonction / procedures Utilitaire
        //**************************************************
        function RegisterWSMethodInfoForName(pObjectMethodInfoList : array of TWSInfoHTTPMethodTypeInfo; aMethodName : string) : boolean;
        function WSMethodGetIndexInfoForName(pObjectMethodInfoList : array of TWSInfoHTTPMethodTypeInfo; aMethodName : string) : integer;
        function WSMethodAddEnumInfo(pObjectMethodInfo : TWSIntSet; aEnumAddon : TWSIntSet) : TWSIntSet;
        function CreateWSRegisterObjectProperties( pObjectPropertiesList : array of string): boolean; overload;
        function CreateWSRegisterObjectProperties( pObjectPropertiesList : array of string; pURIFieldParameterList : array of string ): boolean; overload;
        //**************************************************
        function CreateWSRegisterConsumerValidatation(pObjectPropertiesList : array of string): boolean;
        function ReadWSRegisterConsumerValidatation() : boolean;
        //**************************************************
        function CreateWSRegisterConsumerProperties( pObjectPropertiesList : array of TWSInfoHTTPMethodTypeInfo): boolean; overload;
        function CreateWSRegisterConsumerProperties( pObjectPropertiesList : array of TWSInfoHTTPMethodTypeInfo;  pURIFieldParameterList : array of string): boolean; overload;
        function WSFindMethodInfo( pObjectMethodInfoList : array of TWSInfoHTTPMethodTypeInfo; aMethodName : string; aReturnPropertyName: string ) : string;
        
        //**************************************************
        function WSGetUserInfos: string;
        //**************************************************
        function WS_func_StringInArray(const Value : String;const ArrayOfString : Array of String) : Boolean;
        //**************************************************
        function WS_func_trim(const S: string): string; overload; 
        //**************************************************
        function WS_func_strrtrim(const s: string): string; overload;// default char is decode("%20")
        //**************************************************
        function WS_func_strrtrim(const s: string; c: Char): string; overload;	
        //**************************************************
        function WS_func_explodeStr(sCharNeedle : string; sOriginalString : TWSString): TWSArrayObject; overload;
        //**************************************************
        function WS_func_explodeStr(sCharNeedle : string; sOriginalString : TWSStringStream): TWSArrayObject; overload;
        //**************************************************
        function WS_func_explodeStr(sCharNeedle : string; sOriginalString : widestring): TWSArrayObject; overload;
        //**************************************************
        //**************************************************
         
        //property ClassName 					: string read _ClassName;
        property ClassVersion 				: string read _aClassVersion;
        //**************************************************
        //**************************************************
        
        property count                                                    : integer read WSReadParsedResponseCount ;
        property capacity                                                 : integer read WSReadParsedResponseCount ;
        
        property countParams                                                    : integer read WSReadParamsCount ;
        property capacityParams                                                 : integer read WSReadParamsCount ;
        
        
        property WSInternalRaiseOnError                                            : Boolean read _raiseOnError write _raiseOnError;
 
        property WSInternalRaiseOnTechError                                     : Boolean read _raiseOnTechError write _raiseOnTechError;
        property WSInternalLogActivityToFileLog 	                                : Boolean read _wslogActivityToFile write _wslogActivityToFile;
        property WSInternalLogActivityBitMask 		: integer read _wslogActivityBitMask write _wslogActivityBitMask;
        
        property WSInternalLogActivityToFileScreen 	: Boolean read _wslogActivityToFileScreen write _wslogActivityToFileScreen;
        
        property  WSInternalLogActivityToFileNameDirPath : widestring read WSReadEventLogName write  WSWriteEventLogName;
        property WSClientMethodInfoAvail :  TWSInfoHTTPMethodTypeInfoList read  __WSClientMethodInfoAvail write __WSClientMethodInfoAvail;
        //************************************************** 
        // Affecter la property ServeurEnvType pour modifier le mode d'utilisation du WS
        //**************************************************        
        property WSHTTPServerEnvType                       : TWSClientMode read _serverEnvType write _serverEnvType;
        property IdHTTPClient                                         : TWSHTTPConnectionStreamObject read _WSServerConnection write _WSServerConnection;
        property WSInternalServerConnection                  : TWSHTTPConnectionStreamObject read _WSServerConnection write _WSServerConnection;
        //************************************************** 
        property WSInternalProcessName                        : string read _programName write _programName;
        //**************************************************        
        property WSInternalInfoUserIP                            : string read _hostIpClient write _hostIpClient;
        property WSInternalInfoUserHostname                 : string read _hostnameClient write _hostnameClient;
        property WSInternalInfoUserMACAddr                  : string read _hostadrClient write _hostadrClient;

         //**************************************************
        // HTTP  Communication Methods Relatives
        //**************************************************        
        property WSHTTPQuerySendMethod                        : string read _WSHTTPQuerySendMethod write _WSHTTPQuerySendMethod;
        property WSHTTPQueryContentType                       : string read _wsread_props_WSHTTPQueryContentType write _wswrite_props_WSHTTPQueryContentType;
        property WSHTTPQueryContentEncoding                 : string read _wsread_props_WSHTTPQueryContentEncoding write _wswrite_props_WSHTTPQueryContentEncoding;
        
        //**************************************************
       //**************************************************
        property WSHTTPQueryUseSSLType                       : TWSHTTPSSLClientMethodType     read _WS_QueryUseSSL write _WS_QueryUseSSL;
        property WSHTTPQueryUseAuthType                      : TWSHTTPAuthClientMethodType   read _WS_AUTHMethodType write _WS_AUTHMethodType;
        //**************************************************
        //**************************************************
        // Format URL
        // [WSHTTPQueryProtocolType][WSHTTPQueryHost]:[WSHTTPQueryPort]/[WSEntryPoint]-[WSVersion]/[WSEndPoint]/[ParamEndPoint]/[WSMethodName][WSConsumeEndPoint == WSConsumeEndPointParams]
        //**************************************************
        property WSHTTPQueryProtocolType                       : string read _WS_BASEHTTP_protocol write _WS_BASEHTTP_protocol;
        //**************************************************
        property WSHTTPQueryPort                                   : string read _WS_BASEHTTP_port write _WS_BASEHTTP_port;
        property WSHTTPQueryHost                                  : string read _WS_BASEHTTP_host write _WS_BASEHTTP_host;
        //**************************************************
        //**************************************************        
        // groupe
        //**************************************************
         property WSConsumeEntryPoint                              : string read _WS_BASEHTTP_uri_baseentrypoint write _WS_BASEHTTP_uri_baseentrypoint;
        //**************************************************        
        property WSConsumeVersion                                              : string read _WS_BASE_VERSION write _WS_BASE_VERSION;
       //**************************************************
        property WSConsumeMethodName                         : string read _WSMethodName write _WSMethodName;
       //**************************************************
       
        //************************************************** 
        // services/
        //**************************************************        
        property WSConsumeEndPoint                                : string read _WS_BASEHTTP_uri_baseconsumepoint write _WS_BASEHTTP_uri_baseconsumepoint;
        //**************************************************        
        // [AltAccess]/
        //**************************************************        
        property WSConsumeEndPointParams[const aKeyName : string]       : widestring read _wsread_props_WSConsumeEndPointParams write _wswrite_props_WSConsumeEndPointParams;
        //**************************************************        
        // URI Assembled
        //**************************************************
        property WSHTTPQueryURIText                             : string read _WSUrlConsumed;
        // :: property WSHTTPQueryText                           : string read _wsread_props_WSRawQuery write _wswrite_props_WSRawQuery;
        // :: property WSHTTPQueryData                           : TGNAssocValueObject read _WSRawQueryData write _WSRawQueryData;
        
        //**************************************************
        //**************************************************
         //**************************************************
        property WSHTTPQueryURIRAWParams                  : widestring read _wsread_props_WSRAWUrlParameters write _wswrite_props_WSRAWUrlParameters;
         //**************************************************
        property WSHTTPQueryURIParams[const aKeyName: string] : widestring read _wsread_props_WSHTTPQueryURIParams write _wswrite_props_WSHTTPQueryURIParams;
         
         //**************************************************
         //**************************************************
        property WSHTTPQueryHeaders[const aKeyName: string] : widestring read _wsread_props_WSHTTPQueryHeaders write _wswrite_props_WSHTTPQueryHeaders;
        //**************************************************
        //**************************************************
        
        property WSHTTPQueryBodyParams[const aKeyName: string] : widestring   read _wsread_props_WSHTTPQueryBodyParams write _wswrite_props_WSHTTPQueryBodyParams;
        //**************************************************
        //**************************************************
        //**************************************************
        // Valeurs Parsed au retour du webService ou status Interne du traitement
        //**************************************************        
        property WSHTTPResponseCode                                 : string read _wsread_props_WSResponseCodeHTTP; // ::  write _wswrite_props_WSResponseCodeHTTP;
        //**************************************************
        //**************************************************
        property WSErrorCode                                           : string read _wsread_props_WSResponseCodeHTTP; // ::  write _wswrite_props_WSResponseCodeHTTP;
        property WSErrorMessage                                      : string read _wsread_props_WSResponseErrorJson write _wswrite_props_WSResponseErrorJson;
        //**************************************************
        //**************************************************
        property WSResponseMessage                                : string read _wsread_props_WSParsedResponse; // ::  write _wswrite_props_WSParsedResponse;
        property WSResponseCodeJSON                              : TWSObjectJSONObject read _WSParsedResponseCodeJSON; // ::  write _WSParsedResponseCodeJSON;
        property WSResponseCode                                     : string read _wsread_props_WSParsedResponseCode write _wswrite_props_WSParsedResponseCode;
        //**************************************************
        //**************************************************
        property WSResponseText                                         : string read _wsread_props_WSRawResponse; // ::  write _wswrite_props_WSRawResponse;
        property WSResponseRawMessage                              : string read _wsread_props_WSRawResponse; // ::  write _wswrite_props_WSRawResponse;
        property WSResponseRawText                                   : string read _wsread_props_WSRawResponse; // ::  write _wswrite_props_WSRawResponse;
        property WSResponseRawCode                                   : string read _wsread_props_WSRawResponseCodeHTTP; // ::  write _wswrite_props_WSRawResponseCodeHTTP;
        //**************************************************
        //**************************************************
        // Valeurs Parsed Valide de la Reponse du WebService
        //**************************************************
        property WSResponseJSON[vDataIndex: Variant]       : Variant read WSPropsReading; // ::  write WSPropsWriting;
        //**************************************************
        property WSResponseJSONObject                           : TWSObjectJSONObject read _WSParsedResponseJSON; // ::  write _WSParsedResponseJSON
        //**************************************************
        
        // ***** ::::  property WSQueryParams[Index: Variant] : Variant read WSPropsReading write WSPropsWriting;
        
        
        //**************************************************
        // *********************** SUPER Property ;; Object Virtual Dynamic Property ***********************
        //**************************************************
        // acces generic aux proprietes dynamiquement cree comme "object property" (Dynamic)Object["custom_props"] == (Compiled)Object.custom_props
        //**************************************************
        property ObjectInterfaceData[vDataIndex: Variant]    : Variant read WSPropsReading write WSPropsWriting;
        property ObjectProperty[vDataIndex: Variant]           : Variant read WSPropsReading write WSPropsWriting;
        //**************************************************
        //**************************************************
        property Response[ColumnName: variant]              : TObject read WSReadParsedResponse write WSWriteParsedResponse;
        property Request[ColumnName: variant]              : TObject read WSPropsReadingObject write WSPropsWritingObject;  default;
        //property Values[ColumnIndex: integer]: string read GetColumnValue write SetColumnValue; default;
        //**************************************************
       
class function aSetOf():TWSIntSet ;
// ***********************
// ***********************

end;
//**************************************************
        
//**************************************************
        
//**************************************************
        
implementation
{$IFDEF FPC}
constructor TCustomIpClient.Create(AOwner: TComponent); 
begin
    inherited create();
end;
{$ENDIF}
class function TWSRESTClient.aSetOf():TWSIntSet ;
 begin
        {[
                                                                                kIdxclientId,
                                                                                kIdxcontratId,
                                                                                kIdxdemandKey,
                                                                                kIdxshippingDemandType,
                                                                                kIdxshippingAddress,
                                                                                kIdxequipments,
                                                                                kIdxcomment ]} 
 end; 
{==============================================================================
                              TRESTClient
==============================================================================}
destructor TWSRESTClient.Destroy;
var iKeyIndex : integer;
begin
    try
    
        
        
        _WSServerConnectionBodyDataStream.Destroy();
         
         if( _WSServerConnectionSSLHandler <> nil) then
        begin
            _WSServerConnectionSSLHandler.Destroy();
        end;
        
        
         if( _WSServerConnection <> nil) then
        begin
            _WSServerConnection.Destroy();
        end;
         
         if( __WSClientMethodInfoAvail <> nil) then
        begin             
                Finalize(__WSClientMethodInfoAvail);
        end;
     
        Finalize(__WSClientMethodInfoUsed);
       
        _hostQueryInfos.Destroy();
        _hostInfoClient.Destroy();
        
        _WSParsedResponseJSON.Destroy();
        _WSParsedResponseCodeJSON.Destroy();
        _WSParsedResponse.Destroy();
        _WSParsedResponseCode.Destroy();
        _WSResponseErrorJson.Destroy();
        _WSResponseErrorCodeJson.Destroy();
        _WSResponseCodeHTTP.Destroy();
        
        _WSRawResponseCodeHTTP.Destroy();
        _WSRawResponse.Destroy();
        
        _WSRawQuery.Destroy();
        _WSRawQueryData.Destroy();
        _WSRawQueryJSON.Destroy();
        
        _WSHTTPQueryURIParams.Destroy();
        _WSHTTPQueryHeaders.Destroy();
        _WSHTTPQueryBodyParams.Destroy();
       
       inherited Destroy();
     
    except
    on Err: Exception do
        begin
             // WSErrorMessage := Err.Message ;
                // **************************
                // **** MessageDlg('La demande à echoué :: Exception :: '+chr(13)+' Message :'+ Err.Message ,mtError,[mbOk],0);
                // **************************
                {if(WSInternalRaiseOnError or WSInternalRaiseOnTechError ) then
                begin
                    WSRaiseLogStack('');
                end;
                }
                  WSWriteEventLog('TWSRestClient::Destroy :: Exception :: '+Err.Message, kWSEventLogType_EWSTechInternalRuntimeErrorException);
    
        end;
    end;
    
end;

//**************************************************
        
constructor TWSRESTClient.Create();
var NbInfos : Integer;
var sDateTimeEvent			: TDateTime;
var sDateTimeEventString : string;
begin

        
        
        sDateTimeEvent :=   Now();
         
        sDateTimeEventString :=  StringReplace(DateToStr(sDateTimeEvent)+'_'+TimeToStr(sDateTimeEvent), '/', '.',[rfReplaceAll, rfIgnoreCase]);
         
         __WSLOGCALLSTACK := TStringList.create();
         __appendLogStack('class.client.Create() :: '+sDateTimeEventString);
        // **********************
        _raiseOnError       := false;
        _raiseOnTechError   := false;
        
        _wslogActivityToFile                             := false;
        _wslogActivityToFileName                     := _kwslogActivityToFileNameFormat; // :: [_wslogActivityToFileName].log.csv
        _wslogActivityToFileNameDirPath           := GetCurrentDir();
        _wslogActivityToFileScreen                   := false;
        _wslogActivityToFile_HTTP                    := false;
        _wslogActivityToFile_JSON                    := false;	
        _wslogActivityToFile_HTTP_Verbosity     := 0;
        _wslogActivityToFile_JSON_Verbosity     := 0;
        // **********************
        
        // WSWriteEventLog('WS Factory Create .... ');
        // *******************************
        
        _serverEnvType 	   := wsDev;
        
        _WSHTTPQueryContentType                  := TWSHTTPContentType.kWSHTTPContentType_APPJSON;
        _WSHTTPQueryContentEncoding             := TWSHTTPContentType.kWSHTTPContentType_ENCODING_UTF8;
        _WSServerConnection := TWSHTTPConnectionStreamObject.Create(nil);
        //_WSServerConnection.ProtocolVersion := pv1_1;
        _WSServerConnection.ProtocolVersion := pv1_1;
        _WSServerConnection.HTTPoptions := _WSServerConnection.HTTPoptions + [hoKeepOrigProtocol];
        //_WSServerConnection.Request.Accept := 'text/javascript';
        _WSServerConnection.Request.ContentType :=   _WSHTTPQueryContentType;
        _WSServerConnection.Request.ContentEncoding := _WSHTTPQueryContentEncoding;
         
        {déclaration TimeOut ???}
        //
        _WSServerConnection.ConnectTimeOut              :=	10000;
        //
        _WSServerConnection.ReadTimeOut                 :=	10000;
        _WSServerConnection.AuthRetries := 0;
        _WSServerConnection.MaxAuthRetries := 3;
        // **************************
        // **************************
        _WSServerConnection.HandleRedirects        := True;
        // **************************
        // **************************
        _WSServerConnectionBodyDataStream       := TWSHTTPBodyDataStreamObject.create();
        // **************************
        // **************************
         // TThreadTrt = class(TThread);
        // **********************
        _programName                                                    := _kWSProcessName; // nom du programme utilisateur
        _WSMethodName                                                 := _kWSMethodName; // nom de la methode envoyee en "entete" REST
        // **********************
        _hostQueryInfos                 := TStringList.create();
        _hostInfoClient                   :=     TCustomIpClient.Create(nil); // Objet complete par les infos utilisateur actuel
        _hostIpClient                      := ''; // IP localhost / station de travail
        _hostnameClient                 := ''; // Hostname localhost / station de travail
        _hostadrClient                    := ''; // MAC Addr localhost / station de travail
        
        // **************************
        _hostnameClient        :=      _hostInfoClient.LocalHostName;
        _hostadrClient           :=      _hostInfoClient.LocalHostAddr;
        _hostDomainClient           := _hostInfoClient.LocalDomainName;
        // **************************
        if(length(_hostInfoClient.LocalHost)<5) then
        begin
                _hostInfoClient.LocalHost := WSGetLocalIP();
        end;
        _hostIpClient:= _hostInfoClient.LocalHost;
        // **************************
        
        
        
        // **********************
        _WS_Username 					:= '';
        _WS_Password 					:= '';
        _WS_AUTHMethodType 				:= kWSHTTPAuthMethod_NONE;
        _WS_QueryUseSSL					:= kWSHTTPSSLMethod_NONE;
        
        
        _WSUrlConsumed 			    	:= '';
        // _WSUrlConsumedService 	:= '';
        _WSHTTPQuerySendMethod 		    	:= TWSHTTPMethodType.kWSHTTPMethodType_GET;
        // **********************
        
        _WSParsedResponseJSON 		  	:= TWSObjectJSONObject.Create();
        _WSParsedResponseCodeJSON 		:= TWSObjectJSONObject.Create();
        _WSParsedResponse 			    := TWSStringStream.Create('');
        _WSParsedResponseCode 			:= TWSStringStream.Create('');
        // **********************
        _WSResponseErrorJson 		  	:= TWSStringStream.Create('');
        _WSResponseErrorCodeJson 		:= TWSStringStream.Create('');
        
        _WSResponseCodeHTTP			  	:= TWSStringStream.Create('');
        _WSRawResponseCodeHTTP    		:= TWSStringStream.Create('');
        // **********************
        _WSRawResponse 					:= TWSStringStream.Create('');
        
        _WSRawQuery 		  			:= TWSStringStream.Create('');
        
        _WSRawQueryData 				:= TGNAssocValueObject.Create();
        _WSRawQueryJSON 				:= TWSObjectJSONObject.Create();
        
        _WSHTTPQueryURIParams			:= TWSArrayObject.Create();
        _WSHTTPQueryBodyParams                  := TWSArrayObject.Create();
        _WSHTTPQueryHeaders                     := TWSArrayObject.Create();
        
        _WS_BASE_VERSION					        := '' ;// '2.0.x';
        _WS_BASEHTTP_protocol				      	:= TWSHTTPProtocolType.ProtocolType_HTTP ;//= 'http://';
        _WS_BASEHTTP_host					        := '' ;
        _WS_BASEHTTP_port					        := '8080' ; // = '8080';
        _WS_BASEHTTP_uri_baseentrypoint                                   := '' ; // = '[wsname]';
        
        _WS_BASEHTTP_uri_baseconsumepoint                              := '' ; //= '[services/]';
        _WS_BASEHTTP_uri_baseconsumepointparams                    := TWSArrayObject.Create('') ; //= '[alt/]';
        
        _WSRAWUrlParameters                                                     := TWSString.Create('') ; // = '';
         
        _WSUrlParametersPropsArrayIdxKeys                                   := TWSArrayObject.create();
        
        
        
         __appendLogStack('class.client.Create()::END:: '+sDateTimeEventString);
        // WSWriteEventLog('WS Factory Create .... Clear ');
end;
//**************************************************
//**************************************************
procedure TWSRESTClient.Clear(); 
begin
        try
                WSInternalClear();
        except
        on ErrClassInheritedClear: Exception do
                begin
                        __appendLogStack(' Exception::TWSRESTClient::inherited::Clear('+ErrClassInheritedClear.Message+')');                                
                        raise TEWSTechErrorException.create( __LogStackGetLast() );
                        exit;
                end;
        end;
                
end;
//**************************************************
//**************************************************
procedure TWSRESTClient.WSInternalClear();
var sLastError : wideString;
var sParamsNameKey 		: widestring;
var sParamsNameValue 	: widestring;
// var sParamsEndPointValue 	: TWSString;
var sP_WSVERSION		: string;
var iKeyIndex 			: integer;
begin

        try
                sLastError := __LogStackGetLast() ;
                __WSLOGCALLSTACK.clear();
                __appendLogStack('Event :: '+sLastError);
                __appendLogStack('Class.Client.Clear()');
                
                
                { ***** Init valeurs JSON / Resultat ******* }
                _WSUrlConsumed := '';
                _WSRawResponse.WriteString('');
                _WSParsedResponse.WriteString('');
                _WSParsedResponseJSON 	:= TWSObjectJSONObject.ParseText('{}');
                
                // **************************
                _WSResponseErrorJson              .WriteString('');
                _WSResponseErrorCodeJson        .WriteString('');
                _WSResponseCodeHTTP             .WriteString('');
        
                // **************************
                _WSServerConnection.ProtocolVersion := pv1_1;
                _WSServerConnection.HTTPoptions := _WSServerConnection.HTTPoptions + [hoKeepOrigProtocol];
                _WSServerConnection.ConnectTimeOut              :=	10000;
                //
                _WSServerConnection.ReadTimeOut                 :=	10000;
                _WSServerConnection.AuthRetries := 0;
                _WSServerConnection.MaxAuthRetries := 3;
                // **************************
                // **************************
                _WSServerConnection.HandleRedirects        := True;
                _WSServerConnection.Request.ContentType :=   _WSHTTPQueryContentType;
                _WSServerConnection.Request.ContentEncoding := _WSHTTPQueryContentEncoding;;
                // **************************
                if _WS_Username <> '' then
                _WSServerConnection.Request.Username := _WS_Username;
        
                // **************************
                if _WS_Password <> '' then
                _WSServerConnection.Request.Password := _WS_Password ;
        
                // **************************
                
                _WSServerConnection.Request.BasicAuthentication := (Integer(_WS_AUTHMethodType) <> 0);
                
                // **************************
                // **************************
                // :: _WSServerConnectionBodyDataStream.Clear();
                // :: _WSServerConnectionBodyDataStream := TWSHTTPBodyDataStreamObject.Create();
                // **************************
                // **************************
                _WSServerConnection.HandleRedirects := True;
                // **************************
                // **************************
                
                if (Integer(_WS_QueryUseSSL) <> integer(kWSHTTPSSLMethod_NONE)) then 
                begin
                
                        _WS_BASEHTTP_protocol := 'https';
                        
                        if(not assigned(_WSServerConnectionSSLHandler)) then
                        begin
                            _WSServerConnectionSSLHandler	:=	TIdSSLIOHandlerSocketOpenSSL.Create(nil);
                        end;
                        
                        // SSL Type 
                        case _WS_QueryUseSSL of
                            kWSHTTPSSLMethod_SSLv1 : begin
                                _WSServerConnectionSSLHandler.SSLOptions.Method 		:= sslvTLSv1;
                            end;
                            kWSHTTPSSLMethod_SSLv2 : begin
                                _WSServerConnectionSSLHandler.SSLOptions.Method 		:= sslvSSLv23;
                            end;
                            kWSHTTPSSLMethod_SSLv3 : begin
                                _WSServerConnectionSSLHandler.SSLOptions.Method 		:= sslvSSLv3;
                            end;
                            else
                            raise TEWSValidationErrorException.create('Exception::Wrong SSL Options Version ... ');
                        end;
                        _WSServerConnectionSSLHandler.SSLOptions.Mode 			:= sslmUnassigned;
                        _WSServerConnectionSSLHandler.SSLOptions.VerifyMode 	:= [];
                        _WSServerConnectionSSLHandler.SSLOptions.VerifyDepth 	:= 0;
        
                        _WSServerConnection.IOHandler	:=	_WSServerConnectionSSLHandler;
                end
                else
                begin
                        _WS_BASEHTTP_protocol := TWSHTTPProtocolType.ProtocolType_HTTP; 
                end;
                
                // ***********************************
                if (_WSHTTPQueryHeaders.Count > 0) then
                begin
                        {Building HTTP Headers and Custom Headers}
        
                        // _WSServerConnection.Request.CustomHeaders.Values[] := '';
                        _WSServerConnection.Request.CustomHeaders.clear();
                        
                        
                end;
                // ***********************************
                if (_WSHTTPQueryURIParams.Count > 0) then
                begin
                        {Building HTTP Uri and associated Parameters}
                        
                        // _WSServerConnection.Request.CustomHeaders.Values[] := '';
                end;
                
                
        except
        on ErrClassWSInternalClear: Exception do
                begin
                        __appendLogStack(' Exception::TWSRESTClient::ErrClassWSInternalClear('+ErrClassWSInternalClear.Message+')');                                
                        raise TEWSTechErrorException.create( __LogStackGetLast() );
                        exit;
                end;
        end;
        // **************************
end;
//**************************************************
//**************************************************
                
//**************************************************
//**************************************************

//**************************************************
//**************************************************
 function TWSRESTClient._wsInternalParamsValidate() : boolean;
begin
        if (not ReadWSRegisterConsumerValidatation()) then
                begin
                        if(_raiseOnTechError or _raiseOnError) then
                        begin
                                raise TEWSValidationErrorException.create('TWSRESTClient::Open() : Error::URIValidation : URI Field Validation Error ... ');
                        end
                        else
                        begin
                                
                                // **************************
                                _WSRawResponse := nil;
                                // **************************
                                _WSParsedResponse.WriteString('TWSRESTClient.Open::EWSValidationErrorException::Error::URIValidation : URI Field Validation Error ... ');
                                _WSResponseCodeHTTP.WriteString('500');
                                
                                // **************************
                                _WSResponseErrorJson .WriteString('{error:"'+_WSParsedResponse.ReadString+'"}');
                                _WSResponseErrorCodeJson .WriteString('{code:'+_WSResponseCodeHTTP.ReadString+'}');
                                WSWriteEventLog(_WSParsedResponse.ReadString);
                                Result := false; 
                                exit;
                        end;
                end;
Result := true;
end;
//**************************************************
//**************************************************
function TWSRESTClient._wsInternalReplyPassed() : Boolean;
begin
Result :=true;
end;
//**************************************************
//************************************************** 

function TWSRESTClient.WSValidate(): boolean;
begin
        Result :=_wsInternalParamsValidate();
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSReplyPassed(): boolean;
begin
        Result := _wsInternalReplyPassed();
end;

//**************************************************
//**************************************************


//**************************************************
//**************************************************


//**************************************************
//**************************************************

procedure TWSRESTClient.Send( query:string;var source:TWSStringStream;var responseText:TWSStringStream;
var responseCode:integer);
{var
  url : string;
  Ch_URL : string;
  Trame_json, _1CompteSip, NouveauSipName  : string;}
begin
 
end;
//**************************************************
//**************************************************

function TWSRESTClient.Open() : Boolean;   
begin
        Result := Open(_WSUrlConsumed, _WSHTTPQuerySendMethod ); 
end;
//**************************************************
//**************************************************	
function TWSRESTClient.Open(withUrl : string ): Boolean;
begin
        Result := Open(withUrl, _WSHTTPQuerySendMethod ); 
end;
//**************************************************
//**************************************************	
function TWSRESTClient.Open(withUrl : string; usedHTTPSendMethod : string ): Boolean;
var _HTTPRawResponse : TStringStream;
var _HTTPRawQueryData : TStringStream;
var sLastError : wideString;
var sParamsNameKey 		: widestring;
var sParamsNameValue 	: widestring;
// var sParamsEndPointValue 	: TWSString;
var sP_WSVERSION		: string;
var iKeyIndex 			: integer;
begin
    
    WSInternalClear();
    _HTTPRawQueryData := TStringStream.Create('');
    _HTTPRawResponse := TStringStream.Create('');

    // **************************
    Result              :=	false;
    // ************************** 
  
    if (length(withUrl) >0 ) and ( WS_func_strrtrim(withUrl) <> '') then
    begin
    _WSUrlConsumed := withUrl;
    end;
    
    
    
     if(length(_WSUrlConsumed)<10) then
    begin
            { if(not (length(_WSUrlConsumed)>0)) then
                    begin
                            _WSUrlConsumed := // preparation de l'url :: inherited
                    end 
                    else
                    begin
                             // recuperation des portions assemblees
                    end;
            }
            // :: Self.WSGetURL();
            _WSUrlConsumed := WSGetURLAssemble();
    end;
    
    
    if(length(_WSUrlConsumed)<10) then
    begin
         WSWriteEventLog('Class.client.Open::Error(invalid URL:'+_WSUrlConsumed+')');
        raise TEWSTechErrorException.create(__LogStackGetLast());
        result := false;
        exit;
    end;
    {
            :: format :: '%s/%s:%s/%s-%s/%s%s' ::
            _WSUrlConsumed := url + query;
    
            _WSUrlConsumed :='http://tomcat-c1.tomcat.xxxxxx.fr:8080/base-client-ws-2.0.x/services/BaseClientIspWS/customers/13098';
    
            http://tomcat-c1.tomcat.xxxxxx.fr:8080/returnTicket-ws-2.0.x/services/
    
     // *********
     equivalent :: getURL
    }
    
    
    if ( not (_wsInternalParamsValidate()) ) then
    begin
            Result := false;
            exit;
    end;

        // **************************
        try
                   
                iKeyIndex := 0 ; 
                for iKeyIndex:= 0 to _WSHTTPQueryHeaders.Count -1 do begin
                    
                    sParamsNameKey := _WSHTTPQueryHeaders.KeyNames[  iKeyIndex  ];
                    sParamsNameValue := widestring(_WSHTTPQueryHeaders[sParamsNameKey].AsString);
                
                    // :: ????
                    _WSServerConnection.Request.RawHeaders.Values[sParamsNameKey] := ''+sParamsNameValue;
                    
                    _WSServerConnection.Request.CustomHeaders.Values[sParamsNameKey] := ''+sParamsNameValue;
                    
                          WSWriteEventLog('TWSRESTClient.Open::' +
                               'ADD Headers : '+string(sParamsNameKey)+'::'+  _WSServerConnection.Request.RawHeaders.Values[sParamsNameKey]); 
                    
                end;
                
                 if (_WSHTTPQueryHeaders.arrayKeyExist('Accept') and ( _WSServerConnection.Request.accept <> string(_WSHTTPQueryHeaders['Accept'].AsString) ) ) then
                 begin
                     _WSServerConnection.Request.accept := _WSHTTPQueryHeaders['Accept'].AsString;
                     WSWriteEventLog('TWSRESTClient.Open::Header Content Accept : ' + _WSServerConnection.Request.accept); 
                 end;       
                // **************************
                // Sending Request
                // **************************
               WSWriteEventLog('TWSRESTClient.Open:: WS send Query With :: ' +
                               '**** Headers : '+ _WSHTTPQueryHeaders.ToStringJSON()+chr(10)+chr(13)+'**** Accept:'+_WSServerConnection.Request.accept
                               );
                // :: WSWriteEventLog('WS  :: ' + _WSRawQuery.AsString);
                
                
                
                if(usedHTTPSendMethod = TWSHTTPMethodType.kWSHTTPMethodType_GET) then
                begin
                        WSWriteEventLog('TWSRESTClient.Open:: WS  GET :: ' + _WSUrlConsumed );
                        _WSServerConnection.Get(_WSUrlConsumed, _HTTPRawResponse);
                end
                else  if(usedHTTPSendMethod = TWSHTTPMethodType.kWSHTTPMethodType_POST) then
                begin
                        WSWriteEventLog('TWSRESTClient.Open:: WS  POST :: ' + _WSUrlConsumed );
                        
                        
                        
                      
                        if(__WSClientMethodInfoUsed.WSInfoHTTPMethodBODYFieldAsJSONString ) then
                        begin
                         
                             WSWriteEventLog('TWSRESTClient.Open:: WS  BODY Count :: ' + inttostr(_WSServerConnectionBodyDataStream.count) );
                            _HTTPRawQueryData.WriteString(_WSServerConnectionBodyDataStream.AsStringJSON());
                            
                            _WSServerConnectionBodyDataStream.clear();
                            
                            _WSServerConnectionBodyDataStream.add(_HTTPRawQueryData.DataString);
                        end
                        else
                        begin
                              _WSServerConnectionBodyDataStream.add('');
                        end;
                        
                        _WSServerConnection.Post(_WSUrlConsumed, _WSServerConnectionBodyDataStream, _HTTPRawResponse);
                        // _WSServerConnection.Post(_WSUrlConsumed, _WSServerConnectionBodyDataStream, _HTTPRawResponse);
                        
                end
                else
                begin
                       WSWriteEventLog('Class.client.Open::Error(invalid HTTPMethod:'+usedHTTPSendMethod+')');
                       raise TEWSTechErrorException.create(__LogStackGetLast());
                       result := false;
                       exit;
                end;
         
        _HTTPRawQueryData.Destroy();
                        
        // **************************
        except
        on EWSHTTPConnectionReadWriteException : TEWSHTTPErrorIOException do
                begin
                        // **************************
                         // _HTTPRawResponse := nil; 
                         // **************************
                         _WSResponseErrorCodeJson .WriteString(IntToStr(_WSServerConnection.ResponseCode));
                         _WSResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                         _WSRawResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                         // **************************
                         if EWSHTTPConnectionReadWriteException.Message <> '' then
                        begin
                                _WSRawResponse.WriteString(EWSHTTPConnectionReadWriteException.Message);
                                _WSParsedResponse.WriteString(EWSHTTPConnectionReadWriteException.Message);
                                _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                                _WSResponseErrorJson .WriteString(EWSHTTPConnectionReadWriteException.Message );
                       
                        end
                        else
                        begin
                                _WSRawResponse.WriteString('HTTPReadWriteError');
                                _WSParsedResponse.WriteString('HTTPReadWriteError');
                                _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                                _WSResponseErrorJson .WriteString('{"error":"HTTPReadWriteError"}' );
                        end;
        
                         // **************************
                         WSWriteEventLog('Class.client.Open::Error::HTTPDataReadWrite  ('+_WSResponseErrorJson.AsString+')::HTTP:'+IntToStr(_WSServerConnection.ResponseCode));

                        _HTTPRawResponse.Destroy();
                        _HTTPRawQueryData.Destroy();

                         
                         raise TEWSTechErrorException.create(__LogStackGetLast());
                         exit;
                         // ************************** 
                end;
        on EWSHTTPConnectionException: TEWSHTTPErrorProtocolException do
                begin
                        // **************************
                        // _HTTPRawResponse := nil;
                        // **************************
                        _WSResponseErrorCodeJson .WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        _WSResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        _WSRawResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        // **************************
                        if EWSHTTPConnectionException.ErrorMessage <> '' then
                        begin
                                _WSRawResponse.WriteString(EWSHTTPConnectionException.ErrorMessage);
                                _WSParsedResponse.WriteString(EWSHTTPConnectionException.ErrorMessage);
                                
                                // :: _WSParsedResponseCode.WriteString(IntToStr(EWSHTTPConnectionException.ResponseCode));
                                
                                _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                                
                                _WSResponseErrorJson .WriteString(EWSHTTPConnectionException.ErrorMessage );
                        end
                        else
                        begin
                                _WSRawResponse.WriteString(EWSHTTPConnectionException.Message);
                                _WSParsedResponse.WriteString(EWSHTTPConnectionException.Message);
                                
                                // :: _WSParsedResponseCode.WriteString(IntToStr(EWSHTTPConnectionException.ResponseCode));
                                
                                _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                                
                                _WSResponseErrorJson .WriteString(EWSHTTPConnectionException.Message );
                        
                        end;
        
                        // **************************
                        WSWriteEventLog('Class.client.Open::Error::EWSHTTPConnectionException ('+_WSResponseErrorJson.AsString+')::HTTP:'+IntToStr(_WSServerConnection.ResponseCode));

                        _HTTPRawResponse.Destroy();
                        _HTTPRawQueryData.Destroy();

                         raise TEWSTechErrorException.create(__LogStackGetLast());
                        exit;
                        // **************************
                end;
        on EWSQuerySendException: Exception do
                begin
                        // **************************
                        // _HTTPRawResponse := nil;
                        // **************************
                        _WSResponseErrorCodeJson .WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        // **************************
                        _WSResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        _WSRawResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        // **************************
                        _WSRawResponse.WriteString(EWSQuerySendException.Message);
                        _WSParsedResponse.WriteString(EWSQuerySendException.Message);
                        // _WSParsedResponseCode.WriteString(IntToStr(EWSQuerySendException.ResponseCode));
                        _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                        _WSResponseErrorJson .WriteString(EWSQuerySendException.Message );
                        
                        // **************************
                        WSWriteEventLog('Class.client.Open::Error::EWSQuerySendException ('+EWSQuerySendException.Message+')::HTTP:'+IntToStr(_WSServerConnection.ResponseCode));
                        // **************************

                        _HTTPRawResponse.Destroy();
                        _HTTPRawQueryData.Destroy();


                        raise TEWSTechErrorException.create(__LogStackGetLast());
                        exit;
                end;
        end;
        // **************************
        // Parsing results
        // **************************
        try
                if (_HTTPRawResponse <> nil) then
                begin

                        WSWriteEventLog('TWSRESTClient.Open:: WS Query PASSED :: ' );
        
                        if ( _HTTPRawResponse.Size <> 0) then
                        begin
                                
                                _WSRawResponse.WriteString(_HTTPRawResponse.DataString);
                                
                                _WSResponseCodeHTTP.WriteString( IntToStr(_WSServerConnection.ResponseCode));
                                
                                WSWriteEventLog('TWSRESTClient.Open:: WS Query :: Parsing Results ('+IntToStr(Length(_WSRawResponse.AsString))+') Bytes');
                                
                                WSWriteEventLog( 'TWSRESTClient.Open:: WS Query :: Value(Response) : '+_WSRawResponse.AsString );
                                
                                _WSParsedResponseJSON           :=      TWSObjectJSONObject.ParseText(_WSRawResponse.AsString);
                                 
                                 if( _WSParsedResponseJSON.ArrayKeyExist('message') ) then
                                 begin
                                    _WSResponseErrorJson.WriteString( _WSParsedResponseJSON['message']);
                                end
                                else
                                begin
                                     _WSResponseErrorJson.WriteString( _WSParsedResponseJSON['error']);
                                end;
                                
                                _WSResponseErrorCodeJson.WriteString( _WSParsedResponseJSON['errorCode']);
                                
                                _WSParsedResponse.WriteString(_HTTPRawResponse.DataString);
                                
                                _WSParsedResponseCode.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                                
                                
                                WSWriteEventLog( 'TWSRESTClient.Open:: WS Query :: Value(Error) : '+_WSResponseErrorJson.ToString() );
                                
                                WSWriteEventLog( 'TWSRESTClient.Open:: WS Query :: Value(ErrorCode) : '+_WSResponseErrorCodeJson.ToString());
                                

                    end
                else
                    begin
                      WSWriteEventLog('TWSRESTClient.Open:: WS Query :: No Parsing Results ('+IntToStr(_WSRawResponse.Size)+') Bytes');

                    end;
                end
                else
                begin
                        WSWriteEventLog('TWSRESTClient.Open:: WS Query :: No Valid Response for Parsing Results (0) Bytes');
                end;
                   // **************************
                        _HTTPRawResponse.Destroy();
                        _HTTPRawQueryData.Destroy();
                    // **************************
        except
        on EWSQueryParseDataException: Exception do
                begin
                        // **************************
                        WSWriteEventLog('TWSRESTClient.Open:: Error::Exception '+EWSQueryParseDataException.Message);
                         // **************************
                        _HTTPRawResponse.Destroy();
                        _HTTPRawQueryData.Destroy(); 
                        // **************************
                        _WSResponseErrorJson.clear();
                        _WSResponseErrorCodeJson.clear();
                        // **************************
                        _WSResponseErrorJson .WriteString(  '{"error":"JSON Parse Error"}');
                        _WSResponseErrorCodeJson.WriteString('500');
                        // **************************
                        // _WSRawResponse.clear();
                        _WSParsedResponse.WriteString(EWSQueryParseDataException.Message);
                        _WSResponseCodeHTTP.WriteString(IntToStr(_WSServerConnection.ResponseCode));
                end;
        end;
        
        // **************************
        // **************************
        
end;

// *****************************************************
// Template pour GetUrl Inherited
// doit etre base sur WSGetURLParts
// *****************************************************

{ function TWSRESTClient.WSGetURL:String;
begin
        Result := '**** NO URI ****';
        try
                
                case _serverEnvType of
                        wsDev         :
                        begin
                                WSHTTPQueryHost  	:= '10.100.109.128'; // ne pas utiliser
                                WSHTTPQueryPort	:= '8080';
                        end;
                        wsPreProd     :
                        begin
                                WSHTTPQueryHost  	:= '10.100.119.12';  // ne pas utiliser
                                WSHTTPQueryPort	:= '8080';
                        end;
                end;
                
                
        //	***************************
        except
                on ErrWSGetURL: Exception do
                        begin
                                __appendLogStack(' Exception::Inherited.WSGetURL  ('+ ErrWSGetURL.Message+')');                                
                                raise TEWSTechErrorException.create( __LogStackGetLast() );

                        end;
                end;
end;}
function TWSRESTClient.WSGetURL:String;
begin	
        Result := WSGetURLParts();
end;
//	***************************
//	***************************
function TWSRESTClient.WSGetURLParts:String;
  begin
   { try
        case _serverEnvType of
            wsDev         :
            begin
                    _WS_BASEHTTP_host  	:= '10.100.109.128'; // ne pas utiliser
                    _WS_BASEHTTP_port	:= '8080';
            end;
            wsPreProd     :
            begin
                    _WS_BASEHTTP_host  	:= '10.100.119.12';  // ne pas utiliser
                    _WS_BASEHTTP_port	:= '8080';
            end;

            wsTest        :
            begin
                    _WS_BASEHTTP_host  	:= '10.100.119.11';  // RECETTE
                    _WS_BASEHTTP_port	:= '8080';
            end;

            wsProduction  :
            begin
                    _WS_BASEHTTP_host  	:= 'tomcat-c1.tomcat.xxxxxx.fr';
                    _WS_BASEHTTP_port	:= '8080';
            end;
    end;
    //	***************************
    except
    on ErrGetUrl: Exception do
        begin
                __appendLogStack(' Exception::TWSRESTClient.WSGetURLParts('+ErrGetUrl.Message+')');                                
                raise TEWSTechErrorException.create( __LogStackGetLast() );

        end;
    end;}
    _WSUrlConsumed := WSGetURLAssemble();
    Result  := _WSUrlConsumed;
end;

// **************************
// **************************

function TWSRESTClient.WSGetURLAssemble():String;
var sParamsNameKey 		: widestring;
var sParamsNameValue 	: widestring;
var sParamsEndPointValue 	: TWSString;
var sP_WSVERSION		: string;
var iKeyIndex 			: integer;
begin
        try	
                //***************************
                sParamsEndPointValue    := TWSString.Create('');
                _WSRAWUrlParameters.Clear();
                _WSServerConnectionBodyDataStream.Clear();
                // :: _WSServerConnectionBodyDataStream := TWSHTTPBodyDataStreamObject.Create();
                
                // :: if _WSRAWUrlParametersQueryMark
                // :: _WSRAWUrlParameters.Append('&');
                
                // **** Prepare URI ****
                _WSUrlConsumed := _WS_BASE_URIFORMAT;
                sP_WSVERSION := '';
                
                // *********************************
                // **** Assemblage des parametres vers String URI
                // *********************************
                //if (_WSHTTPQueryURIParams.count > 0) then
               //  if( assigned(__WSClientMethodInfoUsed.WSInfoHTTPMethodURIUSEFieldIdx ) ) then
                begin
                        
                        // for iKeyIndex := 0 to _WSHTTPQueryURIParams.count-1 do
                        
                         for iKeyIndex := 0  to  kiTWSTypeIntSet_Max do 
                        begin
                                
                                if(not (iKeyIndex in  __WSClientMethodInfoUsed.WSInfoHTTPMethodURIUSEFieldIdx) ) then
                                begin
                                        continue;
                                end;
                                
                                
                                if( _WSHTTPQueryURIParams.count <= iKeyIndex) then
                                begin
                                    break;
                                end;
                                
                                sParamsNameKey := _WSHTTPQueryURIParams.KeyNames[  iKeyIndex  ];
                                 
                                
                                if(length(sParamsNameKey)<2) then
                                begin
                                        continue;
                                end;
                                
                                 if ( __WSClientMethodInfoUsed.WSInfoHTTPMethodURIFieldAsJSONString = true) then
                                begin
                                     sParamsNameValue := widestring(_WSHTTPQueryURIParams[sParamsNameKey].AsStringJson);
                                end
                                else
                                begin
                                      sParamsNameValue := widestring(_WSHTTPQueryURIParams[sParamsNameKey].AsString);
                                end;
                                     
                                if( AnsiPos('{'+sParamsNameKey+'}', _WSUrlConsumed ) >0 ) then 
                                begin
                                        _WSUrlConsumed :=	StringReplace(_WSUrlConsumed, '{'+sParamsNameKey+'}', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                                end
                                else if( AnsiPos('['+sParamsNameKey+']', _WSUrlConsumed ) >0 ) then 
                                begin
                                        _WSUrlConsumed :=	StringReplace(_WSUrlConsumed, '['+sParamsNameKey+']', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                                end else
                                
                                
                                if( AnsiPos('['+sParamsNameKey+']', TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams ) >0 ) then 
                                begin
                                    TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams := StringReplace(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams, '['+sParamsNameKey+']', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                                end
                                else if( AnsiPos('{'+sParamsNameKey+'}', TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams ) >0 ) then 
                                begin
                                    TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams := StringReplace(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams, '{'+sParamsNameKey+'}', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                                end else
                                
                                if( AnsiPos('/[', sParamsNameKey ) > 0 ) then
                                begin
                                     if(length(EncodeURIComponents(sParamsNameValue))>0) then
                                     begin
                                        sParamsEndPointValue.Append( '/'+EncodeURIComponents(sParamsNameValue)  );
                                    end;
                                end
                                else if( AnsiPos('/{', sParamsNameKey ) > 0 ) then
                                begin
                                    if(length(EncodeURIComponents(sParamsNameValue))>0) then
                                    begin
                                        sParamsEndPointValue.Append( '/'+EncodeURIComponents(sParamsNameValue)  );
                                    end;
                                 end
                                 else
                                 begin
                                      _WSRAWUrlParameters.Append(EncodeURIComponents(sParamsNameKey)+'='+EncodeURIComponents(sParamsNameValue));
                                       // :: if _WSRAWUrlParametersQueryParamGroupSeparator
                                       _WSRAWUrlParameters.Append('&');
                                end;
                        end;
                        
                        if(AnsiPos('/', string(_WSRAWUrlParameters.AsString)) <=0 ) then
                        begin
                            sParamsNameValue := _WSRAWUrlParameters.ASString;
                            _WSRAWUrlParameters.clear();
                            _WSRAWUrlParameters.Append('?');
                            _WSRAWUrlParameters.Append(sParamsNameValue);
                            
                        end;
                end;
                                 
                if( __WSClientMethodInfoUsed.WSInfoHTTPMethodURIFieldWithUserInfo) then
                begin
                    _WSRAWUrlParameters.Append('userInfos'+'='+EncodeURIComponents(WSGetUserInfos()));
                end;
                
                 
                for iKeyIndex :=0 to  kiTWSTypeIntSet_Max do 
                begin
                          if(not (iKeyIndex in  __WSClientMethodInfoUsed.WSInfoHTTPMethodBODYUSEFieldIdx) ) then
                        begin
                                continue;
                        end;
                        
                        sParamsNameKey := _WSHTTPQueryURIParams.KeyNames[ iKeyIndex ];
                        sParamsNameValue := widestring(_WSHTTPQueryURIParams[sParamsNameKey].AsString);
                        
                        _WSHTTPQueryBodyParams.add(sParamsNameKey,  sParamsNameValue);
                        
                        if( AnsiPos('['+sParamsNameKey+']', TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams ) >0 ) then 
                        begin
                            TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams := StringReplace(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams, '['+sParamsNameKey+']', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                        end
                        else if( AnsiPos('{'+sParamsNameKey+'}', TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams ) >0 ) then 
                        begin
                            TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams := StringReplace(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams, '{'+sParamsNameKey+'}', EncodeURIComponents(sParamsNameValue) ,[rfReplaceAll, rfIgnoreCase]);
                        end;
                        
                end;
              
                if ( _WSHTTPQueryBodyParams.count > 0) then
                begin
                        for iKeyIndex := 0 to _WSHTTPQueryBodyParams.count-1 do 
                        begin
                                sParamsNameKey := _WSHTTPQueryBodyParams.KeyNames[iKeyIndex];
                               
                                
                                if(length(sParamsNameKey)<2) then
                                begin
                                        continue;
                                end;
                                
                                if ( __WSClientMethodInfoUsed.WSInfoHTTPMethodBODYFieldAsJSONString = true) or ( __WSClientMethodInfoUsed.WSInfoHTTPMethodBODYFieldAsString = true) then
                                begin
                                    
                                        if ( __WSClientMethodInfoUsed.WSInfoHTTPMethodBODYFieldAsJSONString = true) then
                                        begin
                                             sParamsNameValue := widestring(_WSHTTPQueryBodyParams[sParamsNameKey].AsStringJson);
                                        end
                                        else
                                        begin
                                              sParamsNameValue := widestring(_WSHTTPQueryBodyParams[sParamsNameKey].AsString);
                                        end;
                                         
                                        _WSServerConnectionBodyDataStream.add(sParamsNameKey, sParamsNameValue);
                                end        
                                else
                                begin
                                          ;; //:: JSON STRING  _WSServerConnectionBodyDataStream.add(sParamsNameKey, sParamsNameValue);
                                end;
                                
                                // _WSServerConnectionBodyDataStream.AddFormField(sParamsNameKey, sParamsNameValue);
                        end;
                                
                end;

                if( __WSClientMethodInfoUsed.WSInfoHTTPMethodBODYFieldWithUserInfo) then
                begin
                     _WSServerConnectionBodyDataStream.add('userInfos', WSGetUserInfos());
                end;
                
                // **** assemble full URI ****
                if (length(_WS_BASE_VERSION) >0) then
                        begin
                                sP_WSVERSION := '-'+_WS_BASE_VERSION;
                        end;
                        WSWriteEventLog('Create URL with :'
                        +chr(13)+chr(10)+' - _WS_BASEHTTP_protocol                             : '+_WS_BASEHTTP_protocol
                        +chr(13)+chr(10)+' - _WS_BASEHTTP_host                                 : '+_WS_BASEHTTP_host
                        +chr(13)+chr(10)+' - _WS_BASEHTTP_port                                  : '+string(_WS_BASEHTTP_port)
                        +chr(13)+chr(10)+' - _WS_BASEHTTP_uri_baseentrypoint               : '+_WS_BASEHTTP_uri_baseentrypoint
                        +chr(13)+chr(10)+' - sP_WSVERSION                                         : '+string(sP_WSVERSION)
                        +chr(13)+chr(10)+' - _WS_BASEHTTP_uri_baseconsumepoint          : '+_WS_BASEHTTP_uri_baseconsumepoint
                        +chr(13)+chr(10)+' - _WSMethodName                                       : '+_WSMethodName
                        +chr(13)+chr(10)+' - _EndPointParams                                       : '+ string(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams) 
                        
                        +chr(13)+chr(10)+' - _WSRAWUrlParameters                                : '+TWSString(_WSRAWUrlParameters).AsString
                        +chr(13)+chr(10)+' - _WSServerConnectionBodyDataStream                                : '+TWSHTTPBodyDataStreamObject(_WSServerConnectionBodyDataStream).AsStringJSON);
        
                        try
                                if length(_WS_BASEHTTP_port) <= 1 then
                                begin
                                        _WS_BASEHTTP_port  := '0';
                                end;
                                
                                if (strtoint(_WS_BASEHTTP_port) <= 0) then
                                 begin
                                        _WS_BASEHTTP_port  := '0';
                                end;
                                
                        except
                        on ErrPortNumberCast: Exception do
                                begin
                                        _WS_BASEHTTP_port := '0';
                                        __appendLogStack(' Exception::WSGetURLAssemble::PortNumberCast ('+ ErrPortNumberCast.Message+')');                                
                                        raise TEWSTechErrorException.create( __LogStackGetLast() );
        
                                end;
                        end;
                        
                _WSUrlConsumed := format(_WSUrlConsumed, [_WS_BASEHTTP_protocol, _WS_BASEHTTP_host, string(_WS_BASEHTTP_port), _WS_BASEHTTP_uri_baseentrypoint, string(sP_WSVERSION), _WS_BASEHTTP_uri_baseconsumepoint, _WSMethodName, string(TWSInfoHTTPMethodTypeInfo(__WSClientMethodInfoUsed).WSInfoHTTPMethodURIEndPointParams)+''+(TWSString(sParamsEndPointValue).AsString)+''+(TWSString(_WSRAWUrlParameters).AsString) ]);
                
                try
                    if(  strtoint(_WS_BASEHTTP_port) = 0 ) then
                    begin
                        _WSUrlConsumed :=  StringReplace(_WSUrlConsumed, ':0/', '/',[rfReplaceAll, rfIgnoreCase]);    
                    end;
                except
                on ErrReplacePortNumber: Exception do
                        begin
                                _WS_BASEHTTP_port := '0';
                                __appendLogStack(' Exception::WSGetURLAssemble :: Replace Port number Cast ('+ ErrReplacePortNumber.Message+')');                                
                                raise TEWSTechErrorException.create( __LogStackGetLast() );

                        end;
                end;
                        
                         _WSUrlConsumed :=  StringReplace(_WSUrlConsumed, '://', '::::/::::/:::::',[rfReplaceAll, rfIgnoreCase]);
                         _WSUrlConsumed :=  StringReplace(_WSUrlConsumed, '//', '/',[rfReplaceAll, rfIgnoreCase]);
                         _WSUrlConsumed :=  StringReplace(_WSUrlConsumed, '::::/::::/:::::', '://',[rfReplaceAll, rfIgnoreCase]);
        //	***************************
        except
        on ErrGetUrlAssemble: Exception do
                begin
                        __appendLogStack(' Exception::WSGetURLAssemble ('+ ErrGetUrlAssemble.Message+')');                                
                        raise TEWSTechErrorException.create( __LogStackGetLast() );

                end;
        end;
        Result  := _WSUrlConsumed;
        
        sParamsEndPointValue.Destroy();
        
end;
        
// **************************
// **************************

{function GetAURL(const param, value: string): UTF8String;
begin
  Result := 'http://www.example.com/search?'+
    EncodeURIComponent(param)+
    '='+
    EncodeURIComponent(value);
end;}
        
// **************************
// **************************


function TWSRESTClient.EncodeURIComponents(const ASrc: string): UTF8String;
const
  HexMap: UTF8String = '0123456789ABCDEF';

  function IsSafeChar(ch: Integer): Boolean;
  begin
    if (ch >= 48) and (ch <= 57) then Result := True    // 0-9
    else if (ch >= 65) and (ch <= 90) then Result := True  // A-Z
    else if (ch >= 97) and (ch <= 122) then Result := True  // a-z
    else if (ch = 33) then Result := True // !
    else if (ch >= 39) and (ch <= 42) then Result := True // '()*
    else if (ch >= 45) and (ch <= 46) then Result := True // -.
    else if (ch = 95) then Result := True // _
    else if (ch = 126) then Result := True // ~
    else Result := False;
  end;
var
  I, J: Integer;
  ASrcUTF8: UTF8String;
begin
  Result := '';    {Do not Localize}

  ASrcUTF8 := UTF8Encode(ASrc);
  // UTF8Encode call not strictly necessary but
  // prevents implicit conversion warning

  I := 1; J := 1;
  SetLength(Result, Length(ASrcUTF8) * 3); // space to %xx encode every byte
  while I <= Length(ASrcUTF8) do
  begin
    if IsSafeChar(Ord(ASrcUTF8[I])) then
    begin
      Result[J] := ASrcUTF8[I];
      Inc(J);
    end
    else if ASrcUTF8[I] = ' ' then
    begin
      // Result[J] := '+';
      // Inc(J);
       Result[J] := '%';
        Result[J+1] := '2';
         Result[J+2] := '0';
      Inc(J,3);
    end
    else
    begin
      Result[J] := '%';
      Result[J+1] := HexMap[(Ord(ASrcUTF8[I]) shr 4) + 1];
      Result[J+2] := HexMap[(Ord(ASrcUTF8[I]) and 15) + 1];
      Inc(J,3);
    end;
    Inc(I);
  end;

  SetLength(Result, J-1);
end;
        
// **************************
// **************************


function TWSRESTClient.encodeURIComponents(sUriParts : TWSStringArray): widestring ;
var uriComponents: TWSHTTPQueryURIObject;
begin
//	***************************


result := '';
end;
//	***************************
function TWSRESTClient.parseURI(sUrlString : widestring): TWSStringArray;
var uriComponents: TWSHTTPQueryURIObject;
begin
//	***************************

        uriComponents := TWSHTTPQueryURIObject.Create(sUrlString);
         
        WSWriteEventLog(uriComponents.Protocol);
        WSWriteEventLog(uriComponents.Host);
        WSWriteEventLog(uriComponents.Document);

        setLength(result,11);
        Result[0] := string(uriComponents.Protocol);

        Result[1] := string(uriComponents.Username);
        Result[2] := string(uriComponents.Password);

        Result[3] := string(uriComponents.Host);
        Result[4] := string(uriComponents.Port);
        Result[5] := string(uriComponents.Path);
        Result[6] := string(uriComponents.Document);
        Result[7] := string(uriComponents.Bookmark);
        Result[8] := 'IPVersion';// string(inttostr(uriComponents.IPVersion));
        Result[9] := string(uriComponents.Params);
        Result[10] := string(uriComponents.URI);

end;
        
// **************************
// **************************

        
function TWSRESTClient.WSGetUserInfos: string;
begin
        _hostQueryInfos.clear();
       
        _hostQueryInfos.add('ip', _hostIpClient);
        _hostQueryInfos.add('userName', _hostnameClient);
       
        // :: _hostQueryInfos.add('httpUserAgent', _hostnameClient+';'+_programName+';'+_ClassName+':'+_ClassVersion+';'+ClassName+':'+ClassVersion+';'+_WSMethodName);
        
        _hostQueryInfos.add('httpUserAgent', _programName);
       
        // :: _hostQueryInfos.add('userIpAddr', _hostIpClient);
        // :: _hostQueryInfos.add('userIpMacAddr', _hostadrClient);
        // :: _hostQueryInfos.add('userComputerName', GetEnvironmentVariable('COMPUTERNAME') );
        // :: _hostQueryInfos.add('userComputerDomain', GetEnvironmentVariable('USERDNSDOMAIN') );
         // :: _hostQueryInfos.add('userDomain', GetEnvironmentVariable('USERDOMAIN') );
        // :: _hostQueryInfos.add('userDomain', _hostDomainClient);
        
        Result := _hostQueryInfos.AsStringJSON;
        // ::  '{"ip":"'+_hostIpClient+'","userName":"'+_hostnameClient+'","httpUserAgent":"'+_programName+'"}';       
        
end;
// **************************
// **************************
function TWSRESTClient.WSFindMethodInfo( pObjectMethodInfoList : array of TWSInfoHTTPMethodTypeInfo; aMethodName : string; aReturnPropertyName: string ) : string;
var iKeyRecordIndex : integer;
var sValidPropertyResult : widestring;
var bValidPropertyResult : boolean;
begin
 
        result := '';
        sValidPropertyResult := '';
        bValidPropertyResult := false;
        iKeyRecordIndex := 0;
        
        for iKeyRecordIndex := 0 to high(pObjectMethodInfoList) do begin
                
        
                if ( (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodName = aMethodName ) then
                begin
                        
                        if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodName ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodName;
                                bValidPropertyResult := true;
                                break;
                        end
                        else  if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodNameVersion ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodNameVersion;
                                bValidPropertyResult := true;
                                break;
                        end
                        else if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodSendType ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodSendType;
                                bValidPropertyResult := true;
                                break;
                        end
                        else if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEntryPoint ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodURIEntryPoint;
                                bValidPropertyResult := true;
                                break;
                        end
                        else if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIEndPoint ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodURIEndPoint;
                                bValidPropertyResult := true;
                                break;
                        end
                        else if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodURIParamsFormat ) then
                        begin
                                sValidPropertyResult := (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodURIParamsFormat;
                                bValidPropertyResult := true;
                                break;
                        end
                        else if( aReturnPropertyName = TWSInfoHTTPMethodTypeInfo_ReturnInfo.kWSInfoHTTPMethodProcessName ) then
                        begin
                                sValidPropertyResult :=  (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodProcessName;
                                bValidPropertyResult := true;
                                break;
                        end ;
                        
                end;
        end;

        
        if(not bValidPropertyResult) then
        begin
                __appendLogStack(' Exception::WSFindMethodInfo::unable to find('+aMethodName+'::'+aReturnPropertyName+')');                                
                raise TEWSTechErrorException.create( __LogStackGetLast() );
                exit;
        end;
        Result := sValidPropertyResult;
                                
end;
// **************************
// validation des Fields Rules Transmis
// **************************
function TWSRESTClient.ReadWSRegisterConsumerValidatation() : boolean;
begin
        Result := true;
end;
// **************************
// creation de validation des Fields Rules Transmis
// **************************
function TWSRESTClient.CreateWSRegisterConsumerValidatation(pObjectPropertiesList : array of string): boolean;
begin
        Result := true;
end;
// **************************
//  ...
// **************************
function TWSRESTClient.CreateWSRegisterConsumerProperties(pObjectPropertiesList : array of TWSInfoHTTPMethodTypeInfo): boolean;

var iIndexPropCreate :  integer;
begin
        
        setlength(__WSClientMethodInfoAvail, length(pObjectPropertiesList));
       
         for iIndexPropCreate := 0 to length(pObjectPropertiesList)-1 do begin
            
            
             {$IFnDEF FPC}
                  CopyMemory(@  __WSClientMethodInfoAvail[iIndexPropCreate], @pObjectPropertiesList[iIndexPropCreate], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ELSE}
                      __WSClientMethodInfoAvail[iIndexPropCreate] := pObjectPropertiesList[iIndexPropCreate];
                   // :: Move(__WSClientMethodInfoUsed, pObjectMethodInfoList[iKeyRecordIndex], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ENDIF}
        end;
        Result := true;
end;
// **************************
//  ...
// **************************
function TWSRESTClient.CreateWSRegisterConsumerProperties(pObjectPropertiesList : array of TWSInfoHTTPMethodTypeInfo; pURIFieldParameterList : array of string): boolean;
var iIndexPropCreate :  integer;
begin
        
        setlength(__WSClientMethodInfoAvail, length(pObjectPropertiesList));
        
        for iIndexPropCreate := 0 to length(pObjectPropertiesList) -1 do begin
          
            
             {$IFnDEF FPC}
                  CopyMemory(@  __WSClientMethodInfoAvail[iIndexPropCreate], @pObjectPropertiesList[iIndexPropCreate], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ELSE}
                      __WSClientMethodInfoAvail[iIndexPropCreate] := pObjectPropertiesList[iIndexPropCreate];
                   // :: Move(__WSClientMethodInfoUsed, pObjectMethodInfoList[iKeyRecordIndex], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ENDIF}
            
        end;
        Result := true;
end;
// **************************
//  ...
// **************************
function TWSRESTClient.WSMethodGetIndexInfoForName(pObjectMethodInfoList : array of TWSInfoHTTPMethodTypeInfo; aMethodName : string) : integer;
var iKeyRecordIndex : integer;
begin
    Result := 0;             
    for iKeyRecordIndex := 0 to high(pObjectMethodInfoList) do begin
        if ( (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodName = aMethodName ) then
        begin
            Result := iKeyRecordIndex; 
            exit;
        end;
    end;        
        raise TEWSTechErrorException.create('Unable to find index for MethodName ('+aMethodName+')');
end;

// **************************
//  ...
// **************************
function TWSRESTClient.WSMethodAddEnumInfo(pObjectMethodInfo : TWSIntSet; aEnumAddon : TWSIntSet) : TWSIntSet;
var iKeyIndex : integer;
var aResultInSet : TWSIntSet;
var aTempResultInSet : TWSIntSet;
begin
    aTempResultInSet := [ integer(kiTWSTypeIntSet_Max +1)];
    aResultInSet := [ integer(kiTWSTypeIntSet_Max +1) ];
    for iKeyIndex := 0  to  kiTWSTypeIntSet_Max do 
    begin
            
        if( (iKeyIndex in aEnumAddon ) ) then
        begin
                 
            if( (iKeyIndex in pObjectMethodInfo )  ) then
            begin
                continue;
            end;
            aTempResultInSet := [integer(iKeyIndex)];
            aResultInSet := aResultInSet +  aTempResultInSet;
            // ::  pObjectMethodInfo := pObjectMethodInfo + aTempResultInSet;
        end;
        
       
    end;
    
    Result := aResultInSet;
end;
  
// **************************
//  ...
// **************************   
function TWSRESTClient.RegisterWSMethodInfoForName(pObjectMethodInfoList :  array of TWSInfoHTTPMethodTypeInfo; aMethodName : string) : boolean;
var iKeyRecordIndex : integer;
begin
    Result := false;             
    for iKeyRecordIndex := 0 to high(pObjectMethodInfoList) do begin
        if ( (pObjectMethodInfoList[iKeyRecordIndex]).WSInfoHTTPMethodName = aMethodName ) then
        begin
            // :: Move(pObjectPropertiesList, __WSClientMethodInfoUsed, Length(pObjectPropertiesList));
            // ::__WSClientMethodInfoUsed :=   pObjectMethodInfoList[iKeyRecordIndex];
            // MSWindows DLL :: CopyMemory

            {$IFnDEF FPC}
                  CopyMemory(@__WSClientMethodInfoUsed, @pObjectMethodInfoList[iKeyRecordIndex], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ELSE}
                    __WSClientMethodInfoUsed := pObjectMethodInfoList[iKeyRecordIndex];
                   // :: Move(__WSClientMethodInfoUsed, pObjectMethodInfoList[iKeyRecordIndex], SizeOf(TWSInfoHTTPMethodTypeInfo));
            {$ENDIF}

            Result := true; 
            exit;
        end;
    end;        
        raise TEWSTechErrorException.create('Unable to find MethodName ('+aMethodName+')');
end;
// **************************
//  Creation des Object Virtual Dynamic Property
// **************************
function TWSRESTClient.CreateWSRegisterObjectProperties(pObjectPropertiesList : array of string): boolean;
begin
       Result :=  CreateWSRegisterObjectProperties(pObjectPropertiesList, []);
end;

// **************************
//  ...
// **************************
function TWSRESTClient.CreateWSRegisterObjectProperties(pObjectPropertiesList : array of string; pURIFieldParameterList : array of string ): boolean;
var aKeyNameIdx : integer;
var aKeyName : string;

begin
        WSWriteEventLog('Build prop list :: ');
        try
        if  (High(pObjectPropertiesList) >0) then
        begin
                if NOT assigned(_WSHTTPQueryURIParams) then
                begin
                        _WSHTTPQueryURIParams                   := TWSArrayObject.create();
                end;
                
                if NOT assigned(_WSUrlParametersPropsArrayIdxKeys) then
                begin
                        _WSUrlParametersPropsArrayIdxKeys      := TWSArrayObject.create();
                end;
                
                for aKeyNameIdx := 0 to High(pObjectPropertiesList) do begin
                        
                        aKeyName := pObjectPropertiesList[aKeyNameIdx];
                        
                        WSWriteEventLog('Walk prop building :: '+aKeyName);
                        
                        //_WSUrlParametersPropsArrayIdxKeys[inttostr(aKeyName)].Value := pObjectPropertiesList[aKeyName];
                        // 
                        _WSUrlParametersPropsArrayIdxKeys[inttostr(aKeyNameIdx)] := TWSString.create( aKeyName);
                        _WSHTTPQueryURIParams[aKeyName] :=  TWSString.create(''); // :: void empty string :: '+aKeyName+' :: '+inttostr(aKeyNameIdx)) ;
                        WSWriteEventLog('Walk prop building :: Value :: ');
                        // WSWriteEventLog(' :: '+ _WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx] );
                        
                        WSWriteEventLog('Walk prop :: back :: '+ TWSString(_WSHTTPQueryURIParams[aKeyName]).value);
                end;
        end
        else
        begin
                WSWriteEventLog('Build prop list :: No props received ... ');
        end;
        WSWriteEventLog('Build prop list :: Clear ... ');

//	***************************
        except
        on ErrCreateProps: Exception do
                begin
                        __appendLogStack(' Exception::CreateObjectProps ('+ErrCreateProps.Message+')');                                
                        raise TEWSTechErrorException.create( __LogStackGetLast() );
                        Result:= false;
                        exit;
                end;
        end;	
        Result := true;
end;
 
//	***************************
//	*************************** 
function TWSRESTClient._wsread_props_WSRAWUrlParameters (): widestring ;
begin
 
    Result := _WSRAWUrlParameters.asString;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSRAWUrlParameters(const aValue :widestring);
var aArrayParsedParams  		: TWSArrayObject;
var aArrayParsedParamsKeyValue  : TWSArrayObject;
var sParamsNameKey				: string;
var sParamsNameValue			: widestring;
var iKeyIndex					: integer;
var iKeyIndexGroup				: integer;
begin
        // **** exemple :: apikey=dc5747d8-bc39-442c-9a32-f37e806bfce8&query=%7B%0A%20%20findStreetByRivoli%20(rivoli%3A"057C")%0A%20%20%7Bscope%20%7BcityCode%2C%20cityAfnorLabel%2C%20rivoli%0A%20%20%7D%7D%0A%7D%0A%0A%0A%0A%0A%0A
        // ****** ***** Should explode to ::
        // ****** array[0] == apikey=dc5747d8-bc39-442c-9a32-f37e806bfce8
        // ****** array[1] ==  query=%7B%0A%20%20findStreetByRivoli%20(rivoli%3A"057C")%0A%20%20%7Bscope%20%7BcityCode%2C%20cityAfnorLabel%2C%20rivoli%0A%20%20%7D%7D%0A%7D%0A%0A%0A%0A%0A%0A
        
        aArrayParsedParams  		:= TWSArrayObject.create();
        aArrayParsedParamsKeyValue  := TWSArrayObject.create();
        sParamsNameKey				:= string('');
        sParamsNameValue			:= widestring('');
        iKeyIndex					:= 0;
        iKeyIndexGroup				:= 0;
        
        _WSRAWUrlParameters.WriteString(WS_func_strrtrim(aValue));
        
        // URI Vide, Empty array as well
        // if(not (_WSRAWUrlParameters.size >0))then
        // begin
                // **** empty array
                // _WSHTTPQueryURIParams.clear();
                //exit;
        // end;
        // ******************************
        // :: parseURI
        // ******************************
        aArrayParsedParams := WS_func_explodeStr('&',_WSRAWUrlParameters);
        // Test possible unique group
        aArrayParsedParamsKeyValue := WS_func_explodeStr('=',aArrayParsedParams.KeyNames[iKeyIndexGroup]);
        
        if (aArrayParsedParams.count > 0) then
        begin
                // **** empty array
                _WSHTTPQueryURIParams.Clear();
                // ****** walk in through
                for iKeyIndexGroup := 0 to aArrayParsedParams.count do
                begin
                        aArrayParsedParamsKeyValue := WS_func_explodeStr('=', aArrayParsedParams.KeyNames[iKeyIndexGroup]); // :: TODO KeyIndexes
                        // ****** walk through
                        for iKeyIndex := 0  to aArrayParsedParamsKeyValue.count do
                        Begin
                                sParamsNameKey := aArrayParsedParamsKeyValue.KeyNames[iKeyIndex]; 
                                sParamsNameValue := widestring(TWSString(aArrayParsedParamsKeyValue[iKeyIndex]).AsString); // :: TODO asString
                                _WSHTTPQueryURIParams[sParamsNameKey] := TWSString.create(sParamsNameValue);
                        end;
                end;
        end
        else 
        begin
                if (aArrayParsedParamsKeyValue.count > 0) then
                begin
                        // **** empty array
                        _WSHTTPQueryURIParams.Clear();
                        // ****** walk through
                        for iKeyIndex := 0 to aArrayParsedParamsKeyValue.count do
                        Begin
                                sParamsNameKey := aArrayParsedParamsKeyValue.KeyNames[iKeyIndex]; 
                                sParamsNameValue := widestring(TWSString(aArrayParsedParamsKeyValue[iKeyIndex]).AsString);
                                _WSHTTPQueryURIParams[sParamsNameKey] := TWSString.create(sParamsNameValue);
                        end;
                end
                else
                begin
                        WSWriteEventLog('ParseURI Error .... ', kWSEventLogType_EWSValidationParseQueryParamsErrorException);
                        raise TEWSTechInternalRuntimeErrorException.create('ParseURI Error .... ');
                end;
        end;
        aArrayParsedParams.Destroy();
        aArrayParsedParamsKeyValue.Destroy();
end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSParsedResponse (): string ;
begin
if _WSParsedResponse <> nil then
  begin
    Result := _WSParsedResponse.AsString;
  end
  else
  begin
     _WSParsedResponse := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSParsedResponse(aValue :string);
begin

        // _WSParsedResponse.WriteString('');
        if aValue <> '' then
        begin
                _WSParsedResponse.WriteString(aValue);
        end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSParsedResponseCode (): string ;
begin
if _WSParsedResponseCode <> nil then
  begin
    Result := _WSParsedResponseCode.AsString;
  end
  else
  begin
      _WSParsedResponseCode := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSParsedResponseCode(aValue :string);
begin

        // _WSParsedResponseCode.WriteString('');
if aValue <> '' then
begin
        _WSParsedResponseCode.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSResponseErrorJson (): string ;
begin
if _WSResponseErrorJson <> nil then
  begin
    Result := _WSResponseErrorJson.AsString;
  end
  else
  begin
     _WSResponseErrorJson := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSResponseErrorJson(aValue :string);
begin

        // _WSResponseErrorJson.WriteString('');
if aValue <> '' then
begin
        _WSResponseErrorJson.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSResponseErrorCodeJson (): string ;
begin
if _WSResponseErrorCodeJson <> nil then
  begin
    Result := _WSResponseErrorCodeJson.AsString;
  end
  else
  begin
      _WSResponseErrorCodeJson := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSResponseErrorCodeJson(aValue :string);
begin

        // _WSResponseErrorCodeJson.WriteString('');
if aValue <> '' then
begin
        _WSResponseErrorCodeJson.WriteString(aValue);
end;

end;
        
// **************************
// **************************


 function TWSRESTClient._wsread_props_WSResponseCodeHTTP (): string ;
begin
if _WSResponseCodeHTTP <> nil then
  begin
    Result := _WSResponseCodeHTTP.AsString;
  end
  else
  begin
     _WSResponseCodeHTTP := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSResponseCodeHTTP(aValue :string);
begin

        // _WSResponseCodeHTTP.WriteString('');
if aValue <> '' then
begin
        _WSResponseCodeHTTP.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSRawResponseCodeHTTP (): string ;
begin
if _WSRawResponseCodeHTTP <> nil then
  begin
    Result := _WSRawResponseCodeHTTP.AsString;
  end
  else
  begin
     _WSRawResponseCodeHTTP := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSRawResponseCodeHTTP(aValue :string);
begin

        // _WSRawResponseCodeHTTP.WriteString('');
if aValue <> '' then
begin
        _WSRawResponseCodeHTTP.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSRawResponse (): string ;
begin
if _WSRawResponse <> nil then
  begin
    Result := _WSRawResponse.AsString;
  end
  else
  begin
     _WSRawResponse := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSRawResponse(aValue :string);
begin

        // _WSRawResponse.WriteString('');
if aValue <> '' then
begin
        _WSRawResponse.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSRawQuery (): string ;
begin
if _WSRawQuery <> nil then
  begin
    Result := _WSRawQuery.AsString;
  end
  else
  begin
     _WSRawQuery := TWSStringStream.Create('');
     Result := '';
  end;
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSRawQuery(aValue :string);
begin

        // _WSRawQuery.WriteString('');
if aValue <> '' then
begin
        _WSRawQuery.WriteString(aValue);
end;

end;
        
// **************************
// **************************


function TWSRESTClient._wsread_props_WSConsumeEndPointParams (const aKeyName : string): widestring ;
begin
    Result := widestring(_WS_BASEHTTP_uri_baseconsumepointparams[aKeyName].value);
end;
        
// **************************
// **************************


procedure TWSRESTClient._wswrite_props_WSConsumeEndPointParams(const aKeyName: string; const aValue :widestring);
begin
        _WS_BASEHTTP_uri_baseconsumepointparams[aKeyName].value := aValue;
end;
// **************************
// **************************

function TWSRESTClient._wsread_props_WSHTTPQueryContentEncoding (): string ;
Begin
        Result := _WSHTTPQueryContentEncoding; // :: _WSServerConnection.Request.ContentEncoding;
end;
        
// **************************
// **************************

procedure TWSRESTClient._wswrite_props_WSHTTPQueryContentEncoding	(const aContentEncodingName : string) ;
begin

        if WS_func_StringInArray(string(aContentEncodingName),
                        [ TWSHTTPContentType.kWSHTTPContentType_ENCODING_ISO8859_15,
                        TWSHTTPContentType.kWSHTTPContentType_ENCODING_UTF8 ] ) then
        begin
                       _WSHTTPQueryContentEncoding :=  aContentEncodingName;// :: _WSServerConnection.Request.ContentEncoding := aContentTypeName;
        end
        else
        begin
                raise TEWSHTTPErrorException.create('Wrong HTTP ContentType specified ... ('+string(aContentEncodingName)+') ');
        end;
end;

// **************************
// **************************

function TWSRESTClient._wsread_props_WSHTTPQueryContentType (): string ;
Begin
        Result := _WSHTTPQueryContentType; // :: _WSServerConnection.Request.ContentType;
end;

// **************************
// **************************

procedure TWSRESTClient._wswrite_props_WSHTTPQueryContentType	(const aContentTypeName : string) ;
begin

        if (string(aContentTypeName) = 	TWSHTTPContentType.kWSHTTPContentType_NONE)  then
        begin
        
                _WSServerConnection.Request.ContentType :='';
        end
        else 
        begin
                if WS_func_StringInArray(string(aContentTypeName),[
                                TWSHTTPContentType.kWSHTTPContentType_APPJSON,

                                TWSHTTPContentType.kWSHTTPContentType_TEXTJSON,

                                TWSHTTPContentType.kWSHTTPContentType_JAVASCRIPT,
                                TWSHTTPContentType.kWSHTTPContentType_TEXT,

                                TWSHTTPContentType.kWSHTTPContentType_HTML,
                                TWSHTTPContentType.kWSHTTPContentType_FORMDATA,
                                TWSHTTPContentType.kWSHTTPContentType_APPFORMDATA]) then
                begin
                        _WSHTTPQueryContentType  := aContentTypeName;
                        // :: _WSServerConnection.Request.ContentType := aContentTypeName;
                end
                else
                begin
                        raise TEWSHTTPErrorException.create('Wrong HTTP ContentType specified ... ('+string(aContentTypeName)+') ');
                end;
                        
        end;
        
        
        

end;

//**************************************************
//**************************************************

procedure TWSRESTClient._wswrite_props_WSHTTPQueryBodyParams 	(const aKeyName : string; const aValue: widestring) ;
begin
        if( not _WSHTTPQueryBodyParams.ArrayKeyExist(aKeyName)) then
        begin
                _WSHTTPQueryBodyParams[aKeyName] := TWSString.create(aValue);
        end
        else
        begin
                _WSHTTPQueryBodyParams[aKeyName].value := aValue;
        end;
end;

//**************************************************
//**************************************************

function TWSRESTClient._wsread_props_WSHTTPQueryBodyParams 	(const aKeyName : string): widestring ;
begin
        if( not _WSHTTPQueryBodyParams.ArrayKeyExist(aKeyName)) then
        begin
                Result := TWSString.create('').AsString;
        end
        else
        begin
                Result := widestring(_WSHTTPQueryBodyParams[aKeyName].value );
        end;	
end;

//**************************************************
//**************************************************

procedure TWSRESTClient._wswrite_props_WSHTTPQueryURIParams (const aKeyName : string; const aValue: widestring) ;
begin
        if( not _WSHTTPQueryURIParams.ArrayKeyExist(aKeyName)) then
        begin
                _WSHTTPQueryURIParams[aKeyName] := TWSString.create(aValue);
        end
        else
        begin
                _WSHTTPQueryURIParams[aKeyName].value := aValue;
        end;
end;

//**************************************************
//**************************************************

function TWSRESTClient._wsread_props_WSHTTPQueryURIParams (const aKeyName : string): widestring ;
begin
        if( not _WSHTTPQueryURIParams.ArrayKeyExist(aKeyName)) then
        begin
                Result := TWSString.create('').AsString;
        end
        else
        begin
                Result := widestring(TWSString(_WSHTTPQueryURIParams[aKeyName]).asString );
        end;
end;

//**************************************************
//**************************************************

procedure TWSRESTClient._wswrite_props_WSHTTPQueryHeaders 	(const aKeyName : string; const aValue: widestring);
begin
        if( not _WSHTTPQueryHeaders.ArrayKeyExist(aKeyName)) then
        begin
                _WSHTTPQueryHeaders[aKeyName] := TWSString.create(aValue);
        end
        else
        begin
                _WSHTTPQueryHeaders[aKeyName].value := aValue;
        end;
end;

//**************************************************
//**************************************************

function TWSRESTClient._wsread_props_WSHTTPQueryHeaders 	(const aKeyName : string): widestring ;
begin
        if( not _WSHTTPQueryHeaders.ArrayKeyExist(aKeyName)) then
        begin
                Result := TWSString.create('').AsString;
        end
        else
        begin
                Result := widestring(_WSHTTPQueryHeaders[aKeyName].value );
        end;
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSParamsKeyExists(const aKeyName: string; aArrayObject : TWSArrayObject) : boolean;
begin
        Result := 	boolean(aArrayObject.ArrayKeyExist(aKeyName));
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSReadParamsCount(): integer; 
begin
        WSWriteEventLog('WSReadParamsCount :: ');
        Result := _WSHTTPQueryURIParams.count;
end;
//**************************************************
//**************************************************

function TWSRESTClient.WSReadParsedResponseCount(): integer; 
begin
        WSWriteEventLog('WSReadParsedResponseCount :: ');
        Result := _WSParsedResponseJSON.count;
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSReadParsedResponse(ColumnName: variant): TObject;
begin
        WSWriteEventLog('GetColumnValue :: '+ColumnName);
        result := _WSParsedResponseJSON[ColumnName];
end; 


//**************************************************
//**************************************************


procedure TWSRESTClient.WSWriteParsedResponse(ColumnName: variant;  const Value: TObject);
begin
        
        WSWriteEventLog('WSWriteParsedResponse :: '+ColumnName);
        _WSParsedResponseJSON[ColumnName] := TWSObject(Value);
end;

//**************************************************
//**************************************************

 {	TODO :: _WSParsedResponseJSON 		  := TWSObjectJSONObject.Create();
        _WSParsedResponseCodeJSON 	:= TWSObjectJSONObject.Create();

        // **********************
        _WSRawQueryData 	:= TGNAssocValueObject.Create();
        _WSRawQueryJSON 	:= TWSObjectJSONObject.Create();
 }


//**************************************************
//**************************************************


function TWSRESTClient.WSPropsReadingByIdxTInt(const aKeyNameIdx: integer): Integer ;
begin

    WSWriteEventLog('Access Index ... Reading IdxTInt ... '+inttostr(aKeyNameIdx));
    if _WSUrlParametersPropsArrayIdxKeys.Count = 0 then
    begin
        raise TEWSTechInternalRuntimeErrorException.create(' Exception Error :: Aucune Propriete n est disponible :: Verifier la declaration de properties :: Access Index idx : '+inttostr(aKeyNameIdx));
    end;
        Result  := integer(WSPropsReading(_WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx]));

end;

//**************************************************
//**************************************************

function TWSRESTClient.WSPropsReadingByIdx(const aKeyNameIdx: integer): variant ;
var aKeyName : string;
begin
        
        aKeyName := TWSString(_WSUrlParametersPropsArrayIdxKeys[inttostr(aKeyNameIdx)]).asString;
        
        
        WSWriteEventLog('Access Index ... Reading IDX ... '+inttostr(aKeyNameIdx)+'::'+aKeyName);
      if _WSUrlParametersPropsArrayIdxKeys.Count = 0 then
      begin
         raise TEWSTechInternalRuntimeErrorException.create(' Exception Error :: Aucune Propriete n est disponible :: Verifier la declaration de properties :: Access Index idx : '+inttostr(aKeyNameIdx));
      end;
          
          
           WSWriteEventLog('WSPropsReadingByIdx  :: '+_WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx]);
          
          
          
        Result := string(WSPropsReading(aKeyName));
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSPropsReading(aKeyName: variant): variant ;
var SValidKey, SValidKeyType : String;
begin
        WSWriteEventLog('Access Index ... Reading ... '+string(aKeyName));
        if not assigned(_WSHTTPQueryURIParams)  then
        begin
            _WSHTTPQueryURIParams := TWSArrayObject.create();
        end;

        if not assigned(_WSUrlParametersPropsArrayIdxKeys)  then
        begin
            _WSUrlParametersPropsArrayIdxKeys := TWSArrayObject.create();
        end;

        if _WSHTTPQueryURIParams.Count = 0 then
        begin
                Result := 'null';
                Exit;
        end;

        if _WSUrlParametersPropsArrayIdxKeys.Count = 0 then
        begin
                raise TEWSTechInternalRuntimeErrorException.create(' Exception Error :: Aucune Propriete n est disponible :: Verifier la declaration de properties :: Access Index idx : '+inttostr(aKeyName));
        end;
        
        {
        case Variants.VarType(aKeyName) of
        vtInteger:    SValidKeyType := IntToStr(aKeyName.VInteger);
        vtBoolean:    SValidKeyType := aKeyName.VBoolean;
        vtChar:       SValidKeyType := aKeyName.VChar;
        vtExtended:   SValidKeyType :=FloatToStr(aKeyName.VExtended);
        
        vtString:     SValidKeyType := aKeyName.VString;
        
        vtPChar:      SValidKeyType := aKeyName.VPChar;
        vtObject:     SValidKeyType := aKeyName.VObject.ClassName;
        vtClass:      SValidKeyType := aKeyName.VClass.ClassName;
        vtAnsiString: SValidKeyType := string(aKeyName.VAnsiString);
          // :: vtOleStr:   	
                        begin:
                                // :: ColumnName := VarToIntAsString(vColumnName);			
                                // :: Copy(WideString(Pointer(vColumnName.VOleStr)), 1, MaxInt);
                                VarOleStrToString(ColumnName,vColumnName );
                        end;
        vtCurrency:   SValidKeyType := CurrToStr(aKeyName.VCurrency);
        vtVariant:    SValidKeyType := string(aKeyName.VVariant);
        else
        SValidKeyType := '--ERR--';
        end;
        
        WSWriteEventLog('Access Index : '+SValidKeyType);}




        //WSWriteEventLog(' :: ' +IntToStr( _WSHTTPQueryURIParams[aKeyName ].Count));
        try
        {if( _WSHTTPQueryURIParams[aKeyName ] )  then
        begin
        Result := null;
        Exit;
        end;}
        
                 
                  if( _WSHTTPQueryURIParams[aKeyName ].ClassName = TWSBoolean.ClassName) then
                        begin
                                //
                                WSWriteEventLog('WSPropsReading :: ClassName :: TWSBoolean ' +IntToStr( _WSHTTPQueryURIParams[aKeyName ].Value)+' : '+aKeyName);
                                Result := integer( _WSHTTPQueryURIParams[aKeyName ].Value);
                        end
                else if ( _WSHTTPQueryURIParams[aKeyName ].ClassName = TWSNumber.ClassName) then
                        begin
                                WSWriteEventLog('WSPropsReading :: ClassName :: TWSNumber ' +IntToStr( _WSHTTPQueryURIParams[aKeyName ].Value)+' : '+aKeyName);
                                Result := integer( _WSHTTPQueryURIParams[aKeyName ].Value); // TODO FLOAT / INTEGER
                        end
                else if( _WSHTTPQueryURIParams[aKeyName ] .ClassName = TWSString.ClassName) then
                        begin
                                WSWriteEventLog('WSPropsReading :: ClassName :: TWSString ' + TWSString( _WSHTTPQueryURIParams[aKeyName ]).AsString+' : '+aKeyName);
                                Result := TWSString( _WSHTTPQueryURIParams[aKeyName ]).AsString;
                        end
                else if( _WSHTTPQueryURIParams[aKeyName ] .ClassName = TWSArrayObject.ClassName) then
                        begin
                                WSWriteEventLog('WSPropsReading :: ClassName :: TWSArrayObject ' + inttostr(TWSArrayObject( _WSHTTPQueryURIParams[aKeyName ]).Count)+' : '+aKeyName);
                                Result := TWSArrayObject( _WSHTTPQueryURIParams[aKeyName ]).Count;
                        end
                else if ( _WSHTTPQueryURIParams[aKeyName ].ClassName = TWSObject.ClassName) then
                        begin
                          //
                          WSWriteEventLog('WSPropsReading :: ClassName :: TWSObject [' + IntToStr(_WSHTTPQueryURIParams[aKeyName ].Count) +'] : '+aKeyName);
                          Result := '[OBJECT][' + IntToStr(_WSHTTPQueryURIParams[aKeyName ].Count) +']';
                        end
                else if ( _WSHTTPQueryURIParams[aKeyName ].ClassName = TWSObjectBase.ClassName) then
                        begin
                          WSWriteEventLog('WSPropsReading :: ClassName :: Object BASE [' + string(_WSHTTPQueryURIParams[aKeyName ].ClassName) +'] '+aKeyName);
                          Result := '[OBJECT BASE][' + string(_WSHTTPQueryURIParams[aKeyName ].ClassName) +']';
                        end
                else
                begin
                        raise TEWSTechInternalRuntimeErrorException.create('WSPropsReading cast error ... '+aKeyName+'::'+ string(_WSHTTPQueryURIParams[aKeyName ].ClassName))
        
                end;
        except
                on ErrVariantCastAssign : Exception do
                begin
                        WSWriteEventLog('WSPropsReading::ErrVariantCastAssign ::err print ' );
                end;
        end;
  

end;

//**************************************************
//**************************************************
 function TWSRESTClient.WSPropsReadingObject(aKeyName: variant): TObject ;
 begin
    
        Result := _WSHTTPQueryURIParams[aKeyName ];
    
 end;

//**************************************************
//**************************************************
procedure TWSRESTClient.WSPropsWritingByIdxTInt(const aKeyNameIdx: integer; aValue: Integer) ; 
var SValidKeyType, SValidValueType : String;
begin

        if not assigned(_WSUrlParametersPropsArrayIdxKeys)  then
        begin
                _WSUrlParametersPropsArrayIdxKeys := TWSArrayObject.create();
        end;
        
        
        
 //	_WSHTTPQueryURIParams[ _WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx]] := TlkJSONnumber.Create();
        _WSHTTPQueryURIParams[ TWSString(_WSUrlParametersPropsArrayIdxKeys[inttostr(aKeyNameIdx)]).AsString] := TWSString.create( Integer(aValue));
        
        WSWriteEventLog('Access Index IdxTInt : '+inttostr(aKeyNameIdx)+' :: '+ _WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx] +' ;; '+ inttostr( _WSHTTPQueryURIParams[ _WSUrlParametersPropsArrayIdxKeys.KeyNames[aKeyNameIdx] ].Value) +' ;; Writing  : ;; ');
end;

//**************************************************
//**************************************************


procedure TWSRESTClient.WSPropsWritingByIdx(const aKeyNameIdx: integer; aValue: variant) ;
var SValidKeyType, SValidValueType, aKeyName : String;
var iConvertCast : Integer;
// :: var jselementList :TlkJSONlist;
// :: var jselementarray : TWSObjectJSONObject;
begin
        try
                iConvertCast := 0;
                
                if not assigned(_WSHTTPQueryURIParams)  then
                begin
                    _WSHTTPQueryURIParams := TWSArrayObject.create();
                end;

                if not assigned(_WSUrlParametersPropsArrayIdxKeys)  then
                begin
                    _WSUrlParametersPropsArrayIdxKeys := TWSArrayObject.create();
                end;

                if _WSUrlParametersPropsArrayIdxKeys.Count = 0 then
                begin
                    raise TEWSTechInternalRuntimeErrorException.create(' Exception Error :: Aucune Propriete n est disponible :: Verifier la declaration de properties :: Access Index idx : '+inttostr(aKeyNameIdx));
                end;
                WSWriteEventLog('Access Index idx : '+inttostr(aKeyNameIdx));

      // :: iConvertCast := _WSUrlParametersPropsArrayIdxKeys[aKeyNameIdx];

       // _WSUrlParametersPropsArrayIdxKeys.Add(inttostr(aKeyName), );
                //	WSWriteEventLog(' WSPropsWritingByIdx :: '+ _WSUrlParametersPropsArrayIdxKeys[ inttostr(aKeyNameIdx) ].Value +';; Writing  : ;; ');

                // _WSHTTPQueryURIParams.Delete( iConvertCast );
                aKeyName :=  TWSString(_WSUrlParametersPropsArrayIdxKeys[inttostr(aKeyNameIdx)]).asString;
                WSWriteEventLog(':::: WSPropsWritingByIdx :: Access Index idx : '+inttostr(aKeyNameIdx)+' :: '+aKeyName);
                
                // :: _WSHTTPQueryURIParams[aKeyName] := TWSObjectJSONObject.create() ;
                
                // :: _WSHTTPQueryURIParams[aKeyName ] := TWSObjectJSONObject(aValue);


                case Variants.VarType(aValue) of
                varByte: // type 17
                        begin
                                SValidKeyType := 'Byte'; // IntToStr(aValue.varByte);
                                iConvertCast := Integer(aValue) ;
                                WSWriteEventLog(' type :: '+SValidKeyType+' :: assign keyidx :: '+aKeyName);
                                 
                                _WSHTTPQueryURIParams[ aKeyName ] :=   TWSNumber.Create(iConvertCast);
                        end;

                vtInt64:
                        begin
                                SValidKeyType := IntToStr(aValue.VInteger);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                iConvertCast := integer(aValue.VInteger);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] :=    TWSNumber.Create(integer(iConvertCast));

                        end;
                vtInteger:
                        begin
                                SValidKeyType := IntToStr(aValue.VInteger);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                iConvertCast := integer(aValue.VInteger);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSNumber.Create( integer(iConvertCast) );
                        end;

                vtBoolean: 
                        begin
                                SValidKeyType := aValue.VBoolean;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] :=  TWSBoolean.Create( boolean(aValue) );
                        end;

                vtChar:
                        begin
                                SValidKeyType := aValue.VChar;
                                WSWriteEventLog(' type :: '+SValidKeyType);
 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.Create(string(aValue));
 
                        end;
                        
                vtExtended:
                        begin
                                SValidKeyType := FloatToStr(aValue.VExtended);
                                WSWriteEventLog(' type :: '+SValidKeyType);
 
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtExtended');
                                exit;
                        end;
                        
                vtString:
                        begin
                                SValidKeyType := aValue.VString;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.create(string(aValue));
                                 
                        end;
                varString: // type 256
                        begin
                                SValidKeyType := 'string';//aValue.varString;
                                WSWriteEventLog(' type :: '+SValidKeyType);       
                                 
                                _WSHTTPQueryURIParams[ aKeyName ] := TWSString.create(string(aValue));
                                 
                        end;
                vtPChar:
                        begin
                                SValidKeyType := aValue.VPChar;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // _WSHTTPQueryURIParams[aKeyName ] :=
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtExtended');
                                exit;
                        end;

                vtObject:
                        begin
                                SValidKeyType := aValue.VObject.ClassName;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // _WSHTTPQueryURIParams[aKeyName ] :=
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtObject');
                                exit;
                        end;

                vtClass:
                        begin
                                SValidKeyType := aValue.VClass.ClassName;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 //  _WSHTTPQueryURIParams[aKeyName ] :=  TlkJSONbase.create(aValue);
                                 raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtClass');
                                exit;
                        end;
                                //**************************************************
                                // :: ::
                
                vtAnsiString:
                        begin
                                SValidKeyType := string(aValue.VAnsiString);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // :: 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.Create(string(aValue));
                                 
                        end;

                vtCurrency:
                        begin
                                SValidKeyType := CurrToStr(aValue.VCurrency);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtCurrency');
                                exit;
                        end;

                vtVariant:
                        begin
                                SValidKeyType := string(aValue.VVariant);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtVariant');
                                exit;
                        end;
                        else
                        begin
                                SValidKeyType := '--ERR-- '+ inttostr( Variants.VarType(aValue));
                                WSWriteEventLog(' ERROR :: Access Index idx : '+inttostr(aKeyNameidx)+ ' :: ' +SValidKeyType+' :: Check :: '
      + aKeyName +';; Done  : ;; ');
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: '+SValidKeyType);
                                exit;
                                
                        end;
                end;

                        WSWriteEventLog('Access Index idx : '+inttostr(aKeyNameidx)+ ' :: ' +SValidKeyType+' :: Check :: '
      + aKeyName +';; Done  : ;; ');

    
                // :=  _WSHTTPQueryURIParams[aKeyName ] as TWSObjectJSONObject;
                WSWriteEventLog(' +++ WSPropsWritingByIdx :: Do Writing  .... '+aKeyName);
                
                // _WSHTTPQueryURIParams[aKeyName ] := aValue;
                WSWriteEventLog(' +++WSPropsWritingByIdx :: END :: Writed :: '+aKeyName+'::'+_WSHTTPQueryURIParams[aKeyName ].value);
                WSWriteEventLog(' +++WSPropsWritingByIdx :: END ::  Do Writing  .... '+aKeyName);

        except
                on ErrVariantCastAssign : Exception do
                begin
                        WSWriteEventLog('Exception Errorr :: variant cast :: '+SValidKeyType);
                        WSWriteEventLog('Exception Errorr lor de l Access a l Index idx('+inttostr(aKeyNameidx)+') ::'+ErrVariantCastAssign.Message+'::');
                
                end;
        end;

end;

//**************************************************
//**************************************************

procedure TWSRESTClient.WSPropsWriting(aKeyName: Variant; aValue: Variant) ;
var SValidKeyType, SValidValueType : String;
var iConvertCast : Integer;
begin
        try
                iConvertCast := 0;
                
                if not assigned(_WSHTTPQueryURIParams)  then
                begin
                    _WSHTTPQueryURIParams := TWSArrayObject.create();
                end;

                if not assigned(_WSUrlParametersPropsArrayIdxKeys)  then
                begin
                    _WSUrlParametersPropsArrayIdxKeys := TWSArrayObject.create();
                end;

                if _WSUrlParametersPropsArrayIdxKeys.Count = 0 then
                begin
                    raise TEWSTechInternalRuntimeErrorException.create(' Exception Error :: Aucune Propriete n est disponible :: Verifier la declaration de properties :: Access Index idx : '+aKeyName);
                end;
                WSWriteEventLog('Access Index idx : '+aKeyName);

     
                case Variants.VarType(aValue) of
                varByte: // type 17
                        begin
                                SValidKeyType := 'Byte'; // IntToStr(aValue.varByte);
                                iConvertCast := Integer(aValue) ;
                                WSWriteEventLog(' type :: '+SValidKeyType+' :: assign keyidx :: '+aKeyName);
                                 
                                _WSHTTPQueryURIParams[ aKeyName ] :=   TWSNumber.Create(iConvertCast);
                        end;

                vtInt64:
                        begin
                                SValidKeyType := IntToStr(aValue.VInteger);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                iConvertCast := integer(aValue.VInteger);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] :=    TWSNumber.Create(integer(iConvertCast));

                        end;
                vtInteger:
                        begin
                                SValidKeyType := IntToStr(aValue.VInteger);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                iConvertCast := integer(aValue.VInteger);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSNumber.Create( integer(iConvertCast) );
                        end;

                vtBoolean: 
                        begin
                                SValidKeyType := aValue.VBoolean;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] :=  TWSBoolean.Create( boolean(aValue) );
                        end;

                vtChar:
                        begin
                                SValidKeyType := aValue.VChar;
                                WSWriteEventLog(' type :: '+SValidKeyType);
 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.Create(string(aValue));
 
                        end;
                        
                vtExtended:
                        begin
                                SValidKeyType := FloatToStr(aValue.VExtended);
                                WSWriteEventLog(' type :: '+SValidKeyType);
 
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtExtended');
                                exit;
                        end;
                        
                vtString:
                        begin
                                SValidKeyType := aValue.VString;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.create(string(aValue));
                                 
                        end;
                varString: // type 256
                        begin
                                SValidKeyType := 'string';//aValue.varString;
                                WSWriteEventLog(' type :: '+SValidKeyType);       
                                 
                                _WSHTTPQueryURIParams[ aKeyName ] := TWSString.create(string(aValue));
                                 
                        end;
                vtPChar:
                        begin
                                SValidKeyType := aValue.VPChar;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // _WSHTTPQueryURIParams[aKeyName ] :=
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtExtended');
                                exit;
                        end;

                vtObject:
                        begin
                                SValidKeyType := aValue.VObject.ClassName;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // _WSHTTPQueryURIParams[aKeyName ] :=
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtObject');
                                exit;
                        end;

                vtClass:
                        begin
                                SValidKeyType := aValue.VClass.ClassName;
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                 //  _WSHTTPQueryURIParams[aKeyName ] :=  TlkJSONbase.create(aValue);
                                 raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtClass');
                                exit;
                        end;
                                //**************************************************
                                // :: ::
                
                vtAnsiString:
                        begin
                                SValidKeyType := string(aValue.VAnsiString);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                // :: 
                                _WSHTTPQueryURIParams[aKeyName ] := TWSString.Create(string(aValue));
                                 
                        end;

                vtCurrency:
                        begin
                                SValidKeyType := CurrToStr(aValue.VCurrency);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtCurrency');
                                exit;
                        end;

                vtVariant:
                        begin
                                SValidKeyType := string(aValue.VVariant);
                                WSWriteEventLog(' type :: '+SValidKeyType);
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: vtVariant');
                                exit;
                        end;
                        else
                        begin
                                SValidKeyType := '--ERR-- '+ inttostr( Variants.VarType(aValue));
                                WSWriteEventLog(' ERROR :: Access Index idx : '+aKeyName+ ' :: ' +SValidKeyType+' :: Check :: '
      + aKeyName +';; Done  : ;; ');
                                raise exception.create('Unimplemented :: WSPropsWritingByIdx :: '+SValidKeyType);
                                exit;
                                
                        end;
                end;

                        WSWriteEventLog('Access Index idx : '+aKeyName+ ' :: ' +SValidKeyType+' :: Check :: '
      + aKeyName +';; Done  : ;; ');

    
                // :=  _WSHTTPQueryURIParams[aKeyName ] as TWSObjectJSONObject;
                WSWriteEventLog(' +++ WSPropsWritingByIdx :: Do Writing  .... '+aKeyName);
                
                // _WSHTTPQueryURIParams[aKeyName ] := aValue;
                WSWriteEventLog(' +++WSPropsWritingByIdx :: END :: Writed :: '+aKeyName+'::'+_WSHTTPQueryURIParams[aKeyName ].value);
                WSWriteEventLog(' +++WSPropsWritingByIdx :: END ::  Do Writing  .... '+aKeyName);

        except
                on ErrVariantCastAssign : Exception do
                begin
                        WSWriteEventLog('Exception Errorr :: variant cast :: '+SValidKeyType);
                        WSWriteEventLog('Exception Errorr lor de l Access a l Index idx('+aKeyName+') ::'+ErrVariantCastAssign.Message+'::');
                
                end;
        end;
     
end;
//**************************************************
//**************************************************

procedure TWSRESTClient.WSPropsWritingObject(aKeyName: Variant; aValue: TObject) ;
begin
    
    
    _WSHTTPQueryURIParams[ aKeyName ] := TWSObject(aValue) ;
    
end;
//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************


//**********************************************************************
// 
//  FUNCTION:     GetCurrentUserAndDomain - This function looks up
//                the user name and domain name for the user account
//                associated with the calling thread.
//
//  PARAMETERS:   szUser - a buffer that receives the user name
//                pcchUser - the size, in characters, of szUser
//                szDomain - a buffer that receives the domain name
//                pcchDomain - the size, in characters, of szDomain
// 
//  RETURN VALUE: TRUE if the function succeeds. Otherwise, FALSE and
//                GetLastError() will return the failure reason.
// 
//                If either of the supplied buffers are too small, 
//                GetLastError() will return ERROR_INSUFFICIENT_BUFFER
//                and pcchUser and pcchDomain will be adjusted to
//                reflect the required buffer sizes.
//
//**********************************************************************

function GetCurrentUserAndDomain(szUser : PChar;   pcchUser : DWORD;
       szDomain : PChar;  pcchDomain: DWORD) : boolean;
{$IFDEF FPC}
begin
    result := true;
end;
{$ELSE}
var
   fSuccess : boolean;
   hToken   : THandle;
   ptiUser  : PSIDAndAttributes;
   cbti     : DWORD;
   snu      : SID_NAME_USE;

begin
   ptiUser := nil;
   Result := false;

   try
      // Get the calling thread's access token.
      if (not OpenThreadToken(GetCurrentThread(), TOKEN_QUERY, TRUE,
            hToken)) then

       begin
         if (GetLastError() <> ERROR_NO_TOKEN) then
            exit;

         // Retry against process token if no thread token exists.
         if (not OpenProcessToken(GetCurrentProcess(), TOKEN_QUERY,
               hToken)) then
            exit;
       end;

      // Obtain the size of the user information in the token.
      if (GetTokenInformation(hToken, TokenUser, nil, 0, cbti)) then

         // Call should have failed due to zero-length buffer.
         Exit

       else

         // Call should have failed due to zero-length buffer.
         if (GetLastError() <> ERROR_INSUFFICIENT_BUFFER) then
            Exit;


      // Allocate buffer for user information in the token.
      ptiUser :=  HeapAlloc(GetProcessHeap(), 0, cbti);
      if (ptiUser= nil) then
         Exit;

      // Retrieve the user information from the token.
      if ( not GetTokenInformation(hToken, TokenUser, ptiUser, cbti, cbti)) then
         Exit;

      // Retrieve user name and domain name based on user's SID.
      if ( not LookupAccountSid(nil, ptiUser.Sid, szUser, pcchUser,
            szDomain, pcchDomain, snu)) then
         Exit;

      fSuccess := TRUE;

   finally

      // Free resources.
      if (hToken > 0) then
         CloseHandle(hToken);

      if (ptiUser <> nil) then
         HeapFree(GetProcessHeap(), 0, ptiUser);
   end;

   Result :=  fSuccess;
end;
{$ENDIF}
//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************

procedure TWSRESTClient.WSWriteEventLogName(aFileLogPathName : widestring);
begin
      _wslogActivityToFileName 			:= ExtractFileName(aFileLogPathName);
      _wslogActivityToFileNameDirPath 		:= ExtractFileDir(aFileLogPathName);
      
        if (length(string(_wslogActivityToFileName))<5) then
        begin
                        __appendLogStack(' Exception::([WSLogFile] LogFileName not specified ... '+ inttostr(length(aFileLogPathName))+':'+aFileLogPathName+')');                                
                        raise TEWSTechErrorException.create( __LogStackGetLast() );
                exit;
        end;
        
        if ( not DirectoryExists( _wslogActivityToFileNameDirPath ) ) or ( length( _wslogActivityToFileNameDirPath )  <= 3 ) then
        begin
                // ******* C:/ ***** D:/ ***** %WINDOWSROOT% ***** Invalid Path ***********
                        __appendLogStack(' Exception:: ('+'[WSLogFile] Dir LogFileName not specified nor valid ...'+inttostr(length(_wslogActivityToFileNameDirPath))+')');                                
                       raise TEWSTechErrorException.create( __LogStackGetLast() );
                exit;
        end;    
end;
//**************************************************
//**************************************************

function TWSRESTClient.WSReadEventLogName() : widestring;
begin
        
        Result := widestring( ''+_wslogActivityToFileNameDirPath+''+ PathDelim  +''+ _wslogActivityToFileName+'' );
end;

//**************************************************
//**************************************************



// ******************************
// Formattage de la sortie LogFile
// ******************************
function TWSRESTClient.WSWriteEventLog(stextEvent : widestring): boolean;
begin
        try
                result := WSWriteEventLog(stextEvent, kWSEventLogType_EWSUnknowFormattingOutput);
        // ***********************
        except
        on ErrFileOperation: Exception do
                begin 
                        __appendLogStack(' Exception::ErrFileOperation[1]  ('+ErrFileOperation.Message+')');                                
                       raise TEWSTechErrorException.create( __LogStackGetLast() );
                end;
        end;
end;

//**************************************************
//**************************************************


function TWSRESTClient.WSWriteEventLog(stextEvent : widestring; eventType : TWSEventLogType): boolean;
var sFormatLog                     : TWSStringStream;
var sFormattedLogString         : TWSStringStream;
var sDirPathLog                     : widestring;

var sEventTypeName            : string;
var sDateTimeEvent              : TDateTime;
var sDateTimeEventString : string;
begin

        try
                sFormattedLogString :=  TWSStringStream.Create('');
                sFormatLog              :=  TWSStringStream.Create('');
                sDateTimeEvent          :=   Now();
                 
                sDateTimeEventString :=  StringReplace(DateToStr(sDateTimeEvent)+'_'+TimeToStr(sDateTimeEvent), '/', '.',[rfReplaceAll, rfIgnoreCase]);
                        
                
                sEventTypeName := '';
         
                
                case eventType of
                        kWSEventLogType_INFO:
                                sEventTypeName := 'INFO';
                        kWSEventLogType_USERINFO:
                                sEventTypeName := 'USERINFO';
                        kWSEventLogType_WARNING:
                                sEventTypeName := 'WARNING';
                        
                        kWSEventLogType_ERROR:
                                sEventTypeName := 'ERROR';
                        
                        kWSEventLogType_EWSHTTPErrorException:
                                sEventTypeName := 'TEWSHTTPErrorException';
                        
                        kWSEventLogType_EWSValidationErrorException:
                                sEventTypeName := 'EWSValidationErrorException';
                        kWSEventLogType_EWSValidationParseQueryParamsErrorException:
                                sEventTypeName := 'EWSValidationParseQueryParamsErrorException';
                        kWSEventLogType_EWSValidationParseQueryDataErrorException:
                                sEventTypeName := 'EWSValidationParseQueryDataErrorException';
                        kWSEventLogType_EWSValidationParseResponseErrorException:
                                sEventTypeName := 'EWSValidationParseResponseErrorException';
                        
                        kWSEventLogType_EGenericException:
                                sEventTypeName := 'EGenericException';
                        kWSEventLogType_EWSTechErrorException:
                                sEventTypeName := 'TEWSTechErrorException';
                        
                        kWSEventLogType_EWSTechInternalRuntimeErrorException:
                                sEventTypeName := 'TEWSTechInternalRuntimeErrorException';
                        
                        kWSEventLogType_EWSUnknowERRORException:
                                sEventTypeName := 'EWSUnknowERRORException';
                        else
                                sEventTypeName := '';
                end;
                
                if (length(sEventTypeName) >0) then
                        begin
                                sFormatLog.WriteString( Format('[Event::%s];;%s',[sEventTypeName,kWSFormatLogStringBase]));
                        end
                else
                        begin
                                sFormatLog.WriteString( Format('[%s];;%s',['Notice',kWSFormatLogStringBase]));
                        end;
                
                sFormattedLogString .WriteString( ';;*****************************;;'+chr(13)+chr(10)+Format( sFormatLog.AsString, [ sDateTimeEventString, stextEvent] )+chr(13)+chr(10)+';;*****************************;;' );
                
                 
                
        except
        on ErrFormattingLogStringOperation: Exception do
                begin
                        __appendLogStack(' Exception:: ('+ ErrFormattingLogStringOperation.Message+')');                                
                       raise TEWSTechErrorException.create( __LogStackGetLast() );
                end;
        end;
        // ***********************	
        // ***********************	
        try
                // ***********************
                
                __appendLogStack(widestring(stextEvent));
                
                if( not _wslogActivityToFileScreen) and ( not _wslogActivityToFile) then 
                        begin
                                result := true;
                        end
                else
                        begin
                                if( _wslogActivityToFileScreen) then 
                                begin
                                        writeln(widestring(sFormattedLogString.AsString));
                                        result := true;
                                end;
                         
                                if( not _wslogActivityToFile) then 
                                        begin
                                                result := true;
                                        end
                                else
                                        begin
                                                Result := __appendLogFile(widestring(sFormattedLogString.AsString));
                                        end;
                        
                        end;
        // ***********************
        except
        on ErrFileOperation: Exception do
                begin
                        __appendLogStack(' Exception:: ErrFileOperation[2] ('+ ErrFileOperation.Message+')');                                
                       raise TEWSTechErrorException.create( __LogStackGetLast() );
                end;
        end;
        
end;
//**************************************************
//**************************************************
function TWSRESTClient.__LogStackGetLast() : widestring;
begin
      if (__WSLOGCALLSTACK.count = 0) then
      begin
              result := '';
              exit;
      end;
      result :=   __WSLOGCALLSTACK.Strings[ integer(__WSLOGCALLSTACK.count -1) ];
end;
//**************************************************
//**************************************************
function TWSRESTClient.__appendLogStack(stextEvent : widestring):boolean;
begin
      __WSLOGCALLSTACK.add(stextEvent);
      result :=   (true);
end;
 function TWSRESTClient.WSRaiseLogStack(aMessageString :string; aRaiseError: boolean = true): boolean;
begin
        
        if(length(aMessageString)>2) then
        begin
            WSWriteEventLog(aMessageString, kWSEventLogType_EWSTechErrorException);
        end
        else
        begin
             WSWriteEventLog(__LogStackGetLast(), kWSEventLogType_EWSTechErrorException);
        end;
        
        if(aRaiseError) then
        begin
                raise TEWSTechErrorException.create( __LogStackGetLast() ); 
        end;
        result :=   (true);
end;


//**************************************************
//**************************************************
function TWSRESTClient.__appendLogFile(stextEvent : widestring):boolean;
var swsLogFilePtr                   : TextFile;
var sDateTimeEvent               : TDateTime;
var sDateTimeEventString        : string;
begin
        
        sDateTimeEventString :=  '';
        
        if (length(stextEvent)<5) then
        begin
               __appendLogStack(' Exception:: ('+ '[WSLogFile] Empty Event not Allowed not specified ...'+')');                                
               raise TEWSTechErrorException.create( __LogStackGetLast() ); 
               exit;
        end;
        
        // *******************
        try
               // Try to open the [_wslogActivityToFileName].log.csv file for writing to
               if (AnsiPos('%s_%s_%s_%s_%s', _wslogActivityToFileName) >0 ) then
               begin
                       sDateTimeEvent :=   Now();
                       _wslogActivityToFileName := format(_wslogActivityToFileName,[
                               
                               _programName,
                               
                               StringReplace(StringReplace(_WS_BASEHTTP_uri_baseentrypoint, '/', '_',[rfReplaceAll, rfIgnoreCase]), ':', '_',[rfReplaceAll, rfIgnoreCase]),
        
                               StringReplace(StringReplace(_WSMethodName,  '/', '_',[rfReplaceAll, rfIgnoreCase]), ':', '_',[rfReplaceAll, rfIgnoreCase]),
        
                               StringReplace(DateToStr(sDateTimeEvent), '/', '_',[rfReplaceAll, rfIgnoreCase]),
                               StringReplace(TimeToStr(sDateTimeEvent), ':', '.',[rfReplaceAll, rfIgnoreCase]) 
                       ]);
                       
               end
               else if (AnsiPos('%s_%s_%s_%s', _wslogActivityToFileName) >0 ) then
               begin
                       sDateTimeEvent :=   Now();
                       _wslogActivityToFileName := format(_wslogActivityToFileName,[
        
                               StringReplace(StringReplace(_WS_BASEHTTP_uri_baseentrypoint, '/', '_',[rfReplaceAll, rfIgnoreCase]), ':', '_',[rfReplaceAll, rfIgnoreCase]),
        
                               StringReplace(StringReplace(_WSMethodName,  '/', '_',[rfReplaceAll, rfIgnoreCase]), ':', '_',[rfReplaceAll, rfIgnoreCase]),
        
                               StringReplace(DateToStr(sDateTimeEvent), '/', '_',[rfReplaceAll, rfIgnoreCase]),
                               StringReplace(TimeToStr(sDateTimeEvent), ':', '.',[rfReplaceAll, rfIgnoreCase]) 
                       ]);
                       
               end
               else if (AnsiPos('%s_%s_%s', _wslogActivityToFileName) >0 ) then
               begin
                       sDateTimeEvent :=   Now();
                       _wslogActivityToFileName := format(_wslogActivityToFileName,[ 
                               
                               StringReplace(StringReplace(_WSMethodName,  '/', '_',[rfReplaceAll, rfIgnoreCase]), ':', '_',[rfReplaceAll, rfIgnoreCase]),
        
                               StringReplace(DateToStr(sDateTimeEvent), '/', '_',[rfReplaceAll, rfIgnoreCase]),
                               StringReplace(TimeToStr(sDateTimeEvent), ':', '.',[rfReplaceAll, rfIgnoreCase]) 
                       ]);
                       
               end
               else if (AnsiPos('%s_%s', _wslogActivityToFileName) >0 ) then
               begin
                       sDateTimeEvent :=   Now();
                       _wslogActivityToFileName := format(_wslogActivityToFileName,[ 
                               StringReplace(DateToStr(sDateTimeEvent), '/', '_',[rfReplaceAll, rfIgnoreCase]),
                               StringReplace(TimeToStr(sDateTimeEvent), ':', '.',[rfReplaceAll, rfIgnoreCase]) 
                       ]);
                       
               end
               else if (AnsiPos('%s', _wslogActivityToFileName) >0 ) then  
               begin
                       sDateTimeEvent :=   Now();
        
                       sDateTimeEventString :=  
                               StringReplace(DateToStr(sDateTimeEvent), '/', '_',[rfReplaceAll, rfIgnoreCase])
                               +'_'+
                               StringReplace(TimeToStr(sDateTimeEvent), ':', '.',[rfReplaceAll, rfIgnoreCase] );
                        
                       
                       _wslogActivityToFileName := format(_wslogActivityToFileName,[sDateTimeEventString]);
               end;
               
               
               AssignFile(swsLogFilePtr, ''+_wslogActivityToFileNameDirPath+''+ PathDelim  +''+ _wslogActivityToFileName);
               
               if (not FileExists(  ''+_wslogActivityToFileNameDirPath+''+ PathDelim  +''+ _wslogActivityToFileName+'' )) then
               begin
                       ReWrite(swsLogFilePtr);
                       __appendLogStack(  string( ' Create Log File  :: '+ ''+_wslogActivityToFileNameDirPath+''+ PathDelim  +''+ _wslogActivityToFileName+'' ) ) ;
                                        
                       Writeln(swsLogFilePtr, __LogStackGetLast()  );
                       
               end;
               Append(swsLogFilePtr);
        
               // Write this final line
               Writeln(swsLogFilePtr, stextEvent);
               
               // Empty system file Buffer 
               flush(swsLogFilePtr);
               
               // Close the file
               CloseFile(swsLogFilePtr);
        
               result := true;
        except
        on ErrFileOperation: Exception do
               begin
                       __appendLogStack('Exception::WriteLog::ErrFileOperation ('+ErrFileOperation.Message+')');                           
                       raise TEWSTechErrorException.create( __LogStackGetLast() );
                       exit;
               exit;
               end;
        end;
end;

//**************************************************
//**************************************************

function TWSRESTClient.WSGetLocalIP(): string;
type
        TaPInAddr = array [0..10] of PInAddr;
        PaPInAddr = ^TaPInAddr;
var
        phe: PHostEnt;
        pptr: PaPInAddr;
        Buffer: array [0..63] of char;
        i: Integer;
        GInitData: TWSADATA;
begin
        WSAStartup($101, GInitData);
        Result := '';
        
        GetHostName(Buffer, SizeOf(Buffer));
        
        phe :=GetHostByName(buffer);
        
        if phe = nil then Exit;
        
        pptr := PaPInAddr(Phe^.h_addr_list);
        i := 0;
        
        {$IFnDEF FPC}
        while pptr^[i] <> nil do
        begin
                result:=StrPas(inet_ntoa(TInAddr(pptr^[i]^)));
                Inc(i);
        end;
        {$ENDIF}
        WSACleanup;
end;
              
//**************************************************
//**************************************************

//**************************************************
//**************************************************


//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************
function TWSRESTClient.WS_func_explodeStr(sCharNeedle : string; sOriginalString : TWSString): TWSArrayObject;
begin
        Result := WS_func_explodeStr(sCharNeedle, sOriginalString.AsString);
end;

//**************************************************
//**************************************************
function TWSRESTClient.WS_func_explodeStr(sCharNeedle : string; sOriginalString : TWSStringStream): TWSArrayObject;
begin
        Result := WS_func_explodeStr(sCharNeedle, sOriginalString.AsString);
end;

//**************************************************
//**************************************************

function TWSRESTClient.WS_func_explodeStr(sCharNeedle : string; sOriginalString : widestring): TWSArrayObject;
var aReturnArray : TWSArrayObject;
var sFoundNeedleGroup : string;
var sCurrentFindableString : widestring;
var iCharIndex : integer;
var iNeedleGroupIndex : integer;
var iLastCharIndex : integer;
begin
        // **************************************
        iCharIndex := 0;
        iNeedleGroupIndex := 0;
        iLastCharIndex := length(sOriginalString);
        
        sCurrentFindableString := sOriginalString;
        sFoundNeedleGroup := '';
        
        aReturnArray := TWSArrayObject.create();
        
        // **************************************
       { while (iCharIndex <= iLastCharIndex) do
        begin
                
                sFoundNeedleGroup := sFoundNeedleGroup +''+sOriginalString[iCharIndex];
                
                if((sOriginalString[iCharIndex] = sCharNeedle)) then
                begin
                        aReturnArray.add(sFoundNeedleGroup);
                        sFoundNeedleGroup := '';
                end;
                Inc(iCharIndex);
        end;
       } 
        
        while ( iNeedleGroupIndex > 0) do
        begin
              iNeedleGroupIndex := AnsiPos( sCharNeedle , sCurrentFindableString);
              if ( iNeedleGroupIndex > 0) then
              begin
                        sFoundNeedleGroup             := LeftStr(sCurrentFindableString, iNeedleGroupIndex);
                        sCurrentFindableString          := RightStr(sCurrentFindableString, Length(sCurrentFindableString) - (iNeedleGroupIndex));
                        aReturnArray.add(sFoundNeedleGroup);
              end;
              sFoundNeedleGroup := '';
        end;
        
        // **************************************
        if(length(sCurrentFindableString) >0) then
        begin
                aReturnArray.add(sCurrentFindableString);
        end;
        Result := aReturnArray;
end;

//**************************************************
//**************************************************


//**************************************************
//**************************************************

function TWSRESTClient.WS_func_StringInArray(const Value : String;const ArrayOfString : Array of String) : Boolean;
var
 Loop : String;
begin
  for Loop in ArrayOfString do
  begin
    if Value = Loop then
    begin
       Result := True;
       Exit;
    end;
  end;
  result := false;
end;

//**************************************************
//**************************************************


//**************************************************
//**************************************************

function TWSRESTClient.WS_func_strrtrim(const s: string): string;
begin
        Result := WS_func_strrtrim(s, ' ');
end;
function TWSRESTClient.WS_func_strrtrim(const s: string; c: Char): string;
var
  First, Last: Integer;
begin
  First := 1;
  Last := Length(s);
  while (First <= Last) and (s[First] = c) do
    Inc(First);
  while (First < Last) and (s[Last] = c) do
    Dec(last);
  Result := Copy(s, First, Last - First + 1);
end;
function TWSRESTClient.WS_func_trim(const S: string): string;  
begin
      Result := WS_func_strrtrim(S , (' '));
end;

//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************



//**************************************************
//**************************************************


end.
