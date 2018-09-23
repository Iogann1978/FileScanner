package ru.home.test.filescan.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.home.test.filescan.entity.Action;
import ru.home.test.filescan.repository.ActionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FileService {
    @Autowired
    private ActionRepository actionRepository;

    @Async
    public CompletableFuture<Boolean> copy(Path f, String target_str) throws IOException {
        val file_str = f.toFile().getName();
        val file_new = new java.io.File(target_str + java.io.File.separator + file_str);
        Files.copy(f.toAbsolutePath(), file_new.toPath(), StandardCopyOption.REPLACE_EXISTING);
        val act = Action.builder()
                .descript(String.format("file %s was copied to %s", file_str, target_str))
                .localDateTime(LocalDateTime.now())
                .build();
        actionRepository.save(act);
        return CompletableFuture.completedFuture(true);
    }

    @Async
    public CompletableFuture<Boolean> move(Path f, String target_str) throws IOException {
        val file_str = f.toFile().getName();
        val file_new = new java.io.File(target_str + java.io.File.separator + file_str);
        Files.move(f.toAbsolutePath(), file_new.toPath(), StandardCopyOption.REPLACE_EXISTING);
        val act = Action.builder()
                .descript(String.format("file %s was removed to %s", file_str, target_str))
                .localDateTime(LocalDateTime.now())
                .build();
        actionRepository.save(act);
        return CompletableFuture.completedFuture(true);
    }
}
