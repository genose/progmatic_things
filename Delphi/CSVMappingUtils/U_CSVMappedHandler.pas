Unit U_CSVMappedHandler;

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
  Classes,
	StrUtils, SysUtils, Variants,
	DB, U_CSVMappingStorage, RegExpr , Forms
	{ DBTables,
	ZConnection,
	ZDataSet,
	ZDbcMySql,
	ZDbcCache,
	ZSQLUpdate,
	ZSQLMetadata,
	ZDbcIntfs

  };




type TCSVMappedHandler = class(TObject)
        protected
                var _iCSVCount                            : Longint;
                var _iCSVFileType                         : Integer;
                var _hCSVFileHandle                      : TextFile;
                var _sCSVFileName                       : string;
                var _sCSVFileNameDir                    : string;
                
                var _sCSVFileDate                         : string;
                var _sCSVFileNameDate                 : string;
                var _iFileCurrentPos                       : Longint;
                var _lCSVStringValues                    : TCSVFieldsValues; // cache derniere ligne parse ;; en interne retourne le meme enregistrment selon l'index reclamee
                
                var _aCSVHeaderMapping              : TCSVArrayMappingValues;
                var _sCSVSeparator                      : string;
                // Accesseur
                function   ReadCSVRecordatIndex(iKeyIndex : Longint) : TCSVFieldsValues;
                procedure  WriteCSVRecordatIndex(iKeyIndex : Longint; aValueArray: Variant );

                
                function   ReadCSVHeaderCount():integer;
                
                function   ReadCurrentFieldCount() : integer;
                function   ReadCurrentFieldRecordAtIndex(vKeyIndex : variant) : string;
                procedure  WriteCurrentFieldRecordAtIndex(vKeyIndex : variant; aFieldValue : string);
                
                function   ReadCSVHeader(vKeyIndex : variant) : TCSVMapFieldValuesStorageFieldValues;
                procedure  WriteCSVHeader(vKeyIndex : variant; aValueArray: TCSVMapFieldValuesStorageFieldValues );
                
                function        SetAjustFieldMap(): boolean;
                procedure     setHeaderSeparator(const aSeparator : string);
                procedure     setFileType(aFileType : integer);
                
                
        public
                property Value[ iLigneIndex : Longint]: TCSVFieldsValues read ReadCSVRecordatIndex  ;  default; // :::: write WriteCSVRecordatIndex
                
                property FileCurrentFieldValue[ iFieldIndex : variant ]: string read ReadCurrentFieldRecordAtIndex  ; // :::: write WriteCurrentFieldRecordAtIndex
                
                property Count : Longint read  _iCSVCount;
                
                property FieldsCount : Longint read  ReadCurrentFieldCount;
                
                property FileCurrentPos : Longint read  _iFileCurrentPos;
                
                property FileFieldSeparator : string read _sCSVSeparator write setHeaderSeparator ;
                
                property FileHeader [vKeyIndex : variant] : TCSVMapFieldValuesStorageFieldValues read  ReadCSVHeader write WriteCSVHeader;
                property FileHeaderCount : integer read  ReadCSVHeaderCount;
                
                property FileTypeFormat : integer read _iCSVFileType write setFileType;
                
                // nom BASE du fichier
                property FileName : string read _sCSVFileName;
                // nom BASE du dossier
                property FilePath : string read _sCSVFileNameDir;
                // date system du fichier
                property FileDate : string read _sCSVFileDate;
                // date extraite du nom du fichier
                property FileDateName : string read _sCSVFileNameDate;
                
                
                constructor Create(); overload;
                
                // Methods CSV / File
                function AssignFileInput(sFilePath : string) : Boolean;
                function AssignFileOutput(sFilePath : string) : Boolean;
                function AssignFileHeader( aCSVHeaderNamesObjectArray : TCSVFieldsValues; iUseNames : boolean = true ) : Boolean; overload;
                function AssignFileHeader( aCSVHeaderNamesObjectArray : array of string; iUseNames : boolean = true ) : Boolean; overload;
                function ParseFileHeader( iFistLineNamed: boolean = false) : Boolean;
                
                function Rewind(): boolean;
                function Current(): TCSVFieldsValues;
                
                function Next(): TCSVFieldsValues;
                function Prev(): TCSVFieldsValues;
                function First(): TCSVFieldsValues;
                function Last(): TCSVFieldsValues;
                function FieldByName(aColumnName: string): variant;
                
                

end;
implementation
// ****************************************
// ****************************************
// ****************************************
// ****************************************
constructor TCSVMappedHandler.Create();
begin
  inherited Create();
  _iCSVCount := 0;
  _iCSVFileType :=  TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType;
  _iFileCurrentPos := 0; 
  _sCSVFileName := '';
  _sCSVFileNameDir := '';
  _sCSVFileDate := '';
  _sCSVFileNameDate := '';
  _lCSVStringValues := TCSVFieldsValues.create();
  _aCSVHeaderMapping := TCSVArrayMappingValues.create();
  _sCSVSeparator := '';
  // ***** hCSVFileHandle := nil; 
end;

{ PROCEDURE TextSeek(VAR F : TEXT ; POS : Cardinal);
  BEGIN
    WITH TTextRec(F) DO BEGIN
      BufPos:=0; BufEnd:=0;
      SetFilePointer(Handle,POS,NIL,FILE_BEGIN)
    END
  end;  }
 {
function CSVFileSeekLn(sFileNameHandle : string; iFileLineIndex : longint): string;
var iFileCurrentPos : longint;
var sResukltStr : string;
var hFileHandle : File;
begin
	sResukltStr := '';
	assignfile(hFileHandle,sFileNameHandle);
	iFileCurrentPos := FilePos(hFileHandle);
	if (iFileCurrentPos >iFileLineIndex) then
		begin
			Reset(hFileHandle);
		
		end;
	
	while (not SeekEof(hFileHandle)) and (iFileLineIndex>=1) do
	  begin
		// Read numbers one at a time
		// ShowMessage('Start of a new line');
		//while not SeekEoln(hFileHandle) do
		//begin
		//end;
		dec(iFileLineIndex);
		ReadLn(hFileHandle);
	end;
	Result := sResukltStr;
end;
  }
 
// ******************************************
// ******************************************

