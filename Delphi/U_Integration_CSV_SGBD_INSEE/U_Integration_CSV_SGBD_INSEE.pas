unit U_Integration_CSV_SGBD_INSEE;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}

interface


uses
{$IFnDEF FPC}
  VarCmplx, VarConv, Windows,
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
  Classes,
	StrUtils, SysUtils, Variants,
    // TObject List Definition dans Unit Contnrs
  Contnrs,
	U_CSVMappingStorage, U_CSVMappedHandler ,
	UGns_SQLConnection,
        inifiles
        ;
 // ***************************** 
 // ***************************** 
 // ***************************** 
type TIntegration_CSV_SGBD_INSEE = class(TObject)
        private
                var _hProgressionSessionParamsFile : TIniFile;
                var _FileHandle : TCSVMappedHandler;
                var _hStorageConnectionHandle	: TGns_SQLConnection;
                var _ResultsSQLFound : TObjectList;
                var _aFieldResultListNames : TStringList;
                var _CachedQueryParams : widestring;
                
                
                var _sRequeteSQL : widestring;
                var _sRequeteParamsSQL : widestring;
                
               
        public
                 // Methods  SBGD
                function   Open(): Boolean; overload;
                function   Close(): Boolean; overload;
                
                constructor Create; overload;
                destructor Destroy; overload;
                
                function ReadResultFound(): integer;
                 procedure  FTruncateTable(sAtable :string );

                function saveProgression(aKeyProgression : string; aErreurTraitement : boolean = false)      : boolean;
                function restoreProgression(aKeyProgression : string)   : integer;

                function SaveCurrent(aAssocFieldList : TStringList; const aTable: string; bCreateMode : boolean = false): Integer;
                function FieldRecordAssocExists(aFieldsAssoc : TStringList; const aSQLTableParam: string): integer;

                property StorageConnectionHandle : TGns_SQLConnection read _hStorageConnectionHandle write _hStorageConnectionHandle;
                property FileHandle : TCSVMappedHandler read _FileHandle write _FileHandle;

                property ResultFound : integer read ReadResultFound;

                
end;
 // ***************************** 
 // ***************************** 
 // ***************************** 
 
implementation

// ******************************************
// ******************************************
constructor TIntegration_CSV_SGBD_INSEE.Create();
begin
        inherited Create();
        _FileHandle := TCSVMappedHandler.Create();
        _FileHandle.FileTypeFormat := TCSVMapStorageArrayValues_Definition.PropertyMapFieldType.kiCSVFieldType;
        _hStorageConnectionHandle	:= TGns_SQLConnection.create(nil);
        _hProgressionSessionParamsFile := TIniFile.Create(GetCurrentDir()+''+PathDelim+''+'SessionIntegrationProgressionParams.ini' );
        // :: ( ChangeFileExt(Application.ExeName,'.ini' ) ;
        _ResultsSQLFound := TObjectList.create();
        
        
        _sRequeteSQL := '';
        _sRequeteParamsSQL := '';

  
end;

destructor TIntegration_CSV_SGBD_INSEE.Destroy;  
begin
    _hProgressionSessionParamsFile.Destroy();
    _FileHandle.Destroy();
    _hStorageConnectionHandle.Destroy();
    _ResultsSQLFound.Destroy();
    _aFieldResultListNames .Destroy();
    _CachedQueryParams  := '';
end;
// ******************************************
// ******************************************
        function TIntegration_CSV_SGBD_INSEE.ReadResultFound(): integer;
        begin
                Result := _ResultsSQLFound.count;
        end;
