package dev.subtypezero.betternotes.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.BetterNotesPluginPanel;
import dev.subtypezero.betternotes.common.DisplayAttributes;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.serialization.ColorDeserializer;
import dev.subtypezero.betternotes.serialization.ColorSerializer;
import dev.subtypezero.betternotes.serialization.SectionDeserializer;
import dev.subtypezero.betternotes.serialization.SectionSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.subtypezero.betternotes.BetterNotesPlugin.MAX_NAME_LENGTH;

@Slf4j
public class ImportExportUtils {
    private final BetterNotesPlugin plugin;
    private final BetterNotesPluginPanel panel;
    private final DataManager dataManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImportExportUtils(BetterNotesPlugin plugin) {
        this.plugin = plugin;
        this.panel = plugin.getPanel();
        this.dataManager = plugin.getDataManager();

        InjectableValues values = new InjectableValues.Std()
                .addValue(DataManager.class, dataManager);
        this.objectMapper.setInjectableValues(values);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Color.class, new ColorSerializer());
        module.addDeserializer(Color.class, new ColorDeserializer());
        module.addSerializer(Section.class, new SectionSerializer());
        module.addDeserializer(Section.class, new SectionDeserializer());
        this.objectMapper.registerModule(module);
    }

    // Import
    public void importNote() {
        try {
            String json = showImportDialog(Note.class);

            if (StringUtils.isEmpty(json)) {
                return;
            }

            Note note = objectMapper.readValue(json, Note.class);

            if (validateObject(note)) {
                dataManager.addNote(note);
                plugin.updateOverview();
            } else {
                showValidationDialog("Note");
            }
        } catch (Exception e) {
            log.error("Unable to import Note", e);
            showErrorDialog("Import", "Note");
        }
    }

    public void importSection() {
        try {
            String json = showImportDialog(Section.class);

            if (StringUtils.isEmpty(json)) {
                return;
            }

            Section section = objectMapper.readValue(json, Section.class);

            if (validateObject(section)) {
                dataManager.addSection(section);
                plugin.updateOverview();
            } else {
                showValidationDialog("Section");
            }
        } catch (Exception e) {
            log.error("Unable to import Section", e);
            showErrorDialog("Import", "Section");
        }
    }

    private String showImportDialog(Class<?> clazz) {
        String className = clazz.getSimpleName();
        return JOptionPane.showInputDialog(panel,
                "Enter " + className + " data",
                "Import " + className,
                JOptionPane.PLAIN_MESSAGE);
    }

    // Mass Import
    public void massImportNotes() {
        try {
            String json = showMassImportDialog();
            List<Note> notes = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Note.class));

            int invalid = 0;

            for (Note note : notes) {
                if (validateObject(note)) {
                    dataManager.addNote(note);
                } else {
                    invalid++;
                }
            }

            if (invalid > 0) {
                showValidationDialog("Notes", invalid);
            }

            if (invalid < notes.size()) {
                plugin.updateOverview();
            }
        } catch (Exception e) {
            log.error("Unable to mass import Notes", e);
            showErrorDialog("Mass Import", "Note");
        }
    }

    public void massImportSections() {
        try {
            String json = showMassImportDialog();
            List<Section> sections = objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Section.class));

            int invalid = 0;

            for (Section section : sections) {
                if (validateObject(section)) {
                    dataManager.addSection(section);
                } else {
                    invalid++;
                }
            }

            if (invalid > 0) {
                showValidationDialog("Sections", invalid);
            }

            if (invalid < sections.size()) {
                plugin.updateOverview();
            }
        } catch (Exception e) {
            log.error("Unable to mass import Sections", e);
            showErrorDialog("Mass Import", "Section");
        }
    }

    private String showMassImportDialog() throws IOException {
        FileFilter jsonFiler = new FileNameExtensionFilter("JSON files", "json");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select Import File");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(jsonFiler);

        if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
            Path path = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
            return new String(Files.readAllBytes(path));
        }

        return null;
    }

    // Validate
    private <T extends DisplayAttributes> boolean validateObject(T object) {
        if (object == null) {
            return false;
        }

        if (StringUtils.isEmpty(object.getName()) || object.getName().length() > MAX_NAME_LENGTH) {
            return false;
        }

        if (isDuplicate(object)) {
            return false;
        }

        if (object instanceof Section) {
            Section section = (Section) object;

            for (Note note : section.getNotes()) {
                // This is used to indicate one or more notes couldn't be found while importing
                if (note.getName() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private <T extends DisplayAttributes> boolean isDuplicate(T object) {
        if (object instanceof Note) {
            return dataManager.getNotes().stream()
                    .anyMatch(n -> n.getName().equals(object.getName()));
        } else if (object instanceof Section) {
            return dataManager.getSections().stream()
                    .anyMatch(s -> s.getName().equals(object.getName()));
        }

        throw new UnsupportedOperationException("Unsupported Type");
    }

    // Export
    public void exportNote(Note note) {
        exportToClipboard(note, e -> {
            log.error("Unable to export Note", e);
            showErrorDialog("Export", "Note");
        });
    }

    public void exportSection(Section section) {
        exportToClipboard(section, e -> {
            log.error("Unable to export Section", e);
            showErrorDialog("Export", "Section");
        });
    }

    private void exportToClipboard(Object o, ExceptionHandler onException) {
        try {
            String json = objectMapper.writeValueAsString(o);
            StringSelection selection = new StringSelection(json);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        } catch (JsonProcessingException e) {
            onException.handle(e);
        }
    }

    // Mass Export
    public void massExportNotes() {
        massExport(dataManager.getNotes(), "Notes");
    }

    public void massExportSections() {
        massExport(dataManager.getSections(), "Sections");
    }

    private <T> void massExport(List<T> data, String type) {
        File directory = showMassExportDialog(type);

        if (directory == null) {
            return;
        }

        String userName = (plugin.getClient().getLocalPlayer() != null
                ? "_" + plugin.getClient().getLocalPlayer().getName()
                : "")
                .replace(" ", "_");
        String fileName = (directory.getAbsolutePath() + "/" + type.toLowerCase() + userName + ".json")
                .replace("\\", "/");

        try {
            String json = objectMapper.writeValueAsString(data);
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            log.error("Unable to mass export {}", type, e);
            showErrorDialog("Mass Export", type);
        }
    }

    private File showMassExportDialog(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose Directory to Export " + type);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    private void showErrorDialog(String action, String type) {
        JOptionPane.showMessageDialog(panel,
                "Unable to " + action.toLowerCase() + " " + type + ".",
                action + " " + type + " Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showValidationDialog(String type) {
        JOptionPane.showMessageDialog(panel,
                "Validation failed while importing " + type + ".",
                type + " Import Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showValidationDialog(String type, int invalid) {
        JOptionPane.showMessageDialog(panel,
                "Validation failed while importing " + type + " (" + invalid + " skipped).",
                type + " Import Failed",
                JOptionPane.WARNING_MESSAGE);
    }
}