// ******************************************
// ******************************************
 procedure TCSVMappedHandler.setFileType(aFileType : integer);
 begin
        case aFileType of
                TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiCSVFieldType:
                begin
                        if (length(_sCSVSeparator) = 0) then
                        begin
                                _sCSVSeparator := ';';
                        end;
                end;
                TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiFIXEDFieldType:
                begin
                         if (length(_sCSVSeparator) > 0) then
                        begin
                                _sCSVSeparator := '';
                        end;
                end;
                TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiSPECIALMIXEDFieldType:
                begin
                         if (length(_sCSVSeparator) > 0) then
                        begin
                                _sCSVSeparator := '';
                        end;
                
                end;
                TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType:
                begin
                         if (length(_sCSVSeparator) > 0) then
                        begin
                                _sCSVSeparator := '';
                        end;
                
                end;
        
        else
                raise exception.create('Exception::CSVHandler::Invalid File Type');
                exit;
        end;
        _iCSVFileType := aFileType;
 end;
// ******************************************
// ******************************************
procedure TCSVMappedHandler.setHeaderSeparator(const aSeparator : string);
  begin
          _sCSVSeparator := aSeparator;
          // seprator empty or trailing char
         if( _iCSVFileType =  TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiCSVFieldType) then
          begin      
             if( (length(_sCSVSeparator) = 0) or (_sCSVSeparator = ' ' )) then
             begin
                     raise exception.create('Exception::CSVHandler (CSVSeparator invalid) ');
                     exit;
             end;
        end;
  end;
  
// ******************************************
// ******************************************
function TCSVMappedHandler.Rewind(): boolean;
begin
        _iFileCurrentPos := 0;
        result := true ;
end;
// ******************************************
// ******************************************
function TCSVMappedHandler.FieldByName(aColumnName: string): variant;
var aFieldsValues : TCSVFieldsValues;
begin
         Result := string('');
        try
                Current();
                Result := _lCSVStringValues[aColumnName]; 
         except
        on E: Exception do
                begin
                       
                        raise exception.create('Exception::TCSVMappedHandler ( FieldByname convertion :: '+E.Message+')');
                        exit;
                end;
        end;
end;
// ******************************************
// ******************************************
function TCSVMappedHandler.Current(): TCSVFieldsValues;
begin
    try
        result := ReadCSVRecordatIndex(_iFileCurrentPos);
    except
    on E: Exception do
        begin
        // ***** writeln('AssignFile :: Exception :: '+E.Message);
                raise Exception.Create('Exception::Current ('+E.Message+')');
        end;
    end;
end;
// ******************************************
// ******************************************
function TCSVMappedHandler.Next(): TCSVFieldsValues;
begin
        inc(_iFileCurrentPos);
        result := ReadCSVRecordatIndex(_iFileCurrentPos) ;
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.Prev(): TCSVFieldsValues;
begin
        if( (_iFileCurrentPos -1) >= 0) then
        begin
                dec(_iFileCurrentPos);
        end;
        result := ReadCSVRecordatIndex(_iFileCurrentPos) ;
end;
// ******************************************
// ******************************************
function TCSVMappedHandler.First(): TCSVFieldsValues;
begin
    try
        _iFileCurrentPos := 0 ; 
        result := ReadCSVRecordatIndex(_iFileCurrentPos) ;
    except
    on E: Exception do
        begin
        // ***** writeln('AssignFile :: Exception :: '+E.Message);
                raise Exception.Create('Exception::First ('+E.Message+')');
        end;
    end;
end;
// ******************************************
// ******************************************
function TCSVMappedHandler.Last(): TCSVFieldsValues;
begin
        _iFileCurrentPos := _iCSVCount ; 
        result := ReadCSVRecordatIndex(_iFileCurrentPos) ;
end;

// ******************************************
// ******************************************
function   TCSVMappedHandler.ReadCurrentFieldCount() : integer;
 begin
         
        // Current();
        result := _lCSVStringValues.count;
end;
// ******************************************
// ******************************************

function   TCSVMappedHandler.ReadCurrentFieldRecordAtIndex(vKeyIndex : variant) : string;
begin
        // Current();
        result :=  TCSVFieldsValues(_lCSVStringValues)[vKeyIndex];
end;
// ******************************************
// ******************************************

procedure  TCSVMappedHandler.WriteCurrentFieldRecordAtIndex(vKeyIndex : variant; aFieldValue : string);
begin
        // Current();
        _lCSVStringValues[vKeyIndex] := aFieldValue;
end;
// ******************************************
// ******************************************
function   TCSVMappedHandler.ReadCSVRecordatIndex(iKeyIndex : Longint) : TCSVFieldsValues;
        var iCurrentLineFieldIndex                       : Integer; // position
        var iCurrentMapFieldIndex                      : Integer;
        
        var sCurrentFileData                       : String;
        var sCurrentFileDataExtracted           : String;
        var sCurrentFileDataField                  : String;

