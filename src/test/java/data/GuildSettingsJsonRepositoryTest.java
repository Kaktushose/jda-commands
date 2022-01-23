package data;

import com.github.kaktushose.jda.commands.data.impl.GuildSettingsJsonRepository;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GuildSettingsJsonRepositoryTest {

    private static GuildSettingsJsonRepository repository;
    private static File tempFile;

    @BeforeAll
    public static void setup() {
        String path = GuildSettingsJsonRepositoryTest.class.getResource("guilds.json").getFile();
        repository = new GuildSettingsJsonRepository(path);
        tempFile = new File(path.replace("guilds", "temp"));
    }

    @AfterAll
    public static void cleanup() throws IOException {
        Files.deleteIfExists(tempFile.toPath());
    }

    @Test
    public void findById_WithExistingId_ShouldBePresent() {
        assertTrue(repository.findById(0).isPresent());
    }

    @Test
    public void findById_WithNonExistingId_ShouldBeEmpty() {
        assertFalse(repository.findById(1).isPresent());
    }

    @Test
    public void existsById_WithExistingId_ShouldBeTrue() {
        assertTrue(repository.existsById(0));
    }

    @Test
    public void existsById_WithNonExistingId_ShouldBeFalse() {
        assertFalse(repository.existsById(1));
    }

    @Test
    public void findAll_WithOneEntity_EqualsCount() {
        assertEquals(repository.findAll().size(), repository.count());
    }

    @Test
    public void save_WithDefaultGuildSettings_ShouldWork() {
        GuildSettingsJsonRepository saveRepository = new GuildSettingsJsonRepository(tempFile);

        saveRepository.save(0, new GuildSettings());
        GuildSettingsJsonRepository loadRepository = new GuildSettingsJsonRepository(tempFile);
        Optional<GuildSettings> settings = loadRepository.findById(0);

        assertTrue(settings.isPresent());
    }

    @Test
    public void delete_WithExistingId_ShouldWork() {
        GuildSettingsJsonRepository saveRepository = new GuildSettingsJsonRepository(tempFile);

        saveRepository.delete(0);
        GuildSettingsJsonRepository loadRepository = new GuildSettingsJsonRepository(tempFile);
        Optional<GuildSettings> settings = loadRepository.findById(0);

        assertFalse(settings.isPresent());
    }
}
