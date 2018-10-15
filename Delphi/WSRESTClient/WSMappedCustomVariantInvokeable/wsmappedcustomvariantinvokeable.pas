unit WSMappedCustomVariantInvokeable;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}



interface

uses
{$IFnDEF FPC}
  Windows,   VarConv, VarCmplx, ObjAuto, 
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
  Classes,
  StrUtils,
  SysUtils,
  TypInfo,
  Variants, ConvUtils,

   DB;

 
// *********************************************************************
// *********************************************************************
// *********************************************************************
// *********************************************************************

 
 { A custom variant type that implements the mapping from the property names
          to the DataSet fields. }
        type TWSMappedCustomVariantInvokeable = class(TInvokeableVariantType) // :: TPublishableVariantType
                protected
                        function wReadCustomVariant( const aKeyName : variant): variant;
                        procedure wWriteCustomVariant( const aKeyName : variant; aValue : variant);
                        
                        function getAsVariantType() : variant;
                        function getFromVariant(): TObject;
                        function asArrayType() : TWSMappedCustomVariantInvokeable;
                        var _ainteger : integer;
                public
                        constructor Create(); overload;
                        // destructor Destroy(); overload;
                        
                        procedure Clear(var V: TVarData); override;
                        procedure Copy(var Dest: TVarData; const Source: TVarData; const Indirect: Boolean); override;
                        
                        function LeftPromotion(const V: TVarData; const Operator: TVarOp; out RequiredVarType: TVarType): Boolean;  override;
                        function RightPromotion(const V: TVarData; const Operator: TVarOp; out RequiredVarType: TVarType): Boolean;  override;
            
                        function DoFunction(var Dest: TVarData; const Source: TVarData; const Name: string; const Arguments: TVarDataArray): Boolean;  override;
                        function DoProcedure(const V: TVarData; const Name: string; const Arguments: TVarDataArray): Boolean;  override;
            
                        function GetProperty(var Dest: TVarData; const Source: TVarData; const Name: string): Boolean;    override;
                        {$IFDEF FPC}
                        function SetProperty(var Dest: TVarData; const Name: string; const Value: TVarData): Boolean;      override;
                        {$else}
                        function SetProperty(const Dest: TVarData; const Name: string; const Value: TVarData): Boolean; override;
                        {$ENDIF}

             
                       // 
                        property ItemsList[const aKeyName: variant] : variant read wReadCustomVariant write wWriteCustomVariant; default;
                
                       published
                       property listing: integer read _ainteger;
                       property asVariantType: Variant read getAsVariantType;
                       // :: property asArrayGet: TWSMappedCustomVariantInvokeable read getAsVariantType;

        end;
        
// *********************************************************************
// *********************************************************************


type pTWSMappedCustomVariantInvokeable  = ^TWSMappedCustomVariantInvokeable;
type
        { Our layout of the variants record data.
        We only hold a pointer to the DataSet. }
        TVarDataRecordCustomTObjectInvokeable = packed record
        VType: TVarType;
        Reserved1, Reserved2, Reserved3: Word;
       //  aTObjectCustomInvokeablePTRD: TWSMappedCustomVariantInvokeable;        
        aTObjectCustomInvokeablePTR: pTWSMappedCustomVariantInvokeable;

        Reserved4: LongInt;
end;
{type TComplexVarData = packed record
        VType: TVarType;
        Reserved1, Reserved2, Reserved3: Word;
        VComplex: TComplexData;
        Reserved4: LongInt;
end;  }
  


// *********************************************************************
// *********************************************************************
    function TFPGetTypePTR: TVarType;

    function FindPropertyByName( AObject: pointer; const aPropName : string) : boolean;
    procedure ResetPropertyValues(const AObject: TObject);
// *********************************************************************
// *********************************************************************


// *********************************************************************
// *********************************************************************


implementation
 
 //**************************************************
function GNSVariantObjWriteDebug(aStringDebug : string) : boolean;
begin
          {$IFDEF DEBUG_VERBOSE }
          // writeln( ''+string(aStringDebug)+'' );
          {$ENDIF}
          Result := true;
