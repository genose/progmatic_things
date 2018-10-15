unit U_CSVMappingStorage;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}

{
* Documentation :
* // http://confluence.dvpt.xxxxxx.fr:8090/pages/viewpage.action?pageId=54047441
}

interface


uses
{$IFnDEF FPC}
  VarConv, VarCmplx, Windows,
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
 // TObject List Definition dans Unit Contnrs
  Contnrs,
  Classes,
	StrUtils, SysUtils, Variants, GNSTListHelper,
	DB
	{ DBTables,
	ZConnection,
	ZDataSet,
	ZDbcMySql,
	ZDbcCache,
	ZSQLUpdate,
	ZSQLMetadata,
	ZDbcIntfs
 
  };

  // ***************************** 
  // ***************************** Type de Base pour la description sur le Mapping CSV <--> Storage
  // *****************************

// ******** !! DEFINITION DU FileHeader !!  *********
type TCSVMapStorageArrayValues_Definition = record 
// DONT TOUCHAGE le ORDER DES ARRAY SANS Modifier les Autres
// DONT TOUCHAGE SANS RECOMPILER LES AUTRES PROGRAMMMES UTILISANT CETTE UNITS
    const PropertyMapName : array [0..4] of string = (
                                                               'iCSVFieldIdx',
                                                               'sCSVFieldName',
                                                               'vStorageFieldIdx',
                                                                'iCSVFieldLength',
                                                                'iCSVFieldType'
                                                               // etendre la definition a la suite
                                                               );
            // definition du type de index
    const PropertyMapValueType : array [0..4] of string = (
                                                               'integer',
                                                               'string',
                                                               'variant',
                                                                'integer',
                                                                'integer'
                                                               );
    type PropertyMapIndex  = record
    
        const kiCSVFieldIdx  = 0;
        const ksCSVFieldName = 1;
        const kvStorageFieldIdx = 2;
        const kiCSVFieldLength = 3;
        const kiCSVFieldType = 4;
    
    end;
            
    type PropertyMapFieldType  = record
    
        const kiCSVFieldType  = 1;
                    
                    { record
                            const kFieldTypeName = 'string';
                            const kFieldTypeValue = 1;
                    end; }
                    
        const kiFIXEDFieldType = 2;
        const kiSPECIALMIXEDFieldType = 3;
        const kiUNKNOWFieldType = 0;
    end;
end;
 // ***************************** 
 // ***************************** Type de base de Mapping CSV <--> Storage ;; Utilisation de la definition TCSVMapStorageArrayValues_Definition
 // ***************************** 
type TCSVMapFieldValuesStorageFieldValues = class(TObjectList)
	protected
               
        var _iCSVFieldIdx               : Integer;
        var _sCSVFieldName           : string;
        var _vStorageFieldIdx         : variant;
        var _iCSVFieldLength           : Integer;
        var _iCSVFieldType             : Integer; // Constant Definition Above
        
        function 	ReadFieldAtIndex(vFieldName : variant): variant;
        procedure 	WriteFieldAtIndex(vFieldName : variant; vFieldValue : variant );
        
        function 	ReadFieldCount(): integer;
        
    public
        
        
        function ToString() : string;
        function FromString( sStringFieldAssocDescriptor : string ) : integer;
        
        
        property count : integer read ReadFieldCount;
        constructor create(); overload;
        destructor Destroy(); overload;
        property iCSVFieldIdx 			: Integer 	        read _iCSVFieldIdx 		write _iCSVFieldIdx;
        property sCSVFieldName 		: string 		read _sCSVFieldName 		write _sCSVFieldName;
        property vStorageFieldIdx 	        : variant 		read _vStorageFieldIdx 	        write _vStorageFieldIdx;
        property iCSVFieldLength 		: integer 		read _iCSVFieldLength 		write _iCSVFieldLength;
        property iCSVFieldType 		        : integer 		read _iCSVFieldType 		write _iCSVFieldType;
        
        property Fields[ ColumnName: variant]: variant read ReadFieldAtIndex write WriteFieldAtIndex  ;  default;
end;
 // ***************************** 
 // ***************************** Type de base la Manipulation de Mapping CSV <--> Storage ;; utilisation de plusieurs Objects TCSVMapFieldValuesStorageFieldValues
 // ***************************** 
type TCSVArrayMappingValues = class(TObjectList)
    protected
        var _iCurrentMappingIndex	: integer;
    public
        constructor Create(); overload;
        destructor Destroy(); overload;
        function Add( tFieldObjectValue : Pointer) : integer; overload;
        function Add( tFieldObjectValue : TCSVMapFieldValuesStorageFieldValues) : integer; overload;
        function Remove( tFieldObject: Pointer): Integer; overload;
        function Remove( tFieldObjectIdx : integer) : Integer; overload;
        
        function Rewind(): boolean;
        function Current(): TCSVMapFieldValuesStorageFieldValues;
        
        function Next()       : TCSVMapFieldValuesStorageFieldValues;
        function First()        : TCSVMapFieldValuesStorageFieldValues;
        function Last()        : TCSVMapFieldValuesStorageFieldValues;
        function Clear()       : boolean;
        function Sort()        : boolean;
                
        function       GetMappingAtIndex(vMappaingKeyIndex : variant) : TCSVMapFieldValuesStorageFieldValues;
        procedure    SetMappingAtIndex(vMappaingKeyIndex : variant; tFieldObjectValue : TCSVMapFieldValuesStorageFieldValues) ;
        
        function     ReadCurrentMappingFieldAtIndex( aKeyNameIdx : variant ) : variant;
        procedure   WriteCurrentMappingFieldAtIndex( aKeyNameIdx : variant; aValue: variant ) ;
        
        // function 	ReadFieldAtIndex(vFieldName : variant): variant;
        // procedure 	WriteFieldAtIndex(vFieldName : variant; vFieldValue : variant );
        
        function GetMappingDefinitionKeyNameStrings() : TStringList;
        property MapList[ ColumnName: variant ]: TCSVMapFieldValuesStorageFieldValues read GetMappingAtIndex write  SetMappingAtIndex;  default; // ::::
        
        property CurrentMap[ ColumnName: variant ]: variant read ReadCurrentMappingFieldAtIndex write  WriteCurrentMappingFieldAtIndex;  // ::::
        
        property MapBaseDefinitionFieldKeyName : TStringList read GetMappingDefinitionKeyNameStrings ; 
        // property MapBaseDefinitionFieldKeyNameIndex : read ; 
        
        //  inherited  :: property Count : Integer read ;
        
        property iMapFieldIdx               : Integer 	read 	_iCurrentMappingIndex 			write _iCurrentMappingIndex;
        
        // property iCSVFieldIdx 			: Integer 	Index kiCSVFieldIdx 			read 	ReadMappingFieldAtIndex 		write WriteMappingFieldAtIndex;
        // property sCSVFieldName 		: string 		Index ksCSVFieldName 		read 	ReadMappingFieldAtIndex 		write WriteMappingFieldAtIndex;
        // property vStorageFieldIdx 	: string		Index kvStorageFieldIdx	read 	ReadMappingFieldAtIndex 		write WriteMappingFieldAtIndex;
        
end;
 // ***************************** 
 // ***************************** 
 // ***************************** 
type TCSVArrayValues = class(TObjectList)


end;
type TCSVHeaderNameArray = class(TStringList)


end;
 // ***************************** 
 // ***************************** Type de base la Manipulation de Mapping CSV <--> Storage
 // ***************************** 
type TCSVFieldsValues = class(TObjectList)
        var _MapFields : TCSVArrayMappingValues;
        var _StringsFields : TStringList;
        var __cachedColomns : TStringList;
        var __iCurrentFieldIndex : integer;
        var __sCurrentFieldValue : string;
    public
        constructor Create();overload;
        destructor Destroy(); overload; 
        
        function add(const aStringValue : string): integer; overload;
        function add(const aStringKey : string; const aStringValue : string): integer; overload;
        procedure clear(); overload;
        
        function FieldKeyExists(vFieldName: variant): boolean;
        
        function SetMapField( aArrayOfMappingField : TCSVArrayMappingValues) : boolean;
        function FindMatchField( sValidFieldName : string; iMapFindIdx: integer ): integer;
        
        function Rewind(): boolean;
        function First(): string;
        function Last(): string;
        function Next(): boolean;
        function Current(): string;
        
        
        function GetFieldsCount(): integer;
        function GetMapFieldsCount(): integer;
        function GetFieldAtIndex(vFieldName: variant): string;
        procedure SetFieldAtIndex(vFieldName: variant; aValue: string);
        
        
        function GetStringsAtIndex(iFieldIdx: integer): string;
        
        property Values[ ColumnNameIndex: variant ]: string read GetFieldAtIndex write SetFieldAtIndex;  default;
        property Strings[ ColumnNameIndex: integer ]: string read GetStringsAtIndex  ;
        // :::: write WriteCSVRecordatIndex
        property CurrentFieldValue : string read __sCurrentFieldValue  ; // :::: write WriteCSVRecordatIndex
        property Count : integer read GetFieldsCount;
        property MapCount : integer read GetMapFieldsCount;
        property MapFields : TCSVArrayMappingValues read _MapFields write _MapFields;
