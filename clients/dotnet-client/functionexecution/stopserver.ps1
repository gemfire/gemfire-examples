# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

$GFSH_PATH = ""
if (Get-Command gfsh -ErrorAction SilentlyContinue)
{
    $GFSH_PATH = "gfsh"
}
else
{
    if (-not (Test-Path env:GEMFIRE_HOME))
    {
        Write-Host "Could not find gfsh.  Please set the GEMFIRE_HOME path. e.g. "
        Write-Host "(Powershell) `$env:GEMFIRE_HOME = <path to GemFire>"
        Write-Host " OR"
        Write-Host "(Command-line) set %GEMFIRE_HOME% = <path to GemFire>"
    }
    else
    {
        $GFSH_PATH = "$env:GEMFIRE_HOME\bin\gfsh.bat"
    }
}

if ($GFSH_PATH -ne "")
{
   Invoke-Expression "$GFSH_PATH -e 'connect' -e 'shutdown --include-locators=true'"
}