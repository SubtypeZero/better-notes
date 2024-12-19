package dev.subtypezero.betternotes.util;

import dev.subtypezero.betternotes.common.DisplayAttributes;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DataManager {
    private static final String CONFIG_GROUP = "betternotes";
    private static final String CONFIG_KEY_NOTE_PREFIX = "note_";
    private static final String CONFIG_KEY_SECTION_PREFIX = "section_";

    private static DataManager instance;

    private final ConfigManager configManager;

    private final Section unassignedSection;

    @Getter
    private List<Note> notes;

    @Getter
    private List<Section> sections;

    private DataManager(ConfigManager configManager) {
        this.configManager = configManager;
        notes = new ArrayList<>();
        sections = new ArrayList<>();
        unassignedSection = new Section("Unassigned");
    }

    public static synchronized DataManager getInstance(ConfigManager configManager) {
        if (instance == null) {
            instance = new DataManager(configManager);
        }
        return instance;
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("DataManager instance is null");
        }
        return instance;
    }

    public void addNote(Note note) {
        saveNoteToConfig(note);
        notes.add(note);
    }

    public void removeNote(Note note) {
        notes.remove(note);
        removeNoteFromConfig(note);
    }

    public void loadNotesFromConfig() {
        // Get the prefix for note configurations
        String noteKeyPrefix = ConfigManager.getWholeKey(CONFIG_GROUP, null, CONFIG_KEY_NOTE_PREFIX);

        // Use the prefix to get all note config keys
        notes = configManager.getConfigurationKeys(noteKeyPrefix).stream()
                .map(key -> {
                    // Get individual note config keys (e.g. "groupName.notePrefix_noteName" -> "notePrefix_noteName")
                    String noteKey = key.substring(noteKeyPrefix.length() - CONFIG_KEY_NOTE_PREFIX.length());
                    return loadNoteFromConfig(noteKey);
                })
                .collect(Collectors.toList());
    }

    private Note loadNoteFromConfig(String key) {
        return configManager.getConfiguration(CONFIG_GROUP, key, Note.class);
    }

    public void saveNotesToConfig(List<Note> notes) {
        notes.forEach(this::saveNoteToConfig);
    }

    public void saveNoteToConfig(Note note) {
        // TODO Actually save notes
        //String noteKey = CONFIG_KEY_NOTE_PREFIX + note.getName();
        //configManager.setConfiguration(CONFIG_GROUP, noteKey, note);
    }

    private void removeNoteFromConfig(Note note) {
        // TODO Actually remove notes
        //String noteKey = CONFIG_KEY_NOTE_PREFIX + note.getName();
        //configManager.unsetConfiguration(CONFIG_GROUP, noteKey);
    }

    public void addSection(Section section) {
        saveSectionToConfig(section);
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
        removeSectionFromConfig(section);
    }

    public void loadSectionsFromConfig() {
        // Get the prefix for section configurations
        String sectionKeyPrefix = ConfigManager.getWholeKey(CONFIG_GROUP, null, CONFIG_KEY_SECTION_PREFIX);

        // Use the prefix to get all section config keys
        sections = configManager.getConfigurationKeys(sectionKeyPrefix).stream()
                .map(key -> {
                    // Get section keys (e.g. "groupName.sectionPrefix_sectionName" -> "sectionPrefix_sectionName")
                    String sectionKey = key.substring(sectionKeyPrefix.length() - CONFIG_KEY_NOTE_PREFIX.length());
                    return loadSectionFromConfig(sectionKey);
                })
                .collect(Collectors.toList());
    }

    private Section loadSectionFromConfig(String key) {
        return configManager.getConfiguration(CONFIG_GROUP, key, Section.class);
    }

    public void saveSectionsToConfig(List<Section> sections) {
        sections.forEach(this::saveSectionToConfig);
    }

    private void saveSectionToConfig(Section section) {
        // TODO Actually save sections
        //String sectionKey = CONFIG_KEY_SECTION_PREFIX + section.getName();
        //configManager.setConfiguration(CONFIG_GROUP, sectionKey, section);
    }

    private void removeSectionFromConfig(Section section) {
        // TODO Actually remove sections
        //String sectionKey = CONFIG_KEY_SECTION_PREFIX + section.getName();
        //configManager.unsetConfiguration(CONFIG_GROUP, sectionKey);
    }

    public Section getUnassignedSection(List<Note> notes) {
        unassignedSection.setNotes(getUnassignedNotes(notes));
        return unassignedSection;
    }

    private List<Note> getUnassignedNotes(List<Note> notes) {
        Set<Note> unassignedNotes = new HashSet<>(notes);

        for (Section section : sections) {
            for (Note note : section.getNotes()) {
                unassignedNotes.remove(note);
            }
        }

        return new ArrayList<>(unassignedNotes);
    }

    public List<Note> filterNotes(String search) {
        String filterText = search.trim().toLowerCase();
        final String NOTES_SEARCH_TAG = "notes:";

        return notes.stream()
                .filter(note -> {
                    // Filter notes by contents
                    if (filterText.startsWith(NOTES_SEARCH_TAG) && filterText.length() > NOTES_SEARCH_TAG.length()) {
                        String noteText = filterText.substring(NOTES_SEARCH_TAG.length()).trim();
                        return note.getNotes().toLowerCase().contains(noteText);
                    }

                    // Filter notes by name
                    return note.getName().toLowerCase().contains(filterText);
                })
                .collect(Collectors.toList());
    }

    public Set<Note> intersectNotes(List<Note> original, List<Note> filter) {
        Set<Note> intersection = new HashSet<>(original);
        intersection.retainAll(filter);
        return intersection;
    }

    public <T extends DisplayAttributes> void moveUp(T displayItem) {
        List<T> list = getList(displayItem);
        int currentIndex = list.indexOf(displayItem);
        int targetIndex = getTargetIndex(currentIndex - 1, list.size());
        moveItem(list, currentIndex, targetIndex);
    }

    public <T extends DisplayAttributes> void moveDown(T displayItem) {
        List<T> list = getList(displayItem);
        int currentIndex = list.indexOf(displayItem);
        int targetIndex = getTargetIndex(currentIndex + 1, list.size());
        moveItem(list, currentIndex, targetIndex);
    }

    public <T extends DisplayAttributes> void moveToTop(T displayItem) {
        List<T> list = getList(displayItem);
        int currentIndex = list.indexOf(displayItem);
        int targetIndex = 0;
        moveItem(list, currentIndex, targetIndex);
    }

    public <T extends DisplayAttributes> void moveToBottom(T displayItem) {
        List<T> list = getList(displayItem);
        int currentIndex = list.indexOf(displayItem);
        int targetIndex = list.size() - 1;
        moveItem(list, currentIndex, targetIndex);
    }

    public <T extends DisplayAttributes> void moveToPosition(T displayItem, int position) {
        List<T> list = getList(displayItem);
        int currentIndex = list.indexOf(displayItem);
        int targetIndex = getTargetIndex(position, list.size());
        moveItem(list, currentIndex, targetIndex);
    }

    public void moveNoteWithinSection(Section section, Note note, int delta) {
        List<Note> sectionNotes = section.getNotes();
        int currentIndex = sectionNotes.indexOf(note);
        int targetIndex = getTargetIndex(currentIndex + delta, sectionNotes.size());
        moveItem(sectionNotes, currentIndex, targetIndex);
    }

    @SuppressWarnings("unchecked")
    private <T extends DisplayAttributes> List<T> getList(T displayItem) {
        if (displayItem instanceof Note) {
            return (List<T>) notes;
        } else if (displayItem instanceof Section) {
            return (List<T>) sections;
        } else {
            throw new UnsupportedOperationException("This type is not supported");
        }
    }

    private int getTargetIndex(int targetIndex, int size) {
        return Math.max(0, Math.min(targetIndex, size - 1));
    }

    private <T extends DisplayAttributes> void moveItem(List<T> list, int currentIndex, int targetIndex) {
        if (currentIndex == targetIndex) {
            return;
        }

        T displayItem = list.remove(currentIndex);
        list.add(targetIndex, displayItem);
        // TODO Update configuration
    }
}
