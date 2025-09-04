@echo off
REM Demonstration script to run two clients SEQUENTIALLY (not concurrently).

echo Initial Table State:
REM Display the current table content at the beginning.
java -cp target/classes com.distributed.service.Client select
echo.

REM Run the first client instance (blocks until it finishes).
echo Running FIRST client instance...
REM This client might, for example, delete E2 and then update E5, and select all.
java -cp target/classes com.distributed.service.Client delete E2 update E5 name=CaseyNew title="Syst.Anal" select
echo.

REM Now run the second client instance (also blocks until it finishes).
echo After this, running SECOND client instance...
REM This client might, for example, insert E10, then select E5.
java -cp target/classes com.distributed.service.Client insert E10 name=Mary title=Eng select E5
echo.

REM Show final table state:
echo Final Table State:
java -cp target/classes com.distributed.service.Client select
echo.

REM Optionally restore the database to its initial state.
echo Restoring the database to initial state...
java -cp target/classes com.distributed.service.RestoreInvoker
echo.

pause
