package Tests;

import java.time.LocalDate;
import BuisnessLayer.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void testIsExpired() {
        Item expiredItem = new Item(ItemPlace.Store, "Code1", LocalDate.now().minusDays(1), ItemStatus.Available);
        assertTrue(expiredItem.isExpired());

        Item nonExpiredItem = new Item(ItemPlace.Warehouse, "Code2", LocalDate.now().plusDays(10), ItemStatus.Available);
        assertFalse(nonExpiredItem.isExpired());
    }


    @Test
    public void testSetAndGetExpirationDate() {
        Item item = new Item(ItemPlace.Store, "Code", LocalDate.now().plusDays(10), ItemStatus.Available);
        LocalDate newDate = LocalDate.now().plusDays(5);
        item.setExpirationDate(newDate);
        assertEquals(newDate, item.getExpirationDate());
    }

    @Test
    public void testGetItemCode() {
        Item item = new Item(ItemPlace.Store, "Code123", LocalDate.now().plusDays(10), ItemStatus.Available);
        assertEquals("Code123", item.getItemCode());
    }

    @Test
    public void testGetItemStatus() {
        Item item = new Item(ItemPlace.Store, "Code123", LocalDate.now().plusDays(10), ItemStatus.Defective);
        assertEquals(ItemStatus.Defective, item.getStatus());
    }

    @Test
    public void testSetPlace() {
        Item item = new Item(ItemPlace.Store, "Code123", LocalDate.now().plusDays(10), ItemStatus.Available);
        item.setPlace(ItemPlace.Warehouse);
        assertEquals(ItemPlace.Warehouse, item.getStored());
    }
}
