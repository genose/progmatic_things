Unit WSObject;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}


interface

  uses
{$IFnDEF FPC}
  VarCmplx, VarConv, Windows,
  // TObject List Definition dans Unit Contnrs
  Contnrs,
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
  Classes, SysUtils, StrUtils,  Variants, IdURI, WSMappedCustomVariantInvokeable, GNSTListHelper,  uLkJSON;
  // , GNSWSTypes;


    //**************************************************
    //**************************************************
    //**************************************************
    //**************************************************
    type TWSStringArray = array  of string;
    //*************************************************
    //*************************************************
   // type TWSObjectJSON 					= class(TObject);
    // type TWSObjectJSONObject 				= TWSObjectJSON;
    // :: type TWSObjectJSONBase 				= TlkJSONBase;
    // type TWSVariant 						= TWSObjectJSONBase;
    // type TWSArrayObjectReturn				= TlkJSONobject;
     
    //*************************************************
    //*************************************************
    type TWSArray = array of TObject;
    //*************************************************
    //*************************************************
    type TWSMultiArray = array of TWSArray;
    //*************************************************
    //*************************************************
    type TWSArrayStringValues = array of string;
    //*************************************************
    //*************************************************
    type TWSArrayArrayStrings = array of TWSArrayStringValues;
    //*************************************************
    //*************************************************
    type TWSIndexObject = class
    end;
    //TCustomVariantType, TlkJSONbase,
    //*************************************************
    //*************************************************
    //:: TWSMappedCustomVariantInvokeable
    type TWSObjectBase = class(TObject)
        constructor Create; overload;
        destructor Destroy(); overload;
    end;
      //*************************************************
      //*************************************************
    

type TWSObject  = class(TWSObjectBase)
    protected
            // var _key : string;
            // var _value : string;

    public
    // **********************
    // **********************
        constructor Create; overload;
        //constructor Create(aValue:string);	overload;
        //constructor Create(aValue:TObject);	overload;
    // **********************
    // **********************
        
        function asBoolean()                                             :Boolean; virtual;
        function asNumber()                                             :Integer; virtual;
        function asFloat()                                                 :Real; virtual;
        
        function asString()                                                :wideString;  virtual;
        function asStringJSON()                                         :wideString;  virtual;
        
        function fromStringJSON(aStringJSON : wideString)       :TWSObject;  virtual;
        
        
        function asArray()                                                 :TWSMultiArray; virtual;
        
        function asObject()                                               :TWSObject; virtual;
        function asObjectJSON()                                        :TWSObject; virtual;
        
        function ToString()                                                : WideString; virtual;
        function ToStringJson()                                                : WideString; virtual;
        function asVariantType()                                     :variant; overload;
        
        procedure WriteString(aValue:WideString); virtual;
        procedure Clear(); overload;     virtual;
        procedure Reset(); overload;     virtual;
        function GetCountValues(): Integer; virtual;
        
        
        procedure wSetValue(AValue: variant);
        function wGetValue(): variant;
        
        procedure SetKeyValue(const vColumnName: variant; const AValue: TWSObject);
        function GetKeyValue(const vColumnName: variant): TWSObject;
        
        //**************************************************
        property Value : variant read wGetValue write wSetValue;
        
        property Elements[const ColumnName: variant]: TWSObject read GetKeyValue write SetKeyValue;  default;
        //**************************************************
        property count		: integer read GetCountValues ;
        //**************************************************
end;
//**************************************************
//**************************************************
//**************************************************
//**************************************************
type TWSArrayObjectReturn       = TWSObject;
  //*************************************************
//**************************************************
type TWSVariant                       = TWSObject;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
type TWSObjectList = class(TObjectList)

    function GetItemTWS(Index: Integer): TWSObject; overload;
    procedure SetItemTWS(Index: Integer; AObject: TWSObject); overload;
    
    property Items[Index: Integer]: TWSObject read GetItemTWS write SetItemTWS; default;
end;

 //*************************************************
//**************************************************
//**************************************************
//**************************************************
type TWSString = class(TWSObject)
    protected
        var _value : TStringStream;
    
    public
        //procedure WriteString(); overload;
        procedure WriteString(aValue:widestring); overload;
        procedure WriteString(aValue:variant); overload;
        procedure WriteString(aValue:TWSObject); overload;
        
        
        procedure WriteDataStringStream(avalue:TStringStream);
        function ReadDataStringStream(): TStringStream;
        function GetSize(): integer;
        
        function ReadString(): WideString;
        function asString(): WideString;overload;	
        function toString(): WideString;overload;
        
        function asStringJSON(): WideString;overload;	
        function ToStringJSON(): WideString;overload;
        
        
        destructor Destroy(); overload;
        constructor Create(); overload;
        constructor Create(aValue : variant); overload;
        constructor Create(aValue : TStringStream); overload;
        constructor Create(aValue : WideString); overload;
        constructor Create(aValue : string); overload;
        
        procedure Clear(); overload;
        procedure Reset(); overload;
        
        procedure Append(sValueStr : wideString);
        
        property size		: integer read GetSize ;
        property value 		: WideString read ReadString write WriteString;
        property DataString : TStringStream read ReadDataStringStream write WriteDataStringStream;		
    
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSNumber = class(TWSString)
    protected
        var _dvalue : Double;
    public
        constructor Create(); overload;
        constructor Create(aValue : variant); overload;
        destructor Destroy(); overload;
        
        function toString(): WideString; overload;
        function asString(): WideString;overload;
        
        function asStringJSON(): WideString;overload;	
        function ToStringJSON(): WideString;overload;
        
        
        function asInteger(): integer;overload;
        function asDouble(): double;overload;
    // **************************************
        property value  : Double read _dvalue write _dvalue;
end;
    
//**************************************************
//**************************************************

type TWSBoolean = class(TWSNumber)
    protected
        var _bvalue : boolean;
    public
        constructor Create(); overload;
        constructor Create(aValue : boolean); overload;
        destructor Destroy(); overload;
        function toString(): WideString;overload;
        function asString(): WideString;overload;
        
        function asStringJSON(): WideString;overload;	
        function ToStringJSON(): WideString;overload;
        
        
        function asInteger(): integer;overload;
        // **************************************
        property value  : boolean read _bvalue write _bvalue;
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSStringStream = class(TWSString)
   
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSCustomArrayObject  = class(TWSObject)
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSArrayObject  = class(TWSCustomArrayObject)

    protected 
    var _key : TStringList;
    var _value : TWSObjectList;
    public
        const _kClassName :string = 'TWSArrayObject';
        //**************************************************
        //**************************************************
        function _GetClassName(): string;
        destructor Destroy(); overload;
        constructor Create();	overload;
        constructor Create(aValue : string);	overload;
        constructor Create(aValue : TWSObject);	overload;
        constructor Create(aValue : TWSArrayObject);	overload;
        //**************************************************
        //**************************************************
     
        function AsString()             : WideString; overload;
        function AsStringJSON()     : WideString; overload;
        function ToString()             : WideString; overload;
        function ToStringJSON()     : WideString; overload;
        
        function asArray()			:TWSMultiArray; overload;
        function asObject()			:TWSArrayObject; overload;
        function asObjectJSON()		:TWSArrayObject; overload;
        
        function eachAsArray( aFilterArray : array of string) : TWSArrayArrayStrings;
        
        
        procedure Clear(); overload;
        procedure Reset(); overload;
        
        //**************************************************
        //**************************************************
        function add(aValue : variant): integer;overload;
        function add(aKeyName : string; aValue : TWSVariant): integer;overload;
        function add(aKeyName : string; aValue : string): integer; overload;
        function add(aKeyName : string; aValue : TlkJSONbase): integer;overload;
        
        //**************************************************
        //**************************************************
        function GetCountValues(): Integer;
        
        function GetColumnValue(const ColumnName: string): TWSVariant; overload;
        // function GetColumnValue(Index: Integer): Pointer; overload;
        // procedure SetColumnValue(Index: integer; const Value: string);
        procedure SetColumnValue(const ColumnName: string; const Value: TWSVariant);
        
        procedure SetKeyValue(const vColumnName: variant; const AValue: TWSVariant);
        function GetKeyValue(const vColumnName: variant): TWSVariant;
        function GetKeyName(aKeyIndex: integer): string;
        
        function ArrayKeyExist(const vColumnName: variant): Boolean;
        //**************************************************
        property count		: integer read GetCountValues ;
        //**************************************************
        property capacity	: integer read GetCountValues ;
        //**************************************************
        // property ClassName : string read _GetClassName;
        // property Values[const ColumnName: string]: TWSObject read GetColumnValue write SetColumnValue;  default;
        //**************************************************
        property KeyNames[aKeyIndex : integer]: string read GetKeyName ;
        //**************************************************
        property Elements[const ColumnName: variant]: TWSVariant read GetKeyValue write SetKeyValue;  default;

        

end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSArrayVariant  = class(TWSArrayObject)
    private
        procedure wSetKeyValue(const vColumnName: variant; const AValue: variant); overload;
        function wGetKeyValue(const vColumnName: variant): variant; overload;
    public
        property Fields[const ColumnName: variant]: variant read wGetKeyValue write wSetKeyValue; 
end;
 
//**************************************************
//**************************************************
//**************************************************
//**************************************************

type TWSObjectJSONObject  = class(TWSArrayVariant)
    public
        class function ParseText(aStringJSON : wideString; aUrlDecodeText : boolean = false)       :TWSObjectJSONObject;
end;
//**************************************************
//**************************************************	
    
implementation
//**************************************************
//**************************************************	
//**************************************************
//**************************************************	

function WSObjWriteDebug(aStringDebug : string) : boolean;
begin
      {$IFDEF DEBUG_VERBOSE} writeln( ''+string(aStringDebug)+'' ); {$ENDIF}
      Result := true;
end;