end;
 
implementation

		
// ****************************************
// ****************************************
// ****************************************
// ****************************************
//  Class MapField  < -- > StorageField
// ****************************************
// ****************************************

constructor TCSVMapFieldValuesStorageFieldValues.create();
begin
    inherited create;
    _iCSVFieldIdx := (-1);
    _sCSVFieldName := '';
    _vStorageFieldIdx := '';
    _iCSVFieldLength := 0;
    _iCSVFieldType := TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType ;

end;

destructor TCSVMapFieldValuesStorageFieldValues.Destroy();
begin
    _iCSVFieldIdx := (-1);
    _sCSVFieldName := '';
    _vStorageFieldIdx := '';
    _iCSVFieldLength := 0;
    _iCSVFieldType := TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType ;
    inherited Destroy();
end;

// ****************************************
// ****************************************
function 	TCSVMapFieldValuesStorageFieldValues.ReadFieldCount(): integer;
begin
        Result := high(TCSVMapStorageArrayValues_Definition.PropertyMapName);
end;

// ****************************************
// ****************************************
function TCSVMapFieldValuesStorageFieldValues.ToString() : string;
var rsMapDescriptor : string;
var iValidFieldKey : integer;
begin
        rsMapDescriptor := '';
        iValidFieldKey := 0;
        for iValidFieldKey := 0 to (high(TCSVMapStorageArrayValues_Definition.PropertyMapName)) do
        begin
                        rsMapDescriptor := rsMapDescriptor+''+string(Fields[TCSVMapStorageArrayValues_Definition.PropertyMapName[iValidFieldKey]])+';'; 
        end;
        Result := string(rsMapDescriptor);
end;

// ****************************************
// ****************************************
function TCSVMapFieldValuesStorageFieldValues.FromString( sStringFieldAssocDescriptor : string ) : Integer;
var sCurrentDescriptorData : string;
var sFoundFieldValue : string;
var iValidFieldKey : integer;
var iFoundFieldIndex : integer;
var iNBFieldOccurence: integer;
begin
        try
                sCurrentDescriptorData := sStringFieldAssocDescriptor;
                sFoundFieldValue := '*';
                iValidFieldKey := 0;
                iNBFieldOccurence := 0;
                iFoundFieldIndex := (-2);
                
                if( ( AnsiPos(';', sCurrentDescriptorData) > 0) ) then
                begin
                        while (not (iFoundFieldIndex = 0 )) do
                        Begin
                                iFoundFieldIndex := AnsiPos(';', sCurrentDescriptorData);
                                if (iFoundFieldIndex >0) then
                                begin
                                        if(high(TCSVMapStorageArrayValues_Definition. PropertyMapName) < iValidFieldKey) then
                                         begin
                                            Raise exception.create('Exception::Out_Of_Bound (Too Much Descriptor)');
                                            exit;
                                        end;
                                         
                                         sFoundFieldValue := string(LeftStr(sCurrentDescriptorData,( iFoundFieldIndex-1))) ; 
    
                                        if(TCSVMapStorageArrayValues_Definition. PropertyMapValueType[iValidFieldKey] = 'variant') then
                                        begin
                                            if( strtoint( string(sFoundFieldValue) ) = (-1) ) then
                                            begin
                                                    Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] := string(sFoundFieldValue);
                                            end
                                            else
                                            begin
                                                    Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] := strtoint(sFoundFieldValue);
                                            end;
                                        end
                                        else if(TCSVMapStorageArrayValues_Definition. PropertyMapValueType[iValidFieldKey] = 'string') then
                                        begin 
                                                Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] := string(sFoundFieldValue);
                                        end
                                        else if(TCSVMapStorageArrayValues_Definition. PropertyMapValueType[iValidFieldKey] = 'integer') then
                                        begin 
                                            Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] := strtoint(sFoundFieldValue);
                                            if ( integer(Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] ) < 0)  then
                                            begin
                                                   raise Exception.create('Exception::FromString (Invalid Field Definition Cast @'+inttostr(iFoundFieldIndex)+') '); 
                                            end;
                                        end
                                        else
                                        begin
                                                raise Exception.create('Exception::FromString (Invalid Field Definition @'+inttostr(iFoundFieldIndex)+') ');
                                        end;
                                        
                                        sCurrentDescriptorData := RightStr(sCurrentDescriptorData, Length(sCurrentDescriptorData) - (iFoundFieldIndex));
                                        inc(iValidFieldKey);
                                        inc(iNBFieldOccurence);
                                end
                                else
                                begin
                                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]] := sCurrentDescriptorData;
                                        inc(iValidFieldKey);
                                        inc(iNBFieldOccurence);
                                end;
                        end;
                end
                else
                begin
                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapIndex.ksCSVFieldName ] 		:=  sStringFieldAssocDescriptor;
                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapIndex.kiCSVFieldidx ] 		:=  (-2);
                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapIndex.kvStorageFieldIdx ]	:=  (-2);
                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapIndex.kiCSVFieldLength ]	        :=  (-2);                        
                        Fields[TCSVMapStorageArrayValues_Definition. PropertyMapIndex.kiCSVFieldType ]            :=  TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType;
                        
                        inc(iValidFieldKey);
                        inc(iNBFieldOccurence);
                end;
        except
        on E: Exception do
                begin
                        // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                       raise exception.create('Exception::FromString ('+E.Message+')'); 
                end;
        end;	
        Result := integer(iNBFieldOccurence);
end;

// ****************************************
// ****************************************
// Lecture d'une MapField Unique
// ****************************************
// ****************************************
function 	TCSVMapFieldValuesStorageFieldValues.ReadFieldAtIndex(vFieldName : variant): variant;
var sValidFieldName : string;
var iValidFieldKey : integer;
begin
       
        sValidFieldName := '*';
        iValidFieldKey := -1;

        case VarType(vFieldName) of
            varByte:
                    iValidFieldKey := integer(vFieldName);
            vtInt64:
                    iValidFieldKey := integer(vFieldName);
            vtInteger:
                    iValidFieldKey := integer(vFieldName);
            varInteger:
                    iValidFieldKey := integer(vFieldName);
            vtChar:
                    sValidFieldName := string(vFieldName);
            vtString:
                    sValidFieldName := string(vFieldName);
            varString: 
                    sValidFieldName := string(vFieldName);
            vtAnsiString:	
                    sValidFieldName := string(vFieldName);
            else
            begin
                    raise exception.create('ReadFieldAtIndex::Exception:: (Invalid Key variant::Type:: '+inttostr(VarType(vFieldName))+')');
                    exit;
            end;
        end;
        
        try 
            // ********************************
            // ********************************
            if(sValidFieldName <> '*') then
            begin
                for iValidFieldKey := 0 to (high(TCSVMapStorageArrayValues_Definition.PropertyMapName)) do
                begin
                     if(  AnsiLowerCase(TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]) =  AnsiLowerCase(sValidFieldName) ) then
                     begin 
                                     sValidFieldName := TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey];
                                     break;
                     end;
                end;
            end 
            else if(iValidFieldKey >= 0) and (iValidFieldKey <= (high(TCSVMapStorageArrayValues_Definition.PropertyMapName))) then
            begin
                    sValidFieldName := TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey];
            end;
         except
         on E: Exception do
                 begin
                         // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                        raise exception.create('Exception::ReadFieldAtIndex ('+E.Message+')');
                        exit;
                 end;
         end;
         // ********************************
         // ********************************
         if(sValidFieldName = '*') then
         begin
                 raise exception.create('ReadFieldAtIndex::Exception:: Invalid Key Name ');
                 exit;
         end;
                // ********************************
                // TODO	Items[];
                // ********************************
        try
             VarClear(Result);
             
            if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[0]) then
                    begin
                            Result := integer(_iCSVFieldIdx);		
                    end
            else if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[1]) then
                    begin 
                            Result := string(_sCSVFieldName);
                    end
            else if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[2]) then
                    begin  
                            Result := variant(_vStorageFieldIdx); // Yep Cast By Yourself ... 
                    end
            else
             if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[3]) then
                    begin  
                            Result := integer(_iCSVFieldLength);
                    end
            else
             if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[4]) then
                    begin  
                            Result := integer(_iCSVFieldType);
                    end
            else
            begin
                    Result := '***ERROR::UnDefinedReadIndex::'+sValidFieldName+'***';
            end;
        except
        on E: Exception do
                begin
                        // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                       raise exception.create('Exception::ReadFieldAtIndex ('+E.Message+')'); 
                end;
        end;
