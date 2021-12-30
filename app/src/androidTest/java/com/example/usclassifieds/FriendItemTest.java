package com.example.usclassifieds;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FriendItemTest {

    FriendItem item;

    @Before
    public void init() {
        item = new FriendItem(500, "Example Name", 1);
    }

    @Test
    public void testGetImg() {
        assertEquals(item.getImg(), 500);
    }

    @Test
    public void testGetUsername() {
        assertEquals(item.getUsername(), "Example Name");
    }

    @Test
    public void testGetId() {
        assertEquals(item.getId(), 1);
    }
}