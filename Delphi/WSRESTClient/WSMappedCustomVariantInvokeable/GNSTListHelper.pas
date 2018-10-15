unit GNSTListHelper;
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
  Variants, ConvUtils, WSMappedCustomVariantInvokeable ;

 
       // *********************************************************************
        // *********************************************************************
        
        // *********************************************************************
        // *********************************************************************

type TStringListHelper = class helper for TStringList
    private
            {function GetCurrentRec: Variant;}
            
     // *********************************************************************
    // *********************************************************************
    
    public
            // function add(const aValue : string): integer; overload;
            function add(const aKey : string; const aValue : string): integer; overload;
            function add(const aKey : string; const aValue : TObject): integer; overload;
            function add(const aValue : TObject): integer; overload;
            function IndexOf(const S: string): Integer; overload;
            function AsStringJSON(rootisObject: boolean = false): string; overload;
            function ToStringJSON(rootisObject: boolean = false): string; overload;
    // *********************************************************************
    // *********************************************************************
    
            constructor  Create(aTList: TList); overload;
            constructor Create(aTStringList: TStringList); overload;
     // *********************************************************************
    // *********************************************************************
    
             // :: TODO ::  property CurrentRec: Variant read GetCurrentRec;
            // :: TODO :: property asVariant: Variant read GetCurrentRec;
    
    end;



type
        { Our layout of the variants record data.
        We only hold a pointer to the DataSet. }
        TVarDataRecordTStringListData = packed record
        VType: TVarType;
        Reserved1, Reserved2, Reserved3: Word;
        
        DataSetPTR: TStringList;
        Reserved4: LongInt;
end;

implementation

function GNSTStringListHelperObjWriteDebug(aStringDebug : string) : boolean;
begin
          {$IFDEF DEBUG_VERBOSE_INTERNAL } writeln( ''+string(aStringDebug)+'' ); {$ENDIF}
          Result := true;
end;
// *********************************************************************
// *********************************************************************

{function TFPVariantPTRAssignTStringListData(ADataSet: TStringList): Variant;}
{var aList : TStringList ;}
{begin}
{    try}
{        GNSTStringListHelperObjWriteDebug('TFPVariantPTRAssignTStringListData::Begin. ');}
{        aList := ADataSet;}
{        VarClear(result);}
{        TVarDataRecordTStringListData(result).VType := TFPGetTypePTR ;}
{        if(assigned(ADataSet)) then}
{        begin}
{                GNSTStringListHelperObjWriteDebug('TFPVariantPTRAssignTStringListData::count '+inttostr(ADataSet.count));}
{                TVarDataRecordTStringListData(result).DataSetPTR := ADataSet;}
{        end}
{        else}
{        begin}
{             raise exception.create('TFPVariantPTRAssignTStringListData::VariantCreate(ADataSet is null)');}
{            exit;}
{        end;}
{    except}
{    on E: Exception do}
{        begin}
{               }
{                raise exception.create('Exception::TFPVariantPTRAssignTObjectInvokeableProperty ( Variant create :: '+E.Message+')');}
{                exit;}
{        end;}
{    end;}
{    GNSTStringListHelperObjWriteDebug('TFPVariantPTRAssignTStringListData::end. ');}
{end;}

// *********************************************************************
// *********************************************************************

constructor  TStringListHelper.Create(aTList: TList);
var iListIndex : integer;
var sListIndexName : string;
var aClassInfo: TClass;
var aPointer : pointer;
begin
        GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::Begin.(TStringList::TList) ');
        inherited Create();
        iListIndex := 0;
        if assigned(aTList) then
        begin
               for  iListIndex := 0 to aTList.count -1 do
               begin
                       GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::Copy.(TStringList::TList)@ '+inttostr(iListIndex));
                       {
                       sListIndexName := inttostr(iListIndex);
                        aPointer := @aTList.Items[iListIndex];
                       if ( TypeInfo(aPointer)  = TypeInfo(String) )  then
                       begin
                          AddStrings(aTList.Items[iListIndex]);
                       end
                       else
                       if ( TypeInfo(aPointer)  = TypeInfo(Integer)  )  then
                       begin
                          AddStrings(inttostr(aTList.Items[iListIndex])));
                       end
                       else}
                      //begin
                              // aClassInfo := PPointer(aTList.Items[iListIndex]);
                              aPointer := aTList.Items[iListIndex];
                                if(  TTypeInfo(aPointer^).name = 'tkClass' ) then 
                                begin
                                   AddObject(sListIndexName, TObject(aTList.Items[iListIndex]));
                               end
                               else
                               begin
                                        raise Exception.create('Exception::Unsupported Create Type Operation ');
                                end;
                      // end;

               end;
        end;
        GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::End.(TStringList::TList) ');
end;
// *********************************************************************
// *********************************************************************
 
// *********************************************************************
// *********************************************************************

