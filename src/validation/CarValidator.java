package validation;

import model.Car;

public class CarValidator {

    public static String getValidationError(Car car) {
        if (car == null) {
            return "Car information cannot be null.";
        }

        if (car.getLicensePlate() == null
                || car.getLicensePlate().trim().isEmpty()) {
            return "License plate cannot be empty.";
        }

        if (!isValidOwner(car.getCarOwner())) {
            return "Car owner must contain 2 to 35 characters.";
        }

        if (car.getCarBrand() == null
                || car.getCarBrand().trim().isEmpty()) {
            return "Car brand cannot be empty.";
        }

        if (!isValidValue(car.getVehicleValue())) {
            return "Vehicle value must be greater than 999.";
        }

        if (car.getRegistrationDate() == null) {
            return "Registration date is invalid.";
        }

        if (car.getRegistrationPlace() == null
                || car.getRegistrationPlace().trim().isEmpty()) {
            return "Registration place cannot be empty.";
        }

        if (!isValidVehicleType(car.getVehicleType())) {
            return "Vehicle type must be 5, 7, or 9.";
        }

        return null;
    }

    public static boolean isValid(Car car) {
        return getValidationError(car) == null;
    }

    public static boolean isValidVehicleType(int vehicleType) {
        return vehicleType == 5
                || vehicleType == 7
                || vehicleType == 9;
    }

    public static boolean isValidOwner(String owner) {
        return owner != null
                && owner.trim().length() >= 2
                && owner.trim().length() <= 35;
    }

    public static boolean isValidValue(long value) {
        return value > 999;
    }
}