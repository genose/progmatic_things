unit UGns_SQLConnection;

interface

uses
  Classes, DB, SysUtils, StrUtils,
  ZConnection,
ZDataSet,
ZDbcMySql,
ZDbcCache,
ZSQLUpdate,
ZSQLMetadata,
//ZClasses,
//ZCompatibility,
//ZDbcDBLib
ZAbstractDataset,
ZDbcIntfs
;

type TSQLConfigFunctionPTR = function(): boolean of object;
  
 type  TGns_SQLQueryUpdate = class(TZUpdateSQL )

end;

type  TGns_SQLQuery = class(TZQuery)

end;

type TGns_SQLConnection = class(TZConnection)
    private
        var __SQLConnectionAccessMode : integer;
        var __SQLConnection_AutoCommitAfterQuery : boolean;
        var __SQLConnection_AutoCommitOnCloseQuery : boolean;
        var __SQLConnection_AutoTransactionStart : boolean;
        var __FSQLConfigFuncPtr : TSQLConfigFunctionPTR;
        var __aOwner  : TComponent;
        var __iNBAffectedRows : integer;
        var __SQLWriteMODE : boolean;
    public
        var QueryHandle : TZQuery;
        
        constructor Create(AOwner: TComponent); override;
        destructor  Destroy; override;
        
        
        procedure Open(); overload;
        procedure Close(); overload;
        procedure Clear(); overload;
        procedure Transaction_Start;
        procedure Transaction_Commit;
        procedure Transaction_RollBack;
       
       
       function QuotedStr(sOriginalString : widestring) : widestring;
       function TrimAll(sOriginalString : widestring) : widestring;
       
        procedure GetFieldNames(const aDatabaseName: string; const aTableName: string; aFieldListPTR : TStrings);
        
        property SQLServeurConnectionEnv : integer read __SQLConnectionAccessMode write __SQLConnectionAccessMode;
        
        property SQLCommitAfterQuery : boolean read __SQLConnection_AutoCommitAfterQuery write __SQLConnection_AutoCommitAfterQuery;
        
        property SQLCommitOnCloseQuery : boolean read __SQLConnection_AutoCommitOnCloseQuery write __SQLConnection_AutoCommitOnCloseQuery;
        
        property SQLTransactionBeforeQuery : boolean read __SQLConnection_AutoTransactionStart write __SQLConnection_AutoTransactionStart;
         
        property SQLConfigFuncPtr : TSQLConfigFunctionPTR read __FSQLConfigFuncPtr write __FSQLConfigFuncPtr;
        property SQLWriteMODE : boolean read __SQLWriteMODE write __SQLWriteMODE ;
                 
end;

implementation

{ TWSQuery ====================================================================}

constructor TGns_SQLConnection.Create(AOwner: TComponent);
begin
    inherited Create(AOwner);
    __aOwner := AOwner;
    // Connection:=TZConnection.Create(AOWner);
    QueryHandle := TZQuery.Create(__aOwner);
    __SQLConnectionAccessMode := 0;
    __SQLConnection_AutoCommitAfterQuery := false;
    __SQLConnection_AutoTransactionStart := false;
    
    QueryHandle.ReadOnly := True;
    QueryHandle.CachedUpdates := false;
    __SQLWriteMODE := false;

end;


// **********************************
// **********************************
// **********************************
 // **********************************
destructor TGns_SQLConnection.Destroy;
begin
    if(SQLCommitOnCloseQuery) then
    begin
            Transaction_Commit();   
    end;

    Disconnect;
    
    QueryHandle.Destroy;
    inherited Destroy;
end;
// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.Close();
begin
    if(SQLCommitOnCloseQuery) then
    begin
            Transaction_Commit();
    end;
    Disconnect();
    QueryHandle.CachedUpdates := False;
    
    QueryHandle.EmptyDataSet();
    
    // QueryHandle.destroy();
    // QueryHandle := TZQuery.Create(__aOwner);
end;

procedure TGns_SQLConnection.Clear();
begin
  
    QueryHandle.CachedUpdates := False;
    
    QueryHandle.EmptyDataSet();
    
    __SQLWriteMODE := false;
   
    // QueryHandle.destroy();
    // QueryHandle := TZQuery.Create(__aOwner);
end;


// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.Open();
var sQueryType : string;
begin
        
     sQueryType := '';
    
   // **********************************
   // **********************************
   if( assigned(@__FSQLConfigFuncPtr) ) then
   begin
        try
            begin
                    __FSQLConfigFuncPtr();
            end;
        except
        on ErrFuncCallException : Exception do
            begin
                raise Exception.create('Erreur de configuration SQL::FuncConfigPtr('+ErrFuncCallException.Message+')');
                exit;
            end;
        end;
   end
   else
   begin
         {  case __SQLConnectionAccessMode of
                    0 :
                           begin
                                   with Connection do
                                           begin
                                                   Protocol := 'mysqld-5';
                                                   HostName := '127.0.0.1';
                                                   Database := '----';
                                                   User     := '';
                                                   Password := '************';
                                           end;
                                    
                          end;
                   else
                   begin
                           raise Exception.create('Erreur de configuration SQL (ServeurEnvMode)');
                           exit;                 
                   end;
           end;}
         ;;
    end; 
   // **********************************
   // **********************************        
    
   if ( not ( ( length(Protocol) >=3 ) and ( length(HostName) >=6 ) and ( length(User) >=3)) ) then
   begin
        raise Exception.create('Erreur de configuration SQL (Empty :: Protocol('+Protocol+')/Hostname('+HostName+')/User('+User+'))');
        exit;
   end;

   // **********************************
   // **********************************        
   
   if (__SQLConnection_AutoTransactionStart) then
   begin
           Transaction_Start;
   end;       
    // **********************************
    // **********************************
   
   try
        begin
               
            // QueryHandle.UpdateMode := umUpdateAll;
            // Zeos Connection
           { if(connected) then
            begin
                Disconnect;
            end;}
            Connect;
             
        end;
    // **********************************
   // **********************************
     
   except
   on Err: Exception do
        begin
            // **********************************
            // **********************************
           try
                try 
                    
                        Disconnect;
                        Connect;
                    
                    except
                    on E: Exception do
                        begin
                            Disconnect;
                            Connect;                   
                             
                        end;
                    end;
                
            except
            on ERRconnection: Exception do
                begin
                    Disconnect;  /// just close the connection
                    
                    raise exception.create('Exception::TGns_SQLConnection::Open ( Connection Error:: '+ERRconnection.Message+')');
                    exit;
                end;
            end;
            // **********************************
            // **********************************     ;
        end;
   end;
    // **********************************
   // **********************************
   try
        // Zeos inherited open == > > Exec SQL Query
      //  sQueryType := lowerCase(leftStr(QueryHandle.SQL.TEXT,10));
        if (__SQLWriteMODE)
        
      {  (AnsiPos( 'update ', sQueryType ) >0)
        or (AnsiPos( 'delete ', sQueryType) >0)
        or (AnsiPos( 'truncate ', sQueryType) >0)
        or (AnsiPos( 'insert ', sQueryType) >0)}
        
        then // :: 
        begin
            ExecuteDirect(QueryHandle.SQL.TEXT,__iNBAffectedRows);
        end else
        begin
            QueryHandle.Connection := Self;
            QueryHandle.Open();
        end;
        
        if (__SQLConnection_AutoCommitAfterQuery) then
        begin
                Transaction_Commit();
        end;
   except
   on E: Exception do
        begin
                //  in case of error we cancel our last
               // Zeos RollBack
                if (__SQLConnection_AutoTransactionStart) or (__SQLConnection_AutoCommitAfterQuery) then
                begin
                        Transaction_RollBack();
                end;
               
                raise exception.create('Exception::TGns_SQLConnection::Open::ExecSql ( Connection Error:: '+E.Message+')');
                exit;
        end;
   end;        
       
end;
// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.Transaction_Start;
begin
  // Transaction possible sur Table InnoDB
  {TZTransactIsolationLevel :(tiNone, tiReadUncommitted, tiReadCommitted,tiRepeatableRead, tiSerializable)}
  TransactIsolationLevel:=tiReadUncommitted;
  StartTransaction;
end;

// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.Transaction_Commit;
begin
  Commit;
  TransactIsolationLevel:=tiNone;
end;

// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.Transaction_RollBack;
begin
  RollBack;
  TransactIsolationLevel:=tiNone;
end;

// **********************************
// **********************************
// **********************************
 // **********************************
 function TGns_SQLConnection.QuotedStr(sOriginalString : widestring) : widestring;
 begin
          
        {if (not Assigned(Connection)) then
        begin
                  Connection:=TZConnection.Create(self);
        end;
        
        Connection.GetEscapeString(result, sOriginaleString, length(sOriginaleString) );
        }
        
        Result := StringReplace( sOriginalString , chr(ord(039)),  chr(ord(092))+chr(ord(039)) ,[rfReplaceAll, rfIgnoreCase]) ;
        Result := '"'+StringReplace( Result , '"', chr(ord(092))+'"',[rfReplaceAll, rfIgnoreCase])+'"' ;
 end;
// **********************************
// **********************************
// ********************************** 
// **********************************
 function TGns_SQLConnection.TrimAll(sOriginalString : widestring) : widestring; 
 var iCharPosFound : integer;
 begin
          
  
       iCharPosFound := AnsiPos(' ',sOriginalString);
       
         //Connection.GetEscapeString(result, sOriginaleString, length(sOriginaleString) );
        while ( ( iCharPosFound >0 ) and (length(sOriginalString)>0 ) ) do
        begin
                iCharPosFound := AnsiPos(' ',sOriginalString);
                if(  iCharPosFound = 1) then
                begin
                        sOriginalString := RightStr(sOriginalString, Length(sOriginalString) - 1);
                end
                else
                begin
                        break;
                end;
        end;
        
        
       iCharPosFound := AnsiPos(' ',sOriginalString);
       
        
        while  ( ( iCharPosFound >0 ) and ( length(sOriginalString)>0 ) ) do
        begin
                  iCharPosFound := AnsiPos(' ',sOriginalString);
                 if( iCharPosFound = length(sOriginalString) ) then
                begin
                        sOriginalString := LeftStr(sOriginalString, Length(sOriginalString) - 1);
                end
                else
                begin
                        break;
                end;
                
        end;
          Result := sOriginalString;
 end;
// **********************************
// **********************************
// **********************************
 // **********************************
procedure TGns_SQLConnection.GetFieldNames(const aDatabaseName: string; const aTableName: string; aFieldListPTR : TStrings);
begin
        
        GetColumnNames(aTableName,'', aFieldListPTR); // :: COLUMN_NAME
end;
// **********************************
// **********************************
 
end.
