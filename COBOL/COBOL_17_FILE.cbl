      ******************************************************************
      * Author:
      * Date:
      * Purpose:
      * Tectonics: cobc
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. YOUR-PROGRAM-NAME.


       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.

       FILE-CONTROL.
           SELECT FD-ENR-EMPLOYEE-FILE ASSIGN TO "EMPLOYEE.DAT"
               ORGANIZATION IS LINE SEQUENTIAL
               ACCESS MODE IS SEQUENTIAL
               FILE STATUS IS WS-FILE-STATUS.

       DATA DIVISION.
       FILE SECTION.
       FD FD-ENR-EMPLOYEE-FILE.

       01 FILE-RECORD.
           10 EMPLOYEE-NAME PIC X(30).
           10 EMPLOYEE-ID PIC 9(5).
           10 EMPLOYEE-DEPARTMENT PIC X(20).

       WORKING-STORAGE SECTION.
       01  WS-FILE-STATUS PIC XX.
       01  WS-EOF-FLAG PIC 9 VALUE ZEROES.
            88 WS-EOF VALUE 1.
            88 WS-NOT-EOF VALUE 0.
       01 WS-INDEX PIC 9(2) VALUE ZEROES.


       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
            DISPLAY "*********** FILE HANDLING EXAMPLE **********".
            OPEN INPUT FD-ENR-EMPLOYEE-FILE
            if WS-FILE-STATUS NOT = "00"
                DISPLAY "Error opening file. Status: " WS-FILE-STATUS
                STOP RUN
            END-IF
            SET WS-EOF-FLAG TO 0

            PERFORM UNTIL WS-EOF-FLAG = 0
                 READ FD-ENR-EMPLOYEE-FILE INTO FILE-RECORD
                 AT END
                     SET WS-EOF TO TRUE
                 NOT AT END
                 DISPLAY "Employee Name: " EMPLOYEE-NAME
                 DISPLAY "Employee ID: " EMPLOYEE-ID
                 DISPLAY "Employee Department: " EMPLOYEE-DEPARTMENT
                END-READ
            END-PERFORM
            CLOSE FD-ENR-EMPLOYEE-FILE

            DISPLAY "*********** WRITING TO FILE **********".
            OPEN OUTPUT FD-ENR-EMPLOYEE-FILE
            if WS-FILE-STATUS NOT = "00"
                DISPLAY "Error opening file for writing. Status: "
                 WS-FILE-STATUS
                STOP RUN
            END-IF
            MOVE "John Doe" TO EMPLOYEE-NAME
            MOVE 12345 TO EMPLOYEE-ID
            MOVE "Engineering" TO EMPLOYEE-DEPARTMENT
            WRITE FILE-RECORD
            if WS-FILE-STATUS NOT = "00"
                DISPLAY "Error writing to file. Status: " WS-FILE-STATUS
                STOP RUN
            END-IF
            CLOSE FD-ENR-EMPLOYEE-FILE

            STOP RUN.
       END PROGRAM YOUR-PROGRAM-NAME.