begin
        iCurrentLineFieldIndex := 0;
        iCurrentMapFieldIndex := 0;
        
        sCurrentFileData := '';
        
         sCurrentFileDataExtracted      := '';
         sCurrentFileDataField             := '';
        
        // :: Not Usable (rec 255 char) :: iFileCurrentPos := FilePos(hCSVFileHandle);	
     
        if (iKeyIndex  >=0) and (iKeyIndex <= Count)  then
        begin
              try  
                    if ( ( Count = 0)   )  then 
                    begin
                        
                            if(not assigned(_lCSVStringValues)) then
                            begin
                                _lCSVStringValues := TCSVFieldsValues.create();
                            end;
                            
                            
                            if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) then
                            begin
                                    _lCSVStringValues.SetMapField( (_aCSVHeaderMapping) );
                                    SetAjustFieldMap();
                             end;
                            
                           Result := _lCSVStringValues;
                           exit;
                    end;
            except
            on ErrParseCached: Exception do
                   begin
                          raise Exception.create('Get::ErrParseCached ('+ErrParseCached.message+')');
                   end;
            end;
                

                
                try
                // ******************************************
                // ******************************************
                 //	seek(hCSVFileHandle, iKeyIndex);
        
                
                // writeln('File Position : '+IntToStr(iFileCurrentPos)+' / index : '+IntToStr(iKeyIndex));
                if (_iFileCurrentPos >iKeyIndex) then
                begin
                
                        
                        Reset(_hCSVFileHandle);
                        _iFileCurrentPos := 0; // :: FilePos(hCSVFileHandle);
                        // writeln('**** RESET File Position **** :  index : '+IntToStr(iKeyIndex));
                end;
        
                 
                if((_iFileCurrentPos = iKeyIndex)) and (_lCSVStringValues.count >0) and ( (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) and _lCSVStringValues.FieldKeyExists('__FILEDATE__') )  then
                begin
                        // inc(iFileCurrentPos);
                        // readln(hCSVFileHandle, sCurrentFileData);
                        // **********************************************
                        // **********************************************
                          // **********************************************
                        // **********************************************
                        if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) then
                        begin
                                 if(  not _lCSVStringValues.FieldKeyExists('__FILEDATE__') )  then
                                begin
                                        raise exception.create('Corrupted MApFields .... ');
                                end;
                        end;
                     
                        Result := _lCSVStringValues;
                        exit ; 
                end
                else if((0 = iKeyIndex))  then
                begin
                        Reset(_hCSVFileHandle);
                        _iFileCurrentPos := 0; // :: FilePos(hCSVFileHandle);
                        readln(_hCSVFileHandle, sCurrentFileData); 
                end
                else
                begin 
                        
                        while (not SeekEof(_hCSVFileHandle)) and (_iFileCurrentPos < iKeyIndex) do
                        begin
                                readln(_hCSVFileHandle, sCurrentFileData);	
                                // writeln('>>>> SEEK File Position **** :  index : '+IntToStr(iKeyIndex)+':@'+IntToStr(iKeyIndex + 1)+' : '+IntToStr(iFileCurrentPos)+'##'+IntToStr(FilePos(hCSVFileHandle))+' :: '+sCurrentFileData);	
                                inc(_iFileCurrentPos);
                        end;
                end;
                
        
                // seprator empty or trailing char
                if (_iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiCSVFieldType ) then
                begin
                        if( (length(_sCSVSeparator) = 0) or (_sCSVSeparator = ' ' )) then
                        begin
                                raise exception.create('Exception::CSVHandler (CSVSeparator invalid) ');
                                exit;
                        end;
                end;
                // writeln('>>>> END :::: SEEK File Position **** :  index : '+IntToStr(iKeyIndex)+':@'+IntToStr(iKeyIndex + 1)+' : '+IntToStr(iFileCurrentPos)+'##'+IntToStr(FilePos(hCSVFileHandle))+' :: '+sCurrentFileData);
                iCurrentLineFieldIndex := -1 ;
                
                if (  length(_sCSVSeparator)>0 )  then
                begin
                        iCurrentLineFieldIndex := AnsiPos(_sCSVSeparator, sCurrentFileData);
                end;
                
                // ******************************************
                // ******************************************
                // Detection de la coherence de format
                {if  (iCurrentLineFieldIndex <= 0 )  and ( not (_iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiFIXEDFieldType  ) )  then
                begin
                        // Matraitance de FIXED Detected
                        raise exception.create('Exception::ReadCSVRecordatIndex (Cant parse FIXED as Anonymous Format ....) ');
                          exit;
                end
                else
                }
                if ( iCurrentLineFieldIndex > 0 ) and ( _iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiFIXEDFieldType  ) then
                begin
                         // Matraitance de CSV Detected
                        raise exception.create('Exception::ReadCSVRecordatIndex (Cant parse CSV AS FIXED Format ....) ');
                          exit;
                end
               
                else if ( iCurrentLineFieldIndex > 0 ) and ( not (_iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiCSVFieldType ) ) then
                begin
                         // Maltraitance de Champ Libre Detected
                        raise exception.create('Exception::ReadCSVRecordatIndex (Cant parse CSV AS Anonymous Format ....) ');
                          exit;
                end
               
                else if ( _iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiUNKNOWFieldType  ) then
                begin
                         // Maltraitance de Champ Libre Detected
                        raise exception.create('Exception::ReadCSVRecordatIndex (Cant parse Anonymous Unknow Format ....) ');
                        exit;
                end
               
                else
                begin
                       // Bonheur Detected 
                end;
                // ******************************************
                // ******************************************
                        _lCSVStringValues.clear();
                        // flush(_hCSVFileHandle);
                        while (not (iCurrentLineFieldIndex = 0 )) do
                        Begin
                        
                                // ************* ***********
                                // :: Application.ProcessMessages;
                                // ************* ***********
                                // ******************************************
                                // ******************************************
                                //  Fichier Champ Taille Fixe
                                // ******************************************
                                // ******************************************
        
                                if (_iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiFIXEDFieldType ) then
                                begin
                                        if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) and ( _aCSVHeaderMapping.count >  iCurrentMapFieldIndex )  then
                                        begin
                                                // simplement la longueur du champ ....
                                                iCurrentLineFieldIndex := (_aCSVHeaderMapping.MapList[iCurrentMapFieldIndex]) [TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldLength] ;  
                                        end
                                        else
                                        begin
                                                iCurrentLineFieldIndex := 0; // Wrong Type
                                        end;
                                end
                                else
                                begin
                                        // ******************************************
                                        // ******************************************
                                        // CSV FILE DATA Type
                                        // ******************************************
                                        // ******************************************
        
                                        iCurrentLineFieldIndex := AnsiPos(_sCSVSeparator, sCurrentFileData);        
                                end;
                                
                                // ******************************************
                                // ******************************************
        
                                if (iCurrentLineFieldIndex >0) then
                                begin
                                        // Recuperation du Field ...
                                         if (_iCSVFileType = TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiFIXEDFieldType ) then
                                        begin
                                              sCurrentFileDataField :=  LeftStr(sCurrentFileData, iCurrentLineFieldIndex);
                                        end
                                        else
                                        begin
                                                sCurrentFileDataField :=  LeftStr(sCurrentFileData, iCurrentLineFieldIndex-1);
                                        end;
                                        // ajout du field dans la valeur de retour ....
                                        _lCSVStringValues.Add(sCurrentFileDataField);
                                        // Recuperation du Restant ...
                                        sCurrentFileData := RightStr(sCurrentFileData, Length(sCurrentFileData) - (iCurrentLineFieldIndex));
                                end;
                               // ******************************************
                                // ******************************************
                               inc(iCurrentMapFieldIndex);
                               // ******************************************
                                // ******************************************
        
                        end;
                        // ******************************************
                        // ******************************************
                        if(Length(sCurrentFileData)>0) then
                        begin
                                _lCSVStringValues.Add(sCurrentFileData);
                        end;
                        // ******************************************
                        // prepare  les valeurs de retour
                        // ******************************************
                        //iCurrentLineFieldIndex := lCSVStringValues.Count; 
                        // SetLength(Result, iCurrentLineFieldIndex );
                        iCurrentLineFieldIndex :=  0;
                        // ******************************************
                        // ******************************************
                        // for iCurrentLineFieldIndex := 0 to lCSVStringValues.Count do
                        //begin
                        //	Result[iCurrentLineFieldIndex] := lCSVStringValues.Strings[iCurrentLineFieldIndex];
                        //end;
                        // ******************************************
                        // ******************************************
                except
                on ErrParse: Exception do
                        begin
                               raise Exception.create('Get::ErrParse ('+ErrParse.message+')');
                        end;
                end;
                
        end
        else
        begin
                
                raise Exception.create('Get::Index Out Of Bounds ('+intToStr(iKeyIndex)+'/'+intToStr(count)+')');
        end;
         
         try   
                // **********************************************
                // **********************************************
                 if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) then
                 begin
                    _lCSVStringValues.SetMapField( (_aCSVHeaderMapping) );
                end;
                SetAjustFieldMap();
        except
        on Err: Exception do
                begin
                        raise exception.create('Exception :: '+Err.Message);
                end;
        end;
        // *******************************************
        Result := _lCSVStringValues;
        // *******************************************
