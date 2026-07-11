unit U_DEBUGConsoleUtils;

interface

uses Classes, SysUtils, Windows;

var FP_GUIConsoleDebugPresent : Boolean;

function AttachConsole(const dwProcessId: THandle): BOOL; stdcall; external kernel32 name 'AttachConsole';
function GDBWindowsConsoleInit() : Boolean;
implementation



function GDBWindowsConsoleInit() : Boolean;
begin
   FP_GUIConsoleDebugPresent := AttachConsole(THandle(-1)) or  AllocConsole;
   Result :=  FP_GUIConsoleDebugPresent;
end;

end.
