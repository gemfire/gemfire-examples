if (-not (Test-Path env:GEMFIRE_HOME))
{
    Write-Host "Could not find GemFire.  Please set the GEMFIRE_HOME path. e.g. " -ForegroundColor Red
    Write-Host "(Powershell) `$env:GEMFIRE_HOME = <path to GemFire>" -ForegroundColor Red
    exit 1
}

set-item -path Env:CLASSPATH -value $Env:GEMFIRE_HOME\lib\gemfire-dependencies.jar
Write-Host "GEMFIRE_HOME = $env:GEMFIRE_HOME" -ForegroundColor Blue
Write-Host "CLASSPATH = $env:CLASSPATH" -ForegroundColor Blue

Write-Host ""
Write-Host "Compile Java Files" -ForegroundColor Blue
javac example\*.java javaobject\*.java
if ( -not $? )
{
    Write-Host "Failed to compile Java files. Check that GEMFIRE_HOME is correctly configured."
    exit 1
}

Write-Host ""
Write-Host "Jar class Files" -ForegroundColor Blue
jar cvf example.jar example\*.class javaobject\*.class
if ( -not $? )
{
    Write-Host "Failed to package Java class files."
    exit 1
}

Write-Host ""
Write-Host "example.jar ready to be used in examples" -ForegroundColor Blue

Remove-Item Env:CLASSPATH