end;

// *********************************************************************
// *********************************************************************
 
// *********************************************************************
// *********************************************************************
    
var
{ The global instance of the custom variant type. The data of the custom
variant is stored in a TVarData record, but the methods and properties
are implemented in this class instance. }
aVarMappedCustomVariantInvokeable: TWSMappedCustomVariantInvokeable = nil;

// *********************************************************************
// *********************************************************************
        
{ A global function the get our custom VarType value. This may vary and thus
  is determined at runtime. }
function TFPGetTypePTR: TVarType;
begin
        GNSVariantObjWriteDebug('TFPGetTypePTR::Type. '+inttostr(aVarMappedCustomVariantInvokeable.VarType));
        result := aVarMappedCustomVariantInvokeable.VarType;
end;

// *********************************************************************
// *********************************************************************
function TFPVariantPTRAssignTObjectInvokeableProperty(aTObjectCustomInvokeable: pTWSMappedCustomVariantInvokeable ): Variant;
begin
    try
        GNSVariantObjWriteDebug('TFPVariantPTRAssignTObjectInvokeableProperty::Begin. ');
        VarClear(result);
        
        if( @(aTObjectCustomInvokeable^) = nil ) then
        begin
            raise exception.create('TFPVariantPTRAssignTObjectInvokeableProperty::VariantCreate(self is null)');
            exit;
        end;
        
        GNSVariantObjWriteDebug('TFPVariantPTRAssignTObjectInvokeableProperty::ObjectAddr.  : '+inttostr(word(@(aTObjectCustomInvokeable)))+' : '+inttostr(word(@TWSMappedCustomVariantInvokeable(aTObjectCustomInvokeable^))));
        TVarData(result).VType := TFPGetTypePTR ;
       // ::   TVarDataRecordCustomTObjectInvokeable(result).aTObjectCustomInvokeablePTRD :=   TWSMappedCustomVariantInvokeable.Create();
        // :: TVarDataRecordCustomTObjectInvokeable(result).aTObjectCustomInvokeablePTR := @(aTObjectCustomInvokeable^);
        TVarData(result).VAny := @(aTObjectCustomInvokeable^);
        // :: TWSMappedCustomVariantInvokeable();
    except
    on E: Exception do
        begin
               
                raise exception.create('Exception::TFPVariantPTRAssignTObjectInvokeableProperty ( Variant create :: '+E.Message+')');
                exit;
        end;
    end;
    GNSVariantObjWriteDebug('TFPVariantPTRAssignTObjectInvokeableProperty::end. ');
 end;
// *********************************************************************
// *********************************************************************
function FindPropertyByName( AObject: pointer; const aPropName : string) : boolean;
var
    PropIndex: Integer;
    PropCount: Integer;
    PropList: PPropList;
    PropInfo: PPropInfo;
const
        TypeKinds: TTypeKinds = tkAny;
        {[
                tkUnknown,tkInteger,tkChar,tkEnumeration,tkFloat,
           tkSet,tkMethod,tkSString,tkLString,tkAString,
           tkWString,tkVariant,tkArray,tkRecord,tkInterface,
           tkClass,tkObject,tkWChar,tkBool,tkInt64,tkQWord,
           tkDynArray,tkInterfaceRaw,tkProcVar,tkUString,tkUChar,
           tkHelper,tkFile,tkClassRef,tkPointer
        ]}
        {[tkEnumeration, tkObject,
                                 tkString, tkLString, tkWString, tkUString,
                                 tkChar, tkWChar,
                                 tkArray,
                                 tkInteger, tkInt64, tkQWord,
                                 tkBool,
                                 tkVariant];}
