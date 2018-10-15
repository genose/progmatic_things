unit TGNSDataSetHelper;
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





type
  TDataSetEnumerator = class
  private
    FBookmark: TBookmark;
    FDataSet: TDataSet;
    FMoveToFirst: Boolean;
    FWasActive: Boolean;
  //:: TODO ::  function GetCurrent: Variant;
  public
    constructor Create(ADataSet: TDataSet);
    destructor Destroy; override; 
    function MoveNext: Boolean;
     //:: TODO ::  property Current: Variant read GetCurrent;
  end;


type
  TDataSetHelper = class helper for TDataSet
  private
    //:: TODO ::  function GetCurrentRec: Variant;
  public
    function GetEnumerator: TDataSetEnumerator;
     //:: TODO :: property CurrentRec: Variant read GetCurrentRec;
  end;


type
        { Our layout of the variants record data.
        We only hold a pointer to the DataSet. }
        TVarDataRecordData = packed record
        VType: TVarType;
        Reserved1, Reserved2, Reserved3: Word;
        
        DataSetPTR: TDataSet;
        Reserved4: LongInt;
end;
implementation


function GNSTDataSetHelperObjWriteDebug(aStringDebug : string) : boolean;
begin
          {$IFDEF DEBUG_VERBOSE_INTERNAL } writeln( ''+string(aStringDebug)+'' ); {$ENDIF}
          Result := true;
end;
// *********************************************************************
// *********************************************************************
    
{ A global function that fills the VarData fields with the correct values. }
{function TFPVariantPTRAssignData(ADataSet: TDataSet): Variant;}
{begin}
{  VarClear(result);}
{  TVarDataRecordData(result).VType := TFPGetTypePTR ;}
{  TVarDataRecordData(result).DataSetPTR := ADataSet;}
{end;}

// *********************************************************************
// *********************************************************************

constructor TDataSetEnumerator.Create(ADataSet: TDataSet);
begin
        inherited create();
end;

destructor TDataSetEnumerator.Destroy;
begin
        FDataSet.destroy;
end;

function TDataSetEnumerator.MoveNext: Boolean;
begin
        result := true;
end;

{function TDataSetEnumerator.GetCurrent: Variant;}
{begin}
{    GNSTDataSetHelperObjWriteDebug(' TDataSetEnumerator.GetCurrent.::Begin ');}
{    if(not assigned(Self)) then}
{    begin}
{        raise exception.create('TDataSetEnumerator.GetCurrentRec::VariantCreate(self is null)');}
{        exit;}
{    end;}
{    Result :=  TDataSet(FDataSet).GetCurrentRec; }
{    GNSTDataSetHelperObjWriteDebug(' TDataSetEnumerator.GetCurrent.::End. '); }
{end;}

// *********************************************************************
// *********************************************************************
    
// *********************************************************************
// *********************************************************************
{}
{function TDataSetHelper.GetCurrentRec: Variant;}
{begin}
{  return one of our custom variants }
{    GNSTDataSetHelperObjWriteDebug('  TDataSetHelper.GetCurrentRec.::Begin ');}
{    if(not assigned(Self)) then}
{    begin}
{        raise exception.create('TDataSetHelper.GetCurrentRec::VariantCreate(self is null)');}
{        exit;}
{    end;}
{    Result := TFPVariantPTRAssignData(Self);}
{    GNSTDataSetHelperObjWriteDebug('  TDataSetHelper.GetCurrentRec.::End. ');}
{end;}

function TDataSetHelper.GetEnumerator: TDataSetEnumerator;
begin
  { return a new enumerator }
    GNSTDataSetHelperObjWriteDebug(' TDataSetHelper.GetEnumerator.::Begin ');
    if(not assigned(Self)) then
    begin
        raise exception.create('TDataSetHelper.GetEnumerator::VariantCreate(self is null)');
        exit;
    end;
    Result := TDataSetEnumerator.Create(Self);
    GNSTDataSetHelperObjWriteDebug(' TDataSetHelper.GetEnumerator.::End. ');
end;


// *********************************************************************
// *********************************************************************
    
// *********************************************************************
// *********************************************************************
end.
