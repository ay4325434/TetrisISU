/*
This class orders songs based on their intensity. Since there is no way for
the computer to differentiate between the intensity (e.g. BPM, instruments,
volume, and riser effects), they have to be hardcoded.
 */
import java.util.*;
public class SongConstants {
    // List of songs in increasing intensity depending on the collection
    public static final Map<Integer, List<String>> SONG_ORDERS = new HashMap<>();
    // Matches the songs with its corresponding image
    public static final Map<Integer, List<String>> IMAGE_ORDERS = new HashMap<>();

    // Adding additional songs and images to the song maps
    public static void addAllElements(){
        SONG_ORDERS.put(1, List.of("collectionA8", "collectionA2", "collectionA3", "collectionA10", "collectionA7",
                        "collectionA4", "collectionA5", "collectionA1", "collectionA6", "collectionA9"));
        IMAGE_ORDERS.put(1, List.of("A8", "A2", "A3", "A10", "A7", "A4", "A5", "A1", "A6", "A9"));
        SONG_ORDERS.put(2, List.of("collectionB8", "collectionB1", "collectionB2", "collectionB3", "collectionB5",
                "collectionB6", "collectionB10", "collectionB9", "collectionB4", "collectionB7"));
        IMAGE_ORDERS.put(2, List.of("B8", "B1", "B2", "B3", "B5", "B6", "B10", "B9", "B4", "B7"));
        SONG_ORDERS.put(3, List.of("collectionC9", "collectionC8", "collectionC10", "collectionC4", "collectionC3",
                "collectionC5", "collectionC6", "collectionC7", "collectionC1", "collectionC2"));
        IMAGE_ORDERS.put(3, List.of("C9", "C8", "C10", "C4", "C3", "C5", "C6", "C7", "C1", "C2"));
        SONG_ORDERS.put(4, List.of("collectionD10", "collectionD1", "collectionD3", "collectionD5", "collectionD7",
                "collectionD8", "collectionD9", "collectionD4", "collectionD6", "collectionD2"));
        IMAGE_ORDERS.put(4, List.of("D10", "D1", "D3", "D5", "D7", "D8", "D9", "D4", "D6", "D2"));
        SONG_ORDERS.put(5, List.of("collectionE10", "collectionE3", "collectionE2", "collectionE4", "collectionE6",
                "collectionE8", "collectionE9", "collectionE5", "collectionE7", "collectionE1"));
        IMAGE_ORDERS.put(5, List.of("E10", "E3", "E2", "E4", "E6", "E8", "E9", "E5", "E7", "E1"));
        SONG_ORDERS.put(6, List.of("collectionF2", "collectionF3", "collectionF6", "collectionF9", "collectionF8",
                "collectionF1", "collectionF10", "collectionF7", "collectionF4", "collectionF5"));
        IMAGE_ORDERS.put(6, List.of("F2", "F3", "F6", "F9", "F8", "F1", "F10", "F7", "F4", "F5"));
        SONG_ORDERS.put(7, List.of("collectionG1", "collectionG10", "collectionG7", "collectionG2", "collectionG5",
                "collectionG3", "collectionG4", "collectionG6", "collectionG8", "collectionG9"));
        IMAGE_ORDERS.put(7, List.of("G1", "G10", "G7", "G2", "G5", "G3", "G4", "G6", "G8", "G9"));
        SONG_ORDERS.put(8, List.of("collectionH4", "collectionH6", "collectionH5", "collectionH8", "collectionH2",
                "collectionH10", "collectionH7", "collectionH1", "collectionH3", "collectionH9"));
        IMAGE_ORDERS.put(8, List.of("H4", "H6", "H5", "H8", "H2", "H10", "H7", "H1", "H3", "H9"));
        SONG_ORDERS.put(9, List.of("collectionI9", "collectionI4", "collectionI3", "collectionI7", "collectionI2",
                "collectionI10", "collectionI1", "collectionI6", "collectionI5", "collectionI8"));
        IMAGE_ORDERS.put(9, List.of("I9", "I4", "I3", "I7", "I2", "I10", "I1", "I6", "I5", "I8"));
        SONG_ORDERS.put(10, List.of("collectionJ1", "collectionJ10", "collectionJ8", "collectionJ6", "collectionJ2",
                "collectionJ9", "collectionJ4", "collectionJ7", "collectionJ5", "collectionJ3"));
        IMAGE_ORDERS.put(10, List.of("J1", "J10", "J8", "J6", "J2", "J9", "J4", "J7", "J5", "J3"));
        SONG_ORDERS.put(11, List.of("collectionK7", "collectionK1", "collectionK4", "collectionK9", "collectionK6",
                "collectionK5", "collectionK10", "collectionK2", "collectionK3", "collectionK8"));
        IMAGE_ORDERS.put(11, List.of("K7", "K1", "K4", "K9", "K6", "K5", "K10", "K2", "K3", "K8"));
    }
}