end;
// ****************************************
// ****************************************
// Ecriture d'une MapField Unique
// ****************************************
// ****************************************
procedure 	TCSVMapFieldValuesStorageFieldValues.WriteFieldAtIndex(vFieldName : variant; vFieldValue : variant );
var sValidFieldName : string;
var iValidFieldKey : integer;

var sValidFieldValue : string;
var iValidFieldValue : integer;
var iCastValidFieldValue : integer;
begin
        
        sValidFieldName := '*';
        iValidFieldKey := (-1);
        
        sValidFieldValue := '';
        iValidFieldValue := (-1);
        
        iCastValidFieldValue := (-1);

        case VarType(vFieldName) of
            varByte:
                    iValidFieldKey := integer(vFieldName);
            vtInt64:
                    iValidFieldKey := integer(vFieldName);
            vtInteger:
                    iValidFieldKey := integer(vFieldName);
            varInteger:
                    iValidFieldKey := integer(vFieldName);
            vtChar:
                    sValidFieldName := string(vFieldName);
            vtString:
                    sValidFieldName := string(vFieldName);
            varString: 
                    sValidFieldName := string(vFieldName);
            vtAnsiString:	
                    sValidFieldName := string(vFieldName);
            else
            begin
                    raise exception.create('Exception::WriteFieldAtIndex (Invalid Key variant::Type:: '+inttostr(VarType(vFieldName))+')');
                    exit;
            end;
        end;
        
        case VarType(vFieldValue) of
                varByte:
                        iValidFieldValue := integer(vFieldValue);
                vtInt64:
                        iValidFieldValue := integer(vFieldValue);
                vtInteger:
                        iValidFieldValue := integer(vFieldValue);
                varInteger:
                        iValidFieldValue := integer(vFieldValue);
                vtChar:
                        sValidFieldValue := string(vFieldValue);
                vtString:
                        sValidFieldValue := string(vFieldValue);
                varString: 
                        sValidFieldValue := string(vFieldValue);
                vtAnsiString:	
                        sValidFieldValue := string(vFieldValue);
                else
                begin
                        raise exception.create('Exception::WriteFieldAtIndex (Invalid Value variant::Type:: '+inttostr(VarType(vFieldValue))+')');
                        exit;
                end;
        end;

        try
                // ********************************
                // ********************************
                // Recherche le Field Demande
                // ********************************
                // ********************************
        
                if(sValidFieldName <> '*') then
                begin
                        for iValidFieldKey := 0 to (high(TCSVMapStorageArrayValues_Definition.PropertyMapName)) do
                        begin
                                if(  AnsiLowerCase(TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]) =  AnsiLowerCase(sValidFieldName) ) then
                                begin
                                
                                                sValidFieldName := TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey];
                                                break;
                                end;
                        end;
                end 
                else if(iValidFieldKey >= 0) and (iValidFieldKey <= (high(TCSVMapStorageArrayValues_Definition.PropertyMapName) )) then
                begin
                        sValidFieldName := TCSVMapStorageArrayValues_Definition.PropertyMapName[iValidFieldKey];
                end;
                
                // ********************************
                // ********************************
                if(sValidFieldName = '*') then
                begin
                        raise exception.create('Exception::WriteFieldAtIndex (Invalid Key Name : '+intToStr(iValidFieldKey)+'::'+intToStr(high(TCSVMapStorageArrayValues_Definition.PropertyMapName))+' / "'+sValidFieldName+'") ');
                        exit;
                end;
                
                if (pos(sValidFieldValue,'*')  >= 0 ) then
                begin
                        iCastValidFieldValue := integer(iValidFieldValue); 
                end
                else
                begin
                        try         
                                if ( iValidFieldValue <> (-1) ) then
                                       begin
                                               iCastValidFieldValue  := integer(iValidFieldValue);
                                       end
                               else  if (strtoint(sValidFieldValue) <> (-1) ) then
                                       begin
                                               iCastValidFieldValue  := integer(strtoint(sValidFieldValue) );
                                       end;
                        except
                        on E: Exception do
                                begin
                                        // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                                       // raise exception.create('Exception::WriteFieldAtIndex ('+E.Message+')');
                                       iCastValidFieldValue  := integer(iValidFieldValue);
                                end;
                        end;
                end;
                
                // ********************************
                // ********************************
                if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[0]) then
                        begin
                              
                                 _iCSVFieldIdx  := integer(iCastValidFieldValue);
                                 
                        end
                else if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[1]) then
                        begin 
                                
                                if ( iCastValidFieldValue <> (-1) ) then
                                        begin
                                                _sCSVFieldName  := string(inttostr(iCastValidFieldValue));
                                        end
                                else
                                begin
                                                _sCSVFieldName  := string( sValidFieldValue );
                                end;
                        end
                else if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[2]) then
                        begin  
                              
                                if ( iCastValidFieldValue <> (-1) ) then
                                        begin
                                                _vStorageFieldIdx  := integer(iCastValidFieldValue);
                                        end
                                else
                                begin
                                                _vStorageFieldIdx  := string( sValidFieldValue );
                                end;
                               
                        end 
                 else if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[3]) then
                        begin  
                                _iCSVFieldLength  := integer(iCastValidFieldValue );
                        end
                else   if(sValidFieldName = TCSVMapStorageArrayValues_Definition. PropertyMapName[4]) then
                        begin  
                                _iCSVFieldIdx  := integer(iCastValidFieldValue );
                        end;
        except
        on E: Exception do
                begin
                        // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                       raise exception.create('Exception::WriteFieldAtIndex ('+E.Message+')'); 
                end;
        end;
end;
// **************************************** 
// ****************************************
// ****************************************
// ****************************************
//  Class Array MapField
// **************************************** 
// ****************************************
// ****************************************
// ****************************************
 
 constructor TCSVArrayMappingValues.Create();
 begin
        inherited create();
        _iCurrentMappingIndex := 0;
end;

// ****************************************
// ****************************************
 Destructor TCSVArrayMappingValues.Destroy();
 begin
    _iCurrentMappingIndex := 0;
    inherited Destroy();
end;