constructor  TStringListHelper.Create(aTStringList: TStringList);
var iListIndex : integer;
var sListIndexName : string;
begin
    GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::Begin.(TStringList) ');
    inherited Create();
    iListIndex := 0;
    if assigned(aTStringList) then
    begin
        for  iListIndex := 0 to aTStringList.count -1 do
        begin
            GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::Copy.(TStringList)@ '+inttostr(iListIndex));
            
            sListIndexName :=  aTStringList.Names [iListIndex];
            if (length(sListIndexName) <1 ) then
            begin
                sListIndexName := inttostr(iListIndex);
            end;
            addObject(sListIndexName, aTStringList.Objects[iListIndex]);
        end;
    end;
    GNSTStringListHelperObjWriteDebug('TStringListHelper.Create::End.(TStringList) ');
end;
// *********************************************************************
// *********************************************************************

{function TStringListHelper.GetCurrentRec: Variant;}
{begin}
{  {//:::return one of our custom variants }
{  GNSTStringListHelperObjWriteDebug('  TStringListHelper.GetCurrentRec.::Begin ');}
{  if(not assigned(Self)) then}
{  begin}
{      raise exception.create('TStringListHelper.GetCurrentRec::VariantCreate(self is null)');}
{      exit;}
{  end;}
{  Result := TFPVariantPTRAssignTStringListData(Self);}
{  GNSTStringListHelperObjWriteDebug('  TStringListHelper.GetCurrentRec.::End. ');}
{end;}


// *********************************************************************
// *********************************************************************
 function  TStringListHelper.AsStringJSON(rootisObject: boolean = false): string;
 begin
    Result := ToStringJSON(rootisObject);
 end;
 
// *********************************************************************
// *********************************************************************
 function  TStringListHelper.ToStringJSON(rootisObject: boolean = false): string;
  var aReturnStringJSON : TStringStream;
  var aReturnStringJSONObject : TStringStream;
  var sKeyString : string;
  var sValueString : string;
  var iKeyIndex : integer;
  var iCountElements : integer;
 { var ctx :TRTTIContext;
  var rtype :TRTTIType;
  var rmethod :TRTTIMethod;}
  var aObjectProc: function : string of Object;
  var isObject  : boolean;
begin
    aReturnStringJSON := TStringStream.create('');
    aReturnStringJSONObject :=  TStringStream.create('');
    sKeyString := '';
    sValueString := '';
    
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.AsJSONString.::Begin ');
    for iKeyIndex := 0 to count-1 do  begin
      
        
        

        if(length( Names[iKeyIndex] ) >0) then
        begin
            sKeyString := Names[iKeyIndex];
        end else if( not rootisObject) then
        begin   
            sKeyString :=  Strings[iKeyIndex];
            //  aReturnStringJSON.WriteString(''+inttostr(iKeyIndex)+''+':');
        end;
               
               
             

                       
            
           
             
        if(Objects[iKeyIndex] <> nil) then
        begin
             // sValueString := Objects[iKeyIndex].ToStringJson();
             
  
             if(length(aReturnStringJSONObject.datastring)>0 ) then
             begin
                 aReturnStringJSONObject.writestring(', ');
             end;
                                      {isObject  := false;}
            if (length(sKeyString) >0)  then
            begin
                aReturnStringJSONObject.WriteString('"'+sKeyString+'"'+':');
            end;

             if(rootisObject ) then
             begin
                 aReturnStringJSONObject.WriteString('{'); 
             end
             else
             begin
                 aReturnStringJSONObject.WriteString('['); 
             end;
             
              
            { if(not isObject) then
             begin
                isObject  := true;
            end;}
             
             
             // aReturnStringJSON.WriteString('{'); 
            try
            sValueString := '';
                GNSTStringListHelperObjWriteDebug('  TStringListHelper('+classname+').AsJSONString.::Looking for Json Helper for Class Object  '+(Objects[iKeyIndex]).classname);;; 
               TMethod(aObjectProc).Code := Objects[iKeyIndex].MethodAddress('ToStringJSON');
               if( not Assigned(aObjectProc) ) then
               begin
                   TMethod(aObjectProc).Code :=  (Objects[iKeyIndex]).MethodAddress('AsStringJSON'); 
               end
               else begin
                    ;;
                    
               end;
             
               if Assigned(aObjectProc) then begin 
                   TMethod(aObjectProc).Data := Objects[iKeyIndex]; 
                   sValueString := aObjectProc;
                end
               else
               if( string((Objects[iKeyIndex]).classname+'Helper') = 'TStringListHelper' ) then
                begin
                     sValueString := TStringList(Objects[iKeyIndex]).ToStringJSON(true);
                    ;;
                end else
                begin
                     GNSTStringListHelperObjWriteDebug('  TStringListHelper.AsJSONString.::No Json Helper for Class Object  '+(Objects[iKeyIndex]).classname);;; 
               end;
               
            except
            on EMethodExistsException: Exception do
               begin
                     GNSTStringListHelperObjWriteDebug('  Exception::TStringListHelper.ToStringJSON ('+EMethodExistsException.Message+')');
                   ;;
               end;
            end;
            
              aReturnStringJSONObject.WriteString( sValueString );
            
            // aReturnStringJSON.WriteString('}');
            
             
                // aReturnStringJSONObject.WriteString(']');

             if(rootisObject ) then
             begin
                 aReturnStringJSONObject.WriteString('}'); 
             end
             else
             begin
                 aReturnStringJSONObject.WriteString(']'); 
             end;
             
            
        end else
        begin
            
            
        if (iKeyIndex >0) and  (length(aReturnStringJSON.datastring) >0) then
        begin
            aReturnStringJSON.WriteString(', ');
        end;
        
        
            
                         {isObject  := false;}
                if (length(sKeyString) >0)   then
                begin
                    aReturnStringJSON.WriteString('"'+sKeyString+'"'+':');
                end;

                sValueString := Values[sKeyString];
      
                 if(ansipos('{',sValueString ) > 0)  or (ansipos('[',sValueString ) > 0)  then
                 begin
                     aReturnStringJSON.WriteString(sValueString);
                 end
                 else
                 begin
                     aReturnStringJSON.WriteString('"'+sValueString+'"');
                 end;
          end;
          
          

          
    end;
    iCountElements := count;
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.AsJSONString.::End. count :: '+inttostr(iCountElements));
    
    Result := '';
    
    // if (ansipos('[',aReturnStringJSONObject.datastring ) <0)  then
    if(not rootisObject ) then
    begin
        Result :=  Result + '{ ';
    end;
    
    Result :=  Result +' '+aReturnStringJSON.DataString;
    
    
    
  
    if(length(aReturnStringJSONObject.datastring)>0 ) and (length( aReturnStringJSON.datastring)>0)  then
    begin
            Result :=  Result  + ' , '+aReturnStringJSONObject.datastring;
    end
    else
    begin
            Result :=  Result  + '  '+aReturnStringJSONObject.datastring;
    end;
    
    
    if(not rootisObject ) then
    begin
        Result :=  Result + ' }';
    end;
    
    
end;
// *********************************************************************
// *********************************************************************
{function  TStringListHelper.add(const aValue : string): integer;
var iCountElements : integer;
begin
    // GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::Begin ');
    iCountElements := inherited add(aValue);
    // GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::String::End. count :: '+inttostr(iCountElements));
    Result :=  (iCountElements);
end;}
// *********************************************************************
// *********************************************************************
function  TStringListHelper.add(const aKey : string; const aValue : string): integer;
var iCountElements : integer;
begin
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::Begin ');
    iCountElements := inherited add(aKey+'='+aValue);
    for iCountElements := count downto 0 do begin
        GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::ListValue ::  '+Values[aKey]+'::'+Values[aKey+'='+aValue]+'::'+inttostr(indexof(aKey))+'::'+inttostr(indexof(aKey+'='+aValue)));
    end;
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::String::TObject::End. count :: '+inttostr(iCountElements));
    Result :=  (iCountElements);
end;

function  TStringListHelper.add(const aKey : string; const aValue : TObject): integer;
var iCountElements : integer;
begin
    // GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::Begin ');
    iCountElements := addObject(aKey, aValue);
    // GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::String::TObject::End. count :: '+inttostr(iCountElements));
    Result :=  (iCountElements);
end;

function  TStringListHelper.add(const aValue : TObject): integer; 
var iCountElements : integer;
begin
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.add::TObject.::Begin ');
    insertItem(count, inttostr(count), aValue);
    iCountElements := count;
    GNSTStringListHelperObjWriteDebug('  TStringListHelper.add.::TObject::End. count :: '+inttostr(iCountElements));
    Result :=  (iCountElements);
end;
function TStringListHelper.IndexOf(const S: string): Integer;
var iValueFound : integer;
var iValueIndex : integer;
begin
    try
            iValueFound := 0;
            iValueIndex := 0;
            iValueFound := inherited  IndexOf(S);
            
            Result :=   iValueFound;
            
            if(iValueFound = (-1)) and ((count - 1) >0) then
            begin
                  for iValueIndex := Count-1 downto 0 do
                    begin
                         iValueFound :=  inherited  IndexOf(S+'='+inttostr(iValueIndex));
                        if( iValueFound > -1) then
                        begin
                            result := iValueIndex;
                         break;
                        end;
                    end;
            
            end;
     except
    on ErrHelperIndexOf: Exception do
        begin
            raise exception.create('TStringListHelper::ErrHelperIndexOf :: Exception :: '+ErrHelperIndexOf.Message);
          
            exit;
        end;
    end;       
end;

end.
