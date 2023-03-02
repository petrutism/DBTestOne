package lt.code.academy.data;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String name;
    private String surname;
    private int balance;

    public User(ObjectId id, String name, String surname, int balance) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.balance = balance;
    }

    public User() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
