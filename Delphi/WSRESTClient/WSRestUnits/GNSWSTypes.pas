unit GNSWSTypes;

{$IFDEF FPC}
  {$MODE Delphi}
{$ENDIF}

{ classes de consommation des données REST }
interface

uses
{$IFnDEF FPC}
  Windows,
{$ELSE}
  LCLIntf, LCLType, LMessages,
{$ENDIF}
  Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, uLkJSON;



{type TGNSWSTypesObject = class(TlkJSONobject)}
{}
{end;}



type TGNAssocValueObject = class
private

	Fkey: string;
	Fvalue: Variant;

  public
    constructor Create(const AName: String); overload;
	  constructor Create(const AName: string; AValue: variant ); overload;
    // destructor Destroy(); override;
	
	//class operator Implicit(aValue: string): TGNAssocValueObject; overload;
	//class operator Implicit(aValue: Variant): TGNAssocValueObject; 
	
  end;

implementation

constructor TGNAssocValueObject.Create(const AName: String);
begin
  FKey := AName;
 // FValue := TObject.Create();
end;

constructor TGNAssocValueObject.Create(const AName: string; AValue: Variant );
begin
  FKey := AName;
  FValue := AValue;
end;

{class operator TGNAssocValueObject.Implicit(AValue: variant): TGNAssocValueObject;
begin
	 FValue := AValue;
end; }


end.




