/*
 * Copyright 2019 - 2021 VMware, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import {createRoot} from 'react-dom/client';
import App from './App';
const root = createRoot(document.getElementById("root"));

root.render(<App />, document.getElementById('root'));