//**************************************************
//**************************************************
constructor TWSObjectBase.Create;
begin
    WSObjWriteDebug(ClassName+':: base empty create :: TWSObjectBase ... ');
    inherited Create;
end;

//**************************************************
//**************************************************
destructor TWSObjectBase.Destroy();
begin
    WSObjWriteDebug(ClassName+':: base empty DESTROY :: TWSObjectBase ... ');
    inherited Destroy;
end;

//**************************************************
//**************************************************
constructor TWSObject.Create();
begin
   // ::  WSObjWriteDebug(ClassName+':: base empty create :: TWSObject ... ');
    inherited Create;
    // _key := string(''); // create();
    // _value := TWSObject(TObject.Create());
end;

//**************************************************
{ procedure TWSObject.Clear();
begin
    freeAndNil(_key);
    freeAndNil(_value);

    _key := string('0'); // create();
    _value := TWSObject.create();
end;}
{procedure TWSObject.Reset();
begin
    freeAndNil(_key);
    freeAndNil(_value);

    _key := string('0'); // create();
    _value := TWSObject.create();
end;}

//**************************************************
//**************************************************
procedure TWSObject.wSetValue( AValue: variant);
begin

    
end;

//**************************************************
//**************************************************
function TWSObject.wGetValue: variant;
begin
    Result := '';
end;
  
//**************************************************
//**************************************************       
procedure TWSObject.SetKeyValue(const vColumnName: variant; const AValue: TWSObject);
   var aValueTemp : TWSVariant;
begin
    WSObjWriteDebug(ClassName+':: base object SetKeyValue ... ');
    
    aValueTemp := self;
  {  if aValueTemp is TWSBoolean then
            begin
                    
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                    // Result :=  TWSBoolean(aValueTemp).ToString();
                    // exit;
            end
    else  if aValueTemp is TWSNumber then
            begin
                    
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                    //Result :=  TWSNumber(aValueTemp).ToString();
                    //exit;
            end
    else  if aValueTemp is TWSString then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).value);
                    //Result :=  TWSString(aValueTemp).ToString();
                    //exit;
            end
    else } if aValueTemp is TWSArrayObject then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSArrayObject :: REAAD :: '+TWSString(aValueTemp).toString);
                    TWSArrayObject(aValueTemp)[vColumnName] := AValue;
                    exit;
            end
    else { if aValueTemp is TWSVariant then
            begin
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSVariant');
                    // Result :=  TWSVariant(aValueTemp).ToString();
                    //exit;
            end
    else if aValueTemp is TWSObject then
            begin
                    WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is TWSObject');
                    //Result :=  TWSVariant(aValueTemp).ToString();
                    // exit;
            end
    else}
    begin
            WSObjWriteDebug('Base::SetKeyValue:::: aValueTemp is ????? ');
            raise Exception.create(' Base::SetKeyValue:: Unknow Object Type ...');
    end;
    
end;

//**************************************************
//**************************************************
function TWSObject.GetKeyValue(const vColumnName: variant): TWSObject;
   var aValueTemp : TWSVariant;
begin
    WSObjWriteDebug(ClassName+':: base object GetKeyValue ... ');
    
    aValueTemp := self;
    if aValueTemp is TWSBoolean then
            begin
                    
                    WSObjWriteDebug('Base::GetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                    Result :=  TWSBoolean(aValueTemp);
                    exit;
            end
    else  if aValueTemp is TWSNumber then
            begin
                    
                    WSObjWriteDebug('Base::GetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                    Result :=  TWSNumber(aValueTemp);
                    exit;
            end
    else  if aValueTemp is TWSString then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::GetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).value);
                    Result :=  TWSString(aValueTemp);
                    exit;
            end
    else  if aValueTemp is TWSArrayObject then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::GetKeyValue:::: aValueTemp is TWSArrayObject :: REAAD :: ('+inttostr(TWSArrayObject(aValueTemp).count)+')'+TWSArrayObject(aValueTemp).toString);
                    Result :=  TWSArrayObject(aValueTemp)[vColumnName];
                    exit;
            end
    else if aValueTemp is TWSVariant then
            begin
                    WSObjWriteDebug('Base::GetKeyValue:::: aValueTemp is TWSVariant');
                    Result :=  TWSVariant(aValueTemp);
                    exit;
            end
    else if aValueTemp is TWSObject then
            begin
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSObject');
                    Result :=  TWSVariant(aValueTemp);
                    exit;
            end
    else
    begin
            WSObjWriteDebug('Base::toString:::: aValueTemp is ????? ');
            raise Exception.create(' Base::toString:: Unknow Object Type ...');
    end;
    Result:=TWSVariant.Create();
end;

//**************************************************
//**************************************************
function TWSObject.asBoolean()		  :Boolean;
begin
    Result := false;
end;

//**************************************************
//**************************************************
function TWSObject.asNumber()			  :Integer;
begin
    Result := 0;
end;

//**************************************************
//**************************************************
function TWSObject.asFloat()			  :real;
begin
    Result := 0.0
end;

//**************************************************
//**************************************************
function TWSObject.asString()			  :wideString;
    var aValueTemp : TWSVariant;
begin
    WSObjWriteDebug(ClassName+':: base object as String ... ');
    
    aValueTemp := self;
    if aValueTemp is TWSBoolean then
            begin
                    
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                    Result :=  TWSBoolean(aValueTemp).ToString();
                    exit;
            end
    else  if aValueTemp is TWSNumber then
            begin
                    
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                    Result :=  TWSNumber(aValueTemp).ToString();
                    exit;
            end
    else  if aValueTemp is TWSString then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).value);
                    Result :=  TWSString(aValueTemp).ToString();
                    exit;
            end
    else  if aValueTemp is TWSArrayObject then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSArrayObject :: REAAD :: '+TWSArrayObject(aValueTemp).toString);
                    Result :=  TWSArrayObject(aValueTemp).ToString();
                    exit;
            end
    else if aValueTemp is TWSVariant then
            begin
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSVariant');
                    // Result :=  TWSVariant(aValueTemp).ToString();
                    //exit;
            end
    else if aValueTemp is TWSObject then
            begin
                    WSObjWriteDebug('Base::toString:::: aValueTemp is TWSObject');
                    // Result :=  TWSVariant(aValueTemp).ToString();
                    // exit;
            end
    else
    begin
            WSObjWriteDebug('Base::toString:::: aValueTemp is ????? ');
            raise Exception.create(' Base::toString:: Unknow Object Type ...');
    end;
    Result:=wideString(''); //:: wideString('**['+classname+'.class]**');
end;
//**************************************************
//**************************************************	

{function TWSObject.asString()			  :wideString;
begin
    WSObjWriteDebug(ClassName+':: base object as String ... ');
 if assigned(_value) then
  begin
    WSObjWriteDebug(ClassName+':: Value ::  '+_value.ClassName);
    Result := widestring(_value.ToString);
end
else
begin
    WSObjWriteDebug(ClassName+':: Value ::  NIL ... ');
    Result := '';
end;

end;  }
//**************************************************
//**************************************************
function TWSObject.asStringJSON()   :wideString;
begin
    Result :=     ToStringJson() ; 
end;

//**************************************************
//**************************************************
function TWSObject.ToStringJson()   : WideString; 
    var aValueTemp : TWSVariant;
begin
    WSObjWriteDebug(ClassName+':: base object as StringJson ... ');
    
    aValueTemp := self;
    if aValueTemp is TWSBoolean then
            begin
                    
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                    Result :=  TWSBoolean(aValueTemp).ToStringJson();
                    exit;
            end
    else  if aValueTemp is TWSNumber then
            begin
                    
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                    Result :=  TWSNumber(aValueTemp).ToStringJson();
                    exit;
            end
    else  if aValueTemp is TWSString then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).value);
                    Result :=  TWSString(aValueTemp).ToStringJson();
                    exit;
            end
    else  if aValueTemp is TWSArrayObject then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSArrayObject :: REAAD :: '+TWSArrayObject(aValueTemp).toString);
                    Result :=  TWSArrayObject(aValueTemp).ToStringJson();
                    exit;
            end
    else if aValueTemp is TWSVariant then
            begin
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSVariant');
                    // Result :=  TWSVariant(aValueTemp).ToString();
                    //exit;
            end
    else if aValueTemp is TWSObject then
            begin
                    WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is TWSObject');
                    // Result :=  TWSVariant(aValueTemp).ToString();
                    // exit;
            end
    else
    begin
            WSObjWriteDebug('Base::ToStringJson:::: aValueTemp is ????? ');
            raise Exception.create(' Base::ToStringJson:: Unknow Object Type ...');
    end;
    Result :=  widestring('');
end;

//**************************************************
//**************************************************
function TWSObject.fromStringJSON(aStringJSON : wideString)       :TWSObject;
begin
        Result :=  TWSObject.Create();
end;

//**************************************************
//**************************************************
function TWSObject.ToString(): WideString;
Begin
// :::: WSObjWriteDebug('TWSObject Base :: '+ClassName+'::ToString():');




    Result := asString();// :: wideString('**['+classname+'.class]**');
end;

//**************************************************
//**************************************************
function TWSObject.asObject()			  :TWSObject;
begin
    Result := TWSObject.Create();
end;

//**************************************************
//**************************************************
function TWSObject.asArray()			:TWSMultiArray;
    var iNbKeys :  integer;
    var iIndexKey : integer;
begin
    iNbKeys  := 0;//_key.count +1;
    
    //setlength(result, iNbKeys +1);
    {for  iIndexKey := 0 to iNbKeys do
    begin
        result[iIndexKey] :=  _value.Items[iIndexKey].Value;
    end;}
    
end;

//**************************************************
//**************************************************	
function TWSObject.asObjectJSON()		:TWSObject;
begin
    Result := TWSObject.Create();;
end;

//**************************************************
//**************************************************	
procedure TWSObject.WriteString(aValue:WideString);
begin
    // _value := WideString(aValue);
end;

