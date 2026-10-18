package utils;

public class Menu {

    public static void showMainMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("     CAR INSURANCE MANAGEMENT");
        System.out.println("====================================");
        System.out.println("1. Add car information");
        System.out.println("2. Find a car");
        System.out.println("3. Update a car");
        System.out.println("4. Delete a car");
        System.out.println("5. Add an insurance statement");
        System.out.println("6. List of insurance statements");
        System.out.println("7. Report on uninsured cars");
        System.out.println("8. Save data");
        System.out.println("9. Load data");
        System.out.println("10. Quit");
        System.out.println("====================================");
    }

    public static String readInsuranceSortField() {
        System.out.println();
        System.out.println("Sort insurance statements by:");
        System.out.println("1. Insurance ID");
        System.out.println("2. Established date");
        System.out.println("3. License plate");
        System.out.println("4. Insurance period");

        int choice = Inputter.readIntInRange("Choose sort field: ", 1, 4);

        switch (choice) {
            case 1:
                return "id";
            case 2:
                return "date";
            case 3:
                return "plate";
            default:
                return "period";
        }
    }

    public static String readCarSortField() {
        System.out.println();
        System.out.println("Sort uninsured cars by:");
        System.out.println("1. License plate");
        System.out.println("2. Car owner");
        System.out.println("3. Registration date");
        System.out.println("4. Vehicle type");

        int choice = Inputter.readIntInRange("Choose sort field: ", 1, 4);

        switch (choice) {
            case 1:
                return "plate";
            case 2:
                return "owner";
            case 3:
                return "date";
            default:
                return "type";
        }
    }

    public static boolean readAscendingDirection() {
        System.out.println();
        System.out.println("Sort direction:");
        System.out.println("1. Ascending (ASC)");
        System.out.println("2. Descending (DESC)");

        int choice = Inputter.readIntInRange("Choose direction: ", 1, 2);

        return choice == 1;
    }

    public static String insuranceSortLabel(String sortField) {
        switch (sortField) {
            case "id":
                return "Insurance Id";
            case "date":
                return "Established Date";
            case "plate":
                return "License plate";
            default:
                return "Insurance period";
        }
    }

    public static String carSortLabel(String sortField) {
        switch (sortField) {
            case "plate":
                return "License plate";
            case "owner":
                return "Vehicle Owner";
            case "date":
                return "Registration Date";
            default:
                return "Vehicle type";
        }
    }

    public static String directionLabel(boolean ascending) {
        return ascending ? "ASC" : "DESC";
    }
}
