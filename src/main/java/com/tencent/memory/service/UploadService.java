package com.tencent.memory.service;

import com.tencent.memory.model.UploadResult;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface UploadService {

    void init();

    UploadResult store(MultipartFile file);

    UploadResult store(InputStream inputStream);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