//**************************************************
//**************************************************
procedure TWSObject.Clear(); 
begin
     
end;

//**************************************************
//**************************************************	
procedure TWSObject.Reset(); 
begin
     
end;

//**************************************************
//**************************************************
function TWSObject.GetCountValues(): integer; 
        var aValueTemp : TWSVariant;
        var ivalue : integer;
begin
    WSObjWriteDebug(ClassName+':: base object GetCountValues ... ');
    
    aValueTemp := self;
    if aValueTemp is TWSBoolean then
            begin
                    
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                    //Result :=  integer(TWSBoolean(aValueTemp).value);
                    //exit;
            end
    else  if aValueTemp is TWSNumber then
            begin
                    
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                    //ivalue := TWSNumber(aValueTemp).value;
                    //Result :=  (ivalue);
                    //exit;
            end
    else  if aValueTemp is TWSString then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).value);
                    Result :=  TWSString(aValueTemp).size;
                    exit;
            end
    else  if aValueTemp is TWSArrayObject then
            begin
                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSArrayObject :: REAAD :: '+TWSString(aValueTemp).toString);
                    Result :=  TWSArrayObject(aValueTemp).count;
                    exit;
            end
    else if aValueTemp is TWSVariant then
            begin
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSVariant');
                    // :: Result :=  TWSVariant(aValueTemp).count;
                    //exit;
            end
    else if aValueTemp is TWSObject then
            begin
                    WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is TWSObject');
                    //Result := 0;//:: TWSVariant(aValueTemp).count;
                    //exit;
            end
    else
    begin
            WSObjWriteDebug('Base::GetCountValues:::: aValueTemp is ????? ');
            raise Exception.create(' Base::GetCountValues:: Unknow Object Type ...');
    end;
    Result := 0;
end;
//**************************************************
//**************************************************
function TWSObject.asVariantType() : variant;
begin
        WSObjWriteDebug(ClassName+':: TWSObjectList :: asVariantType  ('+IntToStr(count)+') ... ');
        Result := '';
end;
//**************************************************
//**************************************************
//**************************************************
//**************************************************


function TWSObjectList.GetItemTWS(Index: Integer): TWSObject;
begin
    WSObjWriteDebug(ClassName+' :: TWSObjectList :: GetItemTWS ('+IntToStr(Index)+'/('+IntToStr(count)+') )  ... ');
    Result:=  TWSObject( GetItem(Index) );
end;

//**************************************************
//**************************************************
procedure TWSObjectList.SetItemTWS(Index: Integer; AObject: TWSObject); 
begin
    WSObjWriteDebug(ClassName+':: TWSObjectList :: SetItemTWS  ('+IntToStr(Index)+'/('+IntToStr(count)+') ... ');
    SetItem(Index, AObject) ;
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
constructor TWSString.Create();
begin
    inherited Create();
end;

//**************************************************
//**************************************************	
destructor TWSString.Destroy();
begin
    WSObjWriteDebug(ClassName+':: base empty DESTROY :: TWSString ... ');
    _value.Destroy();
    // _key.clear();
    inherited Destroy;
end;

//**************************************************
//**************************************************	
constructor TWSString.Create(aValue : string);
begin
    inherited Create();
    _value := TStringStream.create(aValue);
end;

///**************************************************
//**************************************************	
constructor TWSString.Create(aValue : WideString);
begin
    inherited Create();
    _value := TStringStream.create(aValue);
end;

//**************************************************
//**************************************************	
constructor TWSString.Create(aValue : variant);
begin
//	raise Exception.create('Error :: Umplemented :: TWSString.Create::variant');

    inherited Create();
    _value := TStringStream.create(VarToWideStr(aValue));
end;

//**************************************************
//**************************************************	
constructor TWSString.Create(aValue : TStringStream);
begin
    inherited Create();
    _value := TStringStream.create(aValue.DataString);
end;

//**************************************************
//**************************************************	
function TWSString.GetSize(): integer;
begin
    Result := integer(_value.size);
end;

//**************************************************
//**************************************************	
procedure TWSString.Append(sValueStr : wideString);
var sStringValue : wideString;
begin
    
    if(not assigned(_value) ) then
    begin
        _value := TStringStream.create('');
    end;
    // ::	_value :=
    sStringValue := wideString(_value.DataString);
    //_value.Seek(0, soFromBeginning);
    //_value.WriteString('----');
    _value.WriteString(wideString(sValueStr));
end;

//**************************************************
//**************************************************	
procedure TWSString.WriteString(aValue:WideString);
begin
        if(not assigned(_value) ) then
        begin
            _value := TStringStream.create('');
        end;
    // ::	_value :=
    _value.WriteString(aValue);
end;

//**************************************************
//**************************************************
procedure TWSString.WriteString(aValue:variant);
    var aTempTWSObject : TObject;
    var aTempString : string;
begin
            WSObjWriteDebug('TWSString.WriteString(aValue:variant);');
    // ::	_value :=
        if(not assigned(_value) ) then
        begin
            _value := TStringStream.create('');
        end;
        
        try
            if(Variants.VarType(aValue) > 260) then
            begin
                
                //raise Exception.create('TWSString.WriteString(aValue:variant);');
                //exit;
                
                
                //aTempTWSObject := TWSObject.Create();
                //aTempTWSObject := ((aValue).getFromVariant);
                WSObjWriteDebug('TWSString.WriteString(aValue:variant); CustumVariant Detected ....'+inttostr(Variants.VarType(aValue)));
                if( @(TVarDataRecordCustomTObjectInvokeable(aValue).aTObjectCustomInvokeablePTR)  = nil )    then
                begin
                    
                    _value.WriteString('****[OUPS]****');
                    
                  raise exception.create('Exception::TWSString.WriteString ( TWSMappedCustomVariantInvokeable.PTR Empty :: '+')');
                  exit;
                end;
                
                aTempTWSObject := (TVarDataRecordCustomTObjectInvokeable(aValue).aTObjectCustomInvokeablePTR^);
                
                WSObjWriteDebug('TWSString.WriteString(aValue:variant); CustumVariant cast Passed ....');
                
                
                if(not assigned(aTempTWSObject)) then
                begin
                    
                    _value.WriteString('****[OUPS]****');
                  raise exception.create('Exception::TWSString.WriteString ( TWSMappedCustomVariantInvokeable not assigned :: '+')');
                  exit;
                end;
                
                if(aTempTWSObject is TWSObject) then
                begin
                    aTempString := TWSObject(aTempTWSObject).ToString;    
                end
                else
                begin
                    aTempString := '****[OUPS]['+aTempTWSObject.ClassName+']****';
                end;
                 
                WSObjWriteDebug('TWSString.WriteString(aValue:variant); CustumVariant ('+aTempTWSObject.ClassName+') :: value ('+aTempString+') ....');
                
                _value.WriteString(aTempString);
                
                
                exit ; 
            end;
        except
        on Err: Exception do
            begin
                     // **************************
                     WSObjWriteDebug('Exception::TWSString.WriteString ( TWSMappedCustomVariantInvokeable convertion :: '+Err.Message+')');
                    raise exception.create('Exception::TWSString.WriteString ( TWSMappedCustomVariantInvokeable convertion :: '+Err.Message+')');
                    exit;
            end;
        end;
    try
            _value.WriteString(string(aValue));
    except
    on Err: Exception do
        begin
                 // **************************
                 WSObjWriteDebug('Exception::TWSString.ToString ( Variant Cast convertion :: '+Err.Message+')');
                raise exception.create('Exception::TWSString.ToString ( Variant Cast convertion :: '+Err.Message+')');
                exit;
        end;
    end;
    
end;

//**************************************************
//**************************************************	
procedure TWSString.WriteString(aValue:TWSObject);
begin
        WSObjWriteDebug('Write String Object :: '+string(aValue.ClassName));
        if(not assigned(_value) ) then
        begin
            _value := TStringStream.create('');
        end;  
    // _value.WriteString(string(aValue)); 
end;

//**************************************************
//**************************************************	
function TWSString.ReadString(): WideString;
begin
    if(not assigned(_value) ) then
    begin
        _value := TStringStream.create('');
    end; 
    Result := wideString(_value.DataString);
end;

//**************************************************
//**************************************************	
procedure TWSString.WriteDataStringStream(avalue:TStringStream);
begin
    _value := avalue;
end;

//**************************************************
//**************************************************	
function TWSString.ReadDataStringStream(): TStringStream;
begin
    Result := _value;
end;

//**************************************************
//**************************************************	
function TWSString.asString(): WideString;
begin
    Result := ToString();
end;

//**************************************************
//**************************************************	
function  TWSString.asStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//**************************************************
//**************************************************	
function  TWSString.ToStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//************************************************** 
//**************************************************
function TWSSTring.ToString(): WideString;
Begin
    // :::: WSObjWriteDebug('TWSString :: '+ClassName+'::ToString():');
    if(not assigned(_value) ) then
    begin
        _value := TStringStream.create('');
    end; 
    Result := wideString(_value.DataString);
end;

//**************************************************
//**************************************************
procedure TWSString.Clear(); 
  begin
    _value := TStringStream.create('');
end;			
procedure TWSString.Reset(); 
begin
    _value := TStringStream.create('');
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************	

destructor TWSNumber.Destroy();
begin
    WSObjWriteDebug(ClassName+':: base empty DESTROY :: TWSNumber ... ');
    _dvalue := 0.0;
    inherited Destroy;
end;

//**************************************************
//**************************************************	
constructor TWSNumber.Create();
begin
    inherited Create();
    _dvalue := 0.0;
end;

