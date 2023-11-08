package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Main_server
{
    public static void main(String[] args) {

        List<ObjectOutputStream> outputs = new ArrayList<>();
        final List<Integer> status = new ArrayList<>();
        status.add(1);

        char CF_ship ='H';
        char CF_water ='~';
        char CF_dmg ='X';
        char CF_miss ='O';

        List<Character> firstPlayerTables = new ArrayList<>();
        List<Character> secondPlayerTables = new ArrayList<>();
        final int FIELD_SIZE = 10;

        IntStream.range(0, FIELD_SIZE * FIELD_SIZE).forEach(i -> {
            firstPlayerTables.add(CF_water);
            secondPlayerTables.add(CF_water);
        });


        createTables(firstPlayerTables);

        createTables(secondPlayerTables);

        try (ServerSocket server = new ServerSocket(4040)) {
            while (true) {
                Socket socket = server.accept();
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                outputs.add(output);
                synchronized (output) {
                    sendNum(output, outputs.size());
                    sendData(output, new Game_model(status.get(0), firstPlayerTables, secondPlayerTables));
                    output.reset();
                }

                Thread t_run = new Thread(() -> {
                    while (true) {
                        my_points mypoints = null;
                        try {
                            mypoints = (my_points) input.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            outputs.remove(output);
                            break;
                        }
                        assert mypoints != null;

                        int index = (mypoints.x - 1) * FIELD_SIZE + mypoints.y - 1;

                        if (status.get(0) == 2) {
                            Character fieldCell = firstPlayerTables.get(index);
                            if (fieldCell != CF_dmg && fieldCell != CF_miss) {
                                if (fieldCell == CF_ship) {
                                    firstPlayerTables.set(index, CF_dmg);

                                    boolean defeat = true;
                                    for (int i = 0; i < 100; i++) {
                                        if (firstPlayerTables.get(i) == CF_ship) {
                                            defeat = false;
                                        }
                                    }
                                    if (defeat) {
                                        status.set(0, 4);
                                    }

                                    if (status.get(0) == 4) return;
                                }
                                if (fieldCell == CF_water) {
                                    firstPlayerTables.set(index, CF_miss);
                                    status.set(0, 1);
                                }
                            }

                        } else if (status.get(0) == 1) {
                            Character fieldCell = secondPlayerTables.get(index);
                            if (fieldCell != CF_dmg && fieldCell != CF_miss) {
                                if (fieldCell == CF_ship) {
                                    secondPlayerTables.set(index, CF_dmg);

                                    boolean defeat = true;
                                    for (int i = 0; i < 100; i++) {
                                        if (secondPlayerTables.get(i) == CF_ship) {
                                            defeat = false;
                                        }
                                    }
                                    if (defeat) {
                                        status.set(0, 3);
                                    }

                                    if (status.get(0) == 3) return;
                                }
                                if (fieldCell == CF_water) {
                                    secondPlayerTables.set(index, CF_miss);
                                    status.set(0, 2);
                                }
                            }
                        }
                        sendDataToAll(outputs, new Game_model(status.get(0), firstPlayerTables, secondPlayerTables));
                    }
                });
                t_run.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void sendData(ObjectOutputStream toClient, Game_model gamemodel) {
        try {
            toClient.writeObject(gamemodel);
            toClient.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendNum(ObjectOutputStream toClient, int num) {
        try {
            toClient.writeByte(num);
            toClient.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void sendDataToAll(List<ObjectOutputStream> outputs, Game_model gamemodel) {
        for (ObjectOutputStream stream : outputs) {
            synchronized (stream) {
                sendData(stream, gamemodel);
            }
        }
    }

    private static void createTables (List<Character> PlayersTables){
        char CF_ship ='H';
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            
            int cellsOfShip = r.nextInt(4) + 2;
            int V_O_H = r.nextInt(2);
            boolean canToInsertion = false;
            
            int position = 0;
            while (!canToInsertion) {
                position = r.nextInt(100);

                if (V_O_H == 0) {
                    if (position / 10 == (position + cellsOfShip) / 10) {
                        canToInsertion = true;
                    }
                    for (int j = position - 11; j <= position - 11 + 3; j++) {
                        for (int k = j; k <= j + cellsOfShip + 1; k++) {
                            if (k >= 0 && k <= 99) {
                                if (PlayersTables.get(k) == CF_ship) canToInsertion = false;
                            }
                        }
                    }
                }
                if (V_O_H == 1) {
                    if (position + cellsOfShip * 10 < 100) {
                        canToInsertion = true;
                    }
                    for (int j = position - 11; j <= position - 11 + cellsOfShip * 10; j += 10) {
                        for (int k = j; k <= j + 2; k++) {
                            if (k >= 0 && k <= 99) {
                                if (PlayersTables.get(k) == CF_ship) canToInsertion = false;
                            }
                        }
                    }
                }
            }
            for (int j = position; j < position + cellsOfShip * (V_O_H == 1 ? 10 : 1); j += V_O_H == 1 ? 10 : 1) {
                PlayersTables.set(j, CF_ship);
            }
        }
    }
}