// ****************************************
// ****************************************
// Recherche dans le Array d'une MapField Unique
// ****************************************
// **************************************** 
function TCSVArrayMappingValues.GetMappingAtIndex(vMappaingKeyIndex : variant): TCSVMapFieldValuesStorageFieldValues;
    var iCountObjects : longint;
    var iIndexOfMap   : integer;
    var sValidMapFieldName : string;
    var iValidMapFieldKey : integer;
        var iValidMapFieldKeyFound : boolean;
    
    begin
        iIndexOfMap := 0;
        sValidMapFieldName := '*';
        iValidMapFieldKey := -1;
                iValidMapFieldKeyFound := false;
                
        case VarType(vMappaingKeyIndex) of
            varByte:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtInt64:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtInteger:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            varInteger:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtChar:
                sValidMapFieldName := string(vMappaingKeyIndex);
            vtString:
                sValidMapFieldName := string(vMappaingKeyIndex);
            varString: 
                sValidMapFieldName := string(vMappaingKeyIndex);
            vtAnsiString:	
                sValidMapFieldName := string(vMappaingKeyIndex);
            else
            begin
                raise exception.create('Exception::GetMappingAtIndex (Invalid Key variant::Type:: '+inttostr(VarType(vMappaingKeyIndex))+')');
                exit;
            end;
        end;

        
        
        iCountObjects := count;
        if (iCountObjects = 0) then
        begin
            raise Exception.create('Exception::No_Objects::Out_Of_Bounds ('+intToStr(iCountObjects)+')');
        end;
        
    // ********************************
    // ********************************
        if(sValidMapFieldName <> '*') then
        begin
            for iIndexOfMap := 0 to iCountObjects-1 do
            begin
                if( sValidMapFieldName = TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap])[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName]) then
                begin
                        Result := TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap]);
                        exit;
                end;
            end;
        end
        else
        begin 
            if( (iValidMapFieldKey >= 0) ) then
            begin
                    iValidMapFieldKeyFound := false;
                    for iIndexOfMap := 0 to iCountObjects-1 do
                    begin
                            if( iValidMapFieldKey = integer(TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap])[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx]) ) then
                            begin
                                            //Result := TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap]);
                                            iValidMapFieldKey := iIndexOfMap;
                                            iValidMapFieldKeyFound := true;
                                            break;
                            end;
                    end;
                            
                    if(iValidMapFieldKeyFound) then
                    begin
                            Result := TCSVMapFieldValuesStorageFieldValues(self.Items[iValidMapFieldKey]);
                            exit;
                    end
                    else if ( iValidMapFieldKey <= iCountObjects-1 ) then 
                    begin
                            Result := TCSVMapFieldValuesStorageFieldValues(self.Items[iValidMapFieldKey]);
                            exit;  
                    end;
                end;
        end;
                
                raise Exception.create('Exception::Out_Of_Bounds::MapField::NameField ( '+intToStr(iValidMapFieldKey)+' / "'+sValidMapFieldName+'")'+chr(13)+chr(10)+'::Out_Of_Bounds ('+intToStr(iValidMapFieldKey)+'/'+ intToStr(iCountObjects)+')');
     //Result := TCSVMapFieldValuesStorageFieldValues.create();
end;

// ****************************************
// ****************************************
// Recherche  et Ecriture dans le Array d'une MapField Unique
// ****************************************
// **************************************** 
procedure TCSVArrayMappingValues.SetMappingAtIndex(vMappaingKeyIndex : variant; tFieldObjectValue : TCSVMapFieldValuesStorageFieldValues);
    var iCountObjects : integer;
    var iIndexOfMap   : integer;
    var iFieldIdx, iFieldCount : Integer;
    
    var sValidMapFieldName : string;
    var iValidMapFieldKey : integer;

    begin
        iIndexOfMap := 0;
        sValidMapFieldName := '*';
        iValidMapFieldKey := -1;

        case VarType(vMappaingKeyIndex) of
            varByte:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtInt64:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtInteger:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            varInteger:
                iValidMapFieldKey := integer(vMappaingKeyIndex);
            vtChar:
                sValidMapFieldName := string(vMappaingKeyIndex);
            vtString:
                sValidMapFieldName := string(vMappaingKeyIndex);
            varString: 
                sValidMapFieldName := string(vMappaingKeyIndex);
            vtAnsiString:	
                sValidMapFieldName := string(vMappaingKeyIndex);
            else
            begin
                raise exception.create('GetMappingAtIndex::Exception:: (Invalid Key variant::Type:: '+inttostr(VarType(vMappaingKeyIndex))+')');
                exit;
            end;
        end;

        
        
        iCountObjects := count;
        if (iCountObjects = 0) then
        begin
            raise Exception.create('No_Objects :: Out_Of_Bounds ('+intToStr(iCountObjects)+')');
        end;
        
        
        if not (tFieldObjectValue is TCSVMapFieldValuesStorageFieldValues) then
        begin
            raise Exception.create('Transmitted Object not acceptable Type ... ');
        end;
        
        if (iCountObjects < iValidMapFieldKey) or (iValidMapFieldKey < (-1) ) then
        begin
            raise Exception.create('Out_Of_Bounds ('+intToStr(iValidMapFieldKey)+'/'+ intToStr(iCountObjects)+')');
        end;
    // ********************************
    // ********************************
        if(sValidMapFieldName <> '*') then
        begin
            for iIndexOfMap := 0 to iCountObjects-1 do
            begin
                if( sValidMapFieldName = TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap])[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName]) then
                begin
                    
                        iFieldCount := tFieldObjectValue.count;
                        iFieldIdx := 0;
                        // Assign values 
                        for iFieldIdx := 0 to iFieldCount-1 do 
                        begin
                            TCSVMapFieldValuesStorageFieldValues(self.Items[iIndexOfMap])[iFieldIdx] := TCSVMapFieldValuesStorageFieldValues(tFieldObjectValue)[iFieldIdx];
                        end;
                        break;
                end;
            end;
        end
        else
        begin
            if(iValidMapFieldKey < 0 ) then
            begin
                raise Exception.create('Out_Of_Bounds ('+intToStr(iValidMapFieldKey)+'/'+ intToStr(iCountObjects)+')');
                exit;
            end
            else
            begin
                iFieldCount := tFieldObjectValue.count;
                iFieldIdx := 0;
                // Assign values 
                for iFieldIdx := 0 to iFieldCount-1 do 
                begin
                    TCSVMapFieldValuesStorageFieldValues(self.Items[iValidMapFieldKey])[iFieldIdx] := TCSVMapFieldValuesStorageFieldValues(tFieldObjectValue)[iFieldIdx];
                end;
            end;
        end;
    
end;

// ****************************************
// ****************************************
// Lecture dans le Array d'une MapField Unique ;;  Lecture du Field sur MapField en cours
// CF ;; Next() ;; Current() ;; Prev() ;; 
// ****************************************
// **************************************** 
function 		TCSVArrayMappingValues.ReadCurrentMappingFieldAtIndex( aKeyNameIdx : variant ) : variant;
var iCountObjects : integer;
begin
    iCountObjects := count;
    if (iCountObjects = 0) then
    begin
        raise Exception.create('No_Objects :: Out_Of_Bounds ('+intToStr(iCountObjects)+')');
    end;
    
    result := TCSVMapFieldValuesStorageFieldValues(self.Items[_iCurrentMappingIndex])[aKeyNameIdx];
end;
// ****************************************
// ****************************************
// Ecriture dans le Array d'une MapField Unique ;;  Ecriture du Field sur MapField en cours
// CF ;; Next() ;; Current() ;; Prev() ;; 
// ****************************************
// **************************************** 
procedure 	TCSVArrayMappingValues.WriteCurrentMappingFieldAtIndex( aKeyNameIdx : variant; aValue: variant ) ;
var iCountObjects : integer;
begin
    iCountObjects := count;
    if (iCountObjects = 0) then
    begin
        raise Exception.create('No_Objects :: Out_Of_Bounds ('+intToStr(iCountObjects)+')');
    end;
    
    TCSVMapFieldValuesStorageFieldValues(self.Items[_iCurrentMappingIndex])[aKeyNameIdx] := aValue;
end;

// ****************************************
// ****************************************
function TCSVArrayMappingValues.Add( tFieldObjectValue : Pointer) : integer; 
begin
    // ::
    raise Exception.create('Add::pointer::Unauthorized Operation');
    Result := (-1);
end;

// ****************************************
// ****************************************
function TCSVArrayMappingValues.Add( tFieldObjectValue : TCSVMapFieldValuesStorageFieldValues) : integer;
var iCountObjects : integer;
var iInsertedObject : integer;

begin
    try
        iInsertedObject := inherited add(tFieldObjectValue);
        // :: (iCountObjects  <>  count);
        iCountObjects := count;
    
    except
    on ErrAdd: Exception do
        begin
            raise Exception.Create('ErrAdd :: Exception :: '+ErrAdd.Message);
        end;
    end;
	Result := iInsertedObject;
end;
// ****************************************
// ****************************************
function TCSVArrayMappingValues.Remove( tFieldObject : Pointer) : integer;
var iCountStrings : integer;
begin
	
	iCountStrings := count;
	if (iCountStrings = 0 ) or (self.IndexOf(tFieldObject) = (-1) ) then
	begin
		raise Exception.create('Remove::Index Out_Of_Bounds ('+intToStr(iCountStrings)+'/'+intToStr(self.IndexOf(tFieldObject))+')');
	end;
	
	// ::
	raise Exception.create('Remove::Unauthorized Operation');
	Result := integer(count);
