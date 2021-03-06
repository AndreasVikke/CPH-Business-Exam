package facades;

import com.google.gson.Gson;
import entities.DayPlan;
import entities.dto.RecipeDTO;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author emilt
 */
public class RecipeFacade {
    
    private static final Gson gson = new Gson();
    private static RecipeFacade instance;
    
    private RecipeFacade() {}
    
    public static RecipeFacade getFacade() {
        if (instance == null) {
            instance = new RecipeFacade();
        }
        return instance;
    }

    private Map.Entry<Long, RecipeDTO> fetch(String urlStr, long id) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr.trim().replace(" ", "%20"));
            System.out.println(url);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json;charset=UTF-8");
            con.addRequestProperty("User-Agent", "Mozilla/4.76;Chrome"); 
            String jsonStr = "";
            try ( Scanner scan = new Scanner(con.getInputStream())) {
                while (scan.hasNext()) {
                    jsonStr += scan.nextLine();
                }
            }
            return new SimpleEntry<Long, RecipeDTO>(id, gson.fromJson(jsonStr, RecipeDTO.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            con.disconnect();
        }
    }
    
    public RecipeDTO fetch(String name) {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://46.101.217.16:4000/recipe/" + name.trim().replace(" ", "%20"));
            System.out.println(url);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json;charset=UTF-8");
            con.addRequestProperty("User-Agent", "Mozilla/4.76;Chrome"); 
            String jsonStr = "";
            try ( Scanner scan = new Scanner(con.getInputStream())) {
                while (scan.hasNext()) {
                    jsonStr += scan.nextLine();
                }
            }
            return gson.fromJson(jsonStr, RecipeDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            con.disconnect();
        }
    }

    public Map<Long, RecipeDTO> fetch(List<DayPlan> specificList) {
        final ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Queue<Future<Map.Entry<Long, RecipeDTO>>> queue = new ArrayBlockingQueue(specificList.size());
            Map<Long, RecipeDTO> res = new HashMap();
            for (DayPlan specifc : specificList) {
                Future<Map.Entry<Long, RecipeDTO>> future = executor.submit(() -> {
                    return fetch("http://46.101.217.16:4000/recipe/" + specifc.getRecipeId().trim().replace(" ", "%20"), specifc.getId());
                });
                queue.add(future);
            }
            while (!queue.isEmpty()) {
                Future<Map.Entry<Long, RecipeDTO>> specific = queue.poll();
                if (specific.isDone()) {
                    res.put(specific.get().getKey(), specific.get().getValue());
                } else {
                    queue.add(specific);
                }
            }
            return res;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
    }
    
    private List<RecipeDTO> fetch(String[] specificList) {
        final ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Queue<Future<RecipeDTO>> queue = new ArrayBlockingQueue(specificList.length);
            List<RecipeDTO> res = new ArrayList();
            for (String specifc : specificList) {
                Future<RecipeDTO> future = executor.submit(() -> {
                    return fetch(specifc.trim().replace(" ", "%20"));
                });
                queue.add(future);
            }
            while (!queue.isEmpty()) {
                Future<RecipeDTO> specific = queue.poll();
                if (specific.isDone()) {
                    res.add(specific.get());
                } else {
                    queue.add(specific);
                }
            }
            return res;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }
    }
   
    public List<RecipeDTO> fetchAllRecipies() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://46.101.217.16:4000/allRecipes");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json;charset=UTF-8");
            con.addRequestProperty("User-Agent", "Mozilla/4.76;Chrome");
            String jsonStr = "";
            try ( Scanner scan = new Scanner(con.getInputStream())) {
                while (scan.hasNext()) {
                    jsonStr += scan.nextLine();
                }
            }
            jsonStr = jsonStr.substring(1, jsonStr.length()-1);
            return fetch(jsonStr.replace("\"", "").split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            con.disconnect();
        }
    }
}
