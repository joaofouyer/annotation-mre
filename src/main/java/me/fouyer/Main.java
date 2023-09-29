package me.fouyer;

public class Main {
    public static void main(String[] args) {
        String personString = "João Scott Palmer Halpert Schrute Beesly  joao@fouyer.me      Brasil          São Paulo             Software Developer              ";
        Person person = PersonPositional.build(personString); // I wish this was available after first compile.
        System.out.println(person.getCity()); //Should print São Paulo
    }
}