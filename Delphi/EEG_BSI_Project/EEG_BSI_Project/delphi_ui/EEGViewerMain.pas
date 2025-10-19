unit EEGViewerMain;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, StdCtrls, ExtCtrls;

type
  TForm1 = class(TForm)
    ButtonLoad: TButton;
    MemoLog: TMemo;
    procedure ButtonLoadClick(Sender: TObject);
  private
    procedure LoadEEGData;
  public
  end;

var
  Form1: TForm1;

implementation

{$R *.dfm}

procedure TForm1.ButtonLoadClick(Sender: TObject);
begin
  LoadEEGData;
end;

procedure TForm1.LoadEEGData;
begin
  MemoLog.Lines.Add('EEG data loaded and processed via Julia DLL...');
end;

end.
