#!/usr/bin/env bash

# Copyright 2023-2024 Broadcom. All rights reserved.

gfsh -e "connect --locator=localhost[10334]"  -e "shutdown --include-locators=true --time-out=15"