end;

// ******************************************
// ******************************************
procedure  TCSVMappedHandler.WriteCSVRecordatIndex(iKeyIndex : Integer; aValueArray: Variant );
begin
        raise exception.create('Exception::WriteCSVRecord:: UNimplemented ');
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.SetAjustFieldMap(): boolean;
var iCurrentLineFieldIndex : integer;
var iCurrentFieldsCount : integer;
begin
        
        try
        begin
                // **********************************************
                // **********************************************
                // chaque Resultat Contient Une MapFields ....
                // **********************************************
                // ajoute les lignes special
                if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) then
                begin
                        
                          {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('XXXXDATE :: Assign'); {$ENDIF}
                         
                         
                        if ((_aCSVHeaderMapping.count)+3 > _lCSVStringValues.count) then
                        begin
                              iCurrentLineFieldIndex :=  0;
                              iCurrentFieldsCount := _aCSVHeaderMapping.count-1;
                              for iCurrentLineFieldIndex :=_lCSVStringValues.count to iCurrentFieldsCount do
                              begin
                                      {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln(' ***** Low _aCSVHeaderMapping :: '+inttostr(iCurrentLineFieldIndex)+'::'+inttostr(_aCSVHeaderMapping.count-1));{$ENDIF}
                                    _lCSVStringValues.Add(''); // ajout de valeurs vide .... 
                              end;
                        end;
                        
                        {if ((_lCSVStringValues.count) > _aCSVHeaderMapping.count) then
                        begin
                              iCurrentLineFieldIndex :=  0;
                              for iCurrentLineFieldIndex :=_aCSVHeaderMapping.count to _lCSVStringValues.count-1 do
                              begin
                                      writeln(' ***** Low _aCSVHeaderMapping :: '+inttostr(iCurrentLineFieldIndex)+'::'+inttostr(_aCSVHeaderMapping.count-1));
                                      // _lCSVStringValues.Add('=='); // ajout de valeurs vide .... 
                              end;
                        end;}
                        
                        
                        //_lCSVStringValues.SetMapField( (_aCSVHeaderMapping) );
                        
                        // ajoute pour nouvelle ligne ;; la ligne contient le mapping 
                        // compare le mapping et ajoute les colonnes special
                        if(  not _lCSVStringValues.FieldKeyExists('__FILEDATE__') )  then
                        begin
                               
                                _lCSVStringValues.Add('__FILEDATE__', _sCSVFileDate);
                                _lCSVStringValues.Add('__FILEDATENAME__', _sCSVFileName);
                                _lCSVStringValues.Add('__FILENAME__', _sCSVFileNameDate);
                        end;
                         
                        
                                _lCSVStringValues['__FILEDATE__'] := _sCSVFileDate;
                                _lCSVStringValues['__FILENAME__'] :=_sCSVFileName ;
                                _lCSVStringValues['__FILEDATENAME__'] :=_sCSVFileNameDate ;
                        
                          {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('XXXXDATE :: '+_lCSVStringValues['__FILEDATE__']);  {$ENDIF}
                        result := true;
                        exit;
                 end;
        end;
        except
        on E: Exception do
                begin
                        {$IFDEF DEBUG_VERBOSE_INTERNAL}
                                writeln (' ***************************************** ');
                                writeln('Exception::SetAjustFieldMap ('+E.Message+')');
                                writeln (' ***************************************** ');
                        {$ENDIF}
                       raise exception.create('Exception::Main::Importation ('+E.Message+')'); 
                end;
        end;
         result := false;
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.ReadCSVHeaderCount():integer;
begin
        
        if not assigned (_aCSVHeaderMapping ) then
        begin
                _aCSVHeaderMapping := TCSVArrayMappingValues.create();
                raise exception.create('Exception::ReadCSVHeaderCount:: Header is not assigned ... ( forgotten [Object].Create() ?? )');
        end;
        Result := _aCSVHeaderMapping.Count;
end;

// ******************************************
// ******************************************
function    TCSVMappedHandler.ReadCSVHeader(vKeyIndex : variant) : TCSVMapFieldValuesStorageFieldValues;
begin
    // writeln('ReadCSVHeader :: '+inttostr(count));
    
    if not assigned (_aCSVHeaderMapping ) then
    begin
        _aCSVHeaderMapping := TCSVArrayMappingValues.create();
        raise exception.create('Exception::ReadCSVHeader:: Header is not assigned ... ( forgotten [Object].Create() ?? )');
    end;
    
    
        Result := _aCSVHeaderMapping[vKeyIndex];
end;

// ******************************************
// ******************************************
procedure   TCSVMappedHandler.WriteCSVHeader(vKeyIndex : variant; aValueArray: TCSVMapFieldValuesStorageFieldValues );
begin

end;

// ******************************************
// ******************************************
// Methods CSV
// ******************************************
function TCSVMappedHandler.AssignFileHeader( aCSVHeaderNamesObjectArray : array of string; iUseNames : boolean = true ) : Boolean;
var iCSVColumnCount : integer;
var aCSVMappingInfo : TCSVFieldsValues;
begin
        try
                aCSVMappingInfo := TCSVFieldsValues.create();
                for  iCSVColumnCount := 0 to high(aCSVHeaderNamesObjectArray) do
                begin
                        aCSVMappingInfo.Add(aCSVHeaderNamesObjectArray[iCSVColumnCount]);
                end;
               Result := AssignFileHeader( aCSVMappingInfo, iUseNames);
        except
        on E: Exception do
                begin
                        // ***** writeln('AssignFile :: Exception :: '+E.Message);
                       raise exception.create('Exception::AssignFileHeader ('+E.Message+')'); 
                end;
        end;
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.AssignFileHeader( aCSVHeaderNamesObjectArray : TCSVFieldsValues; iUseNames : boolean = true ) : Boolean;
var iCSVColumnCount : integer;
var aCSVMappingInfo     : TCSVMapFieldValuesStorageFieldValues;
var aCSVLineFields         : TCSVFieldsValues;
var sCSVColumnValue     : string;
var iCSVColumnAssigned : integer;
var iCSVColumnFound : integer;
var iLLiCSVColumnFree : integer;
var lFieldIdxReserved     : TStringList;
var lFieldIdxUNReserved : TStringList;
var bFieldNotfound         :boolean;

{function SortUNListed(Item1, Item2: Pointer): Integer;
begin
  Result := CompareText(string(Item1^), string(Item2^));
//  MessageDlg('Compare ' + TMyClass(Item1).MyString + ' to ' + TMyClass(Item2).MyString,
//                 mtInformation, [mbOk], 0);
end;}

begin
    try
            if not assigned (_aCSVHeaderMapping ) then
            begin
                _aCSVHeaderMapping := TCSVArrayMappingValues.create();
                raise exception.create('Exception::AssignFileHeader::Header is not assigned ... ( forgotten [Object].Create() ?? )');
                exit;
            end;
     
            _aCSVHeaderMapping.Destroy();
            _aCSVHeaderMapping := TCSVArrayMappingValues.create();
            aCSVLineFields := TCSVFieldsValues.Create();
            lFieldIdxReserved   := TStringList.create();
            lFieldIdxUNReserved := TStringList.create();
             
            aCSVLineFields := First();
            
           iCSVColumnFound :=  aCSVHeaderNamesObjectArray.count -1;
           
            if(  aCSVHeaderNamesObjectArray.FieldKeyExists('__FILEDATE__') )  then
            begin
                    iCSVColumnFound := iCSVColumnFound -3;
            end;
           
            iLLiCSVColumnFree := 0;
        
            for  iCSVColumnCount := 0 to (iCSVColumnFound) do
            begin
                    
                    if (iUseNames) then
                    begin
                            sCSVColumnValue := string(aCSVHeaderNamesObjectArray.Strings[iCSVColumnCount]);
                    end
                    else
                    begin
                           sCSVColumnValue := inttostr(iCSVColumnCount); 
                    end;
                    
                    aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                    
                    
                    aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kvStorageFieldIdx ]  := '**Unknow**';
    
                    //  Dynamicly assign Column index and  column name
                    // :: '5:name', '2:othername'
                    iCSVColumnAssigned := aCSVMappingInfo.FromString(sCSVColumnValue);
                    // :: writeln(' aCSVMappingInfo.FromString  :: '+intToStr(iCSVColumnAssigned)+'::'+sCSVColumnValue);
                     if ( iCSVColumnAssigned <=1 ) then
                    begin
                             
                            aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := iCSVColumnCount;
                            
                            // lFieldIdxReserved.Sorted := true;
                            
                            // looking for free position ...
                            for iLLiCSVColumnFree := 0 to _aCSVHeaderMapping.count-1 do
                            begin
                                    if  (not   lFieldIdxReserved. Find( string(inttostr(iLLiCSVColumnFree)), iCSVColumnAssigned) ) 
                                    then
                                  
                                    begin
                                        // ::  writeln('Found Free position for concurrent ... '+inttostr(_aCSVHeaderMapping[iLLiCSVColumnFree][ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] )+'::TO::'+inttostr(iLLiCSVColumnFree));
                                         aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := iLLiCSVColumnFree;
                                         break;
                                    end;
                            end;
                            aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName ]  := sCSVColumnValue;
                    end;
                 
                    sCSVColumnValue := aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ];
                 
                 if(not assigned(lFieldIdxReserved)) then
                 begin
                        raise exception.create('Vanished (lFieldIdxReserved)');
                        exit;
                 end;
                   lFieldIdxReserved.add( sCSVColumnValue );
                    
                    {$IFDEF DEBUG_VERBOSE_INTERNAL} Writeln(' >>>> Assign reserved fields ' +aCSVMappingInfo.ToString()); {$ENDIF}
                   _aCSVHeaderMapping.Add(aCSVMappingInfo);
            end;
        except
        on E: Exception do
            begin
                    {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('AssignFile :: Exception :: '+E.Message); {$ENDIF}
                   raise exception.create('Exception::AssignFileHeader ('+E.Message+')'); 
            end;
        end;
        // ************* ***********
        // ************* ***********
        // ************* ***********
        // ************* ***********
        try
            if (_iCSVCount >=1) then
            begin
                    
                // lFieldIdxReserved.Sorted := true;
                
                // use First Line
                iCSVColumnFound := aCSVLineFields.count -1;
                 if(  aCSVLineFields.FieldKeyExists('__FILEDATE__') )  then
                begin
                        iCSVColumnFound := iCSVColumnFound -3;
                end;
                
                for  iCSVColumnCount := 0 to iCSVColumnFound do
                begin
                        // ************* ***********
                        bFieldNotfound := false;
                        // ************* ***********
                        if (lFieldIdxReserved.count =0) then
                        begin
                               bFieldNotfound := true;  
                        end
                        else if  (not  ( lFieldIdxReserved. Find( string(inttostr(iCSVColumnCount)), iCSVColumnAssigned) ) )  then
                        begin
                                bFieldNotfound := true;
                        end;
                        // ************* ***********
                        if(bFieldNotfound) then
                        begin
                                aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                                sCSVColumnValue  := inttostr(iCSVColumnCount);
                                iCSVColumnAssigned := aCSVMappingInfo.FromString(sCSVColumnValue);
                                // Writeln(' >>>> Assign overflowed fields ' +aCSVMappingInfo.ToString());
                                aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := iCSVColumnCount;
                                {$IFDEF DEBUG_VERBOSE_INTERNAL} Writeln(' Assign overflowed fields ' +aCSVMappingInfo.ToString()); {$ENDIF}
                                lFieldIdxUNReserved.add(string(aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ]));
                                
                                _aCSVHeaderMapping.add(TCSVMapFieldValuesStorageFieldValues(aCSVMappingInfo));
                
                        end;
                        // ************* ***********
                        // :: Application.ProcessMessages;
                        // ************* ***********
                end;
            end;
        except
        on E: Exception do
                begin
                        {$IFDEF DEBUG_VERBOSE_INTERNAL} writeln('AssignFile :: Exception :: '+E.Message); {$ENDIF}
                       raise exception.create('Exception::AssignFileHeader ('+E.Message+')'); 
                end;
        end;
        
        //  Order fields by Internal Definition Index ... kiCSVFieldIdx
        //_aCSVHeaderMapping.Sort();
        {$IFDEF DEBUG_VERBOSE_INTERNAL}
        Writeln('**************');
        for  iCSVColumnCount := 0 to _aCSVHeaderMapping.count-1 do
        begin
                Writeln(' Sorted fields ' +_aCSVHeaderMapping.MapList[iCSVColumnCount].ToString());
                
                        // ************* ***********
                        // :: Application.ProcessMessages;
                        // ************* ***********
        end;
        {$ENDIF}
         {  
                 try
                                
                           
                                aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                                sCSVColumnValue  := '__FILEDATE__' ;
                                iCSVColumnAssigned := aCSVMappingInfo.FromString(sCSVColumnValue);
                                // Writeln(' >>>> Assign overflowed fields ' +aCSVMappingInfo.ToString());
                                aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := _aCSVHeaderMapping.count;
                                // :: Writeln(' Assign SPECIAL fields ' +aCSVMappingInfo.ToString());
                               // lFieldIdxUNReserved.add(string(aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ]));
                                _aCSVHeaderMapping.add(TCSVMapFieldValuesStorageFieldValues(aCSVMappingInfo));
                              
                              
                                aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                                sCSVColumnValue  := '__FILEDATENAME__' ;
                                iCSVColumnAssigned := aCSVMappingInfo.FromString(sCSVColumnValue);
                                // Writeln(' >>>> Assign overflowed fields ' +aCSVMappingInfo.ToString());
                                aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := _aCSVHeaderMapping.count;
                                // :: Writeln(' Assign SPECIAL fields ' +aCSVMappingInfo.ToString());
                               // lFieldIdxUNReserved.add(string(aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ]));
                                _aCSVHeaderMapping.add(TCSVMapFieldValuesStorageFieldValues(aCSVMappingInfo));
                                aCSVMappingInfo := TCSVMapFieldValuesStorageFieldValues.create();
                                
                                sCSVColumnValue  := '__FILENAME__' ;
                                iCSVColumnAssigned := aCSVMappingInfo.FromString(sCSVColumnValue);
                                // Writeln(' >>>> Assign overflowed fields ' +aCSVMappingInfo.ToString());
                                aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ] := _aCSVHeaderMapping.count;
                                // :: Writeln(' Assign SPECIAL fields ' +aCSVMappingInfo.ToString());
                               // lFieldIdxUNReserved.add(string(aCSVMappingInfo[ TCSVMapStorageArrayValues_Definition.PropertyMapIndex.kiCSVFieldIdx ]));
                                _aCSVHeaderMapping.add(TCSVMapFieldValuesStorageFieldValues(aCSVMappingInfo));
                                
                                
                                
                
                        except
                        on E: Exception do
                                begin
                                     raise exception.create('Exception::AssignFileHeader('+E.Message+')');
                                end;
                        end;}
                
                
        // :: Writeln('**************');
       { if(  aCSVLineFields.FieldKeyExists('__FILEDATE__') )  then
        begin
                next();
                prev();
        end;}
	Result := true;
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.ParseFileHeader( iFistLineNamed: boolean = false) : Boolean;
var aFieldHeader : TCSVFieldsValues;
begin
    try
        aFieldHeader := TCSVFieldsValues.create();
        aFieldHeader := First(); 
        Result :=         AssignFileHeader(aFieldHeader, iFistLineNamed);
    except
    on E: Exception do
        begin
                // ***** writeln('AssignFile :: Exception :: '+E.Message);
               raise exception.create('Exception::ParseFileHeader ('+E.Message+')'); 
        end;
    end;
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.AssignFileOutput(sFilePath : string) : Boolean;
begin
        Raise Exception.Create('Unimplemented::AssignFileOutput');
