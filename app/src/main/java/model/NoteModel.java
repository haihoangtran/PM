package model;

public class NoteModel {
    private int noteId;
    private String title;
    private String content;

    public NoteModel (int noteID, String title, String content){
        this.noteId = noteID;
        this.title = title;
        this.content = content;
    }

    public int getNoteId(){return noteId;}

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
