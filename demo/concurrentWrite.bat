@echo off
REM Integrated Batch Script to Run Two Concurrent Client Instances and Automatically Show Final Table State.

REM Delete any previous marker files if they exist.
if exist client1_done.txt del client1_done.txt
if exist client2_done.txt del client2_done.txt

echo Initial Table State:
REM Display the current table content at the beginning.
java -cp target/classes com.distributed.service.Client select
echo.

REM Launch the first client instance concurrently.
REM This instance runs a sequence: delete E2, update E5, then select all.
REM After finishing, it echoes "done" into client1_done.txt and then pauses.
start "Client1" cmd /k "java -cp target/classes com.distributed.service.Client delete E2 update E5 name=CaseyNew title=\"Syst.Anal\" select & echo done > client1_done.txt & pause"

REM Launch the second client instance concurrently.
REM This instance runs a sequence: insert E10, then select E5.
REM After finishing, it echoes "done" into client2_done.txt and then pauses.
start "Client2" cmd /k "java -cp target/classes com.distributed.service.Client insert E10 name=Mary title=Eng select E5 & echo done > client2_done.txt & pause"

REM Wait until both marker files exist.
:wait_loop
if not exist client1_done.txt goto wait_loop
if not exist client2_done.txt goto wait_loop

echo.
echo Final Table State:
java -cp target/classes com.distributed.service.Client select
echo.

echo.
echo After all transactions, restore the db file to initial state
java -cp target/classes com.distributed.service.RestoreInvoker
echo.

REM Optionally, clean up marker files.
del client1_done.txt
del client2_done.txt

pause