//**************************************************
//**************************************************	
constructor TWSNumber.Create(aValue : variant);
var iVariantType: integer;
begin
        inherited Create();
        _dvalue := 0.0;
        
        iVariantType := Variants.VarType(aValue);
        case iVariantType of
            
            //**************************************************
            //**************************************************
            // :: ::
            varOleStr:   	
                // :: ColumnName := VarToIntAsString(vColumnName);			
                // :: Copy(WideString(Pointer(vColumnName.VOleStr)), 1, MaxInt);
                 begin
                        try
                                _dvalue := strtofloat( widestring(aValue) );
                        except
                        on E: Exception do
                                begin
                                       
                                        raise exception.create('Exception::TWSNumber ( StringToNumber convertion :: '+E.Message+')');
                                        exit;
                                end;
                        end;
                end;
            //**************************************************
            varString:
                    begin
                            try
                                    _dvalue := strtofloat( string(aValue));
                            except
                            on E: Exception do
                                begin
                                       
                                        raise exception.create('Exception::TWSNumber ( StringToNumber convertion :: '+E.Message+')');
                                        exit;
                                end;
                            end;
                    end;
            //**************************************************
            varSingle:      	_dvalue := double(aValue);
            varDouble:   	_dvalue := double(aValue); 
            varSmallInt: 	_dvalue := double(aValue);
            varInteger:		_dvalue := double(aValue);
            varInt64:    	_dvalue := double(aValue);
            //**************************************************
            else
            //**************************************************
                raise Exception.create('TWSNumber::Exception :: Wrong variant descriptor ... ');
        end;
            
            
    end;

//**************************************************
//**************************************************
function TWSNumber.toString(): WideString;
Begin
    // :::: WSObjWriteDebug('TWSString :: '+ClassName+'::ToString():');
    if( round(_dvalue)  = _dvalue ) then
            begin
                    Result := inttostr(round(_dvalue));
            end
            else
            begin
                     Result := floattostr(_dvalue);
            end;
end;

//**************************************************
//**************************************************

    function TWSNumber.asString(): WideString;
    begin
            Result := toString(); 
    end;

//**************************************************
//**************************************************	
function  TWSNumber.asStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//**************************************************
//**************************************************	
function  TWSNumber.ToStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//**************************************************
//**************************************************

    function  TWSNumber.asInteger(): integer;
    begin
            Result := round(_dvalue);
    end;

//**************************************************
//**************************************************

    function  TWSNumber.asDouble(): double;
     begin
            Result := double(_dvalue);
    end;
    
//**************************************************
//**************************************************
//**************************************************
//**************************************************	
destructor TWSBoolean.Destroy();
begin
    WSObjWriteDebug(ClassName+':: base empty DESTROY :: TWSBoolean ... ');
 
     _bvalue := false;
    inherited Destroy;
end;

//**************************************************
//**************************************************	
constructor TWSBoolean.Create();
begin
        inherited Create();
        _bvalue := false;
end;

//**************************************************
//**************************************************	
constructor TWSBoolean.Create(aValue : boolean);
begin
        inherited Create();
        _bvalue := aValue;
end;

//**************************************************
//**************************************************
function TWSBoolean.asString(): WideString;
begin
        Result := toString(); 
end;

//**************************************************
//**************************************************
function TWSBoolean.ToString(): WideString;
Begin
    // :::: WSObjWriteDebug('TWSString :: '+ClassName+'::ToString():');
    if ( not _bvalue ) then
            begin
                    Result := wideString('false');
            end
            else
            begin
                    Result := wideString('true');
            end;
                    
end;

//**************************************************
//**************************************************	
function  TWSBoolean.asStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//**************************************************
//**************************************************	
function  TWSBoolean.ToStringJSON()   :wideString;
begin
    Result :=     ToString() ; 
end;

//**************************************************
//**************************************************
function  TWSBoolean.asInteger(): integer;
begin
        Result := integer(_bvalue);
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
function TWSArrayObject._GetClassName(): string;
begin
    Result := _kClassName;
end;

//**************************************************
//**************************************************	
destructor TWSArrayObject.Destroy();
begin
    WSObjWriteDebug(ClassName+':: base empty DESTROY :: TWSArrayObject ... ');
    
    _value.clear();
    _key.clear();
    
    _value.Destroy();
    _key.Destroy();
    
    inherited Destroy;
end;

//**************************************************
//**************************************************	
constructor TWSArrayObject.Create();
begin
    WSObjWriteDebug(ClassName+':: array empty create :: string ... ');
    //setlength(_key,0);
 //	setlength(_value,0);
    // _key[0] := string('0'); // create();
    // _value[0] := TWSString.create(aValue);
    _key 	:= TStringList.create();
    _value 	:= TWSObjectList.create();
end;

//**************************************************
//**************************************************	
constructor TWSArrayObject.Create( aValue : TWSObject);
begin
raise Exception.create('Error :: Umplemented :: TWSArrayObject.Create::TWSArrayObject');

    WSObjWriteDebug(ClassName+':: array create :: TObject ... ');
    
    _key 	:= TStringList.create();
    _value 	:= TWSObjectList.create();

    _key.Add('0');
    _value.Add( aValue );
    
    //_value[0].Assign(aValue);
end;

constructor TWSArrayObject.Create( aValue : TWSArrayObject);
begin
raise Exception.create('Error :: Umplemented :: TWSArrayObject.Create::TWSArrayObject');

    //setlength(_value,1);
    _key 	:= TStringList.create();
    _value 	:= TWSObjectList.create();

    _key.Add('0');		
    _value.Add(aValue);
end;

//**************************************************
constructor TWSArrayObject.Create(aValue:string);
begin
//raise Exception.create('Error :: Umplemented :: TWSArrayObject.Create::TWSArrayObject');

    WSObjWriteDebug(ClassName+':: array create :: string ... ');

    _key 	:= TStringList.create();
    _value 	:= TWSObjectList.create();

    _key.Add('0');	
    _value.Add(TWSString.create(aValue));
    
end;

//**************************************************
//**************************************************
function TWSArrayObject.asString()			  :wideString;
begin
    Result := ToString();
end;

//**************************************************
//**************************************************
function TWSArrayObject.ToString(): WideString;
var iKeyIndex : integer;
Begin
    WSObjWriteDebug('TWSArrayObject :: '+ClassName+'::ToString():(@'+inttostr(count)+')');
    Result := wideString('**[TWSArrayObject.class(@'+inttostr(count)+')]**');
    
    for iKeyIndex := 0 to  _value.Count-1 do
    begin
       WSObjWriteDebug('TWSArrayObject :: Child ::  '+KeyNames[iKeyIndex]+'::'+(Elements[iKeyIndex]).ClassName+'::ToString():');
        Result :=  Result + ''+'TWSArrayObject :: Child ::  '+KeyNames[iKeyIndex]+'::'+(Elements[iKeyIndex]).ClassName+'::ToString():';
    end;
    
end;

//**************************************************
//**************************************************
function TWSArrayObject.AsStringJSON(): WideString;
Begin
    Result := ToStringJSON();
end;

//**************************************************
//**************************************************
function TWSArrayObject.ToStringJSON(): WideString;
Begin
    WSObjWriteDebug('TWSArrayObject :: '+ClassName+'::ToString():');
    Result := wideString('{"Description":"**[TWSArrayObject.class(@'+inttostr(count)+')]**"}');
end;

//**************************************************
//**************************************************	
function TWSArrayObject.asObject()			  :TWSArrayObject;
begin
    Result := TWSArrayObject.Create();
end;

//**************************************************
//**************************************************
function TWSArrayObject.asObjectJSON()		:TWSArrayObject;
begin
    Result := TWSArrayObject.Create();;
end;

//**************************************************
//**************************************************	
function TWSArrayObject.asArray()			:TWSMultiArray;
begin

end;

//**************************************************
//**************************************************
procedure TWSArrayObject.Clear(); 
    var iIndexKey : integer;
    var iIndexKeyCount : integer;
begin
    try
        iIndexKeyCount := _value.count;
        for iIndexKey := 0 to iIndexKeyCount do 
        begin
            if(_value.count>0) then
            begin
                _value.delete(0);
                _key.delete(0);
            end;
        end;
        
         
    except
    on E: Exception do
        begin
            // :::: WSObjWriteDebug('TWSArrayObject::Clear :: Exception :: '+E.Message);

        end;
    end;
    //raise Exception.create('Error :: Umplemented :: TWSArrayObject.Clear::');
end;

//**************************************************
//**************************************************	
procedure TWSArrayObject.Reset(); 
   var iIndexKey : integer;
   var iIndexKeyCount : integer;
begin
    try
        iIndexKeyCount := _value.count;
        for iIndexKey := 0 to iIndexKeyCount do 
        begin
            if(_value.count>0) then
            begin
                _value.delete(0);
            end;
        end;
        
        iIndexKeyCount := _key.count;
        for iIndexKey := 0 to iIndexKeyCount do 
        begin
            if(_value.count>0) then
            begin
                _value.Add(TWSArrayObjectReturn.create());
            end;
        end;
    except
    on E: Exception do
        begin
            // :::: WSObjWriteDebug('TWSArrayObject::Reset :: Exception :: '+E.Message);

        end;
    end;
        //raise Exception.create('Error :: Umplemented :: TWSArrayObject.Reset::');
end;

//**************************************************
//**************************************************
function TWSArrayObject.EachAsArray( aFilterArray : array of string) : TWSArrayArrayStrings;
    var sCurrentFilterName : string;
    var indexKey : integer;
    var indexKeyCount : integer;
    var iFilterIndex : integer;
    var iNbFilter : integer;
    var aObjectArray: TWSArrayObject;
    var aObject: TWSObject;
    var aObjectPtr: Pointer;
    var aPtrMultiDimResult : TWSMultiArray;
    var sObjectValue : string;
