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
 * You should have received a copy of the GNU General Public License
 * along with DiscoAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.foojay.api;


import io.foojay.api.pkg.Pkg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum DownloadManager {
    INSTANCE;

    private static final Logger                          LOGGER    = LoggerFactory.getLogger(DownloadManager.class);
    private        final ConcurrentHashMap<String, Long> downloads = new ConcurrentHashMap<>();


    public void preloadDownloads() {
        Map<String, Long> downloadsFromMongoDb = new HashMap<>();
        if (MongoDbManager.INSTANCE.connect()) {
            downloadsFromMongoDb = MongoDbManager.INSTANCE.getDowloads();
            LOGGER.debug("Successfully loaded downloads from mongodb.");
        }
        if (downloadsFromMongoDb.isEmpty()) {
            LOGGER.debug("Downloads in mongodb are empty.");
        } else {
            downloads.putAll(downloadsFromMongoDb);
            LOGGER.debug("Successfully loaded downloads for {} package ids from mongodb.", downloadsFromMongoDb.size());
        }
    }

    public void addDownload(final String pkgId, final String userAgent, final String ipAddress) {
        if (downloads.isEmpty()) { preloadDownloads(); }

        if (!CacheManager.INSTANCE.pkgCache.getKeys().contains(pkgId)) { return; }
        long noOfDownloads = downloads.containsKey(pkgId) ? downloads.get(pkgId) : 0L;
        noOfDownloads++;
        downloads.put(pkgId, noOfDownloads);

        // Store package id and no of downloads for this package
        try {
            Pkg pkg = CacheManager.INSTANCE.pkgCache.get(pkgId);
            MongoDbManager.INSTANCE.upsertDownloadForId(pkgId, noOfDownloads);
            MongoDbManager.INSTANCE.addDownloadFromUserAgent(pkgId, userAgent, ipAddress);
            MongoDbManager.INSTANCE.addDownloadToToday(pkg.getDistribution().getDistro(), pkg.getMajorVersion().getAsInt());
            LOGGER.debug("Successfully stored download for package id {} to mongodb.", pkgId);
        } catch (Exception e) {
            LOGGER.error("Error storing download download for package id {} to mongodb. {}", pkgId, e);
        }
    }
}
