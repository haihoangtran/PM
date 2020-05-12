package model;

public class DictionaryModel {
    private String word;

    public DictionaryModel (String word){
        this.word = word.toLowerCase();
    }

    public String getWord(){return this.word;}
}