begin
    
    indexKeyCount   :=  _key.Count -1;
    
 
    
    if (indexKeyCount >0) then
    begin
        
        iNbFilter := high(aFilterArray);
        
        setlength(Result, indexKeyCount, iNbFilter );
        
        for indexKey := 0 to indexKeyCount do
        begin
           //  aObjectPtr := _value;
            {if(_value.Items[indexKey] is TWSArrayObject) then
            begin
                aObject := (_value.Items[indexKey]).toString();
            end
            else
            begin
                aObject := _value.Items[indexKey];
            end;}
            aObject := _value.Items[indexKey];
            // aObject := aObjectPtr.asArrayObject;
            if (iNbFilter >0) then 
            begin
                //setlength(Result[indexKey], iNbFilter +1);
                // sur chaque element stockee appliquer les filtres ;; extraire les donnes correspondante
                for iFilterIndex := 0 to iNbFilter  do
                begin
                    sCurrentFilterName  :=  aFilterArray[iFilterIndex];
                    sObjectValue := TWSObject(aObject[sCurrentFilterName]).toString();
                    Result[indexKey,iFilterIndex] := string(sObjectValue);
                end;
                
                end
            else
            begin
                //setlength(Result[indexKey], aObject.count+1); 
                //Result[indexKey]^ := aObject;
            end;
        end; 
    end;

end;

//**************************************************
//**************************************************
function TWSArrayObject.GetCountValues(): integer; 
begin
    //// :::: WSObjWriteDebug('TWSArrayObject::GetCountValues :: ');
    //// :::: WSObjWriteDebug('TWSArrayObject::GetCountValues ::  == '+inttostr(_key.count));
    Result := _key.count;
end;

//**************************************************
//**************************************************
function TWSArrayObject.ArrayKeyExist(const vColumnName: variant): Boolean;
    var iKeyIndex : integer;
    var indexKeyCount : integer;
    var aObject: TWSObject;
    var ColumnName : string;
    var iVariantType : integer;
begin
   
       Result := 	false ;
       try
           
            iVariantType := Variants.VarType(vColumnName);
            
            case iVariantType of
                    //**************************************************
                    varEmpty:   raise Exception.create('ArrayKeyExist::Exception :: empty key variant ... ');
                    varNull:	raise Exception.create('ArrayKeyExist::Exception :: empty key variant ... ');
                    //**************************************************
                    //**************************************************
                    // :: ::
                    varOleStr:   	
                    // :: ColumnName := VarToIntAsString(vColumnName);			
                    // :: Copy(WideString(Pointer(vColumnName.VOleStr)), 1, MaxInt);
                    ColumnName := widestring(vColumnName );
                    //**************************************************
                    varString:		ColumnName := string(vColumnName);
                    //**************************************************
                    varSingle:   	ColumnName := inttostr(Round(vColumnName));
                    varDouble:   	ColumnName := inttostr(Round(vColumnName)); 
                    varSmallInt: 	ColumnName := inttostr(integer(vColumnName));
                    varInteger:		ColumnName := inttostr(integer(vColumnName));
                    varInt64:    	ColumnName := inttostr(integer(vColumnName));
                    //**************************************************
            else
                begin
                        //**************************************************
                        raise Exception.create('ArrayKeyExist::Exception :: Wrong key variant descriptor ... ');
                end;
            end;
           
           
           // :::: WSObjWriteDebug('TWSArrayObject::ArrayKeyExist :: '+ColumnName);
           
           indexKeyCount := _key.count -1; 

           iKeyIndex := _key.IndexOf(ColumnName);
           if not (iKeyIndex = -1) then
           begin
                result := true ;
                // :::: WSObjWriteDebug('TWSArrayObject::ArrayKeyExist :: Exist :: '+ColumnName);
                exit;
           end;
           // :::: WSObjWriteDebug('TWSArrayObject::ArrayKeyExist :: Not Exist :: '+ColumnName);
               
           Result := 	false ;
       except
       on E: Exception do
           begin
               // :::: WSObjWriteDebug('TWSArrayObject::ArrayKeyExist :: Exception :: '+ColumnName+'::'+E.Message);

           end;
       end;
end;
//**************************************************
//**************************************************
procedure TWSArrayObject.SetKeyValue(const vColumnName: variant; const AValue: TWSVariant);
    var ColumnName : string;
    var stTempString : string;
    var aValueIdx : integer;
    var iNewValueIndex : integer;
    var  iVariantType : integer;
    var iKeyIndex : integer;
begin
        try
             stTempString := '****'; 
             iNewValueIndex := (-1);
             // inherited SetValue(AValue);
             // :::: WSObjWriteDebug('TWSArrayObject::SetKeyValue :: ');
             iVariantType := Variants.VarType(vColumnName);
             case iVariantType of
                     //**************************************************
                     varEmpty:   raise Exception.create('ArrayKeyExist::Exception :: empty key variant ... ');
                     varNull:	raise Exception.create('ArrayKeyExist::Exception :: empty key variant ... ');
                     //**************************************************
                     // :: ::
                     varOleStr:   	
                     // :: ColumnName := VarToIntAsString(vColumnName);			
                     // :: Copy(WideString(Pointer(vColumnName.VOleStr)), 1, MaxInt);
                     ColumnName := widestring(vColumnName );
                     //**************************************************
                     varString:		ColumnName := string(vColumnName);
                     //**************************************************
                     varSingle:   	ColumnName := inttostr(Round(vColumnName));
                     varDouble:   	ColumnName := inttostr(Round(vColumnName));
                     varSmallInt: 	ColumnName := inttostr(integer(vColumnName));
                     varInteger:		ColumnName := inttostr(integer(vColumnName));
                     varInt64:    	ColumnName := inttostr(integer(vColumnName));
                     //**************************************************
             else
                //**************************************************
                    raise Exception.create('SetKeyValue::Exception :: Wrong key variant descriptor ... ');
                                        // inttostr(vColumnName.VType));
             end;
           
           
            // :::: WSObjWriteDebug('TWSArrayObject::SetKeyValue :: '+ColumnName);
            
            if not assigned(aValue) then
            begin
                raise Exception.create('TWSArrayObject::SetKeyValue::Exception :: UNAssigned NULL PTR aValue variant ... ');
            end;
            if (aValue = nil) then
            begin
                raise Exception.create('TWSArrayObject::SetKeyValue::Exception :: empty/NULL PTR aValue variant ... ');
            end;
            { case (aValue) of
                //**************************************************
                varEmpty:   raise Exception.create('TWSArrayObject::SetKeyValue::Exception :: empty/NULL aValue variant ... ');
                varNull:	raise Exception.create('TWSArrayObject::SetKeyValue::Exception :: NULL aValue variant ... ');
                //**************************************************
                //**************************************************
            end;
            }
            if (not ArrayKeyExist(ColumnName)) then
            begin
            
                _key.Add(ColumnName); // create();
                iNewValueIndex := _value.Add(TWSArrayObjectReturn.create());
                // :::: WSObjWriteDebug('TWSArrayObject::SetColumnValue :: Not Exist :: Insert :: '+ColumnName+'::@'+inttostr(iNewValueIndex));
            end;
            iKeyIndex := _key.IndexOf(ColumnName);
            // :::: WSObjWriteDebug('TWSArrayObject::SetColumnValue :: Reading :: count '+inttostr(_value.count));
            WSObjWriteDebug(':: Keyidx :: '+inttostr(iKeyIndex));
            { try
                for aValueIdx := 0 to _value.count-1 do
                begin
                    WSObjWriteDebug(':: STEP :: Value :: @'+inttostr(aValueIdx));
                    if assigned(_value.Items[aValueIdx]) then
                    begin
                        //_value.Items[aValueIdx].value;
                        WSObjWriteDebug(':: Value :: @'+inttostr(aValueIdx));
                    end;
                end;
            
            except
                on ErrVariantCastAssign : Exception do
                begin
                    //WSWriteEventLog('TWSArrayObject::SetKeyValue :: Exception Errorr :: variant cast :: '+SValidKeyType);
                    // :::: WSObjWriteDebug('TWSArrayObject::SetKeyValue :: Exception STEP Errorr vVariant ::'+ErrVariantCastAssign.Message+'::');
                end;
            end;
            }
            
           WSObjWriteDebug(':::: Assign new VALUE ... ');
           _value.Items[iKeyIndex] := avalue;
           
           
           {if iNewValueIndex <> -1 then
           begin
               WSObjWriteDebug(':::: Assign new ... ');
               // _value.Items[iNewValueIndex] :=
               
                   if aValue is TWSString then
                   begin			
                       WSObjWriteDebug(':::: aValue is TWSString');
                       _value.add( TWSSring(avalue) );
                   end
                   else
                   begin
                       if aValue is TWSObject then
                       begin			
                           WSObjWriteDebug(':::: aValue is TWSObject');
                       end
                       else if aValue is TWSVariant then
                           begin			
                               WSObjWriteDebug(':::: aValue is TWSVariant');
                           end
                           else
                               begin
                                   WSObjWriteDebug(':::: aValue is ????? ');
                               end;
                           
                   end;
               
               
               
               _value.Exchange(iNewValueIndex,iNewValueIndex+1);
                _value.delete(iNewValueIndex+1);
               WSObjWriteDebug(':::: Reading NEW :: '+string(TWSString(_value.Items[iNewValueIndex]).value));
               
           end;}
           // TWSArrayObjectReturn().Add();
       
       
        except
            on ErrVariantCastAssign : Exception do
            begin
                //WSWriteEventLog('TWSArrayObject::SetKeyValue :: Exception Errorr :: variant cast :: '+SValidKeyType);
                // :::: WSObjWriteDebug('TWSArrayObject::SetKeyValue :: Exception Errorr lor de l Access a l Index vVariant ::'+ErrVariantCastAssign.Message+'::');
            end;
        end;
       
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
function TWSArrayObject.GetKeyValue(const vColumnName: variant): TWSVariant; 
    var ColumnName : string;
    var stTempString : string;
    var aValueTemp : TWSVariant;
    var iVariantType : integer;
    var iKeyIndex : integer;
