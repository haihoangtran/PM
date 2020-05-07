package model;

public class DictionaryModel {
    private int wordId;
    private String word;

    public DictionaryModel (int wordId, String word){
        this.wordId = wordId;
        this.word = word;
    }

    public int getWordId(){return this.wordId;}

    public String getWord(){return this.word;}
}
