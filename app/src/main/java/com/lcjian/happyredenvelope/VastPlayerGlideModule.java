package com.lcjian.happyredenvelope;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.lib.util.common.StorageUtils;

import java.io.File;

public class VastPlayerGlideModule extends OkHttpGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new AppDiskCacheFactory(context)).setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    private final class AppDiskCacheFactory extends DiskLruCacheFactory {

        private AppDiskCacheFactory(Context context) {
            this(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR,
                    DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
        }

        private AppDiskCacheFactory(final Context context, final String diskCacheName,
                                    int diskCacheSize) {
            super(new CacheDirectoryGetter() {
                @Override
                public File getCacheDirectory() {
                    File cacheDirectory = StorageUtils.getCacheDirectory(context);
                    if (cacheDirectory == null) {
                        return null;
                    }
                    if (diskCacheName != null) {
                        return new File(cacheDirectory, diskCacheName);
                    }
                    return cacheDirectory;
                }
            }, diskCacheSize);
        }
    }

}