begin
    try
        Result := TWSVariant.Create();
        // inherited SetValue(AValue);
        // :::: WSObjWriteDebug('TWSArrayObject::GetKeyValue :: ');
        iVariantType := Variants.VarType(vColumnName);
        case iVariantType of
            //**************************************************
            varEmpty:   raise Exception.create('TWSArrayObject::GetKeyValue::Exception :: empty key variant ... ');
            varNull:	raise Exception.create('TWSArrayObject::GetKeyValue::Exception :: empty key variant ... ');
            //**************************************************
            //**************************************************
            // :: ::
            varOleStr:   	
                // :: ColumnName := VarToIntAsString(vColumnName);			
                // :: Copy(WideString(Pointer(vColumnName.VOleStr)), 1, MaxInt);
                ColumnName := widestring(vColumnName );
            //**************************************************
            varString:		ColumnName := string(vColumnName);
            vtAnsiString:	ColumnName := string(vColumnName);
            //**************************************************
            varSingle:   	ColumnName := inttostr(Round(vColumnName));
            varDouble:   	ColumnName := inttostr(Round(vColumnName));
            varSmallInt: 	ColumnName := inttostr(integer(vColumnName));
            varInteger:		ColumnName := inttostr(integer(vColumnName));
            varInt64:    	ColumnName := inttostr(integer(vColumnName));
            //**************************************************
        else
            //**************************************************
                raise Exception.create('TWSArrayObject::GetKeyValue::Exception :: Wrong key variant descriptor ... ');
                                    // :: +inttostr((vColumnName).VType));
        end;
        
        
        
       iKeyIndex := _key.IndexOf(ColumnName);
       // :::: WSObjWriteDebug('TWSArrayObject::GetKeyValue :: '+ColumnName+'::@'+inttostr(iKeyIndex)+'::size::'+inttostr(_value.count));
        if ( iKeyIndex > (-1))then
        begin
            if ( _value.Items[iKeyIndex] <> nil ) then
            begin
                        
                    aValueTemp := _value.Items[iKeyIndex];
                    if aValueTemp is TWSBoolean then
                            begin			
                                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSString :: REAAD('+ColumnName+') :: '+TWSBoolean(aValueTemp).toString);
                                    Result :=  TWSBoolean( _value.Items[iKeyIndex] );
                            end
                    else  if aValueTemp is TWSNumber then
                            begin			
                                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSString :: REAAD('+ColumnName+') :: '+TWSNumber(aValueTemp).toString);
                                    Result :=  TWSNumber(_value.Items[iKeyIndex]);
                            end
                    else  if aValueTemp is TWSString then
                            begin			
                                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSString :: REAAD('+ColumnName+') :: '+TWSString(aValueTemp).value);
                                    Result :=  TWSString(_value.Items[iKeyIndex]);
                            end
                    else  if aValueTemp is TWSArrayObject then
                            begin			
                                    //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSArrayObject :: REAAD('+ColumnName+') :: '+TWSArrayObject(aValueTemp).ToString);
                                    Result :=  TWSArrayObject(_value.Items[iKeyIndex]);
                            end
                    else if aValueTemp is TWSVariant then
                            begin			
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSVariant('+ColumnName+')');
                                    Result :=  TWSArrayObjectReturn(_value.Items[iKeyIndex]);
                            end
                    else if aValueTemp is TWSObject then
                            begin			
                                    WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp is TWSObject('+ColumnName+')');
                                    Result :=  TWSArrayObjectReturn(_value.Items[iKeyIndex]);
                            end
                    else
                    begin
                            WSObjWriteDebug(' TWSArrayObject.GetKeyValue:::: aValueTemp('+ColumnName+') is ????? ');
                            raise Exception.create(' TWSArrayObject.GetKeyValue:: Unknow Object Type ...');
                            exit;
                    end;
                                                
                                 
                        
            
                { stTempString := string(TWSArrayObjectReturn(_value.Items[iKeyIndex]).FieldByIndex[0].SelfTypeName);
                // :::: WSObjWriteDebug('TWSArrayObject::GetKeyValue :: classname ::  '+string(stTempString));
                // :::: WSObjWriteDebug('TWSArrayObject::GetKeyValue :: classname count ::  '+inttostr(TWSArrayObjectReturn(_value.Items[iKeyIndex]).count));
                }
                //Result :=  TWSArrayObjectReturn(_value.Items[iKeyIndex]).FieldByIndex[0];
                end;
        end
        else
        begin
             WSObjWriteDebug('TWSArrayObject::GetKeyValue :: cant determine null classname :: or not Key ('+ColumnName+') ');
        end;
    
    except
        on ErrVariantCastAssign : Exception do
        begin
            //WSWriteEventLog('TWSArrayObject::GetKeyValue :: Exception Errorr :: variant cast :: '+SValidKeyType);
            // :::: WSObjWriteDebug('TWSArrayObject::GetKeyValue :: Exception Errorr lor de l Access a l Index vVariant ::'+ErrVariantCastAssign.Message+'::');
        end;
    end;
    
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
function  TWSArrayObject.add(aKeyName : string; aValue : TWSVariant): integer;
begin
        
        _key.Add(aKeyName); // create();
        _value.Add(aValue);
        
        result := _value.count ;
end;

//**************************************************
//**************************************************
function  TWSArrayObject.add(aKeyName : string; aValue : string): integer;
begin
        
        _key.Add(aKeyName); // create();
        _value.Add(TWSString.create(aValue));
        
        result := _value.count ;
end;

//**************************************************
//**************************************************	                   
function  TWSArrayObject.add(aKeyName : string; aValue : TlkJSONbase): integer;
    var aTransientObject : TWSObject;
         
    var aObjectJsonChildParsed : TlkJSONobject ;
    var aObjectJsonChildElement : TlkJSONbase ;
    var iJsonChildKey : integer;
    var iJsonChildCount : integer;
    var sSubElementKey: string;
    var  sFieldName :string;
    var iJsonFieldType : TlkJSONtypes;
       
begin
    
    
     if (aValue is TlkJSONobject ) then
        begin
                aTransientObject := TWSArrayObject.create();
               
                for iJsonChildKey:=0 to TlkJSONobject(aValue).Count-1 do
                begin
                    
                    aObjectJsonChildElement :=  TlkJSONobject(aValue).child[iJsonChildKey];
                    sSubElementKey := ''+inttostr(iJsonChildKey);
                     if(aObjectJsonChildElement is TlkJSONobjectmethod ) then
                    begin
                        sSubElementKey := TlkJSONobjectMethod(aObjectJsonChildElement).Name;
                        TWSArrayObject(aTransientObject).add(sSubElementKey,TlkJSONbase(TlkJSONobjectmethod(aObjectJsonChildElement).ObjValue));
                    end
                    else
                    begin
                        TWSArrayObject(aTransientObject).add(sSubElementKey, aObjectJsonChildElement);
                    end;
                end;
        end
        else
    
        if (aValue is TlkJSONlist ) then
        begin
            aTransientObject := TWSArrayObject.create();
            
             for iJsonChildKey:=0 to TlkJSONobject(aValue).Count-1 do
             begin
                 
                 aObjectJsonChildElement :=  TlkJSONList(aValue).child[iJsonChildKey];
                 sSubElementKey := ''+inttostr(iJsonChildKey);
                 if(aObjectJsonChildElement is TlkJSONobjectmethod ) then
                 begin
                     sSubElementKey := TlkJSONobjectMethod(aObjectJsonChildElement).Name;
                     TWSArrayObject(aTransientObject).add(sSubElementKey,TlkJSONbase(TlkJSONobjectmethod(aObjectJsonChildElement).ObjValue));
                end
                else
                begin
                      TWSArrayObject(aTransientObject).add(sSubElementKey, aObjectJsonChildElement);
                end;
                 
              
             end;
               
        end
        else if (aValue is TlkJSONnull ) then
        begin
               aTransientObject := TWSObject.create();
        end
        else  if (aValue is TlkJSONboolean ) then
        begin
               aTransientObject := TWSBoolean.create(aValue.value);
        end
        else  if (aValue is TlkJSONnumber ) then
        begin
               aTransientObject := TWSNumber.create(aValue.value);
        end
        else  if (aValue is TlkJSONstring ) then
        begin
               aTransientObject := TWSString.create(aValue.value);
        end
        else   if (aValue is TlkJSONobjectmethod ) then
        begin
                
                aTransientObject := TWSArrayObject.create();
                TWSArrayObject(aTransientObject).add(aKeyName, TlkJSONbase(TlkJSONobjectmethod(aValue).ObjValue));
                iJsonChildCount := TWSArrayObject(aTransientObject).count;
                 WSObjWriteDebug('TWSArrayObject.add:::: TlkJSONbase :: '+aKeyName+'::'+inttostr(iJsonChildCount));
                        _key.Add(aKeyName); // create();
                        _value.Add(TWSArrayObject(aTransientObject)[aKeyName]);
                        
                        result := _value.count ;
                        exit;
                
                //aObjectJsonChildElement  := TlkJSONbase(TlkJSONobjectmethod(aValue).ObjValue);
               // aTransientObject := TWSString.create(aObjectJsonChildElement.Value  );
               
               // TlkJSONbase(TlkJSONobjectmethod(aValue).ObjValue)
        end
        else   if (aValue is TlkJSONbase ) then
        begin
            
            
            
               
               // sFieldName :=  aValue.NameOf[iElementsJsonCurrentKey];
               iJsonChildCount :=   TlkJSONobject(aValue).count ; 
               
               aTransientObject := TWSString.create( (aValue).Value);
               
               
        end;
        
        _key.Add(aKeyName); // create();
        _value.Add(aTransientObject);
        
        result := _value.count ;
end;

//**************************************************
//**************************************************
function  TWSArrayObject.add(aValue : variant): integer;
    var iConvertCast: integer;
    var aObjectVariant : TWSVariant;
    var iVariantType : integer;
