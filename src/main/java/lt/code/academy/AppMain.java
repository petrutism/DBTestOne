package lt.code.academy;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import lt.code.academy.client.MongodbObjectClientProvider;
import lt.code.academy.data.User;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class AppMain {
    private final Set<User> bankUsers = new HashSet<>();
    private User sender;
    private User receiver;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MongoClient client = MongodbObjectClientProvider.getClient();
        AppMain appMain = new AppMain();

        appMain.userAction(sc, client);
    }

    private void userAction(Scanner sc, MongoClient client) {
        String action;

        userLogin(sc, client);

        do {
            menu();
            action = sc.nextLine();
            switch (action) {
                case "1" -> sendMoney(sc, client);
                case "2" -> System.out.println("Finishing...");
                default -> System.out.println("There is no such action...");
            }
        } while (!action.equals("2"));
    }

    private void readUsers(MongoClient client) {
        MongoCollection<User> users = client.getDatabase("Bank")
                .getCollection("Users", User.class);
        FindIterable<User> readUsers = users.find();

        for (User u : readUsers) {
            bankUsers.add(u);
        }

        if (bankUsers.isEmpty()) {
            User firstUser = new User(null, "Mindaugas", "Petrutis", 10000);
            users.insertOne(firstUser);
        }
    }

    private void sendMoney(Scanner sc, MongoClient client) {
        int sumToSend;

        MongoCollection<User> users = client.getDatabase("Bank")
                .getCollection("Users", User.class);

        System.out.println("Potential receivers is: ");
        FindIterable<User> potentialReceivers = users.find(nor(and(eq("name", sender.getName()), eq("surname", sender.getSurname()))));

        for (User pr : potentialReceivers) {
            System.out.printf("%s %s%n", pr.getName(), pr.getSurname());
        }

        System.out.println("Enter recipients name: ");
        String recipientsName = sc.nextLine();
        System.out.println("Enter recipients surname: ");
        String recipientsSurname = sc.nextLine();

        if (userNotExists(recipientsName, recipientsSurname)) {
            System.out.println("There is no such recipient...");
            return;
        }

        FindIterable<User> rUser = users.find(and(eq("name", recipientsName), eq("surname", recipientsSurname)));
        for (User r : rUser) {
            receiver = r;
        }

        System.out.println("Enter amount of money (int type) to send: ");
        try {
            sumToSend = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("You entered wrong amount of money, we will send nothing...");
            return;
        }
        if (sumToSend > sender.getBalance()) {
            System.out.println("Go to work, you do not have enough money...");
            return;
        }
        users.updateOne(and(eq("name", receiver.getName()), eq("surname", receiver.getSurname())), set("balance", receiver.getBalance() + sumToSend));
        users.updateOne(and(eq("name", sender.getName()), eq("surname", sender.getSurname())), set("balance", sender.getBalance() - sumToSend));
        sender.setBalance(sender.getBalance() - sumToSend);
        receiver.setBalance(receiver.getBalance() + sumToSend);

        System.out.printf("%s euros successfully transferred to %s %s...%n", sumToSend, receiver.getName(), receiver.getSurname());
        System.out.printf("Your still have: %s euros... %n", sender.getBalance());

    }

    private boolean userNotExists(String name, String surname) {
        for (User u : bankUsers) {
            if (name.equals(u.getName()) && surname.equals(u.getSurname())) {
                return false;
            }
        }

        return true;
    }

    private void userLogin(Scanner sc, MongoClient client) {
        welcome();

        int balance;
        readUsers(client);

        MongoDatabase database = client.getDatabase("Bank");
        MongoCollection<User> users = database.getCollection("Users", User.class);

        System.out.println("Your name: ");
        String name = sc.nextLine();
        System.out.println("Your surname: ");
        String surname = sc.nextLine();

        if (userNotExists(name, surname)) {
            System.out.println("Your balance is (int type): ");
            try {
                balance = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("You entered wrong amount of money, so your balance will be 1000 for free...");
                balance = 1000;
            }

            User newUser = new User(null, name, surname, balance);
            bankUsers.add(newUser);
            users.insertOne(newUser);
        }

        FindIterable<User> sUser = users.find(and(eq("name", name), eq("surname", surname)));
        for (User u : sUser) {
            sender = u;
        }
        System.out.printf("Your balance is: %s euros... %n", sender.getBalance());
    }

    private void menu() {
        String text = """
                1 -> Send money
                2 -> Finish
                """;
        System.out.println(text);
    }

    private void welcome() {
        String welcome = """
                --- WELCOME TO BANK ---
                You must login now.
                To login, enter your name and surname.
                If you are not registered in our system, it will be done automatic.
                So let's begin:
                """;
        System.out.println(welcome);
    }
}