package commer.mmr.m2;

public class Telefone {
    private String number;
    private String type;

    public Telefone() {
        // Construtor vazio necessário para Firebase --> virou sql kkkk
    }

    public Telefone(String number, String type) {
        this.number = number;
        this.type = type;
    }

    // Getters e Setters
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