begin
    GNSVariantObjWriteDebug('ClassName :: '+string(TObject(AObject^) .ClassName));
    try
    if( TObject(AObject^) is TWSMappedCustomVariantInvokeable ) then
    begin
            PropCount := GetPropList(TWSMappedCustomVariantInvokeable(AObject^).ClassInfo, TypeKinds, nil);
            GetMem(PropList, PropCount * SizeOf(PPropInfo));
            GetPropList(TWSMappedCustomVariantInvokeable(AObject^).ClassInfo, TypeKinds, PropList);
    end
    else
    begin
            PropCount := GetPropList(TObject(AObject^).ClassInfo, TypeKinds, nil);
            GetMem(PropList, PropCount * SizeOf(PPropInfo));
            GetPropList(TObject(AObject^).ClassInfo, TypeKinds, PropList);
    end;
    
  
            for PropIndex := 0 to PropCount - 1 do
            begin
                    PropInfo := PropList^[PropIndex];
                    GNSVariantObjWriteDebug(' Prop Name Found :: '+string(PropInfo^.Name))
                    {if Assigned(PropInfo^.SetProc) then
                    begin
                        case PropInfo^.PropType^.Kind of
                        tkString, tkLString, tkUString, tkWString:
                        begin
                                SetStrProp(AObject, PropInfo, '');
                        end;
                        tkEnumeration:
                        begin
                            if (GetTypeData(
                                           (PropInfo^.PropType)
                                           ).BaseType^).Kind = TTypeInfo(TypeInfo(Boolean)^).Kind then
                            begin
                                    SetOrdProp(AObject, PropInfo, 0);
                            end;
                        end;
                    end;
                end;}
            end;
    finally
    FreeMem(PropList);
    end;
    Result := true;
end;

// *********************************************************************
// *********************************************************************
procedure ResetPropertyValues(const AObject: TObject);
var
        PropIndex: Integer;
        PropCount: Integer;
        PropList: PPropList;
        PropInfo: PPropInfo;
const
        TypeKinds: TTypeKinds = [tkEnumeration, tkString, tkLString, tkWString];
begin
    PropCount := GetPropList(AObject.ClassInfo, TypeKinds, nil);
    
    GetMem(PropList, PropCount * SizeOf(PPropInfo));
    try
        GetPropList(AObject.ClassInfo, TypeKinds, PropList);
        
        for PropIndex := 0 to PropCount - 1 do
        begin
                PropInfo := PropList^[PropIndex];
                GNSVariantObjWriteDebug(' Prop Name Found :: '+string(PropInfo^.Name))
                {if Assigned(PropInfo^.SetProc) then
                begin
                    case PropInfo^.PropType^.Kind of
                    tkString, tkLString, tkUString, tkWString:
                    begin
                            SetStrProp(AObject, PropInfo, '');
                    end;
                    tkEnumeration:
                    begin
                        if (GetTypeData(
                                       (PropInfo^.PropType)
                                       ).BaseType^).Kind = TTypeInfo(TypeInfo(Boolean)^).Kind then
                        begin
                                SetOrdProp(AObject, PropInfo, 0);
                        end;
                    end;
                end;
            end;}
        end;
    finally
    FreeMem(PropList);
    end;
end;

// *********************************************************************
// *********************************************************************

// *********************************************************************
// *********************************************************************

// *********************************************************************
// *********************************************************************

// *********************************************************************
// *********************************************************************

constructor TWSMappedCustomVariantInvokeable.Create();
begin
    GNSVariantObjWriteDebug(ClassName+':: InvokeableVariant empty Create :: TWSMappedCustomVariantInvokeable ... ');
    inherited Create();
end;
// *********************************************************************
// *********************************************************************
{
destructor TWSMappedCustomVariantInvokeable.Destroy();
begin
    GNSVariantObjWriteDebug(ClassName+':: InvokeableVariant empty DESTROY :: TWSMappedCustomVariantInvokeable ... ');
    inherited Destroy;
end;}


// *********************************************************************
// *********************************************************************


 procedure TWSMappedCustomVariantInvokeable.Clear(var V: TVarData);
begin
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Clear::begin ');
    { No fancy things to do here, we are only holding a pointer to a TDataSet and
    we are not supposed to destroy it here. }
    SimplisticClear(V);
    {  if ( TVarDataRecordCustomTObjectInvokeable(V).aTObjectCustomInvokeablePTR <> nil) then
    begin
          ResetPropertyValues(TVarDataRecordCustomTObjectInvokeable(V).aTObjectCustomInvokeablePTR^);
    end;}
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Clear::end. ');
    end;


