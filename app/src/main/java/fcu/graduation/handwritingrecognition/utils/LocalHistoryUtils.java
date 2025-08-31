package fcu.graduation.handwritingrecognition.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.graduation.handwritingrecognition.model.History;

public class LocalHistoryUtils {
    private static final String FILE_NAME = "history.json";
    private final Gson gson = new Gson();

    private File getFile(Context context) {
        return new File(context.getFilesDir(), FILE_NAME);
    }

    public Map<String, History> load(Context context) {
        File file = getFile(context);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Type type = new TypeToken<Map<String, History>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void save(Context context, Map<String, History> data) {
        File file = getFile(context);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 刪除某筆紀錄
    public void delete(Context context, String imageUrl) {
        Map<String, History> historyMap = load(context);  // 先讀出所有紀錄
        if (historyMap.containsKey(imageUrl)) {
            historyMap.remove(imageUrl);                  // 移除指定的 key
            save(context, historyMap);                    // 存回檔案
        }
    }

    public List<Map.Entry<String, History>> loadSorted(Context context) {
        Map<String, History> historyMap = load(context);

        List<Map.Entry<String, History>> list = new ArrayList<>(historyMap.entrySet());
        list.sort((a, b) -> Long.compare(b.getValue().getTimestamp(), a.getValue().getTimestamp()));
        return list;
    }
}