// ******************************************
// ******************************************
       
        function TIntegration_CSV_SGBD_INSEE.saveProgression(aKeyProgression : string; aErreurTraitement : boolean = false):boolean;
        begin
                // hProgressionSessionParamsFile.ReadString();
                // read  file fields :: session_progression.ini
                result := false;
                // ******************************************
                // ******************************************
                 
                
                 if(length(aKeyProgression)< 3 ) then
                begin
                        raise exception.create('Exeception::restoreProgression (Empty / invalid Identifier ['+aKeyProgression+'])');
                        exit;
                end;

                // ******************************************
                // ******************************************
                       
                try
                         _hProgressionSessionParamsFile.EraseSection('progression_integration_'+aKeyProgression);
                         _hProgressionSessionParamsFile.WriteString('progression_integration_'+aKeyProgression, 'progression_integration_pos', 'Position a partir de Zero' );
                         _hProgressionSessionParamsFile.WriteInteger('progression_integration_'+aKeyProgression, aKeyProgression+'_pos', _FileHandle.FileCurrentPos+1 );
                         _hProgressionSessionParamsFile.WriteString('progression_integration_'+aKeyProgression, 'progression_integration_count', 'Position a partir de Zero' );
                         _hProgressionSessionParamsFile.WriteInteger('progression_integration_'+aKeyProgression, aKeyProgression+'_count', _FileHandle.count );
                         _hProgressionSessionParamsFile.WriteString('progression_integration_'+aKeyProgression, aKeyProgression+'_date', DateToStr(SysUtils.Now()) );
                         
                        // ******************************************
                        // ******************************************
                          
                         if( aErreurTraitement ) then
                         begin
                                _hProgressionSessionParamsFile.WriteString('progression_integration_'+aKeyProgression, aKeyProgression+'_erreur', 'OUI' );
                         end
                         else
                         begin
                                 _hProgressionSessionParamsFile.WriteString('progression_integration_'+aKeyProgression, aKeyProgression+'_erreur', 'NON' );
                         end;
                                
                       // ******************************************
                       // ******************************************
                         
                         _hProgressionSessionParamsFile.UpdateFile;
                         Result := true;
                except
                on E: Exception do
                        begin
                               raise exception.create('Exeception::saveProgression ('+E.message+')');
                               exit;
                        end;
                end ;  
        end;
// ******************************************
// ******************************************
       
        function  TIntegration_CSV_SGBD_INSEE.restoreProgression(aKeyProgression : string): integer;
        var sValueString : string;
        begin
                // hProgressionSessionParamsFile.WriteString();
                // read  file fields :: session_progression.ini
                result := 0;
                                
                // ******************************************
                // ******************************************
                       
                if(length(aKeyProgression)< 3 ) then
                begin
                        raise exception.create('Exeception::restoreProgression (Empty / invalid Identifier ['+aKeyProgression+'])');
                        exit;
                end;
                // ******************************************
                // ******************************************
                       
                try
                        sValueString := _hProgressionSessionParamsFile.ReadString(
                                                                            string('progression_integration_'+aKeyProgression),
                                                                            string(aKeyProgression+'_pos'), '1' );
                        result := strtoint(sValueString)-1;
                except
                on E: Exception do
                        begin
                                try
                                        raise exception.create('Exeception::restoreProgression ('+E.message+')');
                                         _hProgressionSessionParamsFile.EraseSection('progression_integration_'+aKeyProgression);
                                         _hProgressionSessionParamsFile.WriteInteger(aKeyProgression+'_count', aKeyProgression, _FileHandle.FileCurrentPos );
                                         _hProgressionSessionParamsFile.UpdateFile;
                                        sValueString := _hProgressionSessionParamsFile.ReadString(
                                                                            string('progression_integration_'+aKeyProgression),
                                                                            string(aKeyProgression+'_pos'), '1' );
                                        result := strtoint(sValueString) -1;
                                except
                                on E: Exception do
                                        begin
                                                // :: Result :=_FileHandle.FileCurrentPos;
                                                raise exception.create('Exeception::restoreProgression ('+E.message+')');
                                                exit;
                                        end;
                                end ;    
                        end;
                end;
        end;
        
// ******************************************
// ******************************************
 // Methods  SBGD
 
        function  TIntegration_CSV_SGBD_INSEE.Open(): Boolean;
        begin
                if(not assigned(_hStorageConnectionHandle)) then
                begin
                        raise exception.create('Open :: No Connection instance');
                end;
                
                
                with _hStorageConnectionHandle do
                 begin
                        {$IFNDEF DEBUG or TEST}
                                Protocol := 'mysqld-5';
                                HostName := 'mysql-rw.mysql-disi.xxxxxx.fr';
                                Database := 'parametrage_commun';
                                User     := 'billing';
                                Password := 'bil-mac';
                        {$ELSE}
                                Protocol := 'mysqld-5';
                                HostName := 'mysql-rw.mysql-disi.xxxxxx.fr';
                                Database := 'parametrage_commun';
                                User     := 'billing';
                                Password := 'bil-mac';
                        {$ENDIF}
                end;
                   // ****************************
              
                
                
                  _hStorageConnectionHandle.Open;
                Result :=true;
        end;