end;
// ****************************************
// ****************************************
function TCSVArrayMappingValues.Remove( tFieldObjectIdx : integer) : integer;
var iCountStrings : integer;
begin
	
	iCountStrings := count;
	if (iCountStrings = 0 ) or (iCountStrings < tFieldObjectIdx) then
	begin
		raise Exception.create('Remove::Index Out_Of_Bounds ('+intToStr(iCountStrings)+'/'+intToStr(tFieldObjectIdx)+')');
	end;
	
	// ::
	raise Exception.create('Remove::Unauthorized Operation');
	Result := integer(count);
end;
// ****************************************
// ****************************************
// Efface tout le Array ;; Retire tout MapField
// ****************************************
// ****************************************

function TCSVArrayMappingValues.Clear(): boolean;
var iCountObjects : integer;
begin
    
    iCountObjects := integer(count)-1;
    
    if (iCountObjects <= 0) then
    begin
        result := true;
        exit;
    end;
    
    try

         for iCountObjects := (count -1) downto 0 do
         begin
             
             if( integer(count) = 0) then
             begin
                 break;
             end;
             
             if (self.Items[0]  <> nil ) then
                 begin
                     self.Extract(TCSVMapFieldValuesStorageFieldValues(self.Items[0] ));
                     if( integer(count) = 0) then
                     begin
                         break;
                     end;
                     //TCSVMapFieldValuesStorageFieldValues(self.Items[0]).Free;
                     
                 end;
         end;
        result := true;
    except
    on E: Exception do
        begin
            {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('TCSVArrayMappingValues.clear :: Exception :: '+E.Message); {$ENDIF}
                   raise exception.create('Exception::TCSVArrayMappingValues.clear ('+E.Message+')'); 
        result := false;
        end;
    end;
    

end;
// ****************************************
// ****************************************
function TCSVArrayMappingValues.First(): TCSVMapFieldValuesStorageFieldValues;
var iCountStrings : integer;
begin
	iCountStrings := count;
	if (iCountStrings = 0 ) then
	begin
		raise Exception.create('TCSVArrayMappingValues.First::Index Out_Of_Bounds (0/'+intToStr(iCountStrings)+')');
	end;

	Result := TCSVMapFieldValuesStorageFieldValues(self.Items[0]);
end;
// ****************************************
// ****************************************
function TCSVArrayMappingValues.Last(): TCSVMapFieldValuesStorageFieldValues;
var iCountObjects : integer;
begin
	iCountObjects := count;
	if (iCountObjects = 0 ) then
	begin
		raise Exception.create('TCSVArrayMappingValues.Last::Index Out_Of_Bounds ('+intToStr(iCountObjects)+')');
	end;
	Result := TCSVMapFieldValuesStorageFieldValues(self.Items[iCountObjects -1 ]);
end;
// ****************************************
// ****************************************
function TCSVArrayMappingValues.Rewind(): boolean;
begin
	_iCurrentMappingIndex := 0;
	Result := true;
end;
// ****************************************
// ****************************************

function TCSVArrayMappingValues.Next(): TCSVMapFieldValuesStorageFieldValues;
var iCountObjects : integer;
begin
	iCountObjects := count;
	if (iCountObjects = 0 ) then
	begin
		raise Exception.create('TCSVArrayMappingValues.Next::Index Out_Of_Bounds ('+intToStr(iCountObjects)+')');
	end;
	
	if ((_iCurrentMappingIndex +1) > iCountObjects) then
	begin
		_iCurrentMappingIndex := _iCurrentMappingIndex; // Dummy assign ... 
	end
	else
	begin
		inc(_iCurrentMappingIndex);
	end;
		Result := TCSVMapFieldValuesStorageFieldValues(self.Items[_iCurrentMappingIndex ]);
end;
	
// ****************************************
// ****************************************

function TCSVArrayMappingValues.Current(): TCSVMapFieldValuesStorageFieldValues;
var iCountObjects : integer;
begin
	iCountObjects := count;
	if (iCountObjects = 0 ) then
	begin
		raise Exception.create('TCSVArrayMappingValues.Current::Index Out_Of_Bounds ('+intToStr(iCountObjects)+')');
	end;
	 
		Result :=  TCSVMapFieldValuesStorageFieldValues(self.Items[_iCurrentMappingIndex]);
end;

// ******************************************
// ******************************************
function TCSVArrayMappingValues.Sort(): boolean;
var iCurrentMapSortIndex : Integer;
var iSortDone : Boolean;
var iUnSortFound : Boolean;
var  aObjectCompareUp, aObjectCompareDown: TCSVMapFieldValuesStorageFieldValues;
begin
        try
                iCurrentMapSortIndex := 0;
                iSortDone := false;
                iUnSortFound := false;
                
                 // aObjectCompareUp         := TCSVMapFieldValuesStorageFieldValues.create();
                 // aObjectCompareDown     := TCSVMapFieldValuesStorageFieldValues.create();
                
                
                if( count <= 1) then
                Begin
                        Result := true;
                        exit;
                end;
                
                // some Radix Sort
                While (not iSortDone) do
                begin
                        aObjectCompareUp := TCSVMapFieldValuesStorageFieldValues(Items[iCurrentMapSortIndex]);
                        aObjectCompareDown := TCSVMapFieldValuesStorageFieldValues(Items[integer(iCurrentMapSortIndex)]);
                        if( (iCurrentMapSortIndex +1) < count ) then
                        begin
                                aObjectCompareDown := TCSVMapFieldValuesStorageFieldValues(Items[integer(iCurrentMapSortIndex + 1)]);
                        end;
                        
                        if ( integer(TCSVMapFieldValuesStorageFieldValues(aObjectCompareUp)[TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ]) >
                            integer(TCSVMapFieldValuesStorageFieldValues(aObjectCompareDown)[TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ])  ) then
                        begin
                                iUnSortFound := true;
                                exchange(iCurrentMapSortIndex,  integer(iCurrentMapSortIndex + 1) );
                        end;
                        inc(iCurrentMapSortIndex);
                        if ( iCurrentMapSortIndex >= count ) then
                        begin
                                iCurrentMapSortIndex := 0;
                                
                                if (iUnSortFound = false ) then
                                begin
                                        iSortDone := true; // each time we finish scan the whole array, we check for unsorted element 
                                end
                                else
                                begin
                                        iUnSortFound := false;
                                end;
                        end; 
                       
                        
                end;
        
        except
        on E: Exception do
                begin
                        // ***** writeln('AssignCSV :: Exception :: '+E.Message);
                       raise exception.create('Execption::Sort ('+E.Message+')'); 
                end;
        end;
        Result := true;
end;

// ****************************************
// ****************************************
//  Creation d'une description de MapField Unique ;;  Pratique pour le versionning entre Build d epoque differente
// ****************************************
// **************************************** 
	function TCSVArrayMappingValues.GetMappingDefinitionKeyNameStrings() : TStringList;
		var rKeysStringsList : TStringList;
		var iValidFieldKey : integer;
	begin
		rKeysStringsList := TStringList.create();
		iValidFieldKey := 0;
		
		for iValidFieldKey := 0 to (high(TCSVMapStorageArrayValues_Definition.PropertyMapName)) do
		begin
			rKeysStringsList.add(TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidFieldKey]); 
		end;
		Result := rKeysStringsList;
	end;
		
// ****************************************
// ****************************************
// ****************************************
// ****************************************
 // class Fields ;; Lecture du 		
// ****************************************
// ****************************************
// ****************************************
// ****************************************

constructor TCSVFieldsValues.Create();
begin
    inherited create();
    _MapFields := TCSVArrayMappingValues.create();
    _StringsFields := TStringList.Create();
    __cachedColomns := TStringList.create();
end;

destructor TCSVFieldsValues.Destroy();
var iObjectIndexDelete : integer;
begin
    {iObjectIndexDelete := 0;
    for iObjectIndexDelete := _MapFields.count -1 downto 0  do begin
    
        _MapFields.delete(0);
         //_StringsFields. Capacity :=  iObjectIndexDelete;
    end;}
      freeandnil(__cachedColomns);
     _MapFields.free;
    iObjectIndexDelete := 0;
     for iObjectIndexDelete := _StringsFields.count -1 downto 0 do begin
    
        _StringsFields.delete(0);
         //_StringsFields. Capacity :=  iObjectIndexDelete;
    end;
     
    _StringsFields.Destroy;
    inherited destroy;
end;