begin
            aObjectVariant := TWSVariant.create();
            iVariantType := Variants.VarType(aValue);
    case iVariantType of
    
    varByte: // type 17
        begin
            iConvertCast := Integer(aValue) ; 
            aObjectVariant :=   TWSNumber.Create(iConvertCast);
        end;

    vtInt64:
        begin
            iConvertCast := integer(aValue.VInteger);
            aObjectVariant :=    TWSNumber.Create(integer(iConvertCast));
        end;
    vtInteger:
        begin
            iConvertCast := integer(aValue.VInteger); 
            aObjectVariant := TWSNumber.Create( integer(iConvertCast) );
        end;

    vtBoolean: 
        begin 
            aObjectVariant :=  TWSBoolean.Create( boolean(aValue) );
        end;

    vtChar:
        begin
            aObjectVariant := TWSString.Create(string(aValue));
        end;
        
    vtExtended:
        begin
                            raise exception.create('Unimplemented :: TWSArrayObject.add  :: vtExtended');
                            exit;
        end;
        
    vtString:
        begin
             
            aObjectVariant := TWSString.create(string(aValue));
             
        end;
    varString: // type 256
        begin
             
            aObjectVariant := TWSString.create(string(aValue));
             
        end;
    vtPChar:
        begin
             
            raise exception.create('Unimplemented :: TWSArrayObject.add :: vtExtended');
                            exit;
        end;

    vtObject:
        begin
             
            raise exception.create('Unimplemented :: TWSArrayObject.add :: vtObject');
                            exit;
        end;

    vtClass:
        begin

             raise exception.create('Unimplemented :: TWSArrayObject.add :: vtClass');
                            exit;
        end;
            //**************************************************
            // :: ::
    
    vtAnsiString:
        begin

            aObjectVariant := TWSString.Create(string(aValue));
             
        end;

    vtCurrency:
        begin
             
            raise exception.create('Unimplemented :: TWSArrayObject.add :: vtCurrency');
                            exit;
        end;

    vtVariant:
        begin
             
            raise exception.create('Unimplemented :: TWSArrayObject.add :: vtVariant');
                            exit;
        end;
        else
            begin
                    
                    raise exception.create('Unimplemented :: TWSArrayObject.add :: '+'--ERR-- ' +inttostr( iVariantType ));
                    exit;
    
            end;
        end;
            
            SetKeyValue( GetCountValues()+1 , aObjectVariant);
            
            Result := GetCountValues() ;
end;

//**************************************************
//**************************************************
function TWSArrayObject.GetKeyName(aKeyIndex: integer): string;
begin

    
    Result := '**GetKeyName::NULL**';
    try           
            // :::: WSObjWriteDebug('TWSArrayObject::GetKeyName:: index integer :: ');
            
            if ( integer(_key.count) >0) and ( aKeyIndex < integer(_key.count) ) and ( aKeyIndex >= 0) then
            begin
                Result := _key[aKeyIndex];
            end
            else
                raise Exception.create('TWSArrayObject.GetKeyName:: Exception :: Out of bound index :: ('+inttostr(aKeyIndex)+'/'+inttostr(_key.count)+')');
                
         except
        on ErrGetKeyName: Exception do
                begin

                        raise Exception.create('ErrGetKeyName.TWSArrayObject::GetKeyName('+ErrGetKeyName.message+')' );
                        exit;
                end;
        end;	
end;

//**************************************************
//**************************************************
function TWSArrayObject.GetColumnValue(const ColumnName: string): TWSVariant;

var iKeyIndex : integer;
var indexKeyCount : integer;
var aObject: TWSObject;
begin
    try
        // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: '+ColumnName);
        
        result := nil; 
        indexKeyCount := _key.count -1; 

        iKeyIndex := _key.IndexOf(ColumnName);
        if not (iKeyIndex = -1) then
        begin
            
            result := _value[iKeyIndex] ;
            // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: Found :: '+ColumnName);
            exit;
        end;
        {			for indexKey := 0 To indexKeyCount do
            begin
            aObject := TWSObject(_key[indexKey]);
                if ( aObject.ToString() = ColumnName) then
                begin
                    result := _value[indexKey] ;
                    // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: Found :: '+aObject.ToString());
                    exit;
                end;
            end;
}
        // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: NOT Found :: '+ColumnName);
        // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: NOT Found :: CREATE INDEX :: '+ColumnName);
        
        _key.Add(ColumnName); // create();
        _value.Add(TWSObject.Create()); 
        
        // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: NOT Found :: DONE CREATE INDEX :: '+ColumnName);

        Result := 	_value[_key.count-1] ;
    except
    on E: Exception do
        begin
            // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: Exception :: '+ColumnName+'::'+E.Message);

        end;
    end;		
    
    
end; 
{
function TWSArrayObject.GetColumnValue(Index: Integer): Pointer<TWSObject>;
begin
    // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue Int :: ');
    // :::: WSObjWriteDebug('TWSArrayObject::GetColumnValue :: '+inttostr(Index));
    result := TWSObject.create();
end; 
}


//**************************************************
//**************************************************	
// procedure SetColumnValue(Index: integer; const Value: string);
procedure TWSArrayObject.SetColumnValue(const ColumnName: string; const Value: TWSVariant);
var iKeyIndex : integer;
begin
    
    // :::: WSObjWriteDebug('TWSArrayObject::SetColumnValue :: '+ColumnName);
    if (not ArrayKeyExist(ColumnName)) then
    begin
        // :::: WSObjWriteDebug('TWSArrayObject::SetColumnValue :: Not Exist :: Insert :: '+ColumnName);
        _key.Add(ColumnName); // create();
        _value.Add(Value);
    end;
    
    iKeyIndex :=   _key.IndexOf(ColumnName);
     if(iKeyIndex  < (0)) then
     begin
         raise Exception.create('TWSArrayObject.SetColumnValue::NotFound('+ColumnName+')');
         exit;
     end;
    _value.Items[iKeyIndex] := Value;
    
     
end;

//**************************************************
//**************************************************
//**************************************************
//**************************************************
procedure TWSArrayVariant.wSetKeyValue(const vColumnName: variant; const AValue: variant);
    var iConvertCast: integer;
    var aObjectVariant : TWSVariant;
    var iVariantType : integer;
begin
                
        try
                // aObjectVariant := TWSVariant.create();
                 iVariantType := Variants.VarType(aValue);
            case iVariantType of
                
                varByte: // type 17
                        begin
                                 
                                iConvertCast := Integer(aValue) ; 
                                aObjectVariant :=   TWSNumber.Create(iConvertCast);
                        end;
        
                vtInt64:
                        begin
                                 
                                iConvertCast := integer(aValue.VInteger);
                                 
                                aObjectVariant :=    TWSNumber.Create(integer(iConvertCast));
        
                        end;
                vtInteger:
                        begin
                                
                                iConvertCast := integer(aValue.VInteger);
                                 
                                aObjectVariant := TWSNumber.Create( integer(iConvertCast) );
                        end;
        
                vtBoolean: 
                        begin
                                 
                                aObjectVariant :=  TWSBoolean.Create( boolean(aValue) );
                        end;
        
                vtChar:
                        begin
                                 
                                aObjectVariant := TWSString.Create(string(aValue));
        
                        end;
                        
                vtExtended:
                        begin
                                 
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtExtended');
                                exit;
                        end;
                        
                vtString:
                        begin
                                 
                                aObjectVariant := TWSString.create(string(aValue));
                                 
                        end;
                varString: // type 256
                        begin
                                 
                                aObjectVariant := TWSString.create(string(aValue));
                                 
                        end;
                vtPChar:
                        begin
                                 
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtExtended');
                                exit;
                        end;
        
                vtObject:
                        begin
                                 
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtObject');
                                exit;
                        end;
        
                vtClass:
                        begin
        
                                 raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtClass');
                                exit;
                        end;
                                //**************************************************
                                // :: ::
                
                vtAnsiString:
                        begin
        
                                aObjectVariant := TWSString.Create(string(aValue));
                                 
                        end;
        
                vtCurrency:
                        begin
                                 
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtCurrency');
                                exit;
                        end;
        
                vtVariant:
                        begin
                                 
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: vtVariant');
                                exit;
                        end;
                        else
                        begin
                                
                                raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: '+'--ERR-- ' +inttostr( iVariantType ));
                                exit;
                                
                        end;
                end;
                
                if(not assigned(aObjectVariant)) then
                begin
                    raise exception.create('Unimplemented ::  TWSArrayVariant.wSetKeyValue :: Unassigned value ');
                end;
                
               SetKeyValue(  vColumnName, aObjectVariant);
               
        except
                on ErrVariantCastAssign : Exception do
                begin
                        
                        // ::::
                        raise exception.Create('TWSArrayVariant.wSetKeyValue :: Exception Errorr lors de l Access a l Index vVariant ::'+ErrVariantCastAssign.Message+'::');
                end;
        end;     
        
end;
//**************************************************
//**************************************************

