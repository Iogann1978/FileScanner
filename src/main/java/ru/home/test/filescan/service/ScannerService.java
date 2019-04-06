package ru.home.test.filescan.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.home.test.filescan.entity.Action;
import ru.home.test.filescan.entity.File;
import ru.home.test.filescan.repository.ActionRepository;
import ru.home.test.filescan.repository.ConfigRepository;
import ru.home.test.filescan.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class ScannerService {
    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Scheduled(fixedRate = 60000)
    public void scanSource() throws IOException {
        val config_src = Optional.of(configRepository.findByName("source_path"));
        val config_dest = Optional.of(configRepository.findByName("dest_path"));

        if(config_src.isPresent() && config_dest.isPresent()) {
            val source = Paths.get(config_src.get().getValue());
            val target = Paths.get(config_dest.get().getValue());
            val source_str = source.toFile().getAbsolutePath();
            val target_str = target.toFile().getAbsolutePath();
            log.info("source_path: {}", source_str);
            log.info("dest_path: {}", target_str);

            val act1 = Action.builder()
                    .descript(String.format("scan files in %s path", source_str))
                    .localDateTime(LocalDateTime.now())
                    .build();
            actionRepository.save(act1);

            Files.newDirectoryStream(Paths.get(source_str),
                    filter -> filter.toString().endsWith(".xml")).forEach(f -> {

                        val file_str = f.toFile().getName();
                        val act2 = Action.builder()
                            .descript(String.format("%s file was finded in %s", file_str, source_str))
                            .localDateTime(LocalDateTime.now())
                            .build();
                        actionRepository.save(act2);

                        log.info("file: {}", file_str);

                        val file_db = Optional.ofNullable(fileRepository.findByName(file_str));
                        if(!file_db.isPresent()) {
                            fileService.copy(f, target_str).thenAcceptAsync(r -> log.info("copied: {}", r));
                        }
            });
        }
    }

    @Scheduled(fixedRate = 60000)
    public void scanTarget() throws IOException {
        val config_dest = Optional.of(configRepository.findByName("dest_path"));
        val config_arch = Optional.of(configRepository.findByName("dest_arch"));

        if(config_dest.isPresent() && config_arch.isPresent()) {
            val source = Paths.get(config_dest.get().getValue());
            val target = Paths.get(config_arch.get().getValue());
            val source_str = source.toFile().getAbsolutePath();
            val target_str = target.toFile().getAbsolutePath();
            log.info("dest_path: {}", source_str);
            log.info("arch_path: {}", target_str);

            val act1 = Action.builder()
                    .descript(String.format("scan files in %s path", target_str))
                    .localDateTime(LocalDateTime.now())
                    .build();
            actionRepository.save(act1);

            Files.newDirectoryStream(Paths.get(source_str),
                    filter -> filter.toString().endsWith(".xml")).forEach(f -> {

                        val file_str = f.toFile().getName();
                        val act2 = Action.builder()
                            .descript(String.format("%s file was finded in %s", file_str, source_str))
                            .localDateTime(LocalDateTime.now())
                            .build();
                        actionRepository.save(act2);

                        try {
                            val content = Files.readAllBytes(f.toAbsolutePath());
                            File file = File.builder()
                                .name(file_str)
                                .content(content)
                                .build();
                            fileRepository.save(file);
                            val act3 = Action.builder()
                                    .descript(String.format("file %s was saved to db", file_str))
                                    .localDateTime(LocalDateTime.now())
                                    .build();
                            actionRepository.save(act3);

                            fileService.move(f, target_str).thenAcceptAsync(r -> log.info("removied: {}", r));

                            jmsTemplate.send("test_destination", session -> {
                                    val bytesMessage = session.createBytesMessage();
                                    bytesMessage.writeBytes(content);
                                    bytesMessage.setStringProperty("Name", file_str);
                                    return bytesMessage;
                            });
                        } catch (IOException e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                        }
            });
        }
    }
}