// *********************************************
// *********************************************
function TCSVFieldsValues.SetMapField( aArrayOfMappingField : TCSVArrayMappingValues) : boolean;
var iCountObjects : integer ; 
begin
      
    try
          //
          
        if (_MapFields = nil)  or (not assigned(_MapFields)) then
        begin
               raise exception.create('Vanished (_MapFields)');
               exit;
        end;
          
          _MapFields.Clear;
        
        // _MapFields.free;
        //_MapFields := TObjectList.create();
        result := false;
         // _MapFields := TObjectList.create();
        if (__cachedColomns = nil)  or (not assigned(__cachedColomns)) then
        begin
            raise exception.create('Vanished (__cachedColomns)');
            exit;
        end;
         __cachedColomns.clear();
         
        iCountObjects := 0;
        // ::  writeln(' SetMapField :: '+ intToStr(aArrayOfMappingField.count));
        for  iCountObjects := (aArrayOfMappingField.count -1) downto 0 do
        begin
            // :: TCSVMapFieldValuesStorageFieldValues
            _MapFields.add( aArrayOfMappingField[iCountObjects] );
        end;
        
        result := true;
    except
    on E: Exception do
        begin
            raise exception.create('SetMapField::Clear :: Exception :: '+E.Message);
            result := false;
            exit;
        end;
    end;

end;
// ************************************************
// ************************************************

function TCSVFieldsValues.GetFieldsCount(): integer;
begin
        Result := _StringsFields.count;
end;
// ************************************************
// ************************************************

function  TCSVFieldsValues.GetMapFieldsCount(): integer;
begin
        Result := _MapFields.count;
end;
// ************************************************
// ************************************************
function TCSVFieldsValues.GetStringsAtIndex(iFieldIdx: integer): string;
var iCountStrings : integer;
begin
    iCountStrings := _StringsFields.Count ;
    // ATTENTION ::  _StringsFields.Count == _MapFields.Count
   if ( _StringsFields.Count < iFieldIdx)  then
   begin
           raise Exception.create('Index Out_Of_Bounds ('+intToStr(iFieldIdx)+'/'+intToStr(_StringsFields.Count )+')');
           exit;
   end;
   try
        __iCurrentFieldIndex := iFieldIdx;
        __sCurrentFieldValue := _StringsFields.Strings[iFieldIdx];
        Result := __sCurrentFieldValue;
   except
   on E: Exception do
           begin 
                  raise exception.create('Exception::GetStringsAtIndex('+E.Message+')'); 
           end;
   end;
end;
// *********************************************
// *********************************************

function TCSVFieldsValues.FieldKeyExists(vFieldName: variant): boolean;
var sValidFieldName  : string;
var iCountStrings : longint;
var iValidFieldKey  :  integer;
var iValidDescriptorFieldKey  :  integer;
var iCountMapFieldsObjects : integer;
var  aKeyMaps : TObject;
begin
    try
      
        sValidFieldName := '*';
        iValidFieldKey := -1;
        iValidDescriptorFieldKey := 0;
        iCountMapFieldsObjects := 0;
        iCountStrings := count; 
        case VarType(vFieldName) of
                varByte:
                        iValidFieldKey := integer(vFieldName);
                vtInt64:
                        iValidFieldKey := integer(vFieldName);
                vtInteger:
                        iValidFieldKey := integer(vFieldName);
                varInteger:
                        iValidFieldKey := integer(vFieldName);
                vtChar:
                        sValidFieldName := string(vFieldName);
                vtString:
                        sValidFieldName := string(vFieldName);
                varString: 
                        sValidFieldName := string(vFieldName);
                vtAnsiString:	
                        sValidFieldName := string(vFieldName);
                else
                begin
                        raise exception.create('KeyExists::Exception::  (Invalid Key variant::Type:: '+inttostr(VarType(vFieldName))+')');
                        exit;
                end;
        end;
    
        
        // ********************************
        // ********************************
        if(sValidFieldName <> '*') then
        begin
                 iCountMapFieldsObjects := 0;
                 // aKeyMaps := TCSVMapFieldValuesStorageFieldValues.create();
                 
                {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('>>>> ******* KeyExists:: Looking up Maps .... ('+inttostr( (_MapFields.count -1))+'::'+sValidFieldName+'::'+inttostr(__cachedColomns.indexof(sValidFieldName))+')' ); {$ENDIF}
                for iCountMapFieldsObjects := 0 to (_MapFields.count -1) do
                begin
                    if( __cachedColomns.indexof(sValidFieldName) > -1) then
                    begin
                        sValidFieldName := __cachedColomns.Values[sValidFieldName];
                          iValidFieldKey :=  strtoint(sValidFieldName);
                        break;
                    end;
                         {$IFDEF DEBUG_VERBOSE_INTERNAL}
                         writeln(' Get Maps .... ('+inttostr(iCountMapFieldsObjects)+')' );
                         {$ENDIF}
                        iValidDescriptorFieldKey := 0;
                        
                        // retourne la position du Fields dans la ligne en cours
                       iValidFieldKey := FindMatchField( sValidFieldName , iCountMapFieldsObjects);
                        
                         if( iValidFieldKey >=0 ) then
                         begin
                              {$IFDEF DEBUG_VERBOSE_INTERNAL}
                              writeln(' ******** FOUND Maps ********  .... ('+inttostr(iCountMapFieldsObjects)+')' );
                                  aKeyMaps :=  _MapFields[iCountMapFieldsObjects];

                                writeln(' >>>> Found Key Maps  for .... ('+ string(TCSVMapFieldValuesStorageFieldValues(aKeyMaps) [TCSVMapStorageArrayValues_Definition. PropertyMapName[TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx]])+
                                        '/'+AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName  ])  +'/'+AnsiLowerCase(sValidFieldName)+')' );
                                {$ENDIF}
                                 break;
                         end;
                         
                end;
        end ;
    except
    on ErrFieldExists: Exception do
        begin
            raise exception.create('FieldKeyExists :: Exception :: '+ErrFieldExists.Message);
            result := false;
            exit;
        end;
    end;
   Result :=  not (iValidFieldKey = (-1)) ;
       // and (iValidFieldKey <= (_StringsFields.count)) ); 
end;


