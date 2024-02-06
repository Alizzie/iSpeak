package fr.thomas.menard.ispeak.Utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WriteCSV extends ViewModel {

    String name;
    /**
     * Get the instance of SensorViewModel
     *
     * @param owner The life cycle owner from activity/fragment
     * @return The SensorViewModel
     */
    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {

        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
    }

    public void writeCSV(String filePath, String idPatient, String task, String compensation)
    {

        try {
            FileWriter outputfile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String [] data = (task +";"+ compensation).split(";");
            writer.writeNext(data);


            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createWriteCSV(String filePath,String idPatient, String task, String compensation)
    {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String date = new SimpleDateFormat("yyyy_MM_dd HH_mm", Locale.getDefault()).format(new Date());


            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient ID", "Time","",""});
            summ.add(new String[] { idPatient, date,"",""});
            summ.add(new String[]{});
            writer.writeAll(summ);


            if(task!=null){
                // create a List which contains String array
                List<String[]> data = new ArrayList<String[]>();
                data.add(new String[] { "Task", "Event"});
                data.add(new String[] { task, compensation});
                writer.writeAll(data);
            }

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean checkFileName(String outputFileName, String filePath) {
        boolean flag = false;
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
        String files;

        if(!folder.exists()){
            folder.mkdirs();
            return false;
        }

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                if (files.equals(outputFileName)) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

}
