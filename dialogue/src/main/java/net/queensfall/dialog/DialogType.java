package net.queensfall.dialog;

public enum DialogType {
    TEXT_INPUT("Pages/DialogueTextInput.ui", 0),
    CHOICE_2("Pages/DialogueChoice2.ui", 2),
    CHOICE_3("Pages/DialogueChoice3.ui", 3),
    CHOICE_4("Pages/DialogueChoice4.ui", 4),
    DIALOG_1("Pages/DialogueDialog1.ui", 1),
    DIALOG_2("Pages/DialogueDialog2.ui", 2),
    DIALOG_3("Pages/DialogueDialog3.ui", 3),
    DIALOG_4("Pages/DialogueDialog4.ui", 4),
    UNSET(null, 0);

    public final String uiPath;
    public final int entries;

    DialogType(String uiPath, int entries) {
        this.uiPath = uiPath;
        this.entries = entries;
    }

    public boolean isInput() {
        return TEXT_INPUT.equals(this);
    }

    public boolean isDialog() {
        return name().startsWith("DIALOG");
    }

    public boolean isChoice() {
        return name().startsWith("CHOICE");
    }
}
