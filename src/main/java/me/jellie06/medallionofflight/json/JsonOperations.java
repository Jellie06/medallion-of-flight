package me.jellie06.medallionofflight.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.jellie06.medallionofflight.MedallionOfFlight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.Scanner;

public class JsonOperations {

    final static String JSON_FILENAME = "recipe.json";



    public static JsonStruct jsonInit(File JSON_FILE) {

        try {
            if (JSON_FILE!=null){


                if(!JSON_FILE.exists()){
                    throw new FileNotFoundException();
                }


                Scanner scan = new Scanner(JSON_FILE);

                String tempstr = "";
                while(scan.hasNextLine()){
                    tempstr+=scan.nextLine();
                }

                scan.close();

                if(tempstr.isEmpty()){
                    return JsonStruct.getDefaultConfig();
                }


               JsonStruct data = new Gson().fromJson(tempstr, JsonStruct.class);
                //JsonType test = new JsonType(JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM);

                return data;








                //END OF IF STATEMENT BY THE WAY (Java is impossible to read)
            }

        }
        catch(NullPointerException e){
            MedallionOfFlight.plugin.getLogger().severe("JsonFileNullError, plugin shutting down");
            return JsonStruct.getDefaultConfig();
        }

        catch(FileNotFoundException e){
            return JsonStruct.getDefaultConfig();
        }

        catch(ExceptionInInitializerError e){
            return JsonStruct.getDefaultConfig();
        }

        return JsonStruct.getDefaultConfig();
    }



    public static void jsonSave(JsonStruct toJson, File JSON_FILE){

        try {
            if (JSON_FILE!=null){

                /*if(!JSON_FILE.canWrite()){
                    throw new ReadOnlyFileSystemException();
                }
*/
                if(!JSON_FILE.exists()){
                    JSON_FILE.createNewFile();
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                String str_data = gson.toJson(toJson);

                FileWriter writer = new FileWriter(JSON_FILE);

                writer.write(str_data);

                writer.close();





                //END OF IF STATEMENT BY THE WAY (Java is impossible to read)
            }

        }
        catch(NullPointerException e){
            MedallionOfFlight.plugin.getLogger().severe("JsonFileNullError, plugin shutting down");
            // return new JsonData(JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.JSON_FILE_NULL);
        }

        catch(ReadOnlyFileSystemException e){
            MedallionOfFlight.plugin.getLogger().severe("Json file is read only... Shutting down (How did you do that then?)");
            // return new JsonData(JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.JSON_FILE_READONLY);
        }

        catch (FileNotFoundException e) {
            JSON_FILE.mkdirs();

            //  return new JsonData(JsonData.HELMET_DEFAULT, JsonData.CHESTPLATE_DEFAULT, JsonData.LEGGINGS_DEFAULT, JsonData.BOOTS_DEFAULT, JsonData.JSON_FILE_EMPTY);
        } catch (IOException e) {
            MedallionOfFlight.plugin.getLogger().severe("Failed to create JSON file...");
        }

        //  return new JsonData(JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.NULLITEM, JsonData.JSON_FILE_NULL);
    }
}
