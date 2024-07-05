package BuisnessLayer;

import java.time.LocalDate;

public class Discount {
    private double discountRate;
    private LocalDate startDate;
    private LocalDate endDate;

    public Discount(double discountRate, LocalDate startDate, LocalDate endDate) {
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isDiscountActive() {
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return (now.isEqual(startDate) || now.isAfter(startDate)) &&
                (now.isEqual(endDate) || now.isBefore(endDate));
    }

    @Override
    public String toString() {
        return this.discountRate + " " + this.startDate + " - " + this.endDate;
    }


}