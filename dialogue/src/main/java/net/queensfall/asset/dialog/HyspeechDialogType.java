package net.queensfall.asset.dialog;

public enum HyspeechDialogType {
    TEXT_INPUT("Pages/HyspeechTextInput.ui", 0),
    CHOICE_2("Pages/HyspeechChoice2.ui", 2),
    CHOICE_3("Pages/HyspeechChoice3.ui", 3),
    CHOICE_4("Pages/HyspeechChoice4.ui", 4),
    DIALOG_1("Pages/HyspeechDialog1.ui", 1),
    DIALOG_2("Pages/HyspeechDialog2.ui", 2),
    DIALOG_3("Pages/HyspeechDialog3.ui", 3),
    DIALOG_4("Pages/HyspeechDialog4.ui", 4),
    UNSET(null, 0);

    public final String uiPath;
    public final int entries;

    HyspeechDialogType(String uiPath, int entries) {
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