function TCSVFieldsValues.FindMatchField( sValidFieldName : string; iMapFindIdx: integer ): integer;
var iValidDescriptorFieldKey : integer;
var iValidFieldKey : integer;
var  aKeyMaps : TObject;
var  aKeyMapsObject :TCSVMapFieldValuesStorageFieldValues;
var sValidKey : string;
var sValidPropsName : string;
var bFieldSame : boolean;
begin
        
    iValidDescriptorFieldKey := 0;
    iValidFieldKey := -1;
     try
        Result := __cachedColomns.indexof(sValidFieldName);
        
        if( Result <> -1) then
        begin
            Result :=  strtoint(__cachedColomns.Values[sValidFieldName]);
            ;;
            exit;
        end;
    except
    on ErrFindMatchField: Exception do
        begin
            raise exception.create('FindMatchField :: Exception ::  Cached :: '+ErrFindMatchField.Message); 
            exit;
        end;
    end;
    
    try

    
         if ( ( iMapFindIdx <= (-1)) or (iMapFindIdx >= _MapFields.count)) then
         begin
             
             raise Exception.create('FindMatchField :: Exception :: Invalid Maps ('+IntToStr(iMapFindIdx)+')' );         
         end;
            
            // aKeyMaps := TCSVMapFieldValuesStorageFieldValues.create();
            // :: aKeyMaps := TCSVMapFieldValuesStorageFieldValues(_MapFields.Items[iMapFindIdx]);
            
      {$IFDEF DEBUG_VERBOSE_INTERNAL}
                writeln('>>>> ******* FindMatchField::Looking up Maps ('+IntToStr(iMapFindIdx)+')  .... ('+inttostr( (_MapFields.count -1)));
              //  writeln('>>>>'+  '::'+AnsiLowerCase( aKeyMaps[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName  ])+')' );
      {$ENDIF}                         
            
                    
            aKeyMaps := _MapFields.Items[iMapFindIdx];
            sValidFieldName := AnsiLowerCase(sValidFieldName);
            
             for iValidDescriptorFieldKey := 0 to (high(TCSVMapStorageArrayValues_Definition.PropertyMapName)) do
            begin
                    if( aKeyMaps = nil ) or (not assigned(aKeyMaps) ) then
                    begin
                                  raise Exception.create('FindMatchField :: Exception :: Corrupted Maps ('+IntToStr(iMapFindIdx)+'::props='+inttostr(iValidDescriptorFieldKey)+')' );     
                    end;
                     
                     {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln('>>>> **** FindMatchField::Looking up Maps:Props .... ('+IntToStr(iValidDescriptorFieldKey)+')');
                            //AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey]])+'::'+ AnsiLowerCase(sValidFieldName)+')' );
                     {$ENDIF}
                     sValidPropsName := TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey];
                     
                     {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln('>>>> **** FindMatchField::Casting up Maps::props::Value .... ('+IntToStr(iValidDescriptorFieldKey)+')');
                            //AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey]])+'::'+ AnsiLowerCase(sValidFieldName)+')' );
                     {$ENDIF}
                     aKeyMapsObject := TCSVMapFieldValuesStorageFieldValues(aKeyMaps);
                     
                    if( aKeyMapsObject = nil ) or (not assigned(aKeyMapsObject) ) then
                    begin
                                  raise Exception.create('FindMatchField :: Exception :: Corrupted Maps ('+IntToStr(iMapFindIdx)+'::props='+inttostr(iValidDescriptorFieldKey)+')' );     
                    end;
                        
                     {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln('>>>> **** '+classname+'::FindMatchField::Getting up Maps::props::ValueObject .... ('+IntToStr(iValidDescriptorFieldKey)+'::'+aKeyMapsObject.classname+')');
                            //AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey]])+'::'+ AnsiLowerCase(sValidFieldName)+')' );
                     {$ENDIF}
                     
                     
                     
                     sValidKey := aKeyMapsObject [ sValidPropsName ];
                         
                     {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln('>>>> **** FindMatchField::PASSED Maps::props::Value::Passed .... ('+IntToStr(iValidDescriptorFieldKey)+'::'+sValidPropsName+')');
                            writeln('>>>> **** FindMatchField::  KEY :: '+sValidKey);
                            //AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey]])+'::'+ AnsiLowerCase(sValidFieldName)+')' );
                     {$ENDIF}
                     
                     
                       sValidKey := string(AnsiLowerCase( string(''+sValidKey+'') ));
                                    
                     {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln('>>>> **** FindMatchField::PASSED Maps::props::Value::Compare .... ('+IntToStr(iValidDescriptorFieldKey)+'::'+sValidPropsName+':'+sValidKey+'::'+sValidFieldName+')');
                            //AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[TCSVMapStorageArrayValues_Definition. PropertyMapName[iValidDescriptorFieldKey]])+'::'+ AnsiLowerCase(sValidFieldName)+')' );
                     {$ENDIF}
                     
                   bFieldSame :=   string(''+sValidKey+'') =  string(''+sValidFieldName+'') ;
                     
                     
                    if(  bFieldSame ) then
                    begin
                            // retourne la position du Fields dans la ligne en cours
                           
                            {$IFDEF DEBUG_VERBOSE_INTERNAL}
                            writeln(' >>>> Found Maps  for .... ('+IntToStr(iMapFindIdx)+'::'+ sValidKey +')');
                            // writeln(' >>>> Found Maps  for .... ('+ string(TCSVMapFieldValuesStorageFieldValues(aKeyMaps) [TCSVMapStorageArrayValues_Definition. PropertyMapName[TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx]])+'/'+AnsiLowerCase( TCSVMapFieldValuesStorageFieldValues(aKeyMaps)[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName  ])  +'/'+AnsiLowerCase(sValidFieldName)+')' );
                            {$ENDIF}
                                sValidPropsName := TCSVMapStorageArrayValues_Definition. PropertyMapName[TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx];
                                sValidKey := TCSVMapFieldValuesStorageFieldValues(aKeyMaps) [sValidPropsName];
                                
                            iValidFieldKey := strtoint(sValidKey );
                            
                            break;
                    end;
            end;
     except
    on ErrFindMatchField: Exception do
        begin
            raise exception.create('FindMatchField :: Exception :: '+ErrFindMatchField.Message);
        
            exit;
        end;
    end;
    if(iValidFieldKey <> -1) and (__cachedColomns.indexof(sValidFieldName) = (-1)) then
    begin
        __cachedColomns.add( sValidFieldName, inttostr(iValidFieldKey));
    end;
    Result := iValidFieldKey;
        
end;
// *********************************************
// *********************************************

procedure TCSVFieldsValues.SetFieldAtIndex(vFieldName: variant; aValue: string);
var sValidFieldName  : string;
var iCountStrings : longint;
var iValidFieldKey  :  integer;
var iValidDescriptorFieldKey  :  integer;
var iCountMapFieldsObjects : integer;
var  aKeyMaps : TCSVMapFieldValuesStorageFieldValues;
begin
    iCountStrings := count; 
    sValidFieldName := '*';
    iValidFieldKey := -1;
    iValidDescriptorFieldKey := 0;
    iCountMapFieldsObjects := 0;
     
    case VarType(vFieldName) of
            varByte:
                    iValidFieldKey := integer(vFieldName);
            vtInt64:
                    iValidFieldKey := integer(vFieldName);
            vtInteger:
                    iValidFieldKey := integer(vFieldName);
            varInteger:
                    iValidFieldKey := integer(vFieldName);
            vtChar:
                    sValidFieldName := string(vFieldName);
            vtString:
                    sValidFieldName := string(vFieldName);
            varString: 
                    sValidFieldName := string(vFieldName);
            vtAnsiString:	
                    sValidFieldName := string(vFieldName);
            else
            begin
                    raise exception.create('WriteFieldAtIndex::Exception::  (Invalid Key variant::Type:: '+inttostr(VarType(vFieldName))+')');
                    exit;
            end;
    end;

    try      
            // ********************************
            // ********************************
            if(sValidFieldName <> '*') then
            begin
                     iCountMapFieldsObjects := 0;
                    // aKeyMaps := TCSVMapFieldValuesStorageFieldValues.create();
                    {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('>>>> ******* WriteFieldAtIndex::Looking up Maps .... ('+inttostr( (_MapFields.count -1))+'::'+AnsiLowerCase( sValidFieldName )+')' ); {$ENDIF}
                    
                    for iCountMapFieldsObjects := 0 to (_MapFields.count -1) do
                    begin
                            // ::  writeln(' Get Maps .... ('+inttostr(iCountMapFieldsObjects)+')' );
                            iValidDescriptorFieldKey := 0;
                            
                            if( __cachedColomns.indexof(sValidFieldName) > -1) then
                            begin
                                sValidFieldName := __cachedColomns.Values[sValidFieldName];
                                iValidFieldKey :=  strtoint(sValidFieldName);
                                break;
                            end;
                    
                            // retourne la position du Fields dans la ligne en cours
                            iValidFieldKey := FindMatchField( sValidFieldName , iCountMapFieldsObjects);
                            
                             if( iValidFieldKey >=0 ) then
                             begin
                                     break;
                             end;
                             
                    end;
            end ;
    except
    on E: Exception do
            begin 
                   raise exception.create('Exception::SetFieldAtIndex(sValidFieldName::'+E.Message+')'); 
            end;
    end;
    {$IFDEF DEBUG_VERBOSE_INTERNAL}
    for iCountStrings := count-1 downto 0 do
    begin
            writeln(' >>>>> TCSVFieldsValues.SetFieldAtIndex::Strings['+inttostr(iCountStrings)+']('+GetStringsAtIndex(iCountStrings)+')');
    end;
   {$ENDIF}
   
    if ( iValidFieldKey = (-1) ) or (_StringsFields.count <= iValidFieldKey) then
    begin
        
         for iCountStrings := _StringsFields.count-1 downto 0 do
        begin
                writeln(' >>>>> TCSVFieldsValues.SetFieldAtIndex::Strings['+inttostr(iCountStrings)+']('+_StringsFields.Strings[iValidFieldKey]+')');
        end;
        
            raise Exception.create('WriteField::Index Out_Of_Bounds ('+intToStr(iValidFieldKey)+'/'+intToStr(  _StringsFields.Count )+' / '+sValidFieldName+')');
    end;
    
    try
            _StringsFields.Strings[iValidFieldKey] := aValue;
    except
    on E: Exception do
            begin 
                   raise exception.create('Exception::SetFieldAtIndex('+E.Message+')'); 
            end;
    end;
end;
// *********************************************
// *********************************************
function TCSVFieldsValues.GetFieldAtIndex(vFieldName: variant): string;
var sValidFieldName  : string;
var iCountStrings : longint;
var iValidFieldKey  :  integer;
var iValidDescriptorFieldKey  :  integer;
var iCountMapFieldsObjects : integer;
// var  aKeyMaps : TCSVMapFieldValuesStorageFieldValues;
begin
        iCountStrings := count;
        
        sValidFieldName := '*';
        iValidFieldKey := -1;
        iValidDescriptorFieldKey := 0;
        iCountMapFieldsObjects := 0;
         
        case VarType(vFieldName) of
                varByte:
                        iValidFieldKey := integer(vFieldName);
                vtInt64:
                        iValidFieldKey := integer(vFieldName);
                vtInteger:
                        iValidFieldKey := integer(vFieldName);
                varInteger:
                        iValidFieldKey := integer(vFieldName);
                vtChar:
                        sValidFieldName := string(vFieldName);
                vtString:
                        sValidFieldName := string(vFieldName);
                varString: 
                        sValidFieldName := string(vFieldName);
                vtAnsiString:	
                        sValidFieldName := string(vFieldName);
                else
                begin
                        raise exception.create('GetFieldAtIndex::Exception::  (Invalid Key variant::Type:: '+inttostr(VarType(vFieldName))+')');
                        exit;
                end;
        end;

        
        // ********************************
        // ********************************
        try
                if(sValidFieldName <> '*') then
                begin
                         iCountMapFieldsObjects := 0;
                         // aKeyMaps := TCSVMapFieldValuesStorageFieldValues.create();
                         for iCountMapFieldsObjects := 0 to (_MapFields.count -1) do
                        begin
                                 {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('>>>> ******* GetFieldAtIndex::Looking up Maps .... ('+IntToStr(iCountMapFieldsObjects)+'::'+inttostr( (_MapFields.count -1))+'::'+AnsiLowerCase( sValidFieldName )+')' ); {$ENDIF}
                      
                                iValidDescriptorFieldKey := 0;
                                
                                if( __cachedColomns.indexof(sValidFieldName) > -1) then
                                begin
                                    sValidFieldName := __cachedColomns.Values[sValidFieldName];
                                    iValidFieldKey :=  strtoint(sValidFieldName);
                                    break;
                                end;
                                // retourne la position du Fields dans la ligne en cours
                                iValidFieldKey := FindMatchField( sValidFieldName , iCountMapFieldsObjects);
                                
                                 if( iValidFieldKey >=0 ) then
                                 begin
                                         break;
                                 end;
                                
                        end;
                end ;
        except
        on E: Exception do
                begin 
                       raise exception.create('Exception::GetFieldAtIndex(sValidFieldName::'+E.Message+')'); 
                end;
        end;
        
        {$IFDEF DEBUG_VERBOSE_INTERNAL}
        for iCountStrings := count-1 downto 0 do
        begin
                writeln(' >>>>> TCSVFieldsValues.GetFieldAtIndex::Strings['+inttostr(iCountStrings)+']('+GetStringsAtIndex(iCountStrings)+')');
        end;
        {$ENDIF}



        if ( iValidFieldKey = (-1) ) then
        begin
               // ::  writeln('ReadField::Index Out_Of_Bounds (Maps:'+intToStr((_MapFields.count -1))+'/idx:'+intToStr(iValidFieldKey)+'/Strings:'+intToStr(  _StringsFields.Count )+'/ '+sValidFieldName+')');
                raise Exception.create('ReadField::Index Out_Of_Bounds (Maps:'+intToStr((_MapFields.count -1))+'/idx:'+intToStr(iValidFieldKey)+'/Strings:'+intToStr(  _StringsFields.Count )+'/ '+sValidFieldName+')');
        end;
        // :: __iCurrentFieldIndex := iValidFieldKey;
        Result := string(GetStringsAtIndex(iValidFieldKey));
end;
// ****************************************
// ****************************************
function TCSVFieldsValues.add(const aStringValue : string): integer;
var iCSVColumnAssigned : integer;
// var aCSVMappingInfo     : TCSVMapFieldValuesStorageFieldValues;
begin

        {aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                        
                        
        aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kvStorageFieldIdx ]  := '**Unknow**';
        
        iCSVColumnAssigned := aCSVMappingInfo.FromString(inttostr(_StringsFields.count));
          
         aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName ]  := inttostr(_StringsFields.count); 
         aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] :=  _StringsFields.count;
         
        _MapFields.add(TCSVMapFieldValuesStorageFieldValues(aCSVMappingInfo));
        }
    try
        Result := _StringsFields.add(aStringValue);
        
    except
    on ErrAdd: Exception do
        begin
                {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('TCSVFieldsValues.add :: Exception :: '+ErrAdd.Message); {$ENDIF}
               raise exception.create('Exception::TCSVFieldsValues.add ('+ErrAdd.Message+')'); 
        end;
    end;
     
end;
function TCSVFieldsValues.add(const aStringKey : string; const aStringValue : string): integer; 
var iCSVColumnAssigned : integer;
var aCSVMappingInfo     : TCSVMapFieldValuesStorageFieldValues;
begin
 
        aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                        
                        
        aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kvStorageFieldIdx ]  := '**Unknow**';
        
        iCSVColumnAssigned := aCSVMappingInfo.FromString(aStringKey);
          
          iCSVColumnAssigned := _StringsFields.add(aStringValue);
          
         aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName ]  := aStringKey; 
         aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] :=  iCSVColumnAssigned;
         
        _MapFields.add( aCSVMappingInfo );
        
        {$IFDEF DEBUG_VERBOSE_INTERNAL}
        writeln('FieldsMap::Add ('+aCSVMappingInfo.ToString()+')');
        {$ENDIF}
        Result := aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ];
         