// *********************************************************************
// *********************************************************************
    
// *********************************************************************
// *********************************************************************
        
procedure TWSMappedCustomVariantInvokeable.Copy(var Dest: TVarData; const Source: TVarData; const Indirect: Boolean);
var iListIndex : integer;
var sListIndexName : string;
// :: var aList : TStringList ;
begin
  { No fancy things to do here, we are only holding a pointer to a TDataSet and
    that can simply be copied here. }
Try

    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::Begin. ');
   // GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::Type. Source '+inttostr(Source.VType));
    // GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::Type. Dest '+inttostr(Dest.VType));
    {$IFnDEF FPC}
           
           //  SimplisticCopy(Dest, Source, Indirect);
     {$ELSE}
     
     {$ENDIF}
     
    
       if Indirect and VarDataIsByRef(Source) then
        begin
                VarDataCopyNoInd(Dest, Source)
        end
        else
        begin
            
            
            
                TVarData(Dest).VType := TVarData(Source).VType;
                TVarData(Dest).VAny := @(TVarData(Source).VAny^);
                sListIndexName := TObject(TVarData(Dest).VAny^ ).classname;
                GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::End. '+sListIndexName );
                
            
            exit;
                if( @(TVarDataRecordCustomTObjectInvokeable(Source).aTObjectCustomInvokeablePTR^) = nil ) then
                begin
                    raise exception.create('TFPVariantPTRAssignTObjectInvokeableProperty::VariantCreate(source::self is null)');
                    exit;
                end;
                
                TVarDataRecordCustomTObjectInvokeable(Dest).VType := TVarDataRecordCustomTObjectInvokeable(Source).VType;
                TVarDataRecordCustomTObjectInvokeable(Dest).aTObjectCustomInvokeablePTR := @TWSMappedCustomVariantInvokeable(TVarDataRecordCustomTObjectInvokeable(Source).aTObjectCustomInvokeablePTR^);
                
                // :: @TWSMappedCustomVariantInvokeable(TVarDataRecordCustomTObjectInvokeable(Source).aTObjectCustomInvokeablePTR^);
                if( @(TVarDataRecordCustomTObjectInvokeable(Dest).aTObjectCustomInvokeablePTR^) = nil ) then
                begin
                    raise exception.create('TFPVariantPTRAssignTObjectInvokeableProperty::VariantCreate(dest::self is null)');
                    exit;
                end;
        end;
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::End::Type. Source '+inttostr(Source.VType));
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::End::Type. Dest '+inttostr(Dest.VType));
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.Copy::End. ');
    except
    on E: Exception do
        begin
               
            raise exception.create('Exception::TWSMappedCustomVariantInvokeable.Copy ( convertion :: '+E.Message+')');
            exit;
        end;
    end;
end;

// *********************************************************************
// *********************************************************************
            
function TWSMappedCustomVariantInvokeable.LeftPromotion(const V: TVarData; const Operator: TVarOp; out RequiredVarType: TVarType): Boolean; 
begin
        GNSVariantObjWriteDebug(' Call LeftPromotion :: ');
        Result := true;
end;

// *********************************************************************
// *********************************************************************
 function TWSMappedCustomVariantInvokeable.RightPromotion(const V: TVarData; const Operator: TVarOp; out RequiredVarType: TVarType): Boolean; 
begin
        GNSVariantObjWriteDebug(' Call RightPromotion :: ');
        Result := true;
end;

// *********************************************************************
// *********************************************************************

function TWSMappedCustomVariantInvokeable.DoFunction(var Dest: TVarData; const Source: TVarData; const Name: string; const Arguments: TVarDataArray): Boolean; 
begin
    GNSVariantObjWriteDebug(' Call DoFunction :: '+Name);
    if  ( AnsiLowerCase(Name) <> 'showtext'  ) then
    begin
          
          Result := False;
    end
    else
    begin
          Variant(dest)  := string('#####hello####');
          Result := true;
    end;
