package dispatcher;

import service.CarManager;
import service.StatementManager;
import fileio.BinaryStorage;
import fileio.Storage;
import model.Car;
import model.InsuranceStatement;
import utils.DateUtils;
import utils.Inputter;
import utils.Menu;
import validation.CarValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String CAR_FILE = "carInfo.dat";
    private static final String INSURANCE_FILE = "insurances.dat";

    private final CarManager carManager = new CarManager();
    private final StatementManager statementManager
            = new StatementManager();

    private final Storage<Map<String, Car>> carStorage
            = new BinaryStorage<>();

    private final Storage<Map<String, InsuranceStatement>> insuranceStorage = new BinaryStorage<>();

    private boolean dataChanged = false;

    public static void main(String[] args) {
        Main application = new Main();
        application.run();
    }

    private void run() {
        while (true) {
            Menu.showMainMenu();

            int choice = Inputter.readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addCars();
                    break;

                case 2:
                    findCar();
                    break;

                case 3:
                    updateCar();
                    break;

                case 4:
                    deleteCar();
                    break;

                case 5:
                    addInsurances();
                    break;

                case 6:
                    listInsuranceStatements();
                    break;

                case 7:
                    reportUninsuredCars();
                    break;

                case 8:
                    saveData();
                    break;

                case 9:
                    loadData();
                    break;

                case 10:
                    if (handleQuit()) {
                        return;
                    }
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1 to 10."
                    );
            }
        }
    }

    // FUNCTION 1: ADD CAR INFORMATION
    private void addCars() {
        while (true) {
            System.out.println();
            System.out.println("--- Add Car Information ---");

            String plate = readNewLicensePlate();
            Car car = readCarDetails(plate, null);

            if (carManager.addCar(car)) {
                dataChanged = true;
                System.out.println("Car added successfully.");
            } else {
                System.out.println("Cannot add car.");
            }

            if (!Inputter.readYesNo("Add another car? (Y/N): ")) {
                return;
            }
        }
    }

    // Hoi bien so cho toi khi nhap mot bien so chua ton tai.
    private String readNewLicensePlate() {
        while (true) {
            String plate = Inputter.readRequiredString("License plate: ");

            if (!carManager.containsCar(plate)) {
                return plate;
            }
            System.out.println("License plate already exists.");
        }
    }

    // Dung cho ca Add va Update.
    // current == null: them xe moi (moi truong bat buoc nhap).
    // current != null: cap nhat xe (Enter de giu gia tri cu).
    private Car readCarDetails(String plate, Car current) {
        String owner = Inputter.readField("Car owner (2-35 characters)",
                current == null ? null : current.getCarOwner(),
                text -> text, Main::parseOwner);

        String brand = Inputter.readField("Car brand",
                current == null ? null : current.getCarBrand(),
                text -> text, text -> text);

        Long value = Inputter.readField("Vehicle value (> 999)",
                current == null ? null : Long.valueOf(current.getVehicleValue()),
                number -> String.valueOf(number), Main::parseValue);

        LocalDate date = Inputter.readField("Registration date (MM/dd/yyyy)",
                current == null ? null : current.getRegistrationDate(),
                DateUtils::formatDate, Main::parseDate);

        String place = Inputter.readField("Registration place",
                current == null ? null : current.getRegistrationPlace(),
                text -> text, text -> text);

        Integer type = Inputter.readField("Vehicle type (5, 7 or 9)",
                current == null ? null : Integer.valueOf(current.getVehicleType()),
                number -> String.valueOf(number), Main::parseVehicleType);

        return new Car(plate, owner, brand, value, date, place, type);
    }

    // FUNCTION 2: FIND A CAR
    private void findCar() {
        System.out.println();
        System.out.println("--- Find a Car ---");

        String plate = Inputter.readRequiredString(
                "Enter license plate: "
        );

        Car car = carManager.findCar(plate);

        if (car == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.printf("| %-22s | %-35s |%n", "Field", "Information");
        System.out.println("----------------------------------------------------------------");

        System.out.printf("| %-22s | %-35s |%n",
                "License plate", car.getLicensePlate());

        System.out.printf("| %-22s | %-35s |%n",
                "Car owner", car.getCarOwner());

        System.out.printf("| %-22s | %-35s |%n",
                "Car brand", car.getCarBrand());

        System.out.printf("| %-22s | %-35s |%n",
                "Vehicle value", car.getVehicleValue());

        System.out.printf("| %-22s | %-35s |%n",
                "Registration date",
                DateUtils.formatDate(car.getRegistrationDate()));

        System.out.printf("| %-22s | %-35s |%n",
                "Registration place", car.getRegistrationPlace());

        System.out.printf("| %-22s | %-35s |%n",
                "Vehicle type", car.getVehicleType());

        System.out.println("----------------------------------------------------------------");
    }

    // FUNCTION 3: UPDATE A CAR
    private void updateCar() {
        System.out.println();
        System.out.println("--- Update a Car ---");

        String plate = Inputter.readRequiredString("Enter license plate: ");
        Car existingCar = carManager.findCar(plate);

        if (existingCar == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        System.out.println("License plate: " + existingCar.getLicensePlate()
                + " (cannot be changed)");
        System.out.println("Press Enter to keep the value shown in [ ].");

        Car updatedCar = readCarDetails(existingCar.getLicensePlate(), existingCar);

        if (carManager.updateCar(existingCar.getLicensePlate(), updatedCar)) {
            dataChanged = true;
            System.out.println("Update successful.");
        } else {
            System.out.println("Update failed.");
        }
    }

    // FUNCTION 4: DELETE A CAR
    private void deleteCar() {
        System.out.println();
        System.out.println("--- Delete a Car ---");

        String plate = Inputter.readRequiredString(
                "Enter license plate: "
        );

        Car car = carManager.findCar(plate);

        if (car == null) {
            System.out.println("Unregistered vehicle");
            return;
        }

        if (statementManager.hasInsuranceForPlate(
                car.getLicensePlate())) {

            System.out.println(
                    "Cannot delete this car because it has "
                    + "an insurance statement."
            );
            return;
        }

        System.out.println(car);

        boolean confirmed = Inputter.readYesNo(
                "Are you sure you want to delete this car? (Y/N): "
        );

        if (!confirmed) {
            System.out.println("Delete operation cancelled.");
            return;
        }

        if (carManager.deleteCar(plate)) {
            dataChanged = true;
            System.out.println("Car deleted successfully.");
        } else {
            System.out.println("Cannot delete car.");
        }
    }

    // FUNCTION 5: ADD AN INSURANCE STATEMENT
    private void addInsurances() {
        while (true) {
            System.out.println();
            System.out.println("--- Add an Insurance Statement ---");

            InsuranceStatement statement = readNewStatement();

            if (statement != null
                    && statementManager.addInsurance(statement, carManager)) {
                dataChanged = true;
                System.out.println("Insurance statement added successfully.");
                printInsuranceTable(java.util.Collections.singletonList(statement));
            } else {
                System.out.println("Insurance statement was not added.");
            }

            if (!Inputter.readYesNo("Add another insurance statement? (Y/N): ")) {
                return;
            }
        }
    }

    // Tra ve null neu bien so chua dang ky.
    // Phi bao hiem truyen 0 vi StatementManager.addInsurance tu tinh lai tu gia tri xe.
    private InsuranceStatement readNewStatement() {
        String id = readNewInsuranceId();

        LocalDate date = Inputter.readField("Established date (MM/dd/yyyy)",
                null, DateUtils::formatDate, Main::parseDate);

        String plate = Inputter.readRequiredString("License plate: ");
        Car car = carManager.findCar(plate);

        if (car == null) {
            System.out.println("Unregistered vehicle");
            return null;
        }

        String customerName = Inputter.readRequiredString("Customer name: ");

        int period = Inputter.readField("Insurance period in months (12, 24 or 36)",
                null, number -> String.valueOf(number), Main::parsePeriod);

        return new InsuranceStatement(id, date, car.getLicensePlate(),
                customerName, period, 0);
    }

    // Hoi ID cho toi khi nhap mot ID chua ton tai.
    private String readNewInsuranceId() {
        while (true) {
            String id = Inputter.readRequiredString("Insurance ID: ");

            if (!statementManager.containsInsurance(id)) {
                return id;
            }
            System.out.println("Insurance ID already exists.");
        }
    }

    // FUNCTION 6: LIST INSURANCE STATEMENTS
    private void listInsuranceStatements() {
        System.out.println();
        System.out.println("--- List of Insurance Statements ---");

        int year = Inputter.readIntInRange(
                "Enter year: ", 1, 9999
        );

        String sortField = Menu.readInsuranceSortField();
        boolean ascending = Menu.readAscendingDirection();

        List<InsuranceStatement> result
                = statementManager.getInsuranceByYear(
                        year, sortField, ascending
                );

        printInsuranceReport(year, sortField, ascending, result);
    }

    private void printInsuranceReport(int year, String sortField,
            boolean ascending, List<InsuranceStatement> statements) {

        System.out.println();
        System.out.println("Report : INSURANCE STATEMENTS");
        System.out.println("From : "
                + DateUtils.formatDate(LocalDate.of(year, 1, 1))
                + " To: "
                + DateUtils.formatDate(LocalDate.of(year, 12, 31)));
        System.out.println("Sorted by: " + Menu.insuranceSortLabel(sortField));
        System.out.println("Sort type : " + Menu.directionLabel(ascending));

        if (statements.isEmpty()) {
            System.out.println(
                    "No insurance statements found for this year.");
            return;
        }

        printInsuranceTable(statements);
    }

    // Tạo đường kẻ ngang theo độ rộng các cột (tương thích Java 8).
    private String createBorder(int... widths) {
        int length = 1;

        for (int width : widths) {
            length += width + 3;
        }

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < length; i++) {
            line.append('-');
        }

        return line.toString();
    }

    private void printInsuranceTable(List<InsuranceStatement> statements) {
        String border = createBorder(3, 12, 10, 13, 20, 6, 14);
        String format
                = "| %-3s | %-12s | %-10s | %-13s | %-20s | %6s | %14s |%n";

        System.out.println(border);
        System.out.printf(format, "No.", "Insurance ID", "Date",
                "License Plate", "Customer Name", "Period", "Fees");
        System.out.println(border);

        int no = 1;

        for (InsuranceStatement statement : statements) {
            System.out.printf(format,
                    no++,
                    statement.getInsuranceId(),
                    DateUtils.formatDate(statement.getEstablishedDate()),
                    statement.getLicensePlate(),
                    statement.getCustomerName(),
                    statement.getInsurancePeriod(),
                    String.format(java.util.Locale.US, "$ %,d",
                            statement.getInsuranceFees()));
        }

        System.out.println(border);
    }

    // FUNCTION 7: REPORT ON UNINSURED CARS
    private void reportUninsuredCars() {
        System.out.println();
        System.out.println("--- Report on Uninsured Cars ---");

        String sortField = Menu.readCarSortField();
        boolean ascending = Menu.readAscendingDirection();

        List<Car> result = carManager.getUninsuredCars(
                statementManager,
                sortField,
                ascending
        );

        printUninsuredReport(sortField, ascending, result);
    }

    private void printUninsuredReport(String sortField,
            boolean ascending, List<Car> cars) {

        System.out.println();
        System.out.println("Report: UNINSURED CARS");
        System.out.println("Sorted by : " + Menu.carSortLabel(sortField));
        System.out.println("Sort type : " + Menu.directionLabel(ascending));

        if (cars.isEmpty()) {
            System.out.println("No uninsured cars found.");
            return;
        }

        String border = createBorder(3, 13, 10, 20, 12, 4, 14);
        String format
                = "| %-3s | %-13s | %-10s | %-20s | %-12s | %4s | %14s |%n";

        System.out.println(border);
        System.out.printf(format, "No.", "License Plate", "Reg. Date",
                "Owner", "Brand", "Type", "Value");
        System.out.println(border);

        int no = 1;

        for (Car car : cars) {
            System.out.printf(format,
                    no++,
                    car.getLicensePlate(),
                    DateUtils.formatDate(car.getRegistrationDate()),
                    car.getCarOwner(),
                    car.getCarBrand(),
                    car.getVehicleType(),
                    String.format(java.util.Locale.US, "$ %,d",
                            car.getVehicleValue()));
        }

        System.out.println(border);
    }

    // FUNCTION 8: SAVE DATA
    private boolean saveData() {
        System.out.println();
        System.out.println("--- Save Data ---");

        boolean carsSaved = carStorage.save(
                CAR_FILE, carManager.getCars()
        );

        boolean insurancesSaved = insuranceStorage.save(
                INSURANCE_FILE, statementManager.getInsurances()
        );

        if (carsSaved && insurancesSaved) {
            dataChanged = false;
            System.out.println("Data saved successfully.");
            System.out.println("Car data file: " + CAR_FILE);
            System.out.println(
                    "Insurance data file: " + INSURANCE_FILE
            );
            return true;
        }

        System.out.println(
                "Some data could not be saved. Please try again."
        );
        return false;
    }

    // FUNCTION 9: LOAD DATA
    private void loadData() {
        System.out.println();
        System.out.println("--- Load Data ---");

        if (dataChanged) {
            boolean continueLoading = Inputter.readYesNo(
                    "Unsaved changes may be lost. Continue loading? (Y/N): "
            );

            if (!continueLoading) {
                System.out.println("Load operation cancelled.");
                return;
            }
        }

        try {
            Map<String, Car> loadedCars
                    = carStorage.load(CAR_FILE);

            Map<String, InsuranceStatement> loadedInsurances
                    = insuranceStorage.load(INSURANCE_FILE);

            if (loadedCars == null || loadedInsurances == null) {
                System.out.println(
                        "Cannot load data. Check whether both data "
                        + "files exist and are valid."
                );
                return;
            }

            carManager.setCars(loadedCars);
            statementManager.setInsurances(loadedInsurances);

            dataChanged = false;

            System.out.println("Data loaded successfully.");
            System.out.println(
                    "Number of cars: " + carManager.size()
            );
            System.out.println(
                    "Number of insurance statements: "
                    + statementManager.size()
            );

        } catch (ClassCastException e) {
            System.out.println(
                    "The data files have an unexpected format."
            );
        }
    }

    // FUNCTION 10: QUIT
    private boolean handleQuit() {
        System.out.println();

        boolean confirmed = Inputter.readYesNo(
                "Are you sure you want to quit? (Y/N): "
        );

        if (!confirmed) {
            System.out.println("Returning to the main menu.");
            return false;
        }

        if (dataChanged) {
            System.out.println(
                    "There are unsaved changes. Saving data..."
            );

            boolean saved = saveData();

            if (!saved) {
                boolean quitAnyway = Inputter.readYesNo(
                        "Saving failed. Quit anyway? (Y/N): "
                );

                if (!quitAnyway) {
                    return false;
                }
            }
        }

        System.out.println("Program terminated.");
        return true;
    }

    // ---------- Parsers dung cho Inputter.readField ----------

    private static String parseOwner(String text) {
        if (!CarValidator.isValidOwner(text)) {
            throw new IllegalArgumentException(
                    "Car owner must contain 2 to 35 characters.");
        }
        return text;
    }

    private static Long parseValue(String text) {
        long value;

        try {
            value = Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid whole number.");
        }

        if (!CarValidator.isValidValue(value)) {
            throw new IllegalArgumentException(
                    "Vehicle value must be greater than 999.");
        }
        return value;
    }

    private static Integer parseVehicleType(String text) {
        try {
            int type = Integer.parseInt(text);

            if (CarValidator.isValidVehicleType(type)) {
                return type;
            }
        } catch (NumberFormatException e) {
            // roi xuong thong bao loi ben duoi
        }
        throw new IllegalArgumentException("Vehicle type must be 5, 7, or 9.");
    }

    private static Integer parsePeriod(String text) {
        try {
            int period = Integer.parseInt(text);

            if (period == 12 || period == 24 || period == 36) {
                return period;
            }
        } catch (NumberFormatException e) {
            // roi xuong thong bao loi ben duoi
        }
        throw new IllegalArgumentException(
                "Insurance period must be 12, 24, or 36 months.");
    }

    private static LocalDate parseDate(String text) {
        try {
            return DateUtils.parseDate(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date. Try MM/dd/yyyy or yyyy-MM-dd.");
        }
    }
}