end;
// ****************************************
// ****************************************
procedure TCSVFieldsValues.clear();
var iObjectIndexDelete : integer;
// :: var aObjecttoFree : pointer ;
begin
    try
        { iObjectIndexDelete := 0;
         for iObjectIndexDelete := _MapFields.count -1 downto 0  do begin
          
                TObject(_MapFields.Extract(_MapFields.Items[0])).Destroy;
             
         end;}
         
         // _MapFields. Capacity :=  0;
         // _MapFields.Clear;
         // _MapFields.free;
           
         iObjectIndexDelete := 0;
          for iObjectIndexDelete := _StringsFields.count -1 downto 0 do begin
      
                _StringsFields.delete(0);
           
         end;
        //  _StringsFields. Capacity :=  0;
         //_StringsFields.Clear;
         // _StringsFields.free;
        // _MapFields := TObjectList.create();
         // _StringsFields := TStringList.create();
         
    except
    on E: Exception do
        begin
                {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('TCSVFieldsValues.clear :: Exception :: '+E.Message); {$ENDIF}
               raise exception.create('Exception::TCSVFieldsValues.clear ('+E.Message+')'); 
        end;
    end;
end;

// ****************************************
// ****************************************
function TCSVFieldsValues.First(): string;
var iCountStrings : longint;
begin
    iCountStrings := _StringsFields.count;
    if (iCountStrings = 0 ) then
    begin
        raise Exception.create('Index Out_Of_Bounds (0/'+intToStr(iCountStrings)+')');
    end;
    __iCurrentFieldIndex := 0;
    __sCurrentFieldValue :=  _StringsFields.Strings[ __iCurrentFieldIndex ];
    Result := __sCurrentFieldValue ;
end;

// ****************************************
// ****************************************
function TCSVFieldsValues.Last(): string;
var iCountStrings : longint;
begin
    iCountStrings := _StringsFields.count;
    if (iCountStrings = 0 ) then
    begin
        raise Exception.create('Index Out_Of_Bounds ('+intToStr(iCountStrings)+')');
    end; 
    __iCurrentFieldIndex := iCountStrings -1;
    __sCurrentFieldValue :=  _StringsFields.Strings[ __iCurrentFieldIndex ];
    Result := __sCurrentFieldValue ;
end;

// ****************************************
// ****************************************
function TCSVFieldsValues.Current(): string;
begin
        
     __sCurrentFieldValue := _StringsFields.Strings[__iCurrentFieldIndex];        
    Result := __sCurrentFieldValue;
end;

// ****************************************
// ****************************************
function TCSVFieldsValues.Rewind(): boolean;
begin
    __iCurrentFieldIndex := 0;
    Result := true;
end;

// ****************************************
// ****************************************
function TCSVFieldsValues.Next(): boolean;
begin
                        
    Result := false;
    if ((__iCurrentFieldIndex+1) >=_StringsFields.count) then
    begin
            inc(__iCurrentFieldIndex);
            Result := true;
    end;

end;


end.
