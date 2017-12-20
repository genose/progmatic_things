//------------------------------------------------------------------------------
// Auteur : S******* P***** dit Shai Le Troll, le Collègue de Shon             -
//------------------------------------------------------------------------------

unit uHashList;
{$mode objfpc}{$H+}
interface

uses
   SysUtils, Classes;

type
  THashStringList = class(TObject)
  private
    InternalList: TStringList;
    function FindTag(Tag: Integer): Integer;
  protected
    function GetHashCodes(Index: Integer): string;
    function GetTag(Index: Integer): Integer;
    procedure SetTag(Index: Integer; Value: Integer);
    function GetHashValues(const HashCode: string): string;
    procedure SetHashValues(const HashCode: string; const Value: string);
    function GetTagValues(Tag: Integer): string;
    procedure SetTagValues(Tag: Integer; const Value: string);
    function GetHashTag(const HashCode: string): Integer;
    procedure SetHashTag(const HashCode: string; Value: Integer);
    function GetObjects(const HashCode: string): TObject;
    procedure SetObjects(const HashCode: string; Obj: TObject);
    function GetCount(): Integer;
  public
    constructor Create();
    destructor Destroy; override;
    procedure Clear();

    property HashCodes[Index: Integer]: string read GetHashCodes; // Liste Triée En Lecture Seule
    property Tag[Index: Integer]: Integer read GetTag write SetTag;
    property HashValues[const HashCode: string]: string read GetHashValues write SetHashValues; default;
    property HashTag[const HashCode: string]: Integer read GetHashTag write SetHashTag;
    property TagValues[Tagi: Integer]: string read GetTagValues write SetTagValues;
    property Objects[const HashCode: string]: TObject read GetObjects write SetObjects;
    property Count: Integer read GetCount;
  end;

implementation

type
  THashStringItem = class
  public
     HashValue: string;
     Tag: Integer;
     SubObject: TObject;
     constructor Create(Value: string); overload;
     constructor Create(Value: Integer); overload;
  end;

resourcestring
  SDuplicateTag = 'Tag : %d déjà affecté !';

{ THashStringList }

constructor THashStringList.Create();
begin
  inherited;

  InternalList := TStringList.Create();
  InternalList.Sorted := True;
  // Impossible d'insérer/modifier deux fois le même élément, les accesseurs normalement sécurisent cela !
  InternalList.Duplicates := dupError;
end;

destructor THashStringList.Destroy;
begin
  Clear();

  InternalList.Free();
  InternalList := nil;

  inherited;
end;

procedure THashStringList.Clear();
var
  Index: Integer;
begin
  if Self <> nil then
  begin
    for Index := 0 to InternalList.Count - 1 do
    begin
       // Libère l'Objet Hash mais pas le SubObject
       THashStringItem(InternalList.Objects[Index]).Free();
    end;
    InternalList.Clear();
  end;
end;

function THashStringList.GetHashCodes(Index: Integer): string;
begin
  writeln('THashStringList.GetHashCodes :: '+inttostr(Index));
  if(Index>=0) and (InternalList.Count >0) then
  begin
      Result := InternalList.Strings[Index];
    end
  else
  begin
     writeln('THashStringList.GetHashCodes :: return void');
     result := '';
  end;

end;

function THashStringList.GetTag(Index: Integer): Integer;
begin
   writeln('THashStringList.GetTag :: '+inttostr(Index));
   if(Index>=0) and (InternalList.Count >0) then
  begin
        Result := THashStringItem(InternalList.Objects[Index]).Tag;
    end
   else
   Begin
      writeln('THashStringList.GetTag :: return -1');
     Result := -1;
  end;

end;

procedure THashStringList.SetTag(Index: Integer; Value: Integer);
begin
  writeln('THashStringList.SetTag :: '+inttostr(Index)+'::'+inttostr(Value));
  THashStringItem(InternalList.Objects[Index]).Tag := Value;
end;