end;

// ******************************************
// ******************************************
function TCSVMappedHandler.AssignFileInput(sFilePath : string) : Boolean;
    var aFileValidPath : boolean;
    var aFileNamePath : string;
    var aFileNameTrailing : boolean;
    var aRegexNumeric : TRegExpr;
    var iCSVCount_Last : integer;
    var iCsvLineRecordsIndex : integer;
    var aFileDateTime : TDateTime ;
    var sTempFileDateTime : string ;
    var sTempFileDateSelected : string ;
    
    var sTempFileDateTime_Day : string;
    var sTempFileDateTime_Month : string;
    var sTempFileDateTime_Year : string;
    
    var sTempFileDateTime_B_Day : string;
    var sTempFileDateTime_B_Month : string;
    var sTempFileDateTime_B_Year : string;
    
    var aDateFormat : TFormatSettings;
begin
	_iCSVCount := 0;
        iCSVCount_Last := 0;
        
	_iFileCurrentPos := 0; 
	Result := False;
	_sCSVFileName := '';
        _sCSVFileNameDir := '';
        
         _sCSVFileDate := '';
        _sCSVFileNameDate := _sCSVFileName;
        
        aFileNameTrailing := false;
        aFileValidPath := false;
        
        aRegexNumeric := TRegExpr.create();
        
        
	try
		// ******************************************
		// ******************************************
		
		_sCSVFileName := ExtractFileName(sFilePath);
		_sCSVFileNameDir := ExtractFileDir(sFilePath);
                
               // not DirectoryExists( ExtractFileDir(sFilePath) )
               
               if( (length(_sCSVFileNameDir) <3) or (not DirectoryExists( _sCSVFileNameDir )) ) then
               begin
                      _sCSVFileNameDir := GetCurrentDir(); 
               end;
               
               while ( AnsiPos('..', _sCSVFileNameDir) >0 ) and (length(_sCSVFileNameDir) > 3) do
                begin 
                      _sCSVFileNameDir := ExtractFileDir(_sCSVFileNameDir);
                      aFileNameTrailing := true;
                end;
                
               
		if ( FileExists(sFilePath ))  then
		begin
                        aFileNamePath := sFilePath;
                        aFileValidPath := true;
                end
                else if ( FileExists( _sCSVFileNameDir+''+PathDelim+''+sFilePath ))  then
		begin
                        aFileNamePath := _sCSVFileNameDir+''+PathDelim+''+sFilePath;
                        aFileValidPath := true;
                end
                else if ( FileExists(  ExtractFileDir(ExtractFileDir(_sCSVFileNameDir))+''+PathDelim+''+sFilePath ) )  then
                begin
                        aFileNamePath :=  ExtractFileDir(ExtractFileDir(_sCSVFileNameDir))+''+PathDelim+''+sFilePath ;
                        aFileValidPath := true; 
                end;
                
                
                
                
             
		if ( aFileValidPath ) then
		begin
			AssignFile(_hCSVFileHandle, aFileNamePath);	
			Reset(_hCSVFileHandle);
			// ******************************************
			// ******************************************
			while (not SeekEof (_hCSVFileHandle)) do
			Begin
                                // ************* ***********
                               
                             
                               if(  ( _iCSVCount  ) = (iCSVCount_Last )   ) then
                               begin  // :: Application.ProcessMessages;
                                        // Application.HandleMessage
                                        iCSVCount_Last := (_iCSVCount +1000);
                                        {$IFDEF DEBUG_VERBOSE_INTERNAL}
                                        writeln('>> AssignFileInput::Recherche ... Pos('+inttostr(_iCSVCount)+')');
                                        {$ENDIF}
                                        // ::
                                        Application.ProcessMessages;
                                         
                                end;
                               
                                
				Inc(_iCSVCount);
				ReadLn(_hCSVFileHandle);
                                
                                
                                // ************* ***********
			end;
			// ******************************************
                        _sCSVFileName := ExtractFileName(aFileNamePath);
                        
                        _sCSVFileNameDir := ExtractFileDir(aFileNamePath);
                        if( length(_sCSVFileNameDir) =0) then
                        begin
                              _sCSVFileNameDir  := GetCurrentDir();
                        end;
                        
                        // **************************
                        // **************************
                        
                        _sCSVFileDate := StringReplace( DateToStr(FileDateToDateTime( FileAge( _sCSVFileNameDir +''+PathDelim+''+_sCSVFileName ) )),'/','', [rfReplaceAll, rfIgnoreCase])  ;
                        _sCSVFileNameDate := '';
                        sTempFileDateSelected := '';
                        
                        // **************************
                        // **************************
                        // recherche de date ou numeric dans le nom de fichier
                        try
                        
                                aRegexNumeric.Expression := '([a-z|A-Z|\_]+)([0-9]+).([a-z|A-Z]+)';
                                if (aRegexNumeric.Exec(_sCSVFileName) AND (length(aRegexNumeric.Match[2]) > 5 ) ) then
                                begin
                                        try
                                               if ( ( strtoint(aRegexNumeric.Match[2]) > 12010 ) ) then // :: date >= 01012018
                                               begin
                                                       _sCSVFileNameDate := aRegexNumeric.Match[2];
                                               end;
                                        except
                                        on E: Exception do
                                                begin
                                                        ;; // :: raise Exception.Create('Exception::AssignFileInput::not a number ('+E.Message+')');
                                                end;
                                        end;
                                end; 
                        except
                        on E: Exception do
                                begin
                                        ;; // :: raise Exception.Create('Exception::AssignFileInput:: ('+E.Message+')');
                                end;
                        end;
                                
                        // **************************
                        // **************************
                        if(length(_sCSVFileNameDate) <3) then
                        begin
                                 try
                                        aRegexNumeric.Expression := '([0-9]+)([a-z|A-Z|\_]+).([a-z|A-Z]+)';
                                          if (aRegexNumeric.Exec(_sCSVFileName) AND (length(aRegexNumeric.Match[1]) > 5 ) ) then
                                        begin
                                                try
                                                       if ( ( strtoint(aRegexNumeric.Match[1]) > 12010 ) ) then // :: date >= 01012018
                                                       begin
                                                               _sCSVFileNameDate := aRegexNumeric.Match[1];
                                                       end;
                                                except
                                                on E: Exception do
                                                        begin
                                                                ;; // :: raise Exception.Create('Exception::AssignFileInput::not a number ('+E.Message+')');
                                                        end;
                                                end;
                                        end; 
                                except
                                on E: Exception do
                                        begin
                                                ;; // :: raise Exception.Create('Exception::AssignFileInput::not a number ('+E.Message+')');
                                        end;
                                end;
                        end;
                        
                        // **************************
                        // **************************
                        
                        if(length(_sCSVFileNameDate) <3) then       
                        begin
                                try 
                                        aRegexNumeric.Expression := '([0-9]+).([a-z|A-Z]+)';
                                        if (aRegexNumeric.Exec(_sCSVFileName) AND (length(aRegexNumeric.Match[1]) > 5 ) ) then
                                        begin
                                                try
                                                       if ( ( strtoint(aRegexNumeric.Match[1]) > 12010 ) ) then // :: date >= 01012018
                                                       begin
                                                               _sCSVFileNameDate := aRegexNumeric.Match[1];
                                                       end;
                                                except
                                                on E: Exception do
                                                        begin
                                                                ;; // :: raise Exception.Create('Exception::AssignFileInput::not a number ('+E.Message+')');
                                                        end;
                                                end;
                                        end;       
                                except
                                on E: Exception do
                                        begin
                                                ;; // :: raise Exception.Create('Exception::AssignFileInput:: ('+E.Message+')');
                                        end;
                                end;
                        end;
                        
                        // **************************
                        // **************************
                        if(length(_sCSVFileNameDate) <3) then     
                        begin
                                _sCSVFileNameDate := _sCSVFileDate;
                        end;
                       
 
                        //  20180821 >>>> 21 08 2018
                               sTempFileDateTime_Year :=   RightStr(_sCSVFileNameDate, 4);
                               
                               sTempFileDateTime_Day :=   LeftStr(_sCSVFileNameDate, 2);
                               
                               sTempFileDateTime_Month :=   RightStr( LeftStr(_sCSVFileNameDate, 4) , 2);
                               
                        //  20180821 >>>> 21 08 2018
                               sTempFileDateTime_B_Year :=   LeftStr(_sCSVFileNameDate, 4);
                               
                               sTempFileDateTime_B_Day :=   RightStr(_sCSVFileNameDate, 2);
                               
                               sTempFileDateTime_B_Month :=  LeftStr ( RightStr(_sCSVFileNameDate, 4) , 2);
                               
                               
                               
                                sTempFileDateTime :=  sTempFileDateTime_Day +'/'+ sTempFileDateTime_Month +'/'+ sTempFileDateTime_Year;
                                // :: +' 09:00:00 AM';
                                
                       try
                       
                               
                                aFileDateTime := StrToDate(sTempFileDateTime);
                                sTempFileDateSelected := FormatDateTime('ddmmyyyy', aFileDateTime);
                        except
                        on ErrFormatDateFRFR: Exception do
                                begin
                                        try
                                                
                                                aDateFormat.ShortDateFormat :='dd/mm/yyyy';
                                                aDateFormat.LongDateFormat :='dd/mm/yyyy';
                                                //aDateFormat.DateSeparator :=  char(0);
                                        
                                                aFileDateTime := StrToDate(sTempFileDateTime, aDateFormat );
                                                sTempFileDateSelected := FormatDateTime('ddmmyyyy', aFileDateTime);
                                        except
                                        on ErrFormatDateENGB: Exception do
                                                begin
                                                        try
                                                               aDateFormat.ShortDateFormat :='yyyy/mm/dd';
                                                                aDateFormat.LongDateFormat :='yyyy/mm/dd';
                                                                //aDateFormat.DateSeparator :=   char(0);
                                                                aFileDateTime := StrToDate(sTempFileDateTime, aDateFormat );
                                                                 sTempFileDateSelected := FormatDateTime('ddmmyyyy', aFileDateTime);
                                                        except
                                                        on ErrFormatDateMM: Exception do
                                                                begin
                                                                        try
                                                                              
                                                                                    sTempFileDateTime :=  sTempFileDateTime_B_Day +'/'+ sTempFileDateTime_B_Month +'/'+ sTempFileDateTime_B_Year;
                                                                              aFileDateTime := StrToDate(sTempFileDateTime );
                                                                              sTempFileDateSelected := FormatDateTime('ddmmyyyy', aFileDateTime);
                                                                         except
                                                                         on ErrFormatDateFF: Exception do
                                                                                 begin
                                                                                         raise Exception.Create('Exception::AssignFileInput::FileDate ('+ErrFormatDateFF.Message+')');
                                                                                         exit;
                                                                                 end;
                                                                         end;
                                                                  
                                                                end;
                                                        end;
                                                  end;       
                                        end;
                                end;
                        end;
                        
                        if(length(sTempFileDateSelected)>5) then
                        begin
                                _sCSVFileNameDate := sTempFileDateSelected;
                        end;

                        // **************************
                        // **************************
                        
                        // ******************************************
                        // Writeln('CSVPath :: '+GetCurrentDir()+''+PathDelim+''+sFilePath );
                       // Writeln('CSVPath :: '+aFileNamePath);
                        Reset(_hCSVFileHandle);
                        ParseFileHeader();
                        
                         if (_aCSVHeaderMapping <> nil) and  ( _aCSVHeaderMapping.count >0) then
                        begin
                                 // :: writeln('DDDDDATE :: Assign');
                                // _lCSVStringValues.SetMapField( (_aCSVHeaderMapping) );
                                // SetAjustFieldMap();
                            
                                // :: writeln('DDDDDATE :: END');
                                
                                
                                
                               {$IFDEF DEBUG_VERBOSE_INTERNAL}
                               writeln('**Header ::** ');
                                iCsvLineRecordsIndex := 0;
                              
                                iCsvLineRecordsIndex := 0;
                               for iCsvLineRecordsIndex := 0 to  FileHeaderCount -1 do
                               begin
                                       writeln('Header :: '+inttostr(iCsvLineRecordsIndex)+'::'+ FileHeader[iCsvLineRecordsIndex] [TCSVMapStorageArrayValues_Definition.PropertyMapIndex.ksCSVFieldName] ); 
                               end;
                               
                               writeln('**Extrait Line ::** ');
                                for iCsvLineRecordsIndex := 0 to  FieldsCount -1 do
                               begin
                                       writeln('Extrait :: '+inttostr(iCsvLineRecordsIndex)+'::'+ FileCurrentFieldValue[iCsvLineRecordsIndex] ); 
                               end;
                                {$ENDIF}
                                
                                
                        end;
                end
                else
                begin
                        raise Exception.Create('Exception::AssignFileInput (Fichier Inexistant ou inaccessible ... ['+sFilePath+'] )');
                end;
        except
        on E: Exception do
                begin
                // ***** writeln('AssignFile :: Exception :: '+E.Message);
                        raise Exception.Create('Exception::AssignFileInput ('+E.Message+')');
                end;
        end;
        
        Result := True;

end;
// ******************************************
// ******************************************
end.