// ******************************************
// ******************************************

        function  TIntegration_CSV_SGBD_INSEE.Close(): Boolean; 
        begin
                if(not assigned(_hStorageConnectionHandle)) then
                begin
                        raise exception.create('Close :: No Connection instance');
                end;
                    _hStorageConnectionHandle.Close();
                Result := False;
        end;
// ******************************************
// ******************************************
  procedure  TIntegration_CSV_SGBD_INSEE.FTruncateTable(sAtable :string );
  begin
          _hStorageConnectionHandle.Clear();
          _hStorageConnectionHandle.SQLWriteMODE := true;
          _hStorageConnectionHandle.QueryHandle.SQL.Text := 'Truncate  '+sAtable;
          
          Self.Open();
  end;
        function  TIntegration_CSV_SGBD_INSEE.SaveCurrent(aAssocFieldList : TStringList; const aTable: string; bCreateMode : boolean = false): Integer; 
      
        var iFieldAssocIndex : integer;
        var iFieldAssocRecordIndex : integer;
        begin
                // ******************************************
                if(not assigned(_hStorageConnectionHandle)) then
                begin
                        raise exception.create('Save :: No Connection instance');
                        exit;
                end;
                // ******************************************
                if (_FileHandle.count = 0) then
                begin
                        result := 0;
                        raise exception.create('Save :: No Input File Results ...');
                        exit;
                end;
                // ******************************************
                
                iFieldAssocIndex                 := 0;
                iFieldAssocRecordIndex       := 0;
                // ******************************************
                _sRequeteSQL         := '';
                
                
                _hStorageConnectionHandle.Clear();
                _hStorageConnectionHandle.SQLWriteMODE := true;
                
                
                if(not bCreateMode) then
                begin
                        _sRequeteSQL  := _sRequeteSQL + ('UPDATE '+aTable);
                        _sRequeteSQL  := _sRequeteSQL + (' SET ');
                end
                else
                begin
                           _sRequeteSQL  := _sRequeteSQL + ('INSERT Into '+aTable);
                           
                         _sRequeteSQL  := _sRequeteSQL + (' ( ');
                         
                        for iFieldAssocIndex := 0 to aAssocFieldList.count-1 do
                        begin
                                if(iFieldAssocIndex > 0) then
                                begin
                                _sRequeteSQL  := _sRequeteSQL + (', ');
                                end;
                                _sRequeteSQL  := _sRequeteSQL + ( aAssocFieldList.Names[iFieldAssocIndex]) ;
                        end;
                        
                        _sRequeteSQL  := _sRequeteSQL + (' ) ');
                        _sRequeteSQL  := _sRequeteSQL + (' VALUES ');
                        _sRequeteSQL  := _sRequeteSQL + (' ( ');
                end;
                
                
                // ******************************************
                for iFieldAssocIndex := 0 to aAssocFieldList.count-1 do
                begin
                        if(iFieldAssocIndex > 0) then
                        begin
                                _sRequeteSQL  := _sRequeteSQL + (', ');
                        end;
                         if(not bCreateMode) then
                        begin
                                _sRequeteSQL  := _sRequeteSQL + ( ''+aAssocFieldList.Names[iFieldAssocIndex] +' = ');
                        end;
                        
                        
                        if( AnsiPos('date', AnsiLowerCase(aAssocFieldList.Names[iFieldAssocIndex]) ) >0) then
                        begin
                               _sRequeteSQL  := _sRequeteSQL + ( ' DATE_FORMAT(STR_TO_DATE(');
                               _sRequeteSQL  := _sRequeteSQL + (
                                                    _hStorageConnectionHandle.QuotedStr( _FileHandle.FieldByName( // valeur Current() du champ dans le  fichier Texte
                                                                                                     aAssocFieldList.Values[ aAssocFieldList.Names[iFieldAssocIndex]  ] //  Mapping SGBD ==>> Cle du champ dans le fichier texte
                                                                                     ) ) +'' );
                               _sRequeteSQL  := _sRequeteSQL + (',"%d%m%Y"), GET_FORMAT(DATE,"INTERNAL")) ');
                        end
                        else
                        begin
                                 _sRequeteSQL  := _sRequeteSQL + (
                                                    _hStorageConnectionHandle.QuotedStr( _FileHandle.FieldByName( // valeur Current() du champ dans le  fichier Texte
                                                                                                     aAssocFieldList.Values[ aAssocFieldList.Names[iFieldAssocIndex]  ] //  Mapping SGBD ==>> Cle du champ dans le fichier texte
                                                                                     ) ) +'' );
                               
                        end;
                        
                        
                end;
                
                
                 if(not bCreateMode) then
                begin
                
                        
                        // ******************************************
                        // where clause
                        // ******************************************
                        _sRequeteSQL  := _sRequeteSQL + (' Where (');
                        // ******************************************
                        _sRequeteSQL  := _sRequeteSQL + ('  ( '+_CachedQueryParams+' ) ');
                        { for iFieldAssocRecordIndex := 0 to _ResultsSQLFound.count-1 do
                        begin
                                if(iFieldAssocRecordIndex > 0) then
                                        begin
                                                _sRequeteSQL  := _sRequeteSQL + (' ) or ( ');
                                        end;
                                // ******************************************        
                                for iFieldAssocIndex := 0 to TStringList(_ResultsSQLFound[iFieldAssocRecordIndex]).count-1 do
                                begin
                                        if(iFieldAssocIndex > 0) then
                                        begin
                                                _sRequeteSQL  := _sRequeteSQL + (' and ');
                                        end;
                                        
                                        _sRequeteSQL  := _sRequeteSQL + ( ''+aAssocFieldList.Names[iFieldAssocIndex] +' = '+
                                                                    _hStorageConnectionHandle.QuotedStr(TStringList(_ResultsSQLFound[iFieldAssocRecordIndex]).Values[ aAssocFieldList.Names[iFieldAssocIndex] ]) +'' );
                                end;
                                // ******************************************
                        end;}
                         _sRequeteSQL  := _sRequeteSQL + (' )'); // femeture de la requete Where
                end
                else
                begin
                                 _sRequeteSQL  := _sRequeteSQL + (' )'); // femeture de la requete Insert
                 end;
                // ******************************************
                _hStorageConnectionHandle.QueryHandle.SQL.Text := _sRequeteSQL;

                {$IFDEF DEBUG_VERBOSE}
                        writeln(' SQL update :: '+_sRequeteSQL);
                {$ENDIF}
                // **************************************************
                // ****** LE MODE TRANSATION EST TREEEEES LENT *******
                // *************************************************
                // Auto Rollback  en cas d erreur 
                _hStorageConnectionHandle.SQLTransactionBeforeQuery := false;
                // ******************************************
                // Auto Commit en cas de succes
                _hStorageConnectionHandle.SQLCommitAfterQuery := false;
                // ******************************************
                // :: {$IFNDEF DEBUG or TEST}
                //
                Self.Open();
                // :: {$ENDIF}
                // ******************************************
                // _ResultsSQLFound.Commit();
                // _ResultsSQLFound.clear();
                // ******************************************
                Result := 0;// AffectedRows .... :: TODO :: 
                // ******************************************
               _hStorageConnectionHandle.clear(); 
        end;
