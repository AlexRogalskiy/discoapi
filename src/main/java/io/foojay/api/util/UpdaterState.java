/*
 * Copyright (c) 2021.
 *
 * This file is part of DiscoAPI.
 *
 *     DiscoAPI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     DiscoAPI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DiscoAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.foojay.api.util;
import java.time.Instant;


public enum UpdaterState {
    RUNNING, RUNNING_NO_MSG, OFFLINE;

    private Instant timestamp;


    UpdaterState() {
        this.timestamp = Instant.now();
    }


    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(final Instant timestamp) { this.timestamp = timestamp; }
}

