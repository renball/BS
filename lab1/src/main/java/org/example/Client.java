package org.example;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    static List<Character> ownPlayersTable = new ArrayList<>();
    static List<Character> enemyHidedTable = new ArrayList<>();
    char CF_ship ='H';
    char CF_water ='~';
    char CF_dmg ='X';
    char CF_miss ='O';
    static int numberOfPlayer = 0;
    static int status = 0;
    static int FIELD_SIZE = 10;

    public static void main(String[] args) throws RemoteException, NotBoundException {


        Socket socket;
        try {
            socket = new Socket(InetAddress.getLocalHost(), 4040);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            Game_model gamemodel = null;

            try {
                numberOfPlayer = input.readByte();
                gamemodel = (Game_model) input.readObject();
            } catch (IOException | ClassNotFoundException ignored) {
            }
            if (numberOfPlayer == 1) {
                ownPlayersTable = gamemodel.firsPlayerTables;
                enemyHidedTable = gamemodel.secondPlayerTables;
            }
            if (numberOfPlayer == 2) {
                ownPlayersTable = gamemodel.secondPlayerTables;
                enemyHidedTable = gamemodel.firsPlayerTables;
            }
            status = gamemodel.status;
            print(ownPlayersTable, enemyHidedTable);

            Scanner scanner = new Scanner(System.in);


            while (true) {
                if (numberOfPlayer == status) {
                    System.out.println("your turn");

                    List<String> s = List.of(scanner.nextLine().split(" "));

                    makeMove(output, Integer.parseInt(s.get(0)), Integer.parseInt(s.get(1)));
                }

                try {
                    gamemodel = (Game_model) input.readObject();
                    System.out.println("after move");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (numberOfPlayer == 1) {
                    ownPlayersTable = gamemodel.firsPlayerTables;
                    enemyHidedTable = gamemodel.secondPlayerTables;
                }
                if (numberOfPlayer == 2) {
                    ownPlayersTable = gamemodel.secondPlayerTables;
                    enemyHidedTable = gamemodel.firsPlayerTables;
                }
                status = gamemodel.status;
                print(ownPlayersTable, enemyHidedTable);

                if (status == 3 && numberOfPlayer == 1 || status == 4 && numberOfPlayer == 2) {
                    System.out.println("you win");
                    break;
                }
                if (status == 3 && numberOfPlayer == 2 || status == 4 && numberOfPlayer == 1) {
                    System.out.println("you defeat");
                    break;
                }

                if (status == 3 || status == 4) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(List<Character> ownPlayersTable, List<Character> enemyHidedTable) {
        char CF_ship ='H';
        char CF_water ='~';
        char CF_dmg ='X';
        char CF_miss ='O';

        int i = 0;
        while (i < FIELD_SIZE) {
            StringBuilder FIELD = new StringBuilder();
            {
                int j = 0;
                while (j < FIELD_SIZE) {
                    FIELD.append(ownPlayersTable.get(FIELD_SIZE * i + j)).append("  ");
                    j++;
                }
            }
            FIELD.append("   ");
            int j = 0;
            while (j < FIELD_SIZE) {
                Character fieldCell = enemyHidedTable.get(FIELD_SIZE * i + j);
                if (fieldCell == CF_ship) fieldCell = CF_water;
                FIELD.append(fieldCell).append("  ");
                j++;
            }
            System.out.println(FIELD);
            i++;
        }
    }

    public static void makeMove(ObjectOutputStream output, int i, int j) {
        synchronized (output) {
            try {
                output.writeObject(new my_points(i, j));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
