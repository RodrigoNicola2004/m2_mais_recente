package commer.mmr.m2;

import java.util.List;

public class Contato {
    private String id;
    private String name;
    private List<Telefone> telefone;

    public Contato() {
        // Construtor vazio necessário para Firebase ---> mudamo para sql
    }

    public Contato(String id, String name, List<Telefone> telefone) {
        this.id = id;
        this.name = name;
        this.telefone = telefone;
    }

    // Getters e Setters acho q é assim q escreve
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Telefone> getTelefone() {
        return telefone;
    }

    public void setTelefone(List<Telefone> telefone) {
        this.telefone = telefone;
    }
}