function THashStringList.GetHashValues(const HashCode: string): string;
begin
   writeln('THashStringList.GetHashValues :: '+HashCode);
   if( InternalList.Count >0 )then
   begin
       Result := THashStringItem(InternalList.Objects[InternalList.IndexOf(HashCode)]).HashValue;
   end
   else
   begin
      writeln('THashStringList.GetHashValues :: return void');
      result := '';
   end;
end;

procedure THashStringList.SetHashValues(const HashCode: string; const Value: string);
var
  Index: Integer;
begin
  writeln('THashStringList.SetHashValues :: '+HashCode +'::Str::'+Value);
  Index := InternalList.IndexOf(HashCode);
  if Index >= 0 then
  begin
     THashStringItem(InternalList.Objects[Index]).HashValue := Value;
  end
  else
  begin
     InternalList.AddObject(HashCode, THashStringItem.Create(Value));
  end;
end;

function THashStringList.GetTagValues(Tag: Integer): string;
begin
  writeln('THashStringList.GetTagValues :: '+inttostr(Tag));
   if(Tag>=0) and (InternalList.Count >0) then
   begin
    Result := THashStringItem(InternalList.Objects[FindTag(Tag)]).HashValue;
    end
   else
   begin
        writeln('THashStringList.GetTagValues :: return void');
    result := '';
   end;
end;

procedure THashStringList.SetTagValues(Tag: Integer; const Value: string);
begin
    writeln('THashStringList.SetTagValues :: '+inttostr(Tag)+'::'+Value);
   THashStringItem(InternalList.Objects[FindTag(Tag)]).HashValue := Value;
end;

function THashStringList.GetHashTag(const HashCode: string): Integer;
begin
      writeln('THashStringList.GetHashTag :: '+HashCode);
    if( InternalList.Count >0 )then
    begin
         Result := THashStringItem(InternalList.Objects[InternalList.IndexOf(HashCode)]).Tag;
    end
    else
    begin
       writeln('THashStringList.GetHashTag :: return -1');
         result := -1;
    end;

end;

procedure THashStringList.SetHashTag(const HashCode: string; Value: Integer);
var
  Index: Integer;
begin

  writeln('THashStringList.SetHashTag :: '+HashCode+'::Int::'+inttostr(Value));
  if FindTag(Value) >= 0 then
  begin
     raise EStringListError.CreateFmt(SDuplicateTag, [Value]);
  end;

  Index := InternalList.IndexOf(HashCode);
  if Index >= 0 then
  begin
     THashStringItem(InternalList.Objects[Index]).Tag := Value;
  end
  else
  begin
     InternalList.AddObject(HashCode, THashStringItem.Create(Value));
  end;
end;

function THashStringList.GetObjects(const HashCode: string): TObject;
begin
  writeln('THashStringList.GetObjects :: '+HashCode);
  Result :=  THashStringItem(InternalList.Objects[InternalList.IndexOf(HashCode)]).SubObject;
end;

procedure THashStringList.SetObjects(const HashCode: string; Obj: TObject);
begin
    writeln('THashStringList.SetObjects :: '+HashCode);
  THashStringItem(InternalList.Objects[InternalList.IndexOf(HashCode)]).SubObject := Obj;
end;

function THashStringList.GetCount(): Integer;
begin
  Result := InternalList.Count;
end;

function THashStringList.FindTag(Tag: Integer): Integer;
begin
  for Result := 0 to InternalList.Count - 1 do
  begin
     if THashStringItem(InternalList.Objects[Result]).Tag = Tag then
     begin
        writeln('THashStringList.FindTag :: return '+inttostr(Result));
        Exit;
     end;
  end;
  writeln('THashStringList.FindTag :: return -1');
  Result := -1;
end;

{ THashStringItem }

constructor THashStringItem.Create(Value: string);
begin
  HashValue := Value;
end;

constructor THashStringItem.Create(Value: Integer);
begin
  HashValue := '';
  Tag := Value;
end;


end.
