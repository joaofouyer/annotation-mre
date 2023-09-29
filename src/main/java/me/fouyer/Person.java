package me.fouyer;

@PositionalObject
public class Person {
    @Positional(position = 0, length = 42)
    private String name;
    @Positional(position = 1, length = 20)
    private String email;
    @Positional(position = 2, length = 16)
    private String country;
    @Positional(position = 3, length = 22)
    private String city;
    @Positional(position = 4, length = 32)
    private String role;

    public Person(String name, String email, String country, String city, String role) {
        this.name = name;
        this.email = email;
        this.country = country;
        this.city = city;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getRole() {
        return role;
    }
}
