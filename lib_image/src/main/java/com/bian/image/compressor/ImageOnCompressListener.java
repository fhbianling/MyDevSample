package com.bian.image.compressor;

import java.util.List;

interface ImageOnCompressListener {

    void onStart();

    void onSuccess(String file);

    void onSuccess(List<String> file);

    void onError(Throwable throwable);
}