end;

// *********************************************************************
// *********************************************************************
function TWSMappedCustomVariantInvokeable.DoProcedure(const V: TVarData; const Name: string; const Arguments: TVarDataArray): Boolean;
begin
    GNSVariantObjWriteDebug(' Call DoProcedure :: '+Name);
    GNSVariantObjWriteDebug(' Call DoProcedure :: Vtype :: '+inttostr(V.VType));
     
    if  ( AnsiLowerCase(Name) <> 'showtext'  ) then
    begin
            Result := False;
    end
    else
    begin
            Result := true;
    end;
end;

// *********************************************************************
// *********************************************************************
            
function TWSMappedCustomVariantInvokeable.GetProperty(var Dest: TVarData; const Source: TVarData; const Name: string): Boolean;
//var fld: TField;
var
    LType: TConvType;
    
    var iListIndex, iListIndexCount : integer;
begin
  { Find a field with the property's name. If there is one, return its current value. }
 // fld := TVarDataRecordData(V).DataSet.FindField(Name);
   // result := (fld <> nil);
  // if result then
   // Variant(dest) := fld.Value;



    // supports...
    //   'Value'
    //   'Type'
    //   'TypeName'
    //   'Family'
    //   'FamilyName'
    //   'As[Type]'
   
     GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.GetProperty :: '+ Name +'::'+ inttostr(Source.VType)+'::TO::'+ inttostr(Dest.VType));
     {       if Name = 'VALUE' then
                    Variant(Dest) := TConvertVarData(V).VValue
            else if Name = 'TYPE' then
                    Variant(Dest) := TConvertVarData(V).VConvType
            else if Name = 'TYPENAME' then
                    Variant(Dest) := ConvTypeToDescription(TConvertVarData(V).VConvType)
            else if Name = 'FAMILY' then
                    Variant(Dest) := ConvTypeToFamily(TConvertVarData(V).VConvType)
            else if Name = 'FAMILYNAME' then
                    Variant(Dest) := ConvFamilyToDescription(ConvTypeToFamily(TConvertVarData(V).VConvType))
            else if System.Copy(Name, 1, 2) = 'AS' then
                    begin
                            if DescriptionToConvType(ConvTypeToFamily(TConvertVarData(V).VConvType), System.Copy(Name, 3, MaxInt), LType) then
                            VarConvertCreateInto(Variant(Dest), Convert(TConvertVarData(V).VValue, TConvertVarData(V).VConvType, LType), LType)
                    else
                            Result := False;
                    end
            else
                    Result := False;}
     

    
     if  ( AnsiLowerCase(Name) = 'subprops'  ) then
    begin
            Variant(Dest) := TFPVariantPTRAssignTObjectInvokeableProperty(@Self);;
            Result := true;
    end
    else 
    if  ( AnsiLowerCase(Name) = 'showtext'  ) then
    begin
            Variant(Dest) := '####GotSjhowText####';
            Result := true;
    end
    else if  ( AnsiLowerCase(Name) = 'count' ) then
    begin
            Variant(Dest) := 0; // TStringList(TVarDataRecordTStringListData(Source).DataSetPTR).Count;
            Result := true;
    end
    else
    begin
        
        if (  FindPropertyByName( @Self , Name) ) then
        begin
                // Dest := variant(string(Name));
                Result := True;
        end else
        begin
                Result := False;
        end;
        
    end;
    
end;

// *********************************************************************
// *********************************************************************
{$IFDEF FPC}
function TWSMappedCustomVariantInvokeable.SetProperty(var Dest: TVarData; const Name: string; const Value: TVarData): Boolean;
{$ELSE}
function TWSMappedCustomVariantInvokeable.SetProperty(const Dest: TVarData; const Name: string; const Value: TVarData): Boolean;
{$ENDIF}
var
fld: TField;
begin
    { Find a field with the property's name. If there is one, set its value. }
    {fld := TVarDataRecordData(V).DataSetPTR  .FindField(Name);
    
    result := (fld <> nil);
    if result then
            Variant(dest) := fld.Value;
    end;}
    GNSVariantObjWriteDebug('TWSMappedCustomVariantInvokeable.SetProperty. ');
    GNSVariantObjWriteDebug('SetProperty :: '+ Name);
    {
    if ( AnsiLowerCase(Name) = 'sub'  )  then
    begin
           Result := true;  
    end
    else}
    if ( AnsiLowerCase(Name) = 'showtext'  )  then
    begin
           Result := true;  
    end
    else
    begin
            
        if (  FindPropertyByName( @Self , Name) ) then
        begin
                // Dest := variant(string(Name));
                Result := True;
        end else
        begin
                Result := False;
        end;
            
    end;
end;

 // *********************************************************************
// *********************************************************************
 function TWSMappedCustomVariantInvokeable.wReadCustomVariant(const  aKeyName : variant): variant;
 begin
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.wReadCustomVariant.::Begin ');
    if(not assigned(Self)) then
    begin
       raise exception.create('TWSMappedCustomVariantInvokeable.wReadCustomVariant::VariantCreate(self is null)');
       exit;
    end;
    Result := '++++';
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.wReadCustomVariant.::End. ');
 end;
// *********************************************************************
// *********************************************************************

procedure TWSMappedCustomVariantInvokeable.wWriteCustomVariant( const aKeyName : variant; aValue : variant);
begin
           GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.wWriteCustomVariant.::Begin ');
        if(not assigned(Self)) then
        begin
            raise exception.create('TWSMappedCustomVariantInvokeable.wWriteCustomVariant::VariantCreate(self is null)');
            exit;
        end;
           GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.wWriteCustomVariant.::End. ');
end;
 // *********************************************************************
// *********************************************************************

function TWSMappedCustomVariantInvokeable.getAsVariantType: Variant;
var aVariantReturn :  Variant;
begin
    { return one of our custom variants }
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.asVariantType.::Begin ');
    if(not assigned(Self)) then
    begin
        raise exception.create('TWSMappedCustomVariantInvokeable.getAsVariantType::VariantCreate(self is null)');
        exit;
    end;
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.getAsVariantType.::'+Self.ClassName+'::'+inttostr(word(@Self)));
    Result := '****////////******';
    // VarClear(result);
    // aVariantReturn := TFPVariantPTRAssignTObjectInvokeableProperty(@Self);
    
    // Result := aVariantReturn;
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.getAsVariantType.::End. ');
end;


function TWSMappedCustomVariantInvokeable.getFromVariant: TObject;
begin
    { return one of our custom variants }
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.getFromVariant.::Begin ');
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.getFromVariant.::'+Self.ClassName);
    { if( TFPGetTypePTR() = TVarData(@Self).VType) then
    begin
        ;;
    end;}
    if(not assigned(Self)) then
    begin
        raise exception.create('TWSMappedCustomVariantInvokeable.getFromVariant::VariantCreate(self is null)');
        exit;
    end;
    Result := Self;
    GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.getFromVariant.::End. ');
end;

// *********************************************************************
// *********************************************************************

function TWSMappedCustomVariantInvokeable.asArrayType() : TWSMappedCustomVariantInvokeable;
begin
  { return one of our custom variants }
  GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.asArrayType.::Begin ');
  if(not assigned(Self)) then
  begin
      raise exception.create('TWSMappedCustomVariantInvokeable.ASARRAY::VariantCreate(self is null)');
      exit;
  end;
  Result := Self;
  GNSVariantObjWriteDebug('  TWSMappedCustomVariantInvokeable.asArrayType.::End. ');
end;

// *********************************************************************
// *********************************************************************
   
// *********************************************************************
// *********************************************************************

initialization
{ Create our custom variant type, which will be registered automatically. }
aVarMappedCustomVariantInvokeable := TWSMappedCustomVariantInvokeable.Create;
finalization
{ Free our custom variant type. }
FreeAndNil(aVarMappedCustomVariantInvokeable);
end.       