//**************************************************
//**************************************************
function TWSArrayVariant.wGetKeyValue(const vColumnName: variant): variant;
var aValueTemp : TWSArrayObjectReturn;
begin
        try
                aValueTemp := GetKeyValue(vColumnName);
                
                Result := variants.Null;
                
                if aValueTemp is TWSBoolean then
                        begin			
                                //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSBoolean(aValueTemp).toString);
                                Result :=  TWSBoolean(aValueTemp).asVariantType;
                        end
                else
                 if aValueTemp is TWSNumber then
                        begin			
                                //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSNumber(aValueTemp).toString);
                                Result :=  TWSNumber(aValueTemp).asVariantType;
                        end
                else
                if aValueTemp is TWSString then
                        begin			
                                //WSObjWriteDebug(':::: aValueTemp is TWSString');
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSString :: REAAD :: '+TWSString(aValueTemp).toString);
                                Result :=  TWSString(aValueTemp).asVariantType;
                        end
                else if aValueTemp is TWSArrayObject then
                        begin			
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSArrayObject');
                                Result :=  TWSArrayObject(aValueTemp).asVariantType;
                        end
                else if aValueTemp is TWSVariant then
                        begin			
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSVariant');
                                Result :=  TWSVariant(aValueTemp).asVariantType;
                        end
                else if aValueTemp is TWSObject then
                        begin			
                                WSObjWriteDebug('TWSArrayVariant.wGetKeyValue:::: aValueTemp is TWSObject');
                                Result :=  TWSObject(aValueTemp).asVariantType;
                        end
               { else  if (aValueTemp is TlkJSONobject ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONlist ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONnull ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONboolean ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONnumber ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONstring ) then
                       begin
                               Result := TWSObject.create();
                       end
                       else  if (aValueTemp is TlkJSONbase ) then
                       begin
                               Result := TWSObject.create();
                       end}
               else               
                begin
                        WSObjWriteDebug('TWSArrayVariant.GetKeyValue:::: aValueTemp is ????? ');
                        raise Exception.create('TWSArrayVariant.GetKeyValue:: Unknow Object Type ...');
                        exit; 
                end
                ;
                

        except
                on ErrVariantCastAssign : Exception do
                begin
                        
                        // ::::
                        raise exception.Create('TWSArrayVariant.GetKeyValue :: Exception Errorr lor de l Access a l Index vVariant ::'+ErrVariantCastAssign.Message+'::');
                end;
        end;                  

end;

//**************************************************
//**************************************************
//**************************************************
 //**************************************************

class function TWSObjectJSONObject.ParseText(aStringJSON : wideString; aUrlDecodeText : boolean = false)       :TWSObjectJSONObject;
     var aObjectJsonParsed : TlkJSONobject ;
     var aObjectJsonElement : TlkJSONbase ;
     
     var aObjectJsonChildParsed : TlkJSONobject ;
     var aObjectJsonChildElement : TlkJSONbase ;
     
     var iElementsJsonCount, iElementsJsonCurrentKey : integer;
     var iJsonChildKey : integer;
     var iJsonFieldType : TlkJSONtypes;
     var sFieldName : string;
     var sSubElementKey: string;
     var aStringJSONAlt : string;
     var aTransientObject : TWSObject;
     var aReturnObject : TWSObjectJSONObject;
     var iJSonPosStart : integer;
begin
        sFieldName := '';
        iElementsJsonCount := 0;
        iElementsJsonCurrentKey  := 0;
        aObjectJsonParsed := TlkJSONobject.create();
        aTransientObject := TWSObject.create();
        aReturnObject := TWSObjectJSONObject.create();
        iJSonPosStart := 0;
        try
        begin
                if (aUrlDecodeText) then
                begin
                        aStringJSON := TIdURI.URLDecode(aStringJSON);
                end;
                
                aObjectJsonParsed :=  TlkJSON.ParseText(aStringJSON) as TlkJSONobject;
                
                  if( not assigned(aObjectJsonParsed) and (not (aObjectJsonParsed is TlkJSONobject))) then
                begin
                    // aStringJSONAlt := StringReplace( aStringJSON, '='+chr(ord(039)), '="',[rfReplaceAll, rfIgnoreCase]);
                    aStringJSONAlt := aStringJSON;
                    aStringJSONAlt := StringReplace( aStringJSONAlt, chr(ord(039)) , '"',[rfReplaceAll, rfIgnoreCase]);
                                                    
                    aStringJSONAlt := StringReplace( aStringJSONAlt, '=', ':',[rfReplaceAll, rfIgnoreCase]);
                      aStringJSONAlt := StringReplace( aStringJSONAlt, '=null', ':{[]}',[rfReplaceAll, rfIgnoreCase]);
                     
                     iJSonPosStart  := AnsiPos('{', aStringJSONAlt);
                    if(iJSonPosStart >0 ) then
                    begin
                        aStringJSONAlt := RightStr(aStringJSONAlt,   (length(aStringJSONAlt) -iJSonPosStart)+1) ;
                    end;
                    
                    
                      aObjectJsonParsed :=  TlkJSON.ParseText(  aStringJSONAlt )  as TlkJSONobject;
                end;
                
                if(assigned(aObjectJsonParsed) and (aObjectJsonParsed is TlkJSONobject)) then
                begin
                         iElementsJsonCount := aObjectJsonParsed.count-1;
                          WSObjWriteDebug (' ParseText Found  :: '+inttostr(iElementsJsonCount)+'');
                         
                         for iElementsJsonCurrentKey := 0 to iElementsJsonCount do
                         begin
                             
                             
                                  aObjectJsonElement :=  aObjectJsonParsed.FieldByIndex[iElementsJsonCurrentKey];
                                  if( not assigned(aObjectJsonElement)) then
                                  begin
                                      
                                     WSObjWriteDebug('ParseText @'+inttostr(iElementsJsonCount)+':: Empty assign ');
                                      continue;
                                      // exit;
                                  end;
                                  sFieldName :=  aObjectJsonParsed.NameOf[iElementsJsonCurrentKey];
                                 iJsonFieldType :=   ( (aObjectJsonParsed.FieldByIndex[iElementsJsonCurrentKey]).SelfType) ; 
                                 
                                 if (length(sFieldName)<1) then
                                 begin
                                         sFieldName := inttostr(iElementsJsonCurrentKey);
                                 end;
                                 
                              
                               if(aObjectJsonElement <> nil) then
                               begin
                                   WSObjWriteDebug (' FieldName Found  :: '+sFieldName+' :: '+aObjectJsonElement.SelfTypeName+' ::@'+inttostr(iElementsJsonCurrentKey));
                               end
                               else
                               begin
                                   raise Exception.create('JSON Parse Error :: @'+sFieldName);
                                   exit;
                               end;
                               
                                  case (iJsonFieldType) of
                                  jsBase:
                                          begin
                                               WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: JsBase :: TWSObject ');
                                                  aTransientObject := TWSObject.Create();
                                          end;
                                       jsNumber:
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsNumber :: TWSNumber ');
                                                  aTransientObject := TWSNumber.Create(aObjectJsonElement.Value);
                                          end;
                                      
                                       jsString:
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsString :: TWSString ');
                                                  aTransientObject := TWSString.Create(aObjectJsonElement.Value);
                                          end;
                                      
                                       jsBoolean:
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsBoolean :: TWSBoolean ');
                                                  aTransientObject := TWSBoolean.Create(integer(aObjectJsonElement.Value));
                                          end;
                                      
                                       jsNull:
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsNull :: TWSObject ');
                                                  aTransientObject := TWSObject.Create();
                                          end;
                                      
                                          jsObject, jsList:
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsList :: TWSArrayObject ');
                                                  // :: TObject(aObjectJsonElement)
                                                  aTransientObject := TWSArrayObject.Create();
                                                  
                                                  // aObjectJsonChildParsed := TlkJSONobject ;
                                                  // aObjectJsonChildElement := TlkJSONbase ;

                                                 //  aObjectJsonChildElement := aObjectJsonElement.child
                                                  
                                                   WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Parse Map to :: jsList ::  '+inttostr( (TlkJSONList(aObjectJsonElement).count -1)));
                                                   iJsonChildKey := 0;
                                                  for iJsonChildKey := 0 to (TlkJSONList(aObjectJsonElement).count -1) do begin
                                                  
                                                       aObjectJsonChildElement :=  TlkJSONList(aObjectJsonElement).child[iJsonChildKey];
                                                       sSubElementKey := ''+inttostr(iJsonChildKey);
                                                       if(aObjectJsonChildElement is TlkJSONobjectmethod ) then
                                                       begin
                                                           sSubElementKey := TlkJSONobjectMethod(aObjectJsonChildElement).Name;
                                                       end;
                                                       
                                                       WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Parse Map Element :: '+inttostr(iJsonChildKey)+' ::  '+sSubElementKey);
                                                      
                                                       TWSArrayObject(aTransientObject).add( sSubElementKey ,aObjectJsonChildElement );
                                                   end;
                                          end;
                                       
                                       {jsObject :
                                          begin
                                              WSObjWriteDebug (' TWSObjectJSONObject.ParseText :: Map  to :: jsObject :: TWSObject ');
                                                  // :: TObject(aObjectJsonElement)
                                                  aTransientObject := TWSObject.Create();
                                          end;}
                                          else
                                          raise Exception.create('TWSObjectJSONObject.ParseText :: Invalid JSON Cast Type ('+inttostr( integer(iJsonFieldType) )+')');
                                      
                                 end;
                                 // :: TlkJSONtypes = (jsBase, jsNumber, jsString, jsBoolean, jsNull, jsList, jsObject);
                                 
                               
                                  aReturnObject.add( sFieldName, aTransientObject);
                         
                                 {case  of
                                          jsBase:
                                          jsNumber:
                                          jsString:
                                          jsBoolean:
                                          jsNull:
                                          jsList:
                                          jsObject:
                                  else
                                          WSObjWriteDebug('Unknow type :: '+inttostr());
                                  end;}
                         
                         
                          end;
                end
                else if(length(aStringJSON) >0) then
                begin
                        // Not a JSON
                       aTransientObject := TWSString.Create(aStringJSON);
                       aReturnObject.add( '0', aTransientObject);
                end else
                begin
                      raise Exception.create('Exception::Error(Invalid Parse Operation)' );
                      exit;  
                end;
        end;
        // ************************
        // ************************
        Result :=  aReturnObject;
        // ************************
        // ************************
 
        except
        on ErrJSONParseOperation: Exception do
               begin
                       raise Exception.create('Exception::ErrJSONParseOperation ('+ErrJSONParseOperation.Message+')' );
                       exit;
               exit;
               end;
        end;
        
        
end;
   
 //**************************************************
 //**************************************************

 //**************************************************
 //**************************************************

initialization
 // ComplexVariantType := TComplexVariantType.Create;
 finalization
 //FreeAndNil(ComplexVariantType);
end.
 

