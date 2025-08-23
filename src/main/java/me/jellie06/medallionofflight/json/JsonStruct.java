package me.jellie06.medallionofflight.json;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class JsonStruct {



    public List<String> recipe_shape;

    //Arraylist to store our items
   public List<JsonItem> items;

    public static JsonStruct getDefaultConfig(){
       return new JsonStruct(Arrays.asList(new JsonItem[]{new JsonItem(8, Material.DIAMOND_BLOCK.name(), 'F'), new JsonItem(1, Material.NETHER_STAR.name(), 'G'), new JsonItem(1, Material.ELYTRA.name(), 'E')}), Arrays.asList("FGF", "GEG", "FGF"));

    }

    JsonStruct(List<JsonItem> json_items, List<String> shape){
        recipe_shape = shape;
        items = json_items;


    }
}
