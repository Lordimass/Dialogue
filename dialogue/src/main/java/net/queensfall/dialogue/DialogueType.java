package net.queensfall.dialogue;

public enum DialogueType {
    TEXT_INPUT("Pages/DialogueTextInput.ui", 0),
    CHOICE_2("Pages/DialogueChoice2.ui", 2),
    CHOICE_3("Pages/DialogueChoice3.ui", 3),
    CHOICE_4("Pages/DialogueChoice4.ui", 4),
    DIALOGUE_1("Pages/Dialogue1.ui", 1),
    DIALOGUE_2("Pages/Dialogueg2.ui", 2),
    DIALOGUE_3("Pages/Dialogue3.ui", 3),
    DIALOGUE_4("Pages/Dialogue4.ui", 4),
    UNSET(null, 0);

    public final String uiPath;
    public final int entries;

    DialogueType(String uiPath, int entries) {
        this.uiPath = uiPath;
        this.entries = entries;
    }

    public boolean isInput() {
        return TEXT_INPUT.equals(this);
    }

    public boolean isDialog() {
        return name().startsWith("DIALOGUE");
    }

    public boolean isChoice() {
        return name().startsWith("CHOICE");
    }
}
