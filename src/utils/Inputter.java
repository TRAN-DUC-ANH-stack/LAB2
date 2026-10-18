package utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.function.Function;

public class Inputter {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static String readString(String message) {
        System.out.print(message);
        return SCANNER.nextLine().trim();
    }

    public static String readRequiredString(String message) {
        while (true) {
            String value = readString(message);

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    public static int readInt(String message) {
        while (true) {
            String input = readString(message);

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }


    public static int readIntInRange(
            String message, int minimum, int maximum) {

        while (true) {
            int value = readInt(message);

            if (value >= minimum && value <= maximum) {
                return value;
            }

            System.out.println(
                    "Please enter a number from "
                    + minimum + " to " + maximum + "."
            );
        }
    }


    public static boolean readYesNo(String message) {
        while (true) {
            String answer = readString(message).toUpperCase();

            if (answer.equals("Y") || answer.equals("YES")) {
                return true;
            }

            if (answer.equals("N") || answer.equals("NO")) {
                return false;
            }

            System.out.println("Please enter Y or N.");
        }
    }

    public static <T> T readField(String label, T current,
            Function<T, String> show, Function<String, T> parser) {

        String hint = current == null ? "" : " [" + show.apply(current) + "]";

        while (true) {
            String text = readString(label + hint + ": ");

            if (text.isEmpty()) {
                if (current != null) {
                    return current;
                }
                System.out.println("This field cannot be empty.");
                continue;
            }

            try {
                return parser.apply(text);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}