package me.jellie06.medallionofflight.json;

public class JsonItem {
    public String item_name;
    public int item_count;
    public char key;

    JsonItem(int c, String n, char k){
        item_count = c;
        item_name = n;
        key = k;
    }
}
