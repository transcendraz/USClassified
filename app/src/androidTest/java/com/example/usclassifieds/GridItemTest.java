package com.example.usclassifieds;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GridItemTest {

    ArrayList<String> tags;

    GridItem item;

    @Before
    public void init() {
        tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");
        item = new GridItem(0, "Example Item", "image.png", 1,
                50.0, new JSONArray(tags), 0.0, 1.0, 2.0, 3.0);
    }

    @Test
    public void testGetTitle() {
        assertEquals(item.getTitle(), "Example Item");
    }

    @Test
    public void testGetImage() {
        assertEquals(item.getImage(), "image.png");
    }

    @Test
    public void testGetItemId() {
        assertEquals(item.getItemId(), 0);
    }

    @Test
    public void testGetUserId() {
        assertEquals(item.getUserId(), 1);
    }

    @Test
    public void testGetPrice() {
        assertEquals(item.getPrice(), 50.0, 0.2);

    }

    @Test
    public void testSetSort() {
        assertEquals(GridItem.sortBy, "");
        String sorter = "Price";
        GridItem.sortBy = sorter;
        assertEquals(GridItem.sortBy, "Price");
    }

    @Test
    public void testSetFilter() {
        assertEquals(GridItem.filterBy, "");
        String filter = "Price (Upper)";
        GridItem.filterBy = filter;
        assertEquals(GridItem.filterBy, "Price (Upper)");
    }

    @Test
    public void testCompare() {
        GridItem item2 = new GridItem(1, "Example Item2", "image.png2", 2,
                49.0, new JSONArray(tags), 1.0, 2.0, 3.0, 4.0);
        GridItem.sortBy = "Price";
        int checker = item.compare(item, item2);
        assertTrue(checker < 0);
        GridItem.sortBy = "User";
        checker = item.compare(item, item2);
        assertTrue(checker > 0);
    }

    @Test
    public void testCompareTo() {
        GridItem item2 = new GridItem(1, "Example Item2", "image.png2", 2,
                49.0, new JSONArray(tags), 1.0, 2.0, 3.0, 4.0);
        item.dist = 0.0;
        item2.dist = 5.0;
        GridItem.sortBy = "Item Name";
        int checker = item.compareTo(item2);
        assertTrue(checker > 0);
        GridItem.sortBy = "Price";
        checker = item.compareTo(item2);
        assertTrue(checker > 0);
        GridItem.sortBy = "User";
        checker = item.compareTo(item2);
        assertTrue(checker < 0);
        GridItem.sortBy = "Proximity";
        checker = item.compareTo(item2);
        assertTrue(checker < 0);
        GridItem.sortBy = "";
        checker = item.compareTo(item2);
        assertTrue(checker < 0);
    }
}