// ******************************************
// ******************************************
       
        function TIntegration_CSV_SGBD_INSEE.FieldRecordAssocExists(aFieldsAssoc : TStringList; const aSQLTableParam: string): integer;
        var sRequeteSelectCountSQL : string;
        var bFieldsAssocFound : boolean;
        var iRecordFieldIndex : integer;
        var aResultFieldList : TStringList;
        
        var sFieldName  : string;
        begin
                
                result := integer( false );
                bFieldsAssocFound := false;
                iRecordFieldIndex := 0;
               
                _ResultsSQLFound.clear();
              
              //  aResultFieldList := TStringList.create();
               
               if(not assigned (_aFieldResultListNames) )then
               begin
                    _aFieldResultListNames := TStringList.create();
               end;
               
                _sRequeteSQL := '';
                _sRequeteParamsSQL := '';;
                
                if(not assigned(_hStorageConnectionHandle)) then
                begin
                        _hStorageConnectionHandle	:= TGns_SQLConnection.create(nil);
                end;
                
                _hStorageConnectionHandle.clear();
                _hStorageConnectionHandle.SQLWriteMODE := false;
                
                try
                        iRecordFieldIndex := 0;
                        
                        _CachedQueryParams := '';
                        
                        // **************************** 
                        _sRequeteSQL :=  ' from '+aSQLTableParam+' WHERE '  ; 
                        // ****************************
                        with _hStorageConnectionHandle.QueryHandle do
                        begin
                                // ****************************
                               
                               //  ParamByName('pvillename').Value     := _hStorageConnectionHandle.QuotedStr(_hStorageConnectionHandle.TrimAll(_FileHandle.FieldByName('ville').AsString));  // :: .AsQuotedSQLString
                                //ParamByName('pinsee').Value          := _hStorageConnectionHandle.QuotedStr(_hStorageConnectionHandle.TrimAll(_FileHandle.FieldByName('insee').AsString));  // :: .AsQuotedSQLString
                                
                                 for iRecordFieldIndex := 0 to  aFieldsAssoc.count -1 do
                                begin
                                        // :: ParamByName(string('p'+)).Value
                                        if ( iRecordFieldIndex > 0) then
                                        begin
                                                 _sRequeteParamsSQL := _sRequeteParamsSQL +' and ';
                                        end;
                                        
                                        _sRequeteParamsSQL := _sRequeteParamsSQL + aFieldsAssoc.Names[iRecordFieldIndex] +' = '  + _hStorageConnectionHandle.QuotedStr(
                                                                                                                // :: _hStorageConnectionHandle.TrimAll(  
                                                                                                                
                                                                                                                       _FileHandle.FieldByName( aFieldsAssoc.Values[aFieldsAssoc.Names[iRecordFieldIndex] ] ) 
                                                                                                                
                                                                                                                // :: )
                                                                                                          ) ;                                
                                      
                                end;
                                
                                _CachedQueryParams := _sRequeteParamsSQL;
                                
                                 _sRequeteSQL := _sRequeteSQL +' '+_CachedQueryParams;
                                 
                                // ****************************
                                // ****************************
                                sRequeteSelectCountSQL := 'select  count(*) Occurences ' +_sRequeteSQL;
                                {$IFDEF DEBUG_VERBOSE}
                                        writeln(' SQL  count :: '+sRequeteSelectCountSQL);
                                {$ENDIF}
                                //
                                SQL.Text := sRequeteSelectCountSQL;
                            
                               Self.Open();
                                
                               
                                // ****************************
                                // ****************************
                                if  ( _hStorageConnectionHandle.QueryHandle.RecordCount > 0 ) then
                                begin
                                    
                                        if(  _aFieldResultListNames.count =0) then
                                        begin
                                            _hStorageConnectionHandle.GetFieldNames('', aSQLTableParam, _aFieldResultListNames);
                                        end;
                                    
                                        // ****************************
                                        result := FieldByName('Occurences').AsInteger;
                                        bFieldsAssocFound := boolean(FieldByName('Occurences').AsInteger);
                                         // ****************************
                                        { _hStorageConnectionHandle.clear();
                                         // ****************************
                                         // ****************************
                                        SQL.Text :=  _sRequeteSQL;
                                        
                                         // ****************************
                                        Self.Open();
                                        // ****************************
                                       while not (_hStorageConnectionHandle.QueryHandle.EOF) do
                                        begin
                                              
                                                aResultFieldList.clear();
                                                        
                                                for iRecordFieldIndex := 0 to _aFieldResultListNames.count-1 do
                                                begin
                                                    sFieldName :=  lowercase(AnsiLowerCase(_aFieldResultListNames[iRecordFieldIndex]));
                                                  
                                                     if( AnsiPos('date', sFieldName) >0) then
                                                     begin
                                                         continue;
                                                     end;
                                                     
                                                        aResultFieldList.add(''+_aFieldResultListNames[iRecordFieldIndex]+'='+FieldByName(_aFieldResultListNames[iRecordFieldIndex]).AsString+'');
                                                end;
                                               
                                               _ResultsSQLFound.add(aResultFieldList);
                                               
                                               _hStorageConnectionHandle.QueryHandle.Next;       
                                       end;}
                                      
                                        // ****************************
                                end;
                        end;
                // ****************************
                // ****************************
                except
                on Err : Exception do
                        begin
                                if Assigned(_hStorageConnectionHandle) then
                                begin
                                        // automaticly Close connection
                                        close();
                                end;
                                // ****************************
                                // ****************************
                                raise exception.create(Err.Message);
                                 exit;
                                // ****************************
                                // ****************************
                        end;
                end;
         // ****************************
        _hStorageConnectionHandle.clear();
    
        // ****************************
        end;
// ****************************
// ****************************
// ****************************
// ****************************
end.
