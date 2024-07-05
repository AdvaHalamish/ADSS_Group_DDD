package BuisnessLayer;

import java.sql.Date;
import java.time.LocalDate;

public class Item {

    private String itemCode;
    public ItemPlace stored;
    private LocalDate expirationDate;
    public ItemStatus status;

    /**
     * Constructs a new Item object with the given parameters.
     *
     * @param stored         : the place stored of the Item
     * @param itemCode       : the code of the Item
     * @param expirationDate : the expiration date of the Item
     * @param status
     */

    public Item(ItemPlace stored, String itemCode, LocalDate expirationDate, ItemStatus status) {
        this.stored = stored;
        this.itemCode = itemCode;
        this.expirationDate = expirationDate;
        this.status = status;
    }


    @Override
    public String toString() {
        return "   itemCode='" + itemCode + '\'' +
                ", stored=" + stored +
                ", expirationDate=" + expirationDate +
                ", status=" + status;
    }

    /**
     * Gets the code of the Item.
     *
     * @return the code of the Item
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * Gets the place stored of the Item.
     *
     * @return the place stored of the Item
     */
    public ItemPlace getStored() {
        return stored;
    }

    /**
     * Updates the place stored of the Item.
     *
     * @param newPlace the new place stored
     */
    public void setPlace(ItemPlace newPlace) {
        stored = newPlace;
    }

    /**
     * Checks if the Item is expired.
     *
     * @return true if the Item is expired, false otherwise
     */
    public boolean isExpired() {
        if (LocalDate.now().isAfter(expirationDate)) {
            setStatus(ItemStatus.Expired);
            return true;
        }
        return false;
    }

    /**
     * Sets the status of the Item.
     *
     * @param status the status to set
     */
    public void setStatus(ItemStatus status) {
        this.status = status;
    }


    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ItemPlace getItemPlace() {
        return stored;
    }

    public ItemStatus getItemStatus() {
    return status;}